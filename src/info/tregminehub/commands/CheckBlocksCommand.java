package info.tregminehub.commands;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IMiscDAO;

public class CheckBlocksCommand extends AbstractCommand {

	public CheckBlocksCommand(Tregmine plugin) {
		super(plugin, "allclear");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canCheckBlocks()) {
			return true;
		}

		if (args.length != 1) {
			player.sendStringMessage(ChatColor.RED + "Please select a radius");
			return true;
		}

		int radius = 0;
		try {
			radius = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendStringMessage(ChatColor.RED + "Please select a valid radius");
		}

		try (IContext ctx = tregmine.createContext()) {
			IMiscDAO dao = ctx.getMiscDAO();
			boolean bool = dao.blocksWereChanged(player.getLocation(), radius);
			if (bool) {
				player.sendStringMessage(ChatColor.RED + "Blocks have been edited in a radius of " + ChatColor.GOLD
						+ radius + ChatColor.RED + " from your location");
			} else {
				player.sendStringMessage(ChatColor.AQUA + "No blocks have been edited within " + radius + " blocks");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

}
