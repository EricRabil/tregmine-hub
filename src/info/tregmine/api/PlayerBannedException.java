package info.tregmine.api;

public class PlayerBannedException extends Exception {
	private static final long serialVersionUID = -8264297613288004596L;

	public PlayerBannedException(String message) {
		super(message);
	}
}
