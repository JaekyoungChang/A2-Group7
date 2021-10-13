package cinco.ticket;

import java.util.ArrayList;

public class Technician extends Account {

	private int level;
	private ArrayList<Ticket> tickets;
	
	public Technician(AccountType type, String email, String name, String phone, String password, int level) {
		super(type, email, name, phone, password);
		this.level = level;
		tickets = new ArrayList<Ticket>();
	}
	
	public int getLevel() {
		return level;
	}
	
	public int numAssignedTickets() {
		return tickets.size();
	}
	
	public void assignTicket(Ticket ticket) {
		tickets.add(ticket);
	}
	
	public void unassignTicket(Ticket ticket) {
		tickets.remove(ticket);
	}
	
	public ArrayList<Ticket> getAssignedTickets() {
		return tickets;
	}

}