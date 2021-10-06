package cinco.ticket;

import java.util.Scanner;

import cinco.ticket.Main.MenuOption;

public class TicketMenu {

	public MenuOption displayMenu(final Scanner scanner) {

		scanner.useDelimiter(System.lineSeparator());

		System.out.println("*** TICKET MENU ***");

		System.out.println("Select from the following options:");
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
