package cinco.ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountManager {

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

	private Account activeAccount;

	public AccountManager() {
		this.accounts = new ArrayList<Account>();
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

	private void updateAccount(final Account account) {

		// check account
		if (getAccount(account.getName()) == null) {
			LOGGER.info("Cannot update account");
			return;
		}

		// update account in memory
		for (final Account inMemoryAccount : accounts) {
			if (account.getName().equals(inMemoryAccount.getName())) {
				accounts.remove(inMemoryAccount);
				accounts.add(account);
			}
		}

		// update account in accounts file
		final String accountString = String.format("%s,%s,%s,%s\n", account.getName(), account.getEmail(),
				account.getPhone(), account.getPassword());
		try {
			try (final Stream<String> stream = Files.lines(Paths.get(ACCOUNTS_FILE_PATH))) {
				final List<String> updatedAccounts = stream.map(line -> {
					final String[] fields = line.split(",");
					final Account storedAccount = new Account(AccountType.valueOf(fields[0]), fields[1], fields[2],
							fields[3], fields[4]);
					return storedAccount.getName().equals(account.getName()) ? accountString : line;
				}).collect(Collectors.toList());
				Files.write(Paths.get(ACCOUNTS_FILE_PATH), updatedAccounts);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		LOGGER.info(String.format("Updated account %s", account));
	}

	private boolean validateRole(final String role) {

		// check role is not empty
		if (role.isEmpty()) {
			LOGGER.warning("Invalid type! Must not be empty.");
			return false;
		}

		// check role is valid
		if (getAccountType(role) == null) {
			LOGGER.warning("Invalid type! Unrecognised.");
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

		// TODO: more tests

		return true;
	}

	private boolean validatePassword(final String password) {

		// check password length
		if (password.length() < 6) {
			LOGGER.warning("Invalid password! Minimum password length is 6 characters.");
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

	public boolean login(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** LOGIN ***");

		System.out.print("Enter Name: ");
		String name = scanner.next();
		while (getAccount(name) == null) {
			LOGGER.warning("Unrecognised account, try again...");
			System.out.print("Enter Name:");
			name = scanner.next();
		}
		final Account account = getAccount(name);

		System.out.print("Enter Password: ");
		String password = scanner.next();
		while (!password.equals(account.getPassword())) {
			LOGGER.warning("Invalid password! try again...");
			System.out.print("Enter Password:");
			password = scanner.next();
		}

		System.out.print(account);
		setActiveAccount(account);

		System.out.println("Login successful.");

		return true;
	}

	public void resetPassword(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** RESET PASSWORD ***");

		System.out.print("Enter Name: ");
		String name = scanner.next();
		while (!validateName(name)) {
			System.out.print("Enter Name:");
			name = scanner.next();
		}
		final Account account = getAccount(name);

		System.out.print("Enter New Password: ");
		String password = scanner.next();
		while (!validatePassword(password)) {
			System.out.print("Enter New Password:");
			password = scanner.next();
		}
		account.setPassword(password);

		updateAccount(account);

		System.out.println("Password reset.");
	}

	public void createAccount(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** CREATE ACCOUNT ***");

		System.out.println("Choose a role:");
		System.out.println("1. Staff");
		System.out.println("2. Technician");
		System.out.println();
		System.out.print("Enter Choice: ");
		String role = scanner.next();
		while (!validateRole(role)) {
			System.out.print("Enter Choice:");
			role = scanner.next();
		}

		System.out.print("Enter Name: ");
		String name = scanner.next();
		while (!validateName(name)) {
			System.out.print("Enter Name:");
			name = scanner.next();
		}

		System.out.print("Enter Email: ");
		String email = scanner.next();
		while (!validateEmail(email)) {
			System.out.print("Enter Email:");
			email = scanner.next();
		}

		System.out.print("Enter Phone: ");
		String phone = scanner.next();
		while (!validatePhone(phone)) {
			System.out.print("Enter Phone:");
			phone = scanner.next();
		}

		System.out.print("Enter Password: ");
		String password = scanner.next();
		while (!validatePassword(password)) {
			System.out.print("Enter Password:");
			password = scanner.next();
		}

		addAccount(new Account(getAccountType(role), name, email, phone, password));

		System.out.println("Account created.");
	}
}
