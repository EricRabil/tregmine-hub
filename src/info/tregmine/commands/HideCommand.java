package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

public class HideCommand extends AbstractCommand {
	public HideCommand(Tregmine tregmine) {
		super(tregmine, "hide");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canSeeHiddenInfo())
			return true;

		if (args.length < 1) {
			player.sendStringMessage("Your Announcement prevention is set to "
					+ (player.hasFlag(TregminePlayer.Flags.HIDDEN_ANNOUNCEMENT) ? "on" : "off") + ".");
			return true;
		}

		String state = args[0];

		if ("on".equalsIgnoreCase(state)) {
			player.setFlag(TregminePlayer.Flags.HIDDEN_ANNOUNCEMENT);
			player.sendStringMessage(AQUA + "Announcement prevention is now turned on for you.");
		} else if ("off".equalsIgnoreCase(state)) {
			player.removeFlag(TregminePlayer.Flags.HIDDEN_ANNOUNCEMENT);
			player.sendStringMessage(AQUA + "Announcement prevention is now turned off for you.");
		} else if ("status".equalsIgnoreCase(state)) {
			player.sendStringMessage("Your Announcement prevention display is set to "
					+ (player.hasFlag(TregminePlayer.Flags.HIDDEN_ANNOUNCEMENT) ? "on" : "off") + ".");
		} else {
			player.sendStringMessage(RED + "The commands are /hide on, /hide off and /hide status.");
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
