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

	private static final Logger LOGGER = Logger.getLogger(AccountManager.class.getName());
	private static final String ACCOUNTS_FILE_PATH = "accounts.csv";

	private final ArrayList<Account> accounts;

	public AccountManager() {
		this.accounts = new ArrayList<Account>();
		loadAccounts();
	}

	private void loadAccounts() {
		try {
			// check if accounts file exists
			if (Files.notExists(Paths.get(ACCOUNTS_FILE_PATH))) {
				// create accounts file
				Files.write(Paths.get(ACCOUNTS_FILE_PATH), "NAME,EMAIL,PHONE,PASSWORD\n".getBytes(),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				LOGGER.info("Created accounts file");
			} else {
				// load accounts from accounts file
				try (final Stream<String> stream = Files.lines(Paths.get(ACCOUNTS_FILE_PATH))) {
					stream.forEach(line -> {
						final String[] fields = line.split(",");
						final Account account = new Account(fields[0], fields[1], fields[2], fields[3]);
						accounts.add(account);
					});
				}
				LOGGER.info("Loaded accounts from accounts file");
			}
		} catch (final IOException e) {
			e.printStackTrace();
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
		final String accountString = String.format("%s,%s,%s,%s\n", account.getName(), account.getEmail(),
				account.getPhone(), account.getPassword());
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
		for (final Account inMemoryAccount : accounts ) {
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
					final Account storedAccount = new Account(fields[0], fields[1], fields[2], fields[3]);
					return storedAccount.getName().equals(account.getName()) ? accountString : line;
				}).collect(Collectors.toList());
				Files.write(Paths.get(ACCOUNTS_FILE_PATH), updatedAccounts);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		LOGGER.info(String.format("Updated account %s", account));
	}
	
	private boolean validateEmail(final String email) {
		
		// check if email address is unique
		for (final Account account : accounts) {
			if (email.equals(account.getEmail())) {
				LOGGER.info("Invalid email! Email address already in use."));
				return false;
			}
		}

		return true;
	}

	private boolean validatePassword(final String password) {

		// check password length
		if (password.length() < 20) {
			LOGGER.info("Invalid password! Minimum password length is 20 characters.");
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
			LOGGER.info("Invalid password! Password must contain a number, uppercase and lowercase letter.");
			return false;
		}
		
		return true;
	}

	public boolean login(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** LOGIN ***");

		System.out.print("Enter Name: ");
		String name = scanner.next();
		while (getAccount(name) == null) {
			System.out.println("Invalid name! No account exists!");
			System.out.print("Enter Name:");
			name = scanner.next();
		}
		final Account account = getAccount(name);

		System.out.print("Enter Password: ");
		String password = scanner.next();
		while (!password.equals(account.getPassword())) {
			System.out.println("Invalid password! try again...");
			System.out.print("Enter Password:");
			password = scanner.next();
		}

		System.out.println("Login successful.");

		return true;
	}

	public void resetPassword(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** RESET PASSWORD ***");

		System.out.print("Enter Name: ");
		String name = scanner.next();
		while (getAccount(name) == null) {
			System.out.println("Invalid name! No account exists!");
			System.out.print("Enter Name:");
			name = scanner.next();
		}
		final Account account = getAccount(name);

		System.out.print("Enter New Password: ");
		String password = scanner.next();
		while (!validatePassword(password)) {
			System.out.print("Enter Password:");
			password = scanner.next();
		}
		account.setPassword(password);

		updateAccount(account);

		System.out.println("Password reset.");
	}

	public void createAccount(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** CREATE ACCOUNT ***");

		System.out.print("Enter Name: ");
		final String name = scanner.next();

		System.out.print("Enter Email: ");
		final String email = scanner.next();
		while (validateEmail(email) == false) {
			System.out.print("Enter Email:");
			email = scanner.next();
		}

		System.out.print("Enter Phone: ");
		final String phone = scanner.next();

		System.out.print("Enter Password: ");
		String password = scanner.next();
		while (validatePassword(password) == false) {
			System.out.print("Enter Password:");
			password = scanner.next();
		}

		addAccount(new Account(name, email, phone, password, AccountType.STAFF));

		System.out.println("Account created.");
	}
}
