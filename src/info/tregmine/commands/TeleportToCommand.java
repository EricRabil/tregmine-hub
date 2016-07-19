package info.tregmine.commands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class TeleportToCommand extends AbstractCommand {
	public TeleportToCommand(Tregmine tregmine) {
		super(tregmine, "tpto");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (args.length != 3) {
			player.sendStringMessage(ChatColor.RED + "Incorrect parameters passed!");
			return true;
		}
		if (!player.getRank().canTeleportToPlayers()) {
			return true;
		}
		if (player.getIsStaff()) {
			player.setLastPos(player.getLocation());
		}

		int x = 0;
		int y = 0;
		int z = 0;
		try {

			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			z = Integer.parseInt(args[2]);

		} catch (NumberFormatException e) {
			if ("~".equals(args[0])) {
				x = player.getLocation().getBlockX();
			}
			if ("~".equals(args[1])) {
				y = player.getLocation().getBlockY();
			}
			if ("~".equals(args[2])) {
				z = player.getLocation().getBlockZ();
			}

			if (x == 0 || y == 0 || z == 0) {
				player.sendStringMessage(ChatColor.RED + "Incorrect parameters passed!");
				return true;
			}
		}

		Location loc = new Location(player.getWorld(), x, y, z);

		World world = loc.getWorld();
		Chunk chunk = world.getChunkAt(loc);
		world.loadChunk(chunk);

		if (world.isChunkLoaded(chunk)) {
			player.teleportWithHorse(loc);
		}

		return true;
	}
}
