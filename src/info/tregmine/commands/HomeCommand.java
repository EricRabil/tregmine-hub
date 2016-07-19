package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.UUIDFetcher;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IHomeDAO;

public class HomeCommand extends AbstractCommand {
	private Tregmine tregmine;

	public HomeCommand(Tregmine tregmine) {
		super(tregmine, "home");
		this.tregmine = tregmine;
	}

	private boolean deleteHome(TregminePlayer player, String name) {
		if (!player.getRank().canSaveHome()) {
			return true;
		}

		Location playerLoc = player.getLocation();
		World playerWorld = playerLoc.getWorld();
		if ("world_the_end".equalsIgnoreCase(playerWorld.getName())) {
			player.sendStringMessage(RED + "You can't set your home in The End");
			return true;
		}

		try (IContext ctx = tregmine.createContext()) {
			IHomeDAO homeDAO = ctx.getHomeDAO();
			homeDAO.deleteHome(player.getId(), name);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		player.sendStringMessage(AQUA + "Home " + name + " deleted!");

		return true;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length == 0) {
			return teleport(player, "default");
		} else if ("save".equalsIgnoreCase(args[0])) {
			if (args.length == 2) {
				return save(player, args[1]);
			} else {
				return save(player, "default");
			}
		} else if ("delete".equalsIgnoreCase(args[0]) && args.length == 2) {
			return deleteHome(player, args[1]);
		} else if ("go".equalsIgnoreCase(args[0])) {
			if (args.length < 2) {
				player.sendStringMessage(RED + "Usage: /home go <name>.");
				return true;
			}

			return teleport(player, args[1]);
		} else if ("list".equalsIgnoreCase(args[0])) {
			if (args.length == 2) {
				return list(player, args[1]);
			} else {
				return list(player, null);
			}
		} else if ("to".equalsIgnoreCase(args[0])) {
			if (args.length == 3) {
				return teleportTo(player, args[1], args[2]);
			} else if (args.length == 2) {
				return teleportTo(player, args[1], "default");
			} else if (args.length < 2) {
				player.sendStringMessage(RED + "Usage: /home to <player> <name>");
				return true;
			}
		} else {
			player.sendStringMessage(RED + "Incorrect Usage:");
			player.sendStringMessage(RED + "/home go <home name> - To go to a home");
			player.sendStringMessage(RED + "/home save <home name> - To save a home");
			player.sendStringMessage(RED + "/home delete <home name> - To delete a home");
			player.sendStringMessage(RED + "/home list - To list your homes");
			if (player.getRank().canVisitHomes()) {
				player.sendStringMessage(RED + "/home to <player> <name>");
			}
		}

		return true;
	}

	private boolean list(TregminePlayer player, String playerName) {
		TregminePlayer target = player;
		if (playerName != null) {
			if (!player.getRank().canVisitHomes()) {
				return true;
			}

			target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(playerName));
			if (target == null) {
				player.sendStringMessage(RED + playerName + " was not found in database.");
				return true;
			}
		}

		List<String> names = null;
		try (IContext ctx = tregmine.createContext()) {
			IHomeDAO homeDAO = ctx.getHomeDAO();
			names = homeDAO.getHomeNames(target.getId());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (names.size() > 0) {
			StringBuilder buffer = new StringBuilder();
			String delim = "";
			for (String name : names) {
				buffer.append(delim);
				buffer.append(name);
				delim = ", ";
			}

			player.sendStringMessage(YELLOW + "List of homes:");
			player.sendStringMessage(YELLOW + buffer.toString());
		} else {
			player.sendStringMessage(YELLOW + "No homes found!");
		}

		return true;
	}

	private boolean save(TregminePlayer player, String name) {
		if (!player.getRank().canSaveHome()) {
			return true;
		}

		Location playerLoc = player.getLocation();
		World playerWorld = playerLoc.getWorld();
		if ("world_the_end".equalsIgnoreCase(playerWorld.getName())) {
			player.sendStringMessage(RED + "You can't set your home in The End");
			return true;
		}

		try (IContext ctx = tregmine.createContext()) {
			IHomeDAO homeDAO = ctx.getHomeDAO();
			List<String> homes = homeDAO.getHomeNames(player.getId());
			int limit = player.getRank().getHomeLimit();
			if (homes.size() > limit) {
				player.sendStringMessage(RED + "You can't have more than " + limit + " homes.");
				return true;
			}

			homeDAO.insertHome(player, name, playerLoc);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		player.sendStringMessage(AQUA + "Home saved!");

		return true;
	}

	private boolean teleport(TregminePlayer player, String name) {
		Location loc = null;
		try (IContext ctx = tregmine.createContext()) {
			IHomeDAO homeDAO = ctx.getHomeDAO();
			loc = homeDAO.getHome(player, name);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (loc == null) {
			player.sendStringMessage(RED + "Telogric lift malfunctioned. " + "Teleportation failed.");
			return true;
		}

		World world = loc.getWorld();
		Chunk chunk = world.getChunkAt(loc);
		world.loadChunk(chunk);

		if (world.isChunkLoaded(chunk)) {
			if (!world.getName().equalsIgnoreCase(player.getWorld().getName())) {
				player.sendStringMessage(RED + "You can't use a home thats in another world!");
				return true;
			}

			player.teleportWithHorse(loc);

			player.sendStringMessage(AQUA + "Hoci poci, little gnome. Magic worked, " + "you're in your home!");
		} else {
			player.sendStringMessage(RED + "Loading your home chunk failed, try /home again.");
		}

		return true;
	}

	private boolean teleportTo(TregminePlayer player, String playerName, String name) {
		if (!player.getRank().canVisitHomes()) {
			player.sendStringMessage(RED + "You can't teleport to other player's homes");
		}

		TregminePlayer target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(playerName));
		if (target == null) {
			player.sendStringMessage(RED + playerName + " was not found in database.");
			return true;
		}

		Location loc = null;
		try (IContext ctx = tregmine.createContext()) {
			IHomeDAO homeDAO = ctx.getHomeDAO();
			loc = homeDAO.getHome(target.getId(), name, tregmine.getServer());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (loc == null) {
			player.sendStringMessage(RED + "Telogric lift malfunctioned. Teleportation failed.");
			return true;
		}

		World world = loc.getWorld();
		Chunk chunk = world.getChunkAt(loc);
		world.loadChunk(chunk);

		if (world.isChunkLoaded(chunk)) {
			player.teleportWithHorse(loc);

			player.sendStringMessage(AQUA + "Like a drunken gnome, you fly across the world to " + playerName
					+ "'s home. Try not to hit any birds.");
		} else {
			player.sendStringMessage(RED + "Loading of home chunk failed, try /home again");
		}

		return true;
	}
}
