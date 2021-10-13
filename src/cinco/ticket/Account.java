package cinco.ticket;

import java.util.UUID;

public class Account {

	private UUID id;
	private AccountType type;
	private AccountLevel level;
	private final String name;
	private final String email;
	private final String phone;
	private String password;

	public Account(final UUID id, final AccountType type, final AccountLevel level, final String name,
			final String email, final String phone, final String password) {
		this.id = id;
		this.type = type;
		this.level = level;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.password = password;
	}

	public Account(final String name, final String email, final String phone, final String password) {
		this(UUID.randomUUID(), AccountType.STAFF, AccountLevel.ZERO, name, email, phone, password);
	}

	public UUID getId() {
		return id;
	}

	public AccountType getType() {
		return type;
	}

	public AccountLevel getLevel() {
		return level;
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
		return String.format("[%s, %s, %s, %s, %s, %s]", id, type, level, name, email, phone);
	}
}
