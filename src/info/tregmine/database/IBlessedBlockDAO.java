package info.tregmine.database;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;

import info.tregmine.api.TregminePlayer;

public interface IBlessedBlockDAO {
	void delete(Location loc) throws DAOException;

	public void insert(TregminePlayer player, Location loc) throws DAOException;

	public Map<Location, Integer> load(Server server) throws DAOException;

	int owner(Location loc) throws DAOException;
}
