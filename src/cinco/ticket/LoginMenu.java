package cinco.ticket;

import cinco.ticket.ConsoleManager.ConsoleException;
import cinco.ticket.ConsoleManager.TextDevice;

public class LoginMenu {

	private final AccountManager accountManager;

	public LoginMenu() {
		accountManager = AccountManager.getAccountManager();
	}

	public MenuOption displayMenu() {

		final TextDevice io = ConsoleManager.defaultTextDevice();

		try {
			io.printf("*** LOGIN MENU ***%n");
			io.printf("Select from the following options:%n");
			io.printf("1. Login%n");
			io.printf("2. Reset Password%n");
			io.printf("3. Create Account%n");
			io.printf("4. Exit%n");
			io.printf("%n");
	
			System.out.print("Enter Choice: ");
			final String choice = io.readLine();
			switch (choice) {
				case "1":
					if (accountManager.login()) {
						return MenuOption.TICKET;
					}
					break;
				case "2":
					accountManager.resetPassword();
					break;
				case "3":
					accountManager.createAccount();
					break;
				case "4":
					io.printf("Exiting program...%n%n");
					System.exit(0);
				default:
					io.printf("Invalid choice. Please enter a number from 1-4.%n");
				}
		} catch (final ConsoleException e) {
			e.printStackTrace();
		}

		return MenuOption.LOGIN;
	}
}
