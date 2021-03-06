package info.tregminehub.commands;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IPlayerDAO;

public class FlyCommand extends AbstractCommand {
	private Tregmine tregmine;

	public FlyCommand(Tregmine tregmine) {
		super(tregmine, "fly");
		this.tregmine = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		if (player.hasFlag(TregminePlayer.Flags.HARDWARNED) || player.hasFlag(TregminePlayer.Flags.SOFTWARNED)) {
			player.sendStringMessage("You are warned and are not allowed to fly.");
			player.setAllowFlight(false);
		}
		if (!player.getRank().canFly())
			return false;

		if (player.hasFlag(TregminePlayer.Flags.FLY_ENABLED)) {
			player.sendStringMessage(ChatColor.YELLOW + "Flying Disabled!");
			player.removeFlag(TregminePlayer.Flags.FLY_ENABLED);
			player.setAllowFlight(false);
		} else {

			player.sendStringMessage(ChatColor.YELLOW + "Flying Enabled!");
			player.setFlag(TregminePlayer.Flags.FLY_ENABLED);
			player.setAllowFlight(true);
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
