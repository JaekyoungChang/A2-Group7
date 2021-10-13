package cinco.ticket;

public enum TicketSeverity {
	LOW(1), MEDIUM(2), HIGH(3);

	private int level;

	private TicketSeverity(final int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public static TicketSeverity valueOf(final int level) {
		return TicketSeverity.values()[level - 1];
	}
}
