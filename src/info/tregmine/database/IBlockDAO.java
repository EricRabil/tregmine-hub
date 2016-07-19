package info.tregmine.database;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IBlockDAO {
	int blockValue(Block a) throws DAOException;

	public boolean isPlaced(Block a) throws DAOException;

	public Map<Material, Integer> loadBlockMinePrices() throws DAOException;
}
