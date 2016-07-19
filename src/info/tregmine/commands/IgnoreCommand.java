package info.tregmine.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.UUIDFetcher;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

public class IgnoreCommand extends AbstractCommand {
	public IgnoreCommand(Tregmine tregmine) {
		super(tregmine, "ignore");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		List<String> ignoredPeople;
		try (IContext ctx = tregmine.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			ignoredPeople = playerDAO.getIgnored(player);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (args.length == 0 || args.length > 2) {

			player.sendStringMessage(ChatColor.DARK_RED + "Incorrect Syntax:");
			player.sendStringMessage(
					ChatColor.AQUA + "/ignore <add/remove> player_name (To add/remove someone to ignore)");
			player.sendStringMessage(ChatColor.AQUA + "/ignore list (To view everyone your ignoring)");

			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

			if (ignoredPeople.size() > 0) {
				player.sendStringMessage(
						ChatColor.AQUA + ignoredPeople.toString().replace(",", " ").replace("[", "").replace("]", ""));
			} else {
				player.sendStringMessage("You are currently not ignoring anyone!");
			}

			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("list")) {

			if (!player.getRank().canViewIgnored())
				return false;

			TregminePlayer target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(args[1]));

			if (target == null) {
				player.sendStringMessage(ChatColor.RED + "Could not find player: " + ChatColor.YELLOW + args[1]);
				return true;
			}

			List<String> ignoredPeopleAdmin;
			try (IContext ctx = tregmine.createContext()) {
				IPlayerDAO playerDAO = ctx.getPlayerDAO();
				ignoredPeopleAdmin = playerDAO.getIgnored(target);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}

			if (ignoredPeopleAdmin.size() > 0) {
				player.sendStringMessage(ChatColor.AQUA
						+ ignoredPeopleAdmin.toString().replace(",", " ").replace("[", "").replace("]", ""));
			} else {
				player.sendStringMessage(target.getDisplayName() + " is not ignoring anyone!");
			}

			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {

			TregminePlayer target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(args[1]));

			if (target == null) {
				player.sendStringMessage(ChatColor.RED + "Could not find player: " + ChatColor.YELLOW + args[1]);
				return true;
			}

			if (target.getRank().canNotBeIgnored()) {
				player.sendStringMessage(ChatColor.RED + "Can not ignore this player!");
				return true;
			}
			if (target.getDelegate() == null) {
				ignoredPeople.add(args[1]);
			} else {
				ignoredPeople.add(target.getDisplayName());
			}

			try (IContext ctx = tregmine.createContext()) {
				IPlayerDAO playerDAO = ctx.getPlayerDAO();
				playerDAO.updateIgnore(player, ignoredPeople);
				player.sendStringMessage(ChatColor.AQUA + "Now ignoring the player: " + target.getDisplayName());
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}

			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {

			if (ignoredPeople.contains(args[1])) {
				ignoredPeople.remove(args[1]);
			} else {
				player.sendStringMessage(ChatColor.DARK_RED + "You are not ignoring: " + args[1] + "!");
				return true;
			}

			try (IContext ctx = tregmine.createContext()) {
				IPlayerDAO playerDAO = ctx.getPlayerDAO();
				playerDAO.updateIgnore(player, ignoredPeople);
				player.sendStringMessage(ChatColor.AQUA + "No longer ignoring the player: " + args[1]);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}

			return true;
		}

		return false;
	}

}
