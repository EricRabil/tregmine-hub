package info.tregmine.commands;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Location;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class PositionCommand extends AbstractCommand {
	public PositionCommand(Tregmine tregmine) {
		super(tregmine, "pos");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		Location loc = player.getLocation();
		Location spawn = player.getWorld().getSpawnLocation();
		double distance = info.tregmine.api.math.MathUtil.calcDistance2d(spawn, loc);

		player.sendStringMessage(DARK_AQUA + "World: " + WHITE + player.getWorld().getName());
		player.sendStringMessage(DARK_AQUA + "X: " + WHITE + loc.getX() + RED + " (" + loc.getBlockX() + ")");
		player.sendStringMessage(DARK_AQUA + "Y: " + WHITE + loc.getY() + RED + " (" + loc.getBlockY() + ")");
		player.sendStringMessage(DARK_AQUA + "Z: " + WHITE + loc.getZ() + RED + " (" + loc.getBlockZ() + ")");
		player.sendStringMessage(DARK_AQUA + "Yaw: " + WHITE + loc.getYaw());
		player.sendStringMessage(DARK_AQUA + "Pitch: " + WHITE + loc.getPitch());
		player.sendStringMessage(DARK_AQUA + "Blocks from spawn: " + WHITE + distance);

		LOGGER.info("World: " + player.getWorld().getName());
		LOGGER.info("X: " + loc.getX() + " (" + loc.getBlockX() + ")");
		LOGGER.info("Y: " + loc.getY() + " (" + loc.getBlockY() + ")");
		LOGGER.info("Z: " + loc.getZ() + " (" + loc.getBlockZ() + ")");
		LOGGER.info("Yaw: " + loc.getYaw());
		LOGGER.info("Pitch: " + loc.getPitch());

		return true;
	}
}
