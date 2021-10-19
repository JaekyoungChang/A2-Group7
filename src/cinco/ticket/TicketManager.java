package cinco.ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cinco.ticket.ConsoleManager.ConsoleException;
import cinco.ticket.ConsoleManager.TextDevice;

public class TicketManager {
	private static final Logger LOGGER = Logger.getLogger(TicketManager.class.getName());
	private static final String TICKETS_FILE_PATH = "data/tickets.csv";
	private static final String TICKETS_FILE_HEADER = "TIMESTAMP,ID,DESCRIPTION,STATUS,SEVERITY,SUBMITTED_BY,ASSIGNED_TO\n";
	private static final long TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000;
	private static final String EXIT_SIGNAL = "x";

	private static TicketManager DEFAULT_TICKET_MANAGER = new TicketManager();;

	public static TicketManager defaultTicketManager() {
		return DEFAULT_TICKET_MANAGER;
	}

	private final AccountManager accountManager;
	private final List<Ticket> tickets;

	public TicketManager() {
		this.accountManager = AccountManager.defaultAccountManager();
		this.tickets = new ArrayList<Ticket>();
		loadTickets();
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public List<Ticket> getTickets(final Account account) {
		if (account.getType() == AccountType.STAFF) {
			// can only see open tickets submitted by them
			return tickets.stream().filter(ticket -> ticket.getStatus() == TicketStatus.OPEN
					&& ticket.getSubmittedBy().equals(account.getId())).collect(Collectors.toList());
		} else if (account.getType() == AccountType.TECHNICIAN) {
			// can only see open tickets assigned to them, and any closed or archived
			// tickets
			return tickets.stream().filter(
					ticket -> ticket.getStatus() != TicketStatus.OPEN || ticket.getAssignedTo().equals(account.getId()))
					.collect(Collectors.toList());
		} else {
			// cannot see any tickets
			LOGGER.warning(String.format("Unknown account type %s", account.getType()));
			return Collections.emptyList();
		}
	}

	public Ticket getTicket(final UUID id) {
		for (final Ticket ticket : tickets) {
			if (id.equals(ticket.getId())) {
				LOGGER.info(String.format("Found ticket for %s", id));
				return ticket;
			}
		}
		LOGGER.info(String.format("Unable to find ticket with id %s", id));
		return null;
	}

	private void loadTickets() {
		try {
			// check if tickets file exists
			if (Files.notExists(Paths.get(TICKETS_FILE_PATH))) {
				// create tickets file
				Files.createDirectories(Paths.get(TICKETS_FILE_PATH).getParent());
				Files.createFile(Paths.get(TICKETS_FILE_PATH));
				Files.write(Paths.get(TICKETS_FILE_PATH), TICKETS_FILE_HEADER.getBytes(), StandardOpenOption.CREATE,
						StandardOpenOption.APPEND);
				LOGGER.info("Created tickets file");
			} else {
				// load tickets from tickets file
				try (final Stream<String> stream = Files.lines(Paths.get(TICKETS_FILE_PATH)).skip(1)) {
					stream.forEach(line -> {
						final String[] fields = line.split(",");
						final Ticket ticket = new Ticket(
								Instant.ofEpochMilli(Long.valueOf(fields[0])).atZone(ZoneId.systemDefault()),
								UUID.fromString(fields[1]), fields[2], TicketStatus.valueOf(fields[3]),
								TicketSeverity.valueOf(fields[4]), UUID.fromString(fields[5]),
								UUID.fromString(fields[6]));
						tickets.add(ticket);
					});
				}
				LOGGER.info("Loaded tickets from tickets file");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// archive any closed tickets greater than 24 hours old
		List<Ticket> archiveTickets = tickets.stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.CLOSED_RESOLVED
						|| ticket.getStatus() == TicketStatus.CLOSED_UNRESOLVED)
				.filter(ticket -> Math.abs(
						Instant.now().getEpochSecond() - ticket.getTimestamp().toEpochSecond()) > TWENTY_FOUR_HOURS)
				.collect(Collectors.toList());
		archiveTickets.forEach(ticket -> {			
			ticket.setStatus(TicketStatus.ARCHIVED);
			updateTicket(ticket);
			LOGGER.info(String.format("Archived ticket %s", ticket));
		});
	}

	private void addTicket(final Ticket ticket) {

		// check ticket doesn't already exist
		if (getTicket(ticket.getId()) != null) {
			LOGGER.info("Cannot add ticket");
			return;
		}

		// add ticket to memory
		tickets.add(ticket);

		// write ticket to tickets file
		final String ticketString = String.format("%d,%s,%s,%s,%s,%s,%s\n", ticket.getTimestamp().toEpochSecond(),
				ticket.getId(), ticket.getDescription(), ticket.getStatus(), ticket.getSeverity(),
				ticket.getSubmittedBy(), ticket.getAssignedTo());
		try {
			Files.write(Paths.get(TICKETS_FILE_PATH), ticketString.getBytes(), StandardOpenOption.APPEND);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		LOGGER.info(String.format("Added ticket %s", ticket));
	}

	private void updateTicket(final Ticket ticket) {

		// check ticket already exists
		if (getTicket(ticket.getId()) == null) {
			LOGGER.info("Cannot update ticket");
			return;
		}

		// update ticket in memory
		Ticket deleteTicket = null;
		for (final Ticket inMemoryTicket : tickets) {
			if (ticket.getId().equals(inMemoryTicket.getId())) {
				deleteTicket = inMemoryTicket;
			}
		}
		if (deleteTicket != null) {
			tickets.remove(deleteTicket);
			tickets.add(ticket);
		}

		// update ticket in tickets file
		final String updatedTicketString = String.format("%d,%s,%s,%s,%s,%s,%s", ticket.getTimestamp().toEpochSecond(),
				ticket.getId(), ticket.getDescription(), ticket.getStatus(), ticket.getSeverity(),
				ticket.getSubmittedBy(), ticket.getAssignedTo());
		try {
			try (final Stream<String> stream = Files.lines(Paths.get(TICKETS_FILE_PATH)).skip(1)) {
				final List<String> updatedTickets = stream.map(line -> {
					final String[] fields = line.split(",");
					final Ticket storedTicket = new Ticket(
							Instant.ofEpochMilli(Long.valueOf(fields[0])).atZone(ZoneId.systemDefault()),
							UUID.fromString(fields[1]), fields[2], TicketStatus.valueOf(fields[3]),
							TicketSeverity.valueOf(fields[4]), UUID.fromString(fields[5]), UUID.fromString(fields[6]));
					return storedTicket.getId().equals(ticket.getId()) ? updatedTicketString : line;
				}).collect(Collectors.toList());
				Files.write(Paths.get(TICKETS_FILE_PATH), TICKETS_FILE_HEADER.getBytes());
				Files.write(Paths.get(TICKETS_FILE_PATH), updatedTickets, StandardOpenOption.APPEND);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		LOGGER.info(String.format("Updated ticket %s", ticket));
	}

	public void assignTicket(final Ticket ticket) {

		// get eligible technicians
		final List<Account> eligibleTechnicians = accountManager.getAccounts().stream()
				.filter(account -> account.getType() == AccountType.TECHNICIAN)
				.filter(account -> (ticket.getSeverity().getLevel() <= 2 && account.getLevel().getLevel() == 1)
						|| (ticket.getSeverity().getLevel() == 3 && account.getLevel().getLevel() == 2))
				.collect(Collectors.toList());

		// assign technician if required
		if (ticket.getAssignedTo() == null
				|| !eligibleTechnicians.contains(accountManager.getAccount(ticket.getAssignedTo()))) {
			final Account assignedTechnician = eligibleTechnicians.stream()
					.min(Comparator.comparing(account -> getTickets(account).size()))
					.orElseThrow(NoSuchElementException::new);
			ticket.setAssignedTo(assignedTechnician.getId());
			LOGGER.info(String.format("Assigned ticket %s", ticket));
		}
	}

	private boolean validateId(final String id) {

		// check id is not empty
		if (id.isEmpty()) {
			LOGGER.warning("Invalid ticket ID! Must not be empty.");
			return false;
		}

		// check the is is a valid uuid
		try {
			UUID.fromString(id);
		} catch (final IllegalArgumentException e) {
			LOGGER.warning("Invalid ticket ID! Not a valid UUID.");
			return false;
		}

		// check id corresponds to a real ticket
		if (getTicket(UUID.fromString(id)) == null) {
			LOGGER.warning("Invalid ticket ID! There is no ticket with that ID.");
			return false;
		}

		return true;
	}

	private boolean validateDescription(final String description) {

		// check description is not empty
		if (description.isEmpty()) {
			LOGGER.warning("Invalid ticket description! Must not be empty.");
			return false;
		}

		return true;
	}

	private boolean validateSeverity(final String severity) {

		// check severity is not empty
		if (severity.isEmpty()) {
			LOGGER.warning("Invalid severity level! Must not be empty.");
			return false;
		}

		// check severity level is in range
		if (Integer.valueOf(severity) < 1 || Integer.valueOf(severity) > 3) {
			LOGGER.warning("Invalid severity level! Must enter a severity level between 1 and 3.");
			return false;
		}

		return true;
	}

	private boolean validateStatus(final String status) {

		// check status is not empty
		if (status.isEmpty()) {
			LOGGER.warning("Invalid status! Must not be empty.");
			return false;
		}

		// check status code is in range
		else if (Integer.parseInt(status) < 1 || Integer.parseInt(status) > 4) {
			LOGGER.warning("Invalid status! Must enter a status code between 1 and 4.");
			return false;

		}

		return true;
	}

	public boolean listTickets() {
		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("%n*** LIST TICKETS ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n", EXIT_SIGNAL));
			io.printf("%n");

			final List<Ticket> accountTickets = getTickets(accountManager.getActiveAccount());
			if (accountTickets.size() > 0) {
				io.printf("Here are your tickets:%n%n");
				for (final Ticket ticket : accountTickets) {
					io.printf("Timestamp:    %s%n", ticket.getTimestamp());
					io.printf("ID:           %s%n", ticket.getId());
					io.printf("Description:  %s%n", ticket.getDescription());
					io.printf("Status:       %s%n", ticket.getStatus().name());
					io.printf("Severity:     %s%n", ticket.getSeverity().name());
					io.printf("Submitted By: %s%n", accountManager.getAccount(ticket.getSubmittedBy()).getName());
					io.printf("Assigned To:  %s%n", accountManager.getAccount(ticket.getAssignedTo()).getName());
					io.printf("%n");
				}
			} else {
				io.printf("You have no tickets%n%n");
			}
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean submitTicket() {
		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("%n*** SUBMIT TICKET ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n%n", EXIT_SIGNAL));

			String description = null;
			while (true) {
				io.printf("Enter ticket description: ");
				description = io.readLine();
				if (description.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateDescription(description)) {
					break;
				}
			}

			String severity = null;
			while (true) {
				io.printf("1. Low%n");
				io.printf("2. Medium%n");
				io.printf("3. High%n");
				io.printf("%n");

				io.printf("Enter ticket severity level from the list above: ");
				severity = io.readLine();
				if (severity.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateSeverity(severity)) {
					break;
				}
			}

			final Ticket ticket = new Ticket(description, TicketSeverity.valueOf(Integer.valueOf(severity)),
					accountManager.getActiveAccount().getId());
			assignTicket(ticket);

			addTicket(ticket);
			io.printf("Ticket submitted.%n%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean changeTicketStatus() {
		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("%n*** UPDATE TICKET STATUS ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n%n", EXIT_SIGNAL));

			final List<Ticket> accountTickets = getTickets(accountManager.getActiveAccount());
			if (accountTickets.size() > 0) {
				String id = null;
				while (true) {
					for (final Ticket ticket : accountTickets) {
						io.printf("Timestamp:    %s%n", ticket.getTimestamp());
						io.printf("ID:           %s%n", ticket.getId());
						io.printf("Description:  %s%n", ticket.getDescription());
						io.printf("Status:       %s%n", ticket.getStatus().name());
						io.printf("Severity:     %s%n", ticket.getSeverity().name());
						io.printf("Submitted By: %s%n", accountManager.getAccount(ticket.getSubmittedBy()).getName());
						io.printf("Assigned To:  %s%n", accountManager.getAccount(ticket.getAssignedTo()).getName());
						io.printf("%n");
					}

					io.printf("Enter ticket ID from the list above: ");
					id = io.readLine();
					if (id.equals(EXIT_SIGNAL)) {
						return false;
					} else if (validateId(id)) {
						break;
					}
				}

				final Ticket ticket = getTicket(UUID.fromString(id));
				if (ticket.getStatus() == TicketStatus.ARCHIVED) {
					io.printf("You cannot update archived tickets.%n%n");
					return false;
				}

				String status = null;
				while (true) {
					io.printf("1. Open%n");
					io.printf("2. Closed (Resolved)%n");
					io.printf("3. Closed (Unresolved)%n");
					io.printf("4. Archived%n");
					io.printf("%n");

					io.printf("Select a ticket status from the list above: ");
					status = io.readLine();
					if (status.equals(EXIT_SIGNAL)) {
						return false;
					} else if (validateStatus(status)) {
						break;
					}
				}

				ticket.setStatus(TicketStatus.valueOf(Integer.valueOf(status)));
				updateTicket(ticket);
				io.printf("Ticket status updated.%n%n");
			} else {
				io.printf("You have no tickets.%n%n");
				return false;
			}
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean changeTicketSeverity() {
		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("%n*** UPDATE TICKET SEVERITY ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n%n", EXIT_SIGNAL));

			List<Ticket> accountTickets = getTickets(accountManager.getActiveAccount());
			if (accountTickets.size() > 0) {
				String id = null;
				while (true) {
					for (final Ticket ticket : accountTickets) {
						io.printf("Timestamp:    %s%n", ticket.getTimestamp());
						io.printf("ID:           %s%n", ticket.getId());
						io.printf("Description:  %s%n", ticket.getDescription());
						io.printf("Status:       %s%n", ticket.getStatus().name());
						io.printf("Severity:     %s%n", ticket.getSeverity().name());
						io.printf("Submitted By: %s%n", accountManager.getAccount(ticket.getSubmittedBy()).getName());
						io.printf("Assigned To:  %s%n", accountManager.getAccount(ticket.getAssignedTo()).getName());
						io.printf("%n");
					}

					io.printf("Enter ticket ID from the list above: ");
					id = io.readLine();
					if (id.equals(EXIT_SIGNAL)) {
						return false;
					} else if (validateId(id)) {
						break;
					}
				}

				final Ticket ticket = getTicket(UUID.fromString(id));
				if (ticket.getStatus() == TicketStatus.ARCHIVED) {
					io.printf("You cannot update archived tickets.%n%n");
					return false;
				}

				String severity = null;
				while (true) {
					io.printf("1. Low%n");
					io.printf("2. Medium%n");
					io.printf("3. High%n");
					io.printf("%n");

					io.printf("Enter ticket severity level from the list above: ");
					severity = io.readLine();
					if (severity.equals(EXIT_SIGNAL)) {
						return false;
					} else if (validateSeverity(severity)) {
						break;
					}
				}

				ticket.setSeverity(TicketSeverity.valueOf(Integer.valueOf(severity)));
				assignTicket(ticket);
				updateTicket(ticket);
				io.printf("Ticket severity updated.%n%n");
			} else {
				io.printf("You have no tickets.%n%n");
				return false;
			}
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}
}
