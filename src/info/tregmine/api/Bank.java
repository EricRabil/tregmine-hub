package info.tregmine.api;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Robert Catron
 * @since 12/7/2013
 * @see Account
 */
public class Bank {
	private int id;
	private int lotId;

	private List<Account> accounts;

	public Bank() {
	}

	public Bank(int lotId) {
		this.lotId = lotId;

		accounts = Lists.newArrayList();
	}

	public Bank(int id, int lotId) {
		this(lotId);
		this.id = id;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public int getId() {
		return this.id;
	}

	public int getLotId() {
		return lotId;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLotId(int id) {
		this.lotId = id;
	}
}
