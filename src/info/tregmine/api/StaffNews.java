package info.tregmine.api;

import java.util.Date;

public class StaffNews {

	private String username = null;
	private String text = null;
	private long timestamp = new Date().getTime();
	private int id = 0;

	public StaffNews() {
	}

	public long getDate() {
		return timestamp;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getUsername() {
		return username;
	}

	public void setId(int v) {
		this.id = v;
	}

	public void setText(String v) {
		this.text = v;
	}

	public void setTimestamp(long date) {
		this.timestamp = date;
	}

	public void setUsername(String v) {
		this.username = v;
	}
}
