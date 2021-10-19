package cinco.ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cinco.ticket.ConsoleManager.ConsoleException;
import cinco.ticket.ConsoleManager.TextDevice;

public class AccountManager {
	private static final Logger LOGGER = Logger.getLogger(AccountManager.class.getName());
	private static final String ACCOUNTS_FILE_PATH = "data/accounts.csv";
	private static final String EXIT_SIGNAL = "x";

	private static AccountManager DEFAULT_ACCOUNT_MANAGER = new AccountManager();

	public static AccountManager defaultAccountManager() {
		return DEFAULT_ACCOUNT_MANAGER;
	}

	private final List<Account> accounts;

	private Account activeAccount;

	public AccountManager() {
		this.activeAccount = null;
		this.accounts = new ArrayList<Account>();
		loadAccounts();
	}

	public Account getActiveAccount() {
		return activeAccount;
	}

	public void setActiveAccount(final Account account) {
		this.activeAccount = account;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public Account getAccount(final UUID id) {
		for (final Account account : accounts) {
			if (account.getId().equals(id)) {
				LOGGER.info(String.format("Found account with id %s", id));
				return account;
			}
		}
		LOGGER.info(String.format("Unable to find account with id %s", id));
		return null;
	}

	public Account getAccount(final String name) {
		for (final Account account : accounts) {
			if (account.getName().equals(name)) {
				LOGGER.info(String.format("Found account for %s", name));
				return account;
			}
		}
		LOGGER.info(String.format("Unable to find account for %s", name));
		return null;
	}

	private void loadAccounts() {
		try {
			// check if accounts file exists
			if (Files.notExists(Paths.get(ACCOUNTS_FILE_PATH))) {
				// create accounts file
				Files.createDirectories(Paths.get(ACCOUNTS_FILE_PATH).getParent());
				Files.createFile(Paths.get(ACCOUNTS_FILE_PATH));
				Files.write(Paths.get(ACCOUNTS_FILE_PATH), "ID,TYPE,LEVEL,NAME,EMAIL,PHONE,PASSWORD\n".getBytes(),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				addHardCodedAccounts();
				LOGGER.info("Created accounts file");
			} else {
				// load accounts from accounts file
				try (final Stream<String> stream = Files.lines(Paths.get(ACCOUNTS_FILE_PATH)).skip(1)) {
					stream.forEach(line -> {
						final String[] fields = line.split(",");
						final Account account = new Account(UUID.fromString(fields[0]), AccountType.valueOf(fields[1]),
								AccountLevel.valueOf(fields[2]), fields[3], fields[4], fields[5], fields[6]);
						accounts.add(account);
					});
				}
				LOGGER.info("Loaded accounts from accounts file");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void addHardCodedAccounts() {
		// TODO: don't hard-code this, move it to a config file
		addAccount(new Account(UUID.randomUUID(), AccountType.STAFF, AccountLevel.ZERO, "test", "test@cinco.com", "0",
				"123"));
		addAccount(new Account(UUID.randomUUID(), AccountType.TECHNICIAN, AccountLevel.ONE, "Harry Styles",
				"harrystyles@cinco.com", "1", "123"));
		addAccount(new Account(UUID.randomUUID(), AccountType.TECHNICIAN, AccountLevel.ONE, "Niall Horan",
				"niallhoran@cinco.com", "2", "123"));
		addAccount(new Account(UUID.randomUUID(), AccountType.TECHNICIAN, AccountLevel.ONE, "Liam Payne",
				"liampayne@cinco.com", "3", "123"));
		addAccount(new Account(UUID.randomUUID(), AccountType.TECHNICIAN, AccountLevel.TWO, "Louis Tomlinson",
				"louistomlinson@cinco.com", "4", "123"));
		addAccount(new Account(UUID.randomUUID(), AccountType.TECHNICIAN, AccountLevel.TWO, "Zayn Malik",
				"zaynmalik@cinco.com", "5", "123"));
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

	private void addAccount(final Account account) {

		// check account
		if (getAccount(account.getId()) != null) {
			LOGGER.info("Cannot add account");
			return;
		}

		// add account to memory
		accounts.add(account);

		// write account to accounts file
		final String accountString = String.format("%s,%s,%s,%s,%s,%s,%s\n", account.getId(), account.getType(),
				account.getLevel(), account.getName(), account.getEmail(), account.getPhone(), account.getPassword());
		try {
			Files.write(Paths.get(ACCOUNTS_FILE_PATH), accountString.getBytes(), StandardOpenOption.APPEND);
			LOGGER.info(String.format("Added account %s to accounts file", account));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void updateAccount(final Account account) {

		// check account
		if (getAccount(account.getId()) == null) {
			LOGGER.info("Cannot update account");
			return;
		}

		// update account in memory
		Account deleteAccount = null;
		for (final Account inMemoryAccount : accounts) {
			if (account.getId().equals(inMemoryAccount.getId())) {
				deleteAccount = inMemoryAccount;
			}
		}
		if (deleteAccount != null) {
			accounts.remove(deleteAccount);
			accounts.add(account);
		}

		// update account in accounts file
		final String accountsHeader = "ID,TYPE,LEVEL,NAME,EMAIL,PHONE,PASSWORD\n";
		final String updatedAccountString = String.format("%s,%s,%s,%s,%s,%s,%s", account.getId(), account.getType(),
				account.getLevel(), account.getName(), account.getEmail(), account.getPhone(), account.getPassword());
		try {
			try (final Stream<String> stream = Files.lines(Paths.get(ACCOUNTS_FILE_PATH)).skip(1)) {
				final List<String> updatedAccounts = stream.map(line -> {
					final String[] fields = line.split(",");
					final Account storedAccount = new Account(UUID.fromString(fields[0]),
							AccountType.valueOf(fields[1]), AccountLevel.valueOf(fields[2]), fields[3], fields[4],
							fields[5], fields[6]);
					return storedAccount.getId().equals(account.getId()) ? updatedAccountString : line;
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

	public boolean login() {
		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("%n*** LOGIN ***%n");
			io.printf("Type \"%s\" to return to the previous menu%n%n", EXIT_SIGNAL);

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

			io.printf("Login successful.%n%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean resetPassword() {
		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("%n*** RESET PASSWORD ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n%n", EXIT_SIGNAL));

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

			String password = null;
			while (true) {
				io.printf("Enter New Password: ");
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

			io.printf("Password reset.%n%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean createAccount() {
		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("%n*** CREATE ACCOUNT ***%n");
			io.printf(String.format("type \"%s\" to return to the previous menu%n%n", EXIT_SIGNAL));

			String name = null;
			while (true) {
				io.printf("Enter Name: ");
				name = io.readLine();
				if (name.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateName(name)) {
					break;
				}
			}

			String email = null;
			while (true) {
				io.printf("Enter Email: ");
				email = io.readLine();
				if (email.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validateEmail(email)) {
					break;
				}
			}

			String phone = null;
			while (true) {
				io.printf("Enter Phone: ");
				phone = io.readLine();
				if (phone.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validatePhone(phone)) {
					break;
				}
			}

			String password = null;
			while (true) {
				io.printf("Enter Password: ");
				password = String.valueOf(io.readPassword());
				if (password.equals(EXIT_SIGNAL)) {
					return false;
				} else if (validatePassword(password)) {
					break;
				}
			}

			addAccount(new Account(name, email, phone, password));

			io.printf("Account created.%n%n");
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return true;
	}
}
