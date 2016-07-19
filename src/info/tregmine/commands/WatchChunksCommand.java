package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

public class WatchChunksCommand extends AbstractCommand {
	public WatchChunksCommand(Tregmine tregmine) {
		super(tregmine, "watchchunks");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length < 1) {
			player.sendStringMessage("Your WatchChunks is set to "
					+ (player.hasFlag(TregminePlayer.Flags.WATCHING_CHUNKS) ? "on" : "off") + ".");
			return true;
		}

		String state = args[0];

		if ("on".equalsIgnoreCase(state)) {
			player.setFlag(TregminePlayer.Flags.WATCHING_CHUNKS);
			player.sendStringMessage(AQUA + "Watching Chunks is now turned on for you.");
		} else if ("off".equalsIgnoreCase(state)) {
			player.removeFlag(TregminePlayer.Flags.WATCHING_CHUNKS);
			player.sendStringMessage(AQUA + "Watching Chunks is now turned off for you.");
		} else if ("status".equalsIgnoreCase(state)) {
			player.sendStringMessage("Your Watching Chunks is set to "
					+ (player.hasFlag(TregminePlayer.Flags.WATCHING_CHUNKS) ? "on" : "off") + ".");
		} else {
			player.sendStringMessage(
					RED + "The commands are /watchchunks on, /watchchunks off and /watchchunks status.");
		}

		try (IContext ctx = tregmine.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			playerDAO.updatePlayer(player);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

}
