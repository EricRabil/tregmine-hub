package info.tregmine.commands;

import static org.bukkit.ChatColor.GREEN;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.Notification;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class MsgCommand extends AbstractCommand {
	public MsgCommand(Tregmine tregmine) {
		super(tregmine, "msg");
	}

	private String argsToMessage(String[] args) {
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < args.length; ++i) {
			buf.append(" ");
			buf.append(args[i]);
		}

		return buf.toString();
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {

		if (args.length < 2) {
			return false;
		}

		String message = ChatColor.translateAlternateColorCodes('#', argsToMessage(args));

		String[] receivingPlayers = args[0].split(",");
		try (IContext ctx = tregmine.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();

			for (String possiblePlayer : receivingPlayers) {
				List<TregminePlayer> candidates = tregmine.matchPlayer(possiblePlayer);

				if (candidates.size() != 1) {
					player.sendNotification(Notification.COMMAND_FAIL,
							new TextComponent(ChatColor.RED + "No player found by the name of " + possiblePlayer));
				}

				TregminePlayer receivingPlayer = candidates.get(0);

				boolean ignored;
				ignored = playerDAO.doesIgnore(receivingPlayer, player);

				if (player.getRank().canNotBeIgnored())
					ignored = false;
				if (ignored)
					continue;

				// Show message in senders terminal, as long as the recipient
				// isn't
				// invisible, to prevent /msg from giving away hidden players
				// presence
				if (!receivingPlayer.hasFlag(TregminePlayer.Flags.INVISIBLE) || player.getRank().canSeeHiddenInfo()) {
					player.sendSpigotMessage(new TextComponent(GREEN + "(to) "), receivingPlayer.decideVS(player),
							new TextComponent(GREEN + ": " + message));
				} else {
					player.sendNotification(Notification.COMMAND_FAIL,
							new TextComponent(ChatColor.RED + "No player found by the name of " + possiblePlayer));
				}
				receivingPlayer.setLastMessenger(player.getName());
				// Send message to recipient
				receivingPlayer.sendNotification(Notification.MESSAGE, new TextComponent(GREEN + "(msg) "),
						player.decideVS(receivingPlayer), new TextComponent(GREEN + ": " + message));
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
