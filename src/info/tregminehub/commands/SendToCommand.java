package info.tregminehub.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class SendToCommand extends AbstractCommand {
	public SendToCommand(Tregmine tregmine) {
		super(tregmine, "sendto");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 2) {
			return false;
		}
		if (!player.getRank().canSendPeopleToOtherWorlds()) {
			return true;
		}

		List<TregminePlayer> candidates = tregmine.matchPlayer(args[0]);
		if (candidates.size() != 1) {
			// TODO: List users
			return true;
		}

		TregminePlayer victim = candidates.get(0);
		Server server = tregmine.getServer();
		World world = server.getWorld(args[1]);
		if (world == null) {
			player.sendStringMessage(ChatColor.RED + "That world does not exist.");
			return true;
		}

		Location cpspawn = world.getSpawnLocation();
		victim.teleportWithHorse(cpspawn);

		return true;
	}
}
