package cinco.ticket;

public enum TicketStatus {
	OPEN(1), CLOSED_RESOLVED(2), CLOSED_UNRESOLVED(3), ARCHIVED(4);
	
	private int code;

	private TicketStatus(final int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static TicketStatus valueOf(final int code) {
		return TicketStatus.values()[code - 1];
	}
}