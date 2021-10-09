package cinco.ticket;

import cinco.ticket.ConsoleManager.ConsoleException;
import cinco.ticket.ConsoleManager.TextDevice;

public class TicketMenu {

	private final AccountManager accountManager;

	public TicketMenu() {
		accountManager = AccountManager.getAccountManager();
	}

	public MenuOption displayMenu() {

		final TextDevice io = ConsoleManager.defaultTextDevice();
		final String username = accountManager.getActiveAccount() == null ? "Nobody"
				: accountManager.getActiveAccount().getName();

		try {
			io.printf("*** TICKET MENU ***%n");
			io.printf("Welcome %s, Select from the following options:%n", username);
			io.printf("1. Example%n");
			io.printf("2. Logout%n");
			io.printf("3. Exit%n");
			io.printf("%n");

			System.out.print("Enter Choice: ");
			final String choice = io.readLine();

			switch (choice) {
				case "1":
					io.printf("Example choice...%n%n");
					break;
				case "2":
					io.printf("Logging out...%n%n");
					return MenuOption.LOGIN;
				case "3":
					io.printf("Exiting program...%n%n");
					System.exit(0);
				default:
					io.printf("Invalid choice. Please enter a number from 1-2.%n");
				}

		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return MenuOption.TICKET;
	}
}
