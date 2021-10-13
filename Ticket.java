package cinco.ticket;

public class Ticket {
	private String description;
	private TicketSeverity severity;
	private TicketStatus status;
	private Account submitedBy;

	public Ticket(String description, TicketSeverity severity, Account submitedBy) {
		this.description = description;
		this.severity = severity;
		this.submitedBy = submitedBy;
		this.status = TicketStatus.OPEN;
	}
	
	public void printDetails(int index) {
		System.out.format("%-5d%-10s%-10s%-10s%n", index, description, severity.toString(), status.toString());
	}
	
	public TicketStatus getStatus() {
		return status;
	}
	
	public void setStatus(TicketStatus status) {
		this.status = status;
	}
	
	public TicketSeverity getSeverity() {
		return severity;
	}
	
	public void setSeverity(TicketSeverity severity) {
		this.severity = severity;
	}
	
	public Account getSubmitedBy() {
		return submitedBy;
	}
}
