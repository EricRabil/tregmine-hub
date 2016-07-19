package info.tregmine.commands;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IWalletDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class WhoCommand extends AbstractCommand {
	public WhoCommand(Tregmine tregmine) {
		super(tregmine, "who");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length == 0) {
			return who(player);
		} else if (args.length == 1 && "world".equalsIgnoreCase(args[0])) {
			return whoWorld(player);
		} else if (args.length > 0) {
			if (!player.getRank().canSeeHiddenInfo()) {
				return true;
			}
			return whoPlayer(player, args);
		}

		return true;
	}

	private String padString(String str, int len) {
		int diff = len - (str.length() + 2);
		if (diff % 2 == 1) {
			str = " " + str + "  ";
			diff--;
		} else {
			str = " " + str + " ";
		}

		int side = diff / 2;
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < side; i++) {
			buf.append("*");
		}

		return DARK_GRAY + buf.toString() + str + DARK_GRAY + buf.toString();
	}

	private boolean who(TregminePlayer player) {
		StringBuilder sb = new StringBuilder();
		String delim = "";

		List<TregminePlayer> players = tregmine.getOnlinePlayers();
		for (TregminePlayer online : players) {
			if (online.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
				// players.remove(online);
				continue;
			}
			sb.append(delim);
			sb.append(online.getChatNameNoHover());
			delim = ChatColor.WHITE + ", ";
		}
		String playerList = sb.toString();

		player.sendStringMessage(padString(DARK_PURPLE + "Player List", 55));
		player.sendStringMessage(playerList);
		player.sendStringMessage(padString(DARK_PURPLE + Integer.toString(players.size()) + " players online", 55));
		return true;
	}

	private boolean whoPlayer(TregminePlayer player, String[] args) {
		String pattern = args[0];

		List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
		if (candidates.size() != 1) {
			return true;
		}

		TregminePlayer whoPlayer = candidates.get(0);

		if (whoPlayer == null) {
			player.sendStringMessage(RED + "That player is not online right now.");
			return true;
		}
		if (whoPlayer.isOnline() != true) {
			player.sendStringMessage(RED + "That player is not online right now.");
			return true;
		}

		double X = whoPlayer.getLocation().getX();
		double Y = whoPlayer.getLocation().getY();
		double Z = whoPlayer.getLocation().getZ();

		float X2 = Math.round(X);
		float Y2 = Math.round(Y);
		float Z2 = Math.round(Z);

		String aliasList = null;

		if (player.getRank().canSeeAliases()) {
			try (IContext ctx = tregmine.createContext()) {

				ILogDAO logDAO = ctx.getLogDAO();
				Set<String> aliases = logDAO.getAliases(whoPlayer);

				StringBuilder buffer = new StringBuilder();
				String delim = "";
				for (String name : aliases) {
					buffer.append(delim);
					buffer.append(name);
					delim = ", ";
				}

				aliasList = buffer.toString();
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		}

		try (IContext ctx = tregmine.createContext()) {
			IWalletDAO walletDAO = ctx.getWalletDAO();

			long balance = walletDAO.balance(whoPlayer);

			player.sendStringMessage(DARK_GRAY + "******************** " + DARK_PURPLE + "PLAYER INFO" + DARK_GRAY
					+ " ********************");
			player.getSpigot().sendMessage(new TextComponent(GOLD + "Player: " + GRAY), whoPlayer.getChatName());
			player.sendStringMessage(GOLD + "World: " + GRAY + whoPlayer.getWorld().getName());
			player.sendStringMessage(GOLD + "Coords: " + GRAY + X2 + ", " + Y2 + ", " + Z2);
			player.sendStringMessage(GOLD + "Channel: " + GRAY + whoPlayer.getChatChannel());
			player.sendStringMessage(GOLD + "Wallet: " + GRAY + balance + " Tregs.");
			player.sendStringMessage(GOLD + "Health: " + GRAY + whoPlayer.getHealth());
			player.sendStringMessage(GOLD + "Country: " + GRAY + whoPlayer.getCountry());
			player.sendStringMessage(GOLD + "City: " + GRAY + whoPlayer.getCity());
			player.sendStringMessage(GOLD + "IP Address: " + GRAY + whoPlayer.getIp());
			player.sendStringMessage(GOLD + "Port: " + GRAY + whoPlayer.getAddress().getPort());
			player.sendStringMessage(GOLD + "Gamemode: " + GRAY + whoPlayer.getGameMode().toString().toLowerCase());
			player.sendStringMessage(GOLD + "Level: " + GRAY + whoPlayer.getLevel());
			if (aliasList != null) {
				player.sendStringMessage(GOLD + "Aliases: " + aliasList);
			}
			if (whoPlayer.hasFlag(Flags.INVISIBLE)) {
				if (player.getRank() == Rank.JUNIOR_ADMIN || player.getRank() == Rank.SENIOR_ADMIN) {
					player.sendStringMessage(BLUE + "This player is invisible.");
				}
			}
			player.sendStringMessage(DARK_GRAY + "*************************************" + "*****************");

			LOGGER.info(player.getName() + " used /who on player " + whoPlayer.getName());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	private boolean whoWorld(TregminePlayer player) {
		for (World world : player.getServer().getWorlds()) {
			if (world.getPlayers().size() > 0) {
				StringBuilder sb = new StringBuilder();
				String delim = "";

				for (Player pl : world.getPlayers()) {
					TregminePlayer p = tregmine.getPlayer(pl);
					if (p.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
						continue;
					}

					sb.append(delim);
					sb.append(p.getChatName());
					delim = ChatColor.WHITE + ", ";
				}

				String playerList = sb.toString();

				player.sendStringMessage(padString(DARK_PURPLE + "Player List for World: " + world.getName(), 55));
				player.sendSpigotMessage(new TextComponent(playerList));
			}
		}

		return true;
	}
}
