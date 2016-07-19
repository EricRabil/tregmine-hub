package info.tregmine.tools;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.commands.AbstractCommand;

public class ToolSpawnCommand extends AbstractCommand {
	public ToolSpawnCommand(Tregmine tregmine) {
		super(tregmine, "tool");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canSpawnTools()) {
			player.sendStringMessage(
					ChatColor.RED + "You cheapskate! If you want to get a tool, you need 16 diamond blocks!");
			return true;
		}
		if (args.length != 1) {
			player.sendStringMessage(ChatColor.RED + "Usage: /tool <lumber/vein/gravity>");
			return true;
		}

		ItemStack tool = null;

		switch (args[0]) {
		case "lumber":
			tool = ToolsRegistry.LumberAxe();
			break;
		case "vein":
			tool = ToolsRegistry.VeinMiner();
			break;
		case "aoe":
			tool = ToolsRegistry.AreaOfEffect();
			break;
		case "gravity":
			tool = ToolsRegistry.GravityGun();
			break;
		}

		if (tool == null) {
			player.sendStringMessage(ChatColor.RED + "Usage: /tool <lumber/vein/gravity>");
			return true;
		}

		HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(tool);

		if (failedItems.size() > 0) {
			player.sendStringMessage(ChatColor.RED + "You have a full inventory, Can't add tool!");
			return true;
		}

		player.sendStringMessage(ChatColor.GREEN + "Spawned in tool token successfully!");
		return true;
	}
}
