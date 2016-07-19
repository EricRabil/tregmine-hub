package info.tregmine.commands;

import static org.bukkit.ChatColor.YELLOW;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IMentorLogDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class ForceCommand extends AbstractCommand {
	public ForceCommand(Tregmine tregmine) {
		super(tregmine, "force");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 2) {
			return false;
		}
		if (!player.getRank().canForceChannel()) {
			player.sendStringMessage(ChatColor.RED + "You cannot force players into channels.");
			return true;
		}
		String playerPattern = args[0];
		String channel = args[1];

		List<TregminePlayer> matches = tregmine.matchPlayer(playerPattern);
		if (matches.size() != 1) {
			// TODO: List candidates
			return true;
		}

		TregminePlayer toPlayer = matches.get(0);

		if (toPlayer.hasFlag(TregminePlayer.Flags.FORCESHIELD) && !player.getRank().canOverrideForceShield()) {
			toPlayer.sendSpigotMessage(new TextComponent((toPlayer.canVS())
					? ChatColor.AQUA + "" + player.getChatNameStaff() + " tried to force you into a channel!"
					: ChatColor.AQUA + "" + player.getChatName() + " tried to force you into a channel!"));
			player.sendSpigotMessage(new TextComponent((toPlayer.canVS())
					? ChatColor.AQUA + "Can not force " + toPlayer.getChatNameStaff() + " into a channel!"
					: ChatColor.AQUA + "Can not force " + toPlayer.getChatName() + " into a channel!"));
			return true;
		}
		String oldChannel = player.getChatChannel();
		player.setChatChannel(channel);
		toPlayer.setChatChannel(channel);
		toPlayer.sendSpigotMessage(new TextComponent(YELLOW + ""), player.decideVS(player),
				new TextComponent(" forced you into channel " + channel.toUpperCase()));
		toPlayer.sendStringMessage(YELLOW + "Write /channel global to switch back to " + "the global chat.");
		player.sendStringMessage(YELLOW + "You are now in a forced chat " + channel.toUpperCase() + " with "
				+ toPlayer.decideVS(player) + ".");
		LOGGER.info(player.getName() + " FORCED CHAT WITH " + toPlayer.getDisplayName() + " IN CHANNEL "
				+ channel.toUpperCase());

		for (TregminePlayer players : tregmine.getOnlinePlayers()) {
			if (oldChannel.equalsIgnoreCase(players.getChatChannel())) {
				players.sendSpigotMessage(new TextComponent(player.decideVS(players) + "" + ChatColor.YELLOW + " and "
						+ toPlayer.getChatName() + ChatColor.YELLOW + " have left channel " + oldChannel));
				players.sendSpigotMessage(player.decideVS(players), new TextComponent(YELLOW + " and "),
						toPlayer.decideVS(players), new TextComponent(YELLOW + " have left channel " + oldChannel));
			} else if (channel.equalsIgnoreCase(players.getChatChannel())) {
				players.sendSpigotMessage(player.decideVS(players), new TextComponent(YELLOW + " and "),
						toPlayer.decideVS(players), new TextComponent(YELLOW + " have joined channel " + channel));
			}
		}

		// If this is a mentor forcing his student, log it in the mentorlog
		TregminePlayer student = player.getStudent();
		if (student != null && student.getId() == toPlayer.getId() && !"global".equalsIgnoreCase(channel)) {
			try (IContext ctx = tregmine.createContext()) {
				IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
				int mentorLogId = mentorLogDAO.getMentorLogId(student, player);
				mentorLogDAO.updateMentorLogChannel(mentorLogId, channel);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		}

		return true;
	}
}
