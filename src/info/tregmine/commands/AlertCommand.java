package info.tregmine.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

public class AlertCommand extends AbstractCommand {
	public AlertCommand(Tregmine tregmine) {
		super(tregmine, "alert");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		List<String> player_keywords;
		try (IContext ctx = tregmine.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			player_keywords = playerDAO.getKeywords(player);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (args.length == 0) {
			player.sendStringMessage(ChatColor.DARK_RED + "Incorrect Syntax:");
			player.sendStringMessage(ChatColor.AQUA + "/alert <add/remove> word (To add/remove alert words)");
			player.sendStringMessage(ChatColor.AQUA + "/alert list (To view your alert words)");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			if (player_keywords.size() > 0) {
				player.sendStringMessage(ChatColor.AQUA
						+ player_keywords.toString().replace(",", " ").replace("[", "").replace("]", ""));
			} else {
				player.sendStringMessage("Detected no alert words!");
			}
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
			player_keywords.add(args[1]);
			try (IContext ctx = tregmine.createContext()) {
				IPlayerDAO playerDAO = ctx.getPlayerDAO();
				playerDAO.updateKeywords(player, player_keywords);
				player.sendStringMessage(ChatColor.AQUA + "Added the alert word: " + args[1]);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			if (player_keywords.contains(args[1])) {
				player_keywords.remove(args[1]);
			} else {
				player.sendStringMessage(ChatColor.AQUA + "The alert word " + args[1] + " doesn't exist to remove!");
				return true;
			}

			try (IContext ctx = tregmine.createContext()) {
				IPlayerDAO playerDAO = ctx.getPlayerDAO();
				playerDAO.updateKeywords(player, player_keywords);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
			player.sendStringMessage(ChatColor.AQUA + "Removed the alert word: " + args[1]);

			return true;
		}
		return false;
	}

}
