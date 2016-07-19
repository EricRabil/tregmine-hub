package info.tregmine.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import info.tregmine.Tregmine;
import info.tregmine.api.PlayerReport;
import info.tregmine.api.PlayerReport.Action;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IPlayerReportDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerLookupListener implements Listener {
	private Tregmine plugin;

	public PlayerLookupListener(Tregmine instance) {
		plugin = instance;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (player == null) {
			event.getPlayer().kickPlayer(ChatColor.RED + "Something went wrong");
			Tregmine.LOGGER.info(event.getPlayer().getName() + " was not found " + "in players map.");
			return;
		}

		try (IContext ctx = plugin.createContext()) {
			IPlayerReportDAO report = ctx.getPlayerReportDAO();
			List<PlayerReport> list = report.getReportsBySubject(player);
			for (PlayerReport i : list) {
				if (i.getAction() != Action.HARDWARN && i.getAction() != Action.SOFTWARN) {
					continue;
				}
				Date validUntil = i.getValidUntil();
				if (validUntil == null) {
					continue;
				}
				if (validUntil.getTime() < System.currentTimeMillis()) {
					continue;
				}

				SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
				player.sendStringMessage(ChatColor.RED + "[" + i.getAction() + "]" + i.getMessage() + " - Valid until: "
						+ dfm.format(i.getTimestamp()));
				break;
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (!player.hasFlag(TregminePlayer.Flags.HIDDEN_ANNOUNCEMENT)) {
			if (player.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
				for (TregminePlayer to : plugin.getOnlinePlayers()) {
					if (to.getRank().canSeeHiddenInfo()) {
						if (player.getCountry() != null) {
							to.sendSpigotMessage(new TextComponent("Welcome "), player.getChatNameStaff(),
									new TextComponent(" from " + player.getCountry() + "!"));
							to.sendSpigotMessage(player.getChatNameStaff(),
									new TextComponent(ChatColor.DARK_AQUA + " is invisible!"));
						} else {
							to.sendSpigotMessage(new TextComponent(ChatColor.DARK_AQUA + "Welcome "),
									player.getChatNameStaff());
							to.sendSpigotMessage(player.getChatNameStaff(),
									new TextComponent("" + ChatColor.DARK_AQUA + " is invisible!"));
						}
						if (player.hasFlag(Flags.CHILD)) {
							to.sendSpigotMessage(player.getChatName(), new TextComponent(
									ChatColor.YELLOW + " is a child; Please be aware when sending messages."));
						}
					}
				}
			} else {
				if (player.getCountry() != null && !player.hasFlag(TregminePlayer.Flags.HIDDEN_LOCATION)) {
					plugin.broadcast(new TextComponent(ChatColor.DARK_AQUA + "Welcome "), player.getChatName(),
							new TextComponent(ChatColor.DARK_AQUA + " from " + player.getCountry() + "!"));
				} else {
					plugin.broadcast(new TextComponent(ChatColor.DARK_AQUA + "Welcome "), player.getChatName());
				}
				if (player.hasFlag(Flags.CHILD)) {
					plugin.broadcast(player.getChatName(), new TextComponent(
							ChatColor.YELLOW + " is a child; Please be aware when sending messages."));
				}
			}

		}
		PermissionAttachment attachment = player.addAttachment(plugin);
		player.setAttachment(attachment);
		if (player.getRank().canUseAllCO()) {
			attachment.setPermission("coreprotect.*", true);
		}
		if (player.getRank().canInspect()) {
			attachment.setPermission("coreprotect.inspect", true);
		}
		if (player.getRank().canLookup()) {
			attachment.setPermission("coreprotect.lookup", true);
		}
		if (player.getRank().canRollback()) {
			attachment.setPermission("coreprotect.rollback", true);
		}
		if (player.getRank().canRestore()) {
			attachment.setPermission("coreprotect.restore", true);
		}
		if (player.getRank().canPurge()) {
			attachment.setPermission("coreprotect.purge", true);
		}
		if (player.getRank().canReload()) {
			attachment.setPermission("coreprotect.reload", true);
		}
		attachment.setPermission("coreprotect.help", true);

		String aliasList = null;
		try (IContext ctx = plugin.createContext()) {
			ILogDAO logDAO = ctx.getLogDAO();
			Set<String> aliases = logDAO.getAliases(player);

			StringBuilder buffer = new StringBuilder();
			String delim = "";
			for (String name : aliases) {
				buffer.append(delim);
				buffer.append(name);
				delim = ", ";
			}

			aliasList = buffer.toString();

			if (aliases.size() > 1) {
				Tregmine.LOGGER.info("Aliases: " + aliasList);

				for (TregminePlayer current : plugin.getOnlinePlayers()) {
					if (!current.getRank().canSeeAliases()) {
						continue;
					}
					if (player.hasFlag(TregminePlayer.Flags.INVISIBLE)
							|| player.hasFlag(TregminePlayer.Flags.HIDDEN_LOCATION)) {
						continue;
					}
				}
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
}
