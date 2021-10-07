package cinco.ticket;

import java.util.Scanner;

public class LoginMenu {

	private final AccountManager accountManager;

	public LoginMenu() {
		accountManager = AccountManager.getAccountManager();
	}

	public MenuOption displayMenu(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** LOGIN MENU ***");

		System.out.println("Select from the following options:");
		System.out.println("1. Login");
		System.out.println("2. Reset Password");
		System.out.println("3. Create Account");
		System.out.println("4. Exit");
		System.out.println();

		System.out.print("Enter Choice: ");
		final String choice = scanner.next();

		switch (choice) {
		case "1":
			if (accountManager.login(scanner)) {
				return MenuOption.TICKET;
			}
			break;
		case "2":
			accountManager.resetPassword(scanner);
			break;
		case "3":
			accountManager.createAccount(scanner);
			break;
		case "4":
			System.out.println("Exiting program...");
			System.exit(0);
		default:
			System.out.println("Invalid choice. Please enter a number from 1-4.");
		}

		return MenuOption.LOGIN;
	}
}
