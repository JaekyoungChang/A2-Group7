package cinco.ticket;

import cinco.ticket.ConsoleManager.ConsoleException;
import cinco.ticket.ConsoleManager.TextDevice;

public class TicketMenu {

	private final AccountManager accountManager;
	private final TicketManager ticketManager;

	public TicketMenu() {
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
			io.printf("*** TICKET MENU ***%n");
			io.printf("Welcome %s, Select from the following options:%n", username);
			io.printf("%n");

			switch (accountType) {
				case STAFF:
					io.printf("1. List Tickets%n");
					io.printf("2. Create Ticket%n");
					io.printf("3. Logout%n");
					io.printf("4. Exit%n");
					io.printf("%n");
	
					System.out.print("Enter Choice: ");
					final String staffChoice = io.readLine();
	
					switch (staffChoice) {
						case "1":
							ticketManager.listTickets();
							break;
						case "2":
							ticketManager.submitTicket();
							break;
						case "3":
							io.printf("Logging out...%n%n");
							return MenuOption.LOGIN;
						case "4":
							io.printf("Exiting program...%n%n");
							System.exit(0);
						default:
							io.printf("Invalid choice. Please enter a number from 1-4.%n");
						}
						break;
				case TECHNICIAN:
					io.printf("1. List Tickets%n");
					io.printf("2. Update Ticket Status%n");
					io.printf("3. Update Ticket Severity%n");
					io.printf("4. Generate Report%n");
					io.printf("5. Logout%n");
					io.printf("6. Exit%n");
					io.printf("%n");
	
					System.out.print("Enter Choice: ");
					final String technicianChoice = io.readLine();
	
					switch (technicianChoice) {
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
							ticketManager.generateReport();
							break;
						case "5":
							io.printf("Logging out...%n%n");
							return MenuOption.LOGIN;
						case "6":
							io.printf("Exiting program...%n%n");
							System.exit(0);
						default:
							io.printf("Invalid choice. Please enter a number from 1-6.%n");
						}
						break;
				default:
					io.printf("Unknown account type %s, returning to login menu.%n", accountType);
					return MenuOption.LOGIN;
			}
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return MenuOption.TICKET;
	}
}
