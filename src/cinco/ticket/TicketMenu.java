package cinco.ticket;

import java.util.Scanner;

public class TicketMenu {

	private final AccountManager accountManager;

	public TicketMenu() {
		accountManager = AccountManager.getAccountManager();
	}

	public MenuOption displayMenu(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** TICKET MENU ***");

		final String username = accountManager.getActiveAccount() == null ? "Nobody"
				: accountManager.getActiveAccount().getName();
		System.out.println(String.format("Welcome %s, Select from the following options:", username));
		System.out.println("1. Example");
		System.out.println("2. Logout");
		System.out.println("3. Exit");
		System.out.println();

		System.out.print("Enter Choice: ");
		final String choice = scanner.next();

		switch (choice) {
		case "1":
			System.out.println("Example choice...");
			break;
		case "2":
			System.out.println("Logging out...");
			return MenuOption.LOGIN;
		case "3":
			System.out.println("Exiting program...");
			System.exit(0);
		default:
			System.out.println("Invalid choice. Please enter a number from 1-2.");
		}

		return MenuOption.TICKET;
	}
}
