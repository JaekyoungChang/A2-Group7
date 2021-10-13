package cinco.ticket;

public enum AccountLevel {
	ZERO(0), ONE(1), TWO(2);

	private int level;

	private AccountLevel(final int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public static AccountLevel valueOf(final int level) {
		return AccountLevel.values()[level];
	}
}
