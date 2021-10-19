package cinco.ticket;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Ticket {

	private ZonedDateTime timestamp;
	private UUID id;
	private String description;
	private TicketSeverity severity;
	private TicketStatus status;
	private UUID submittedBy;
	private UUID assignedTo;

	public Ticket(final ZonedDateTime timestamp, final UUID id, final String description, final TicketStatus status,
			final TicketSeverity severity, final UUID submittedBy, final UUID assignedTo) {
		this.timestamp = timestamp;
		this.id = id;
		this.description = description;
		this.status = status;
		this.severity = severity;
		this.submittedBy = submittedBy;
		this.assignedTo = assignedTo;
	}

	public Ticket(final UUID id, final String description, final TicketStatus status, final TicketSeverity severity,
			final UUID submittedBy, final UUID assignedTo) {
		this(ZonedDateTime.now(ZoneId.systemDefault()), id, description, status, severity, submittedBy, assignedTo);
	}

	public Ticket(final String description, final TicketSeverity severity, final UUID submittedBy) {
		this(UUID.randomUUID(), description, TicketStatus.OPEN, severity, submittedBy, null);
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(final TicketStatus status) {
		this.status = status;
	}

	public TicketSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(final TicketSeverity severity) {
		this.severity = severity;
	}

	public UUID getSubmittedBy() {
		return submittedBy;
	}

	public UUID getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(final UUID assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String toString() {
		return String.format("[%s, %s, %s, %s, %s, %s, %s]", timestamp.format(DateTimeFormatter.ISO_DATE_TIME), id,
				description, status, severity, submittedBy, assignedTo);
	}
}
