package info.tregminehub.database;

import org.bukkit.inventory.ItemStack;

public interface ITradeDAO {
	public int getAmountofTrades(int id) throws DAOException;

	public void insertStacks(int tradeId, ItemStack[] contents) throws DAOException;

	public int insertTrade(int srcId, int recvId, int amount) throws DAOException;
}
