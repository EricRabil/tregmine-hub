package info.tregmine.commands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public abstract class AbstractCommand implements CommandExecutor {
	protected final Logger LOGGER = Logger.getLogger("Minecraft");

	protected Tregmine tregmine;
	protected String command;

	protected AbstractCommand(Tregmine tregmine, String command) {
		this.tregmine = tregmine;
		this.command = command;
	}

	public String getName() {
		return command;
	}

	public void invalidArguments(TregminePlayer player, String arguments) {
		player.sendStringMessage(ChatColor.RED + "Invalid arguments passed for /" + this.command + ".");
		player.sendStringMessage(ChatColor.RED + "The proper syntax is: " + ChatColor.GOLD + arguments);
	}

	public void insufficientPerms(TregminePlayer player) {
		player.sendStringMessage(ChatColor.DARK_RED + "You have insufficient permissions for /" + this.command + ".");
	}

	public boolean handleOther(Server server, String[] args) {
		return false;
	}

	public boolean handlePlayer(TregminePlayer player, String[] args) {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			TregminePlayer player = tregmine.getPlayer((Player) sender);
			if (!player.getRank().canUseCommands()) {
				player.sendStringMessage(ChatColor.RED + "Please complete setup before " + "continuing.");
				return true;
			}

			return handlePlayer(player, args);
		}

		return handleOther(sender.getServer(), args);
	}

}
