package info.tregminehub.commands;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.api.TregminePlayer.Property;

public class ResetNameCommand extends AbstractCommand {
	public ResetNameCommand(Tregmine tregmine) {
		super(tregmine, "rname");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		player.setTemporaryChatName(player.getNameColor() + player.getName());
		player.sendStringMessage(ChatColor.GREEN + "Your name has been reset.");
		player.removeProperty(Property.NICKNAME);
		return true;
	}
}
