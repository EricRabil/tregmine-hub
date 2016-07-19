package info.tregmine.commands;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Server;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.UUIDFetcher;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class SeenCommand extends AbstractCommand {

	public SeenCommand(Tregmine tregmine) {
		super(tregmine, "seen");
	}

	@Override
	public boolean handleOther(Server server, String[] args) {
		if (args.length != 1) {
			return false;
		}

		TregminePlayer target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(args[0]));
		if (target == null) {
			server.getConsoleSender().sendMessage("Could not find player: " + args[0]);
			return true;
		}

		try (IContext ctx = tregmine.createContext()) {
			ILogDAO logDAO = ctx.getLogDAO();
			Date seen = logDAO.getLastSeen(target);

			server.getConsoleSender().sendMessage(args[0] + " was last seen on: " + seen);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 1) {
			return false;
		}

		TregminePlayer target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(args[0]));
		if (target == null) {
			player.sendStringMessage(ChatColor.RED + "Could not find player: " + ChatColor.YELLOW + args[0]);
			return true;
		}

		try (IContext ctx = tregmine.createContext()) {
			ILogDAO logDAO = ctx.getLogDAO();
			Date seen = logDAO.getLastSeen(target);

			if (seen != null) {
				player.sendSpigotMessage(new TextComponent(ChatColor.GREEN + ""), target.getChatName(),
						new TextComponent(ChatColor.YELLOW + " was last seen on: " + ChatColor.AQUA + seen));
			} else {
				player.sendSpigotMessage(new TextComponent(ChatColor.GREEN + ""), target.getChatName(),
						new TextComponent(ChatColor.YELLOW + " hasn't been seen for a while."));
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

}
