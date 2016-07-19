package info.tregmine.commands;

import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.chat.TextComponent;

public class ChannelCommand extends AbstractCommand {
	public ChannelCommand(Tregmine tregmine) {
		super(tregmine, "channel");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 1) {
			return false;
		}

		String channel = args[0];
		String oldchannel = player.getChatChannel();

		player.sendStringMessage(YELLOW + "You are now talking in channel " + channel + ".");
		player.sendStringMessage(YELLOW + "Write /channel global to switch to " + "the global chat.");
		player.setChatChannel(channel);

		if (player.hasFlag(TregminePlayer.Flags.INVISIBLE))
			return true; // Doesn't announce channel change if invisible.

		if (oldchannel.equalsIgnoreCase(channel)) {
			return true;
		}

		for (TregminePlayer players : tregmine.getOnlinePlayers()) {
			if (oldchannel.equalsIgnoreCase(players.getChatChannel())) {
				players.sendSpigotMessage(player.decideVS(player),
						new TextComponent(ChatColor.YELLOW + " has left channel " + oldchannel));
			} else if (channel.equalsIgnoreCase(players.getChatChannel())) {
				players.sendSpigotMessage(player.decideVS(players),
						new TextComponent(ChatColor.YELLOW + " has joined channel " + channel));
			}
		}

		return true;
	}
}
