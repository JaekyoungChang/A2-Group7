package cinco.ticket;

import cinco.ticket.ConsoleManager.ConsoleException;
import cinco.ticket.ConsoleManager.TextDevice;

public class TechnicianMenu {

	private final AccountManager accountManager;
	private final TicketManager ticketManager;

	public TechnicianMenu() {
		this.accountManager = AccountManager.defaultAccountManager();
		this.ticketManager = TicketManager.defaultTicketManager();
	}

	public MenuOption displayMenu() {
		final AccountType accountType = accountManager.getActiveAccount() == null ? AccountType.STAFF
				: accountManager.getActiveAccount().getType();
		final String username = accountManager.getActiveAccount() == null ? "Nobody"
				: accountManager.getActiveAccount().getName();

		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			// TODO: add more options if account type is technician
			io.printf("*** TECHNICIAN MENU ***%n");
			io.printf("Welcome %s, Select from the following options:%n", username);
			io.printf("%n");

			io.printf("1. List Tickets%n");
			io.printf("2. Change Ticket Status%n");
			io.printf("3. Change Ticket Severity%n");
			io.printf("4. Logout%n");
			io.printf("5. Exit%n");
			io.printf("%n");

			System.out.print("Enter Choice: ");
			final String choice = io.readLine();

			switch (choice) {
			case "1":
				ticketManager.listTickets();
				break;
			case "2":
				ticketManager.changeTicketStatus();
				break;
			case "3":
				ticketManager.changeTicketSeverity();
				break;
			case "4":
				io.printf("Logging out...%n%n");
				return MenuOption.LOGIN;
			case "5":
				io.printf("Exiting program...%n%n");
				System.exit(0);
			default:
				io.printf("Invalid choice. Please enter a number from 1-4.%n");
			}

		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return MenuOption.TECHNICIAN;
	}
}
