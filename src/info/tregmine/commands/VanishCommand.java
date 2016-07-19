package info.tregmine.commands;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.YELLOW;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

public class VanishCommand extends AbstractCommand {
	public VanishCommand(Tregmine tregmine) {
		super(tregmine, "vanish");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canVanish()) {
			return true;
		}

		if (args.length == 0) {
			if (player.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
				player.sendStringMessage(DARK_AQUA + "You are currently invisible.");
			} else {
				player.sendStringMessage(DARK_AQUA + "You are currently visible.");
			}
			player.sendStringMessage(DARK_AQUA + "If you wanted to switch your visibility status, do /vanish on|off");
			return true;
		}

		String state = args[0];
		boolean vanish = false;
		if ("on".equalsIgnoreCase(state)) {
			player.setFlag(TregminePlayer.Flags.INVISIBLE);
			vanish = true;
		} else if ("off".equalsIgnoreCase(state)) {
			player.removeFlag(TregminePlayer.Flags.INVISIBLE);
			vanish = false;
		} else {
			return false;
		}

		List<TregminePlayer> players = tregmine.getOnlinePlayers();
		for (TregminePlayer current : players) {
			if (vanish && !current.getRank().canVanish()) {
				current.hidePlayer(player);
			} else {
				current.showPlayer(player);
			}
		}

		try (IContext ctx = tregmine.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			playerDAO.updatePlayer(player);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (vanish) {
			player.sendStringMessage(YELLOW + "You are now invisible!");
		} else {
			player.sendStringMessage(YELLOW + "You are no longer hidden!");
		}

		return true;
	}
}
