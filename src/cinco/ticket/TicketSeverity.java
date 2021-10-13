package cinco.ticket;

public enum TicketSeverity {
	LOW(1), MEDIUM(2), HIGH(3);

	private int severity;

	TicketSeverity(int severity) {
		this.severity = severity;
	}

	public int getSeverityInt() {
		return severity;
	}
}
