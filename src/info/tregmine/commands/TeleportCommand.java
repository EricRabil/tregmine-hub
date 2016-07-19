package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.RED;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.math.MathUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class TeleportCommand extends AbstractCommand {
	private static class TeleportTask implements Runnable {
		private TregminePlayer to;
		private TregminePlayer from;

		public TeleportTask(TregminePlayer to, TregminePlayer from) {
			this.to = to;
			this.from = from;
		}

		@Override
		public void run() {

			if (!from.canBeHere(to.getLocation()).getBoolean()) {
				from.sendSpigotMessage(new TextComponent(RED + "You do not have permission for the location of "),
						to.decideVS(from));
				return;
			}
			// Check position hasn't changed since task started.
			double distance = MathUtil.calcDistance2d(from.getLocation(), to.getLocation());

			if (distance > from.getRank().getTeleportDistanceLimit()) {
				from.sendStringMessage(RED + "Your teleportation spell is not strong enough for this long distance!");
				return;
			}

			from.teleportWithHorse(to.getLocation());
			from.setNoDamageTicks(200);

			if (!from.getRank().canDoHiddenTeleport()) {
				to.sendStringMessage(AQUA + from.getName() + " teleported to you!");
				PotionEffect ef = new PotionEffect(PotionEffectType.BLINDNESS, 60, 100);
				from.addPotionEffect(ef);
			}
		}
	}

	private Tregmine tregmine;

	public TeleportCommand(Tregmine tregmine) {
		super(tregmine, "tp");
		this.tregmine = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		Rank rank = player.getRank();
		if (args.length != 1) {
			return false;
		}
		if (!rank.canTeleport()) {
			return true;
		}

		Server server = tregmine.getServer();
		BukkitScheduler scheduler = server.getScheduler();
		String name = args[0];

		List<TregminePlayer> candidates = tregmine.matchPlayer(name);
		if (candidates.size() != 1) {
			player.sendStringMessage(RED + "Can't find user.");
			return true;
		}

		TregminePlayer target = candidates.get(0);
		if (target.getWorld().getName().equalsIgnoreCase("vanilla") || target.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (target.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
			return true;
		}

		if (target.hasFlag(TregminePlayer.Flags.TPSHIELD) && !player.getRank().canOverrideTeleportShield()) {
			player.sendStringMessage(RED + target.getName() + AQUA + "'s teloptical deflector absorbed all motion. "
					+ "Teleportation failed.");
			target.sendStringMessage(
					player.getName() + AQUA + "'s teleportation spell " + "cannot bypass your sophisticated defenses.");
			return true;
		}

		World sourceWorld = player.getWorld();
		World targetWorld = target.getWorld();
		String targetWorldName = targetWorld.getName();
		String sourceWorldName = sourceWorld.getName();
		if (!sourceWorldName.equalsIgnoreCase(targetWorldName) && !rank.canTeleportBetweenWorlds()) {
			player.sendStringMessage(RED + "The user is in another world called " + BLUE + targetWorld.getName() + ".");
			return true;
		}

		double distance = MathUtil.calcDistance2d(player.getLocation(), target.getLocation());
		if (distance <= rank.getTeleportDistanceLimit()) {
			player.setLastPos(player.getLocation());
			player.sendStringMessage(AQUA + "You started teleport to " + target.getName() + AQUA + " in " + BLUE
					+ targetWorld.getName() + ".");

			scheduler.scheduleSyncDelayedTask(tregmine, new TeleportTask(target, player), rank.getTeleportTimeout());
		} else {
			player.sendStringMessage(
					RED + "Your teleportation spell is not strong " + "enough for the longer distances.");
		}

		return true;
	}
}
