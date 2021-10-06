package cinco.ticket;

public class Account {

	private final String name;
	private final String email;
	private final String phone;
	private String password;

	public Account(final String name, final String email, final String phone, final String password) {
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
		public String getPhone() {
		return phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}
	
	public String toString() {
		return String.format("[%s, %s, %s]", name, email, phone);
	}
}
