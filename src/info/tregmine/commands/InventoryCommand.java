package info.tregmine.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.chat.TextComponent;

public class InventoryCommand extends AbstractCommand {
	public InventoryCommand(Tregmine tregmine) {
		super(tregmine, "inv");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (args.length == 0) {
			player.sendStringMessage(ChatColor.RED + "Incorrect usage:");
			player.sendStringMessage(ChatColor.AQUA + "/inv inspect <name> - Inspect someones inventory");
			player.sendStringMessage(
					ChatColor.AQUA + "/inv reload <name> <true/false> - Reload inventory, optional save");
			player.sendStringMessage(ChatColor.AQUA + "/inv save - Save your current inventory to database");
			return true;
		}
		if (!player.getRank().canInspectInventories()) {
			return true;
		}
		if ("save".equalsIgnoreCase(args[0]) && args.length == 1) {
			player.saveInventory(player.getCurrentInventory());
			return true;
		}
		if ("reload".equalsIgnoreCase(args[0]) && args.length == 3) {
			List<TregminePlayer> candidates = tregmine.matchPlayer(args[1]);
			if (candidates.size() != 1) {
				player.sendStringMessage(ChatColor.RED + "Player: " + args[1] + " not found!");
				return true;
			}
			TregminePlayer candidate = candidates.get(0);
			boolean state = "true".equalsIgnoreCase(args[2]);

			if (state) {
				candidate.loadInventory(candidate.getCurrentInventory(), true);
			} else {
				candidate.loadInventory(candidate.getCurrentInventory(), false);
			}
			player.sendSpigotMessage(new TextComponent(ChatColor.GREEN + "Reloaded "), candidate.decideVS(player),
					new TextComponent("'s inventory from DB!"));
			return true;
		}

		if ("inspect".equalsIgnoreCase(args[0]) && args.length == 2) {
			List<TregminePlayer> candidates = tregmine.matchPlayer(args[1]);
			if (candidates.size() != 1) {
				player.sendStringMessage(ChatColor.RED + "Player: " + args[1] + " not found!");
				return true;
			}
			TregminePlayer candidate = candidates.get(0);
			player.openInventory(candidate.getInventory());
			player.sendSpigotMessage(new TextComponent(ChatColor.GREEN + "Inspecting "), candidate.decideVS(player),
					new TextComponent("'s inventory!"));
			return true;
		}

		player.sendStringMessage(ChatColor.RED + "Incorrect usage:");
		player.sendStringMessage(ChatColor.AQUA + "/inv inspect <name> - Inspect someones inventory");
		player.sendStringMessage(ChatColor.AQUA + "/inv reload <name> <true/false> - Reload inventory, optional save");
		player.sendStringMessage(ChatColor.AQUA + "/inv save - Save your current inventory to database");
		return true;
	}
}
