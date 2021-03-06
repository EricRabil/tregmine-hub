package info.tregminehub.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IPlayerDAO;

public class InventoryLogCommand extends AbstractCommand {
	public InventoryLogCommand(Tregmine tregmine) {
		super(tregmine, "invlog");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length < 1) {
			player.sendStringMessage("Your InventoryLog is set to "
					+ (player.hasFlag(TregminePlayer.Flags.CHEST_LOG) ? "on" : "off") + ".");
			return true;
		}

		String state = args[0];

		if ("on".equalsIgnoreCase(state)) {
			player.setFlag(TregminePlayer.Flags.CHEST_LOG);
			player.sendStringMessage(AQUA + "Inventory Log display is now turned on for you.");
		} else if ("off".equalsIgnoreCase(state)) {
			player.removeFlag(TregminePlayer.Flags.CHEST_LOG);
			player.sendStringMessage(AQUA + "Inventory Log display is now turned off for you.");
		} else if ("status".equalsIgnoreCase(state)) {
			player.sendStringMessage("Your Inventory Log display is set to "
					+ (player.hasFlag(TregminePlayer.Flags.CHEST_LOG) ? "on" : "off") + ".");
		} else {
			player.sendStringMessage(RED + "The commands are /invlog on, /invlog off and /invlog status.");
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
