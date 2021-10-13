package cinco.ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cinco.ticket.ConsoleManager.ConsoleException;
import cinco.ticket.ConsoleManager.TextDevice;

public class AccountManager {

	private static final String EXIT_SIGNAL = "x";

	private static AccountManager accountManager = null;

	public static AccountManager getAccountManager() {
		if (accountManager == null) {
			accountManager = new AccountManager();
		}

		return accountManager;
	}

	private static final Logger LOGGER = Logger.getLogger(AccountManager.class.getName());
	private static final String ACCOUNTS_FILE_PATH = "accounts.csv";

	private final ArrayList<Account> accounts;
	private final ArrayList<Ticket> tickets;

	private Account activeAccount;

	public AccountManager() {
		this.accounts = new ArrayList<Account>();
		this.tickets = new ArrayList<Ticket>();
		loadAccounts();

		this.activeAccount = null;
	}

	private void loadAccounts() {
		try {
			// check if accounts file exists
			if (Files.notExists(Paths.get(ACCOUNTS_FILE_PATH))) {
				// create accounts file
				Files.write(Paths.get(ACCOUNTS_FILE_PATH), "TYPE,NAME,EMAIL,PHONE,PASSWORD\n".getBytes(),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);

				addHardCodeAccounts();

				LOGGER.info("Created accounts file");
			} else {
				// load accounts from accounts file
				try (final Stream<String> stream = Files.lines(Paths.get(ACCOUNTS_FILE_PATH)).skip(1)) {
					stream.forEach(line -> {
						final String[] fields = line.split(",");
						final Account account = new Account(AccountType.valueOf(fields[0]), fields[1], fields[2],
								fields[3], fields[4]);
						accounts.add(account);
					});
				}
				LOGGER.info("Loaded accounts from accounts file");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private AccountType getAccountType(final String role) {
		try {
			final int index = Integer.valueOf(role) - 1;
			final AccountType type = AccountType.values()[index];
			LOGGER.info(String.format("Found account type for %s", role));
			return type;
		} catch (final NumberFormatException | IndexOutOfBoundsException e) {
			LOGGER.info(String.format("Unable to find account type for %s", role));
			return null;
		}
	}

	private Account getAccount(final String name) {
		for (final Account account : accounts) {
			if (name.equals(account.getName())) {
				LOGGER.info(String.format("Found account for %s", name));
				return account;
			}
		}
		LOGGER.info(String.format("Unable to find account for %s", name));
		return null;
	}

	private void addAccount(final Account account) {

		// check account
		if (getAccount(account.getName()) != null) {
			LOGGER.info("Cannot add account");
			return;
		}

		// add account to memory
		accounts.add(account);

		// write account to accounts file
		final String accountString = String.format("%s,%s,%s,%s,%s\n", account.getType(), account.getName(),
				account.getEmail(), account.getPhone(), account.getPassword());
		try {
			Files.write(Paths.get(ACCOUNTS_FILE_PATH), accountString.getBytes(), StandardOpenOption.APPEND);
			LOGGER.info(String.format("Added account %s to accounts file", account));
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	private void addHardCodeAccounts() {

		addAccount(new Account(AccountType.STAFF, "test", "test@cinco.com", "0", "123"));
		addAccount(new Technician(AccountType.TECHNICIAN, "Harry Styles", "harrystyles@cinco.com", "1", "123", 1));
		addAccount(new Technician(AccountType.TECHNICIAN, "Niall Horan", "niallhoran@cinco.com", "2", "123", 1));
		addAccount(new Technician(AccountType.TECHNICIAN, "Liam Payne", "liampayne@cinco.com", "3", "123", 1));
		addAccount(
				new Technician(AccountType.TECHNICIAN, "Louis Tomlinson", "louistomlinson@cinco.com", "4", "123", 2));
		addAccount(new Technician(AccountType.TECHNICIAN, "Zayn Malik", "zaynmalik@cinco.com", "5", "123", 2));

	}

	private void updateAccount(final Account account) {

		// check account
		if (getAccount(account.getName()) == null) {
			LOGGER.info("Cannot update account");
			return;
		}

		// update account in memory
		Account deleteAccount = null;
		for (final Account inMemoryAccount : accounts) {
			if (account.getName().equals(inMemoryAccount.getName())) {
				deleteAccount = inMemoryAccount;
			}
		}
		if (deleteAccount != null) {
			accounts.remove(deleteAccount);
			accounts.add(account);
		}

		// update account in accounts file
		final String accountsHeader = "TYPE,NAME,EMAIL,PHONE,PASSWORD\n";
		final String accountString = String.format("%s,%s,%s,%s,%s", account.getType(), account.getName(),
				account.getEmail(), account.getPhone(), account.getPassword());
		try {
			try (final Stream<String> stream = Files.lines(Paths.get(ACCOUNTS_FILE_PATH)).skip(1)) {
				final List<String> updatedAccounts = stream.map(line -> {
					final String[] fields = line.split(",");
					final Account storedAccount = new Account(AccountType.valueOf(fields[0]), fields[1], fields[2],
							fields[3], fields[4]);
					return storedAccount.getName().equals(account.getName()) ? accountString : line;
				}).collect(Collectors.toList());
				Files.write(Paths.get(ACCOUNTS_FILE_PATH), accountsHeader.getBytes());
				Files.write(Paths.get(ACCOUNTS_FILE_PATH), updatedAccounts, StandardOpenOption.APPEND);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		LOGGER.info(String.format("Updated account %s", account));
	}

	private boolean validateRole(final String role) {

		// check role is not empty
		if (role.isEmpty()) {
			LOGGER.warning("Invalid role! Must not be empty.");
			return false;
		}

		final AccountType type = getAccountType(role);

		// check role is valid
		if (type == null) {
			LOGGER.warning("Invalid role! Unrecognised.");
			return false;
		}

		// check role is not technician
		if (type == AccountType.TECHNICIAN) {
			LOGGER.warning("Invalid role! Technicians can only be added by an administrator.");
			return false;
		}

		return true;
	}

	private boolean validateName(final String name) {

		// check name is not empty
		if (name.isEmpty()) {
			LOGGER.warning("Invalid name! Must not be empty.");
			return false;
		}

		// check name is unique
		if (getAccount(name) != null) {
			LOGGER.warning("Invalid name! Already in use.");
			return false;
		}

		return true;
	}

	private boolean validateEmail(final String email) {

		// check email is not empty
		if (email.isEmpty()) {
			LOGGER.warning("Invalid email address! Must not be empty.");
			return false;
		}

		// check email address is unique
		for (final Account account : accounts) {
			if (email.equals(account.getEmail())) {
				LOGGER.warning("Invalid email address! Already in use.");
				return false;
			}
		}

		return true;
	}

	private boolean validatePhone(final String phone) {

		// check phone number is not empty
		if (phone.isEmpty()) {
			LOGGER.warning("Invalid phone number! Must not be empty.");
			return false;
		}

		// check phone number is numeric
		try {
			Long.parseLong(phone);
		} catch (final NumberFormatException e) {
			LOGGER.warning("Invalid phone number! Must contain only numbers.");
			return false;
		}

		return true;
	}

	private boolean validatePassword(final String password) {

		// check password length
		if (password.length() < 20) {
			LOGGER.warning("Invalid password! Minimum password length is 20 characters.");
			return false;
		}

		// check password complexity
		boolean containsUppercase = false;
		boolean containsLowercase = false;
		boolean containsNumber = false;
		for (int i = 0; i < password.length(); i++) {
			final Character c = password.charAt(i);
			if (Character.isUpperCase(c)) {
				containsUppercase = true;
			} else if (Character.isLowerCase(c)) {
				containsLowercase = true;
			} else if (Character.isDigit(c)) {
				containsNumber = true;
			}
		}
		if (!containsLowercase || !containsUppercase || !containsNumber) {
			LOGGER.warning("Invalid password! Password must contain a number, uppercase and lowercase letter.");
			return false;
		}

		return true;
	}

	public Account getActiveAccount() {
		return activeAccount;
	}

	public void setActiveAccount(final Account account) {
		this.activeAccount = account;
	}

	public boolean login() {

		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("*** LOGIN ***%n");
			io.printf("Type \"%s\" to return to the previous menu%n", EXIT_SIGNAL);
			io.printf("%n");

			String name = null;
			while (true) {
				io.printf("Enter Name: ");
				name = io.readLine();
				if (name.equals(EXIT_SIGNAL)) {
					return false;
				} else if (getAccount(name) == null) {
					LOGGER.warning("Unrecognised account, try again...");
				} else {
					break;
				}
			}
			final Account account = getAccount(name);

			String password = null;
			while (true) {
				io.printf("Enter Password: ");
				password = String.valueOf(io.readPassword());
				if (password.equals(EXIT_SIGNAL)) {
					return false;
				} else if (!password.equals(account.getPassword())) {
					LOGGER.warning("Invalid password! try again...");
				} else {
					break;
				}
			}

			setActiveAccount(account);

			io.printf("Login successful.%n");
			io.printf("%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean resetPassword() {

		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("*** RESET PASSWORD ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n", EXIT_SIGNAL));
			io.printf("%n");

			String name = null;
			while (true) {
				System.out.print("Enter Name: ");
				name = io.readLine();
				if (name.equals(EXIT_SIGNAL)) {
					return false;
				} else if (getAccount(name) == null) {
					LOGGER.warning("Unrecognised account, try again...");
				} else {
					break;
				}
			}

			String password = null;
			while (true) {
				System.out.print("Enter New Password: ");
				password = String.valueOf(io.readPassword());
				if (password.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validatePassword(password)) {
					break;
				}
			}

			final Account account = getAccount(name);
			account.setPassword(password);
			updateAccount(account);

			io.printf("Password reset.%n");
			io.printf("%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean createAccount() {

		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("*** CREATE ACCOUNT ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n", EXIT_SIGNAL));
			io.printf("%n");

			String role = null;
			while (true) {
				io.printf("Choose a role:%n");
				io.printf("1. Staff%n");
				io.printf("2. Technician%n");
				io.printf("%n");
				System.out.print("Enter Choice: ");
				role = io.readLine();
				if (role.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateRole(role)) {
					break;
				}
			}

			String name = null;
			while (true) {
				System.out.print("Enter Name: ");
				name = io.readLine();
				if (name.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateName(name)) {
					break;
				}
			}

			String email = null;
			while (true) {
				System.out.print("Enter Email: ");
				email = io.readLine();
				if (email.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateEmail(email)) {
					break;
				}
			}

			String phone = null;
			while (true) {
				System.out.print("Enter Phone: ");
				phone = io.readLine();
				if (phone.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validatePhone(phone)) {
					break;
				}
			}

			String password = null;
			while (true) {
				System.out.print("Enter Password: ");
				password = String.valueOf(io.readPassword());
				if (password.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validatePassword(password)) {
					break;
				}
			}

			addAccount(new Account(getAccountType(role), name, email, phone, password));

			io.printf("Account created.%n");
			io.printf("%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public void submitTicket() {

		final TextDevice io = ConsoleManager.defaultTextDevice();
		String description = null;

		try {
			while (true) {
				System.out.print("Enter ticket description: ");
				description = io.readLine();
				if (validateDescription(description)) {
					break;
				}
			}

			String severity = null;

			while (true) {
				System.out.print("Enter ticket severity level (1-3): ");
				severity = io.readLine();
				if (validateSeverity(severity)) {
					break;
				}
			}

			Ticket ticket = new Ticket(description, TicketSeverity.values()[Integer.valueOf(severity) - 1],
					activeAccount);

			tickets.add(ticket);
			assignTicket(ticket);
			io.printf("Ticket submitted.%n");
			io.printf("%n");
		} catch (ConsoleException e) {
			e.printStackTrace();
		}
	}

	public boolean validateDescription(final String description) {

		// check if ticket description is not empty
		if (description.isEmpty()) {
			LOGGER.warning("Invalid ticket description! Must not be empty.");
			return false;
		}

		return true;
	}

	public boolean validateSeverity(final String severity) {

		// check if severity level is not empty
		if (severity.isEmpty()) {
			LOGGER.warning("Invalid severity level! Must not be empty.");
			return false;
		}

		if (Integer.valueOf(severity) < 1 || Integer.valueOf(severity) > 3) {
			LOGGER.warning("Invalid severity level! Must enter a severity level between 1 and 3.");
			return false;
		}

		return true;
	}

	public void assignTicket(final Ticket ticket) {

		int i = 0;
		ArrayList<Technician> technician = getTechnicians(ticket.getSeverity().getSeverityInt());
		// list of technicians with the least number of tickets that can be assigned the
		// ticket
		ArrayList<Technician> assignTicketTech = new ArrayList<Technician>();
		Technician assignedTech = null;

		while (assignTicketTech.size() == 0) {
			for (Technician tech : technician) {
				if (tech.numAssignedTickets() == i) {
					assignTicketTech.add(tech);
				}
			}

			i++;
		}

		if (assignTicketTech.size() == 1) {
			assignedTech = assignTicketTech.get(0);
			assignedTech.assignTicket(ticket);
		} else {
			Random rand = new Random();
			assignedTech = assignTicketTech.get(rand.nextInt(assignTicketTech.size()));
			assignedTech.assignTicket(ticket);
		}

	}

	public ArrayList<Technician> getTechnicians(final int severity) {

		// list of technicians that can service ticket
		ArrayList<Technician> technicians = new ArrayList<Technician>();

		for (Account account : accounts) {
			if (account.getType() == AccountType.TECHNICIAN) {
				Technician tech = (Technician) account;
				if (severity <= 2 && tech.getLevel() == 1) {
					technicians.add(tech);
				} else if (severity == 3 && tech.getLevel() == 2) {
					technicians.add(tech);
				}
			}
		}

		return technicians;
	}
}
