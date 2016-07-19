package info.tregmine.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class SpawnCommand extends AbstractCommand {
	private static class SpawnTask implements Runnable {
		private TregminePlayer player;
		private Location loc;
		private final Location oldLocation;

		public SpawnTask(TregminePlayer player, Location loc, Location oldLocation) {
			this.player = player;
			this.loc = loc;
			this.oldLocation = oldLocation;
		}

		@Override
		public void run() {
			if (oldLocation.getBlockX() != player.getLocation().getBlockX()
					|| oldLocation.getBlockY() != player.getLocation().getBlockY()
					|| oldLocation.getBlockZ() != player.getLocation().getBlockZ()) {

				player.sendStringMessage(ChatColor.RED + "Teleportation stopped! You moved...");
				return;
			}
			player.teleportWithHorse(loc);
		}
	}

	public SpawnCommand(Tregmine tregmine) {
		super(tregmine, "spawn");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		player.teleportWithHorse(player.getWorld().getSpawnLocation());
		return true;
	}
}
