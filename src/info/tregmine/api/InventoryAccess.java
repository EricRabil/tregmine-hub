package info.tregmine.api;

import java.util.Date;

public class InventoryAccess {
	private int inventoryId;
	private int playerId;
	private Date timestamp;

	public InventoryAccess() {
	}

	public int getInventoryId() {
		return inventoryId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setInventoryId(int v) {
		this.inventoryId = v;
	}

	public void setPlayerId(int v) {
		this.playerId = v;
	}

	public void setTimestamp(Date v) {
		this.timestamp = v;
	}
}
