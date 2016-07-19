package info.tregmine.api;

import java.util.Random;

/**
 * Represents a bank account holding information as to how much a player
 * currently has in a certain bank.
 * 
 * @author Robert Catron
 * @since 12/7/2013
 */
public class Account {

	private Bank bank;
	private int id;
	private int playerId;
	private long balance;
	private String pin;

	private int account_number;

	private boolean verified;

	public Account() {
		Random r = new Random(9);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			sb.append(r.nextInt());
		}

		try {
			account_number = Integer.parseInt(String.valueOf(id) + sb.toString().trim());
		} catch (NumberFormatException e) {
		}
	}

	public int getAccountNumber() {
		return account_number;
	}

	public long getBalance() {
		return balance;
	}

	public Bank getBank() {
		return bank;
	}

	public int getId() {
		return id;
	}

	public String getPin() {
		return pin;
	}

	public int getPlayerId() {
		return playerId;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setAccountNumber(int v) {
		this.account_number = v;
	}

	public void setBalance(long v) {
		this.balance = v;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public void setId(int v) {
		this.id = v;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public void setPlayerId(int v) {
		this.playerId = v;
	}

	public void setVerified(boolean v) {
		this.verified = v;
	}
}
