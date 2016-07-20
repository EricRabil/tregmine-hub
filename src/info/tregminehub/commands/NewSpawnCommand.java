package info.tregminehub.commands;

import org.bukkit.Location;
import org.bukkit.World;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class NewSpawnCommand extends AbstractCommand {
	public NewSpawnCommand(Tregmine tregmine) {
		super(tregmine, "newspawn");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.isOp()) {
			return false;
		}

		World world = player.getWorld();
		if (world == null) {
			// TODO: error message
			return false;
		}

		Location loc = player.getLocation();
		world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

		return true;
	}
}
