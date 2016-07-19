package info.tregmine.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.Nickname;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Property;

public class ChangeNameCommand extends AbstractCommand {
	public ChangeNameCommand(Tregmine tregmine) {
		super(tregmine, "cname");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 2) {
			return false;
		}
		if (!player.getRank().canChangeName()) {
			player.sendStringMessage(ChatColor.RED + "You can't change your name!");
			return true;
		}
		// String colorstring = args[0];
		String use[] = args[0].split(":");
		String colorstring = use[0];
		List<String> formatting = new ArrayList<String>();
		List<ChatColor> format = new ArrayList<ChatColor>();
		if (use.length >= 2) {
			if (use[1] != null) {
				for (String lol : use) {
					if (lol != colorstring && !formatting.contains(lol.toLowerCase())) {
						formatting.add(lol.toLowerCase());
					}
				}
			}
		}
		for (ChatColor color : ChatColor.values()) {
			if (!color.isFormat()) {
				continue;
			}
			if (formatting.contains(color.name().toLowerCase())) {
				format.add(color);
				continue;
			}
		}
		ChatColor usecolor = null;
		for (ChatColor color : ChatColor.values()) {
			if (!color.isColor()) {
				continue;
			}
			if (colorstring.toLowerCase().equals(color.name().toLowerCase())) {
				usecolor = color;
				break;
			}
		}
		Nickname nname = new Nickname(player, args[1]);
		if (usecolor != null) {
			nname.setColor(usecolor);
		}
		if (!format.isEmpty()) {
			nname.setFormatting(format);
		}
		player.setTemporaryChatName(nname.getNickname());

		player.setProperty(Property.NICKNAME);
		player.sendStringMessage("You are now: " + player.getChatNameNoHover());
		LOGGER.info(player.getName() + " changed name to " + player.getChatNameNoHover());

		return true;
	}
}
