package info.tregminehub.commands;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IMotdDAO;

public class UpdateCommand extends AbstractCommand {
	public UpdateCommand(Tregmine tregmine) {
		super(tregmine, "update");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		String version;
		if (args.length == 1) {
			version = args[0];
			player.sendStringMessage(ChatColor.AQUA + "Searching for Version " + version);
		} else {
			version = tregmine.getDescription().getVersion();
			player.sendStringMessage(ChatColor.AQUA + "No Arguments detected, Searching for latest: " + version);
		}

		try (IContext ctx = tregmine.createContext()) {
			IMotdDAO motdDAO = ctx.getMotdDAO();
			String message = motdDAO.getUpdates(version);
			if (message == null) {
				player.sendStringMessage(ChatColor.AQUA + "Version not found!");
				return true;
			}
			String[] lines = message.split("::");
			for (String line : lines) {
				player.sendStringMessage(ChatColor.GOLD + line);
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}
}
