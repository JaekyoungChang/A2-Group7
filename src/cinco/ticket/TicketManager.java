package cinco.ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
			return tickets.stream().filter(ticket -> ticket.getSubmittedBy().equals(account.getId()))
					.collect(Collectors.toList());
		} else if (account.getType() == AccountType.TECHNICIAN) {
			return tickets.stream().filter(ticket -> ticket.getAssignedTo().equals(account.getId()))
					.collect(Collectors.toList());
		}
		return tickets;
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
				Files.write(Paths.get(TICKETS_FILE_PATH),
						"ID,DESCRIPTION,STATUS,SEVERITY,SUBMITTED_BY,ASSIGNED_TO\n".getBytes(),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				LOGGER.info("Created tickets file");
			} else {
				// load tickets from tickets file
				try (final Stream<String> stream = Files.lines(Paths.get(TICKETS_FILE_PATH)).skip(1)) {
					stream.forEach(line -> {
						final String[] fields = line.split(",");
						final Ticket ticket = new Ticket(UUID.fromString(fields[0]), fields[1],
								TicketStatus.valueOf(fields[2]), TicketSeverity.valueOf(fields[3]),
								UUID.fromString(fields[4]), UUID.fromString(fields[5]));
						tickets.add(ticket);
					});
				}
				LOGGER.info("Loaded tickets from tickets file");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
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

		// check severity is in range
		if (Integer.valueOf(severity) < 1 || Integer.valueOf(severity) > 3) {
			LOGGER.warning("Invalid severity level! Must enter a severity level between 1 and 3.");
			return false;
		}

		return true;
	}

	public void assignTicket(final Ticket ticket) {

		// check ticket
		if (getTicket(ticket.getId()) != null) {
			LOGGER.info("Cannot add ticket");
			return;
		}

		// assign technician
		final List<Account> elligableTechnicians = accountManager.getAccounts().stream()
				.filter(account -> account.getType() == AccountType.TECHNICIAN)
				.filter(account -> (ticket.getSeverity().getLevel() <= 2 && account.getLevel().getLevel() == 1)
						|| (ticket.getSeverity().getLevel() == 3 && account.getLevel().getLevel() == 2))
				.collect(Collectors.toList());
		final Account assignedTechnician = elligableTechnicians.stream()
				.min(Comparator.comparing(account -> getTickets(account).size()))
				.orElseThrow(NoSuchElementException::new);
		ticket.setAssignedTo(assignedTechnician.getId());

		// add ticket to memory
		tickets.add(ticket);

		// write ticket to tickets file
		final String ticketString = String.format("%s,%s,%s,%s,%s,%s\n", ticket.getId(), ticket.getDescription(),
				ticket.getStatus(), ticket.getSeverity(), ticket.getSubmittedBy(), ticket.getAssignedTo());
		try {
			Files.write(Paths.get(TICKETS_FILE_PATH), ticketString.getBytes(), StandardOpenOption.APPEND);
			LOGGER.info(String.format("Added ticket %s to tickets file", ticket));
		} catch (final IOException e) {
			e.printStackTrace();
		}
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
					io.printf("ID:           %s%n", ticket.getId());
					io.printf("Description:  %s%n", ticket.getDescription());
					io.printf("Status:       %s%n", ticket.getStatus().name());
					io.printf("Severity:     %s%n", ticket.getSeverity().name());
					io.printf("Submitted By: %s%n", accountManager.getAccount(ticket.getSubmittedBy()).getName());
					io.printf("Assigned To:  %s%n", accountManager.getAccount(ticket.getAssignedTo()).getName());
					io.printf("%n");
				}
			} else {
				io.printf("You have no tickets");
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
			io.printf(String.format("type \"%s\" to return to the previous menu%n", EXIT_SIGNAL));
			io.printf("%n");

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
				io.printf("Enter ticket severity level (1-3): ");
				severity = io.readLine();
				if (severity.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateSeverity(severity)) {
					break;
				}
			}

			assignTicket(new Ticket(description, TicketSeverity.valueOf(Integer.valueOf(severity)),
					accountManager.getActiveAccount().getId()));

			io.printf("Ticket submitted.%n%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}
}
