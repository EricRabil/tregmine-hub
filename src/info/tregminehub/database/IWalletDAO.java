package info.tregminehub.database;

import info.tregminehub.api.TregminePlayer;

public interface IWalletDAO {
	public boolean add(TregminePlayer player, long amount) throws DAOException;

	public long balance(TregminePlayer player) throws DAOException;

	public String formattedBalance(TregminePlayer player) throws DAOException;

	public void insertTransaction(int srcId, int recvId, int amount) throws DAOException;

	public boolean take(TregminePlayer player, long amount) throws DAOException;
}
