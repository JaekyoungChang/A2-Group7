package cinco.ticket;

import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

	private static final Level LOG_LEVEL = Level.WARNING;
	static {
		final LogManager logManager = LogManager.getLogManager();
		synchronized (logManager) {
			final Enumeration<String> loggerNames = logManager.getLoggerNames();
			while (loggerNames.hasMoreElements()) {
				final Logger logger = logManager.getLogger(loggerNames.nextElement());
				if (logger != null) {
					logger.setLevel(LOG_LEVEL);
					for (final Handler handler : logger.getHandlers()) {
						handler.setLevel(LOG_LEVEL);
					}
				}
			}
		}
	}

	private static final LoginMenu LOGIN_MENU = new LoginMenu();
	private static final TicketMenu TICKET_MENU = new TicketMenu();

	public static MenuOption switchMenu(final MenuOption menu) {
		switch (menu) {
		case LOGIN:
			return LOGIN_MENU.displayMenu();
		case TICKET:
			return TICKET_MENU.displayMenu();
		default:
			System.out.println(String.format("Unknown menu \"%s\", returning to login menu...", menu));
			return MenuOption.LOGIN;
		}
	}

	public static void main(final String[] args) {
		MenuOption menu = MenuOption.LOGIN;
		while (true) {
			menu = switchMenu(menu);
		}
	}
}
