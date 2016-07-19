package info.tregmine.database;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;

import info.tregmine.api.FishyBlock;
import info.tregmine.api.TregminePlayer;

public interface IFishyBlockDAO {
	public enum TransactionType {
		DEPOSIT, WITHDRAW, BUY;
	}

	public void delete(FishyBlock fishyBlock) throws DAOException;

	public void insert(FishyBlock fishyBlock) throws DAOException;

	public void insertCostChange(FishyBlock fishyBlock, int oldCost) throws DAOException;

	public void insertTransaction(FishyBlock fishyBlock, TregminePlayer player, TransactionType type, int amount)
			throws DAOException;

	public Map<Location, FishyBlock> loadFishyBlocks(Server server) throws DAOException;

	public void update(FishyBlock fishyBlock) throws DAOException;
}
