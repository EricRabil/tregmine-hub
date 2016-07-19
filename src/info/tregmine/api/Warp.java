package info.tregmine.api;

import org.bukkit.Location;

public class Warp {
	private int id;
	private String name;
	private Location location;

	public Warp() {
	}

	public int getId() {
		return id;
	}

	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public void setId(int v) {
		this.id = v;
	}

	public void setLocation(Location v) {
		this.location = v;
	}

	public void setName(String v) {
		this.name = v;
	}
}
