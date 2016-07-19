package info.tregmine.database;

import org.bukkit.Location;
import org.bukkit.Server;

import info.tregmine.api.Warp;

public interface IWarpDAO {
	public Warp getWarp(String name, Server server) throws DAOException;

	public void insertWarp(String name, Location loc) throws DAOException;
}
