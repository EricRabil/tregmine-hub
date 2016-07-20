package info.tregminehub.api;

import java.util.Date;

public class PlayerReport {
	public enum Action {
		KICK, SOFTWARN, HARDWARN, BAN, COMMENT;

		public static Action fromString(String str) {
			for (Action action : Action.values()) {
				if (str.equalsIgnoreCase(action.toString())) {
					return action;
				}
			}

			return null;
		}
	};

	private int id = 0;
	private int subjectId = 0;
	private int issuerId = 0;
	private Action action = null;
	private String message = "";
	private Date timestamp = new Date();
	private Date validUntil = null;

	public PlayerReport() {
	}

	public Action getAction() {
		return action;
	}

	public int getId() {
		return id;
	}

	public int getIssuerId() {
		return issuerId;
	}

	public String getMessage() {
		return message;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setAction(Action v) {
		this.action = v;
	}

	public void setId(int v) {
		this.id = v;
	}

	public void setIssuerId(int v) {
		this.issuerId = v;
	}

	public void setMessage(String v) {
		this.message = v;
	}

	public void setSubjectId(int v) {
		this.subjectId = v;
	}

	public void setTimestamp(Date v) {
		this.timestamp = v;
	}

	public void setValidUntil(Date v) {
		this.validUntil = v;
	}
}
