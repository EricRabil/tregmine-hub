package info.tregmine.listeners;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import info.tregmine.Tregmine;
import info.tregmine.api.Notification;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IPlayerDAO;
import info.tregmine.database.IWalletDAO;
import info.tregmine.events.TregmineChatEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatListener implements Listener {
	private Tregmine plugin;

	public ChatListener(Tregmine instance) {
		this.plugin = instance;
	}

	private TextComponent createTC(String str) {
		return new TextComponent(str);
	}

	@EventHandler
	public void onTregmineChat(TregmineChatEvent event) {
		TregminePlayer sender = event.getPlayer();
		if (sender.isMuted() && !sender.getMute().isExpired() && !sender.getMute().isCancelled()) {
			sender.sendStringMessage(ChatColor.YELLOW + "You have been muted; Your mute will expire in "
					+ sender.getMute().secondsLeft() + " seconds.");
			event.setCancelled(true);
			return;
		}
		String channel = sender.getChatChannel();
		if (event.getMessage().contains("%cancel%")) {
			event.setCancelled(true);
			return;
		}
		if (sender.isAfk()) {
			sender.setAfk(false);
		}

		try (IContext ctx = plugin.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			String text = event.getMessage();
			boolean curse = false;
			boolean avoided = false;
			int cursetotal = 0;
			for (String word : plugin.getBannedWords()) {
				if (text.toLowerCase().contains(word.toLowerCase())) {
					curse = true;
					cursetotal += 1;
					String replacement = "";
					for (int count = 1; count <= word.length(); count++) {
						replacement += "*";
					}
					text = text.replaceAll(word, replacement);
					if (text.toLowerCase().contains(word.toLowerCase())) {
						avoided = true;
					}
				}
			}
			if (curse == true) {
				IPlayerDAO playerdao = ctx.getPlayerDAO();
				if (sender.isCurseWarned()) {
					sender.sendSpigotMessage(new TextComponent(ChatColor.RED + "Hey! You shouldn't be cursing! "
							+ cursetotal * 50 + " Tregs have been removed from your account."));
					IWalletDAO wallet = ctx.getWalletDAO();
					wallet.take(sender, cursetotal * 50);
				} else {
					playerdao.updateProperty(sender, "cursewarned", "true");
					sender.sendSpigotMessage(new TextComponent(ChatColor.RED
							+ "Hey! You shouldn't be cursing! This is your only warning. After this, 50 Tregs per curse will be removed from your account."));
					sender.setCurseWarned(true);
				}
				if (avoided) {
					sender.sendSpigotMessage(new TextComponent(
							ChatColor.RED + "You thought you were slick... Your chat has been cancelled."));
					event.setCancelled(true);
					plugin.addBlockedChat(event);
					return;
				}
			}
			for (TregminePlayer to : plugin.getOnlinePlayers()) {
				if (to.getChatState() == TregminePlayer.ChatState.SETUP) {
					continue;
				}

				if (!sender.getRank().canNotBeIgnored()) {
					if (playerDAO.doesIgnore(to, sender)) {
						continue;
					}
				}

				ChatColor txtColor = ChatColor.WHITE;

				for (TregminePlayer online : plugin.getOnlinePlayers()) {

					if (text.contains(online.getRealName()) && !online.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
						if (text.toLowerCase().contains("@" + online.getRealName())) {

							text = text.replaceAll("@" + online.getRealName(), ChatColor.ITALIC + ""
									+ plugin.getRankColor(online.getRank()) + "@" + online.getChatNameNoHover());
						} else {
							text = text.replaceAll(online.getRealName(), online.getChatNameNoHover() + txtColor);
						}
						online.sendNotification(Notification.MESSAGE);
					}

				}

				if (sender.getRank().canUseChatColors()) {
					text = ChatColor.translateAlternateColorCodes('#', text);
				}

				List<String> player_keywords = playerDAO.getKeywords(to);

				if (player_keywords.size() > 0 && player_keywords != null) {
					for (String keyword : player_keywords) {
						if (text.toLowerCase().contains(keyword.toLowerCase())) {
							text = text.replaceAll(Pattern.quote(keyword), ChatColor.AQUA + keyword + txtColor);
						}
					}
				}

				String frontBracket = "<";
				String endBracket = ChatColor.WHITE + "> ";
				String senderChan = sender.getChatChannel();
				String toChan = to.getChatChannel();
				Spigot toSpigot = to.getSpigot();

				TextComponent sendername;
				if (to.getIsStaff()) {
					sendername = sender.getChatNameStaff();
				} else {
					sendername = sender.getChatName();
				}
				if (senderChan.equalsIgnoreCase(toChan) || to.hasFlag(TregminePlayer.Flags.CHANNEL_VIEW)) {

					if (event.isWebChat()) {
						if ("GLOBAL".equalsIgnoreCase(senderChan)) {
							TextComponent begin = createTC("(");
							TextComponent end = createTC(ChatColor.WHITE + ") " + txtColor + text);
							toSpigot.sendMessage(begin, sendername, end);
						} else {
							TextComponent begin = createTC(channel + "(");
							TextComponent end = createTC(ChatColor.WHITE + ") " + txtColor + text);
							toSpigot.sendMessage(begin, sendername, end);
						}
					} else {
						if ("GLOBAL".equalsIgnoreCase(senderChan)) {
							TextComponent begin = createTC(frontBracket);
							TextComponent end = createTC(ChatColor.WHITE + endBracket + txtColor + text);
							toSpigot.sendMessage(begin, sendername, end);
						} else {
							TextComponent begin = createTC(channel + frontBracket);
							TextComponent end = createTC(ChatColor.WHITE + endBracket + txtColor + text);
							toSpigot.sendMessage(begin, sendername, end);
						}
					}
				}

				if (text.contains(to.getRealName()) && "GLOBAL".equalsIgnoreCase(senderChan)
						&& !"GLOBAL".equalsIgnoreCase(toChan)) {

					to.sendSpigotMessage(
							new TextComponent(
									ChatColor.BLUE + "You were mentioned in GLOBAL by " + sender.getNameColor()),
							sender.getChatName());
				}
				/*
				 * if(text.toLowerCase().contains("@" + keyword.toLowerCase())){
				 * text = text.replaceAll("@" + keyword.toLowerCase(),
				 * ChatColor.GOLD + "" + ChatColor.ITALIC + "@" + ChatColor.GOLD
				 * + ChatColor.ITALIC + keyword.toLowerCase()); }
				 */
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		if (event.isWebChat()) {
			Tregmine.LOGGER.info(channel + " (" + sender.getName() + ") " + event.getMessage());
		} else {
			Tregmine.LOGGER.info(channel + " <" + sender.getPlayer().getName() + "> " + event.getMessage());
		}
		try (IContext ctx = plugin.createContext()) {
			ILogDAO logDAO = ctx.getLogDAO();
			logDAO.insertChatMessage(sender, channel, event.getMessage());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		// event.setCancelled(true);
		// WebServer server = plugin.getWebServer();
		// server.executeChatAction(new WebServer.ChatMessage(sender, channel,
		// event.getMessage()));
		// if (plugin.getConfig().getString("general.life-log") == "true") {
		// ListStore lifeChat = new ListStore(new File(plugin.getPluginFolder()
		// + File.separator + "life-log.txt"));
		// lifeChat.load();
		// Date now = new Date();
		// SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy
		// HH:mm:ss");
		// String chatTimeStamp = format.format(now);
		// String userName = event.getPlayer().getName();
		// String message = event.getMessage();
		// String logChat = "[" + chatTimeStamp + "] <" + userName + "> " +
		// message;
		// lifeChat.add(logChat);
		// lifeChat.save();
		// }
	}
}
