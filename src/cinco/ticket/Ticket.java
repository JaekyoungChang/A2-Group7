package cinco.ticket;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Ticket {

	private UUID id;
	private String description;
	private TicketSeverity severity;
	private TicketStatus status;
	private UUID submittedBy;
	private UUID assignedTo;
	private ZonedDateTime created;
	private ZonedDateTime updated;

	public Ticket(final UUID id, final String description, final TicketStatus status, final TicketSeverity severity,
			final UUID submittedBy, final UUID assignedTo, final ZonedDateTime created, final ZonedDateTime updated) {
		this.id = id;
		this.description = description;
		this.status = status;
		this.severity = severity;
		this.submittedBy = submittedBy;
		this.assignedTo = assignedTo;
		this.created = created;
		this.updated = updated;
	}

	public Ticket(final UUID id, final String description, final TicketStatus status, final TicketSeverity severity,
			final UUID submittedBy, final UUID assignedTo) {
		this(id, description, status, severity, submittedBy, assignedTo, ZonedDateTime.now(ZoneId.systemDefault()),
				null);
	}

	public Ticket(final String description, final TicketSeverity severity, final UUID submittedBy) {
		this(UUID.randomUUID(), description, TicketStatus.OPEN, severity, submittedBy, null);
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

	public ZonedDateTime getCreated() {
		return created;
	}

	public ZonedDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(final ZonedDateTime updated) {
		this.updated = updated;
	}

	public String toString() {
		return String.format("[%s, %s, %s, %s, %s, %s, %s, %s]", id, description, status, severity, submittedBy,
				assignedTo, created != null ? created.format(DateTimeFormatter.ISO_DATE_TIME) : "NULL",
				updated != null ? updated.format(DateTimeFormatter.ISO_DATE_TIME) : "NULL");
	}
}
