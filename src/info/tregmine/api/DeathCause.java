package info.tregmine.api;

public enum DeathCause {
	ADMIN("An admin"), CONSOLE("God");

	private String cause;

	private DeathCause(String s) {
		this.cause = s;
	}

	public String getName() {
		return this.cause;
	}
}
