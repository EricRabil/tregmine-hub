package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.RED;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.Warp;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IWarpDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class WarpCommand extends AbstractCommand {
	private static class WarpTask implements Runnable {
		private TregminePlayer player;
		private Location loc;

		public WarpTask(TregminePlayer player, Location loc) {
			this.player = player;
			this.loc = loc;
		}

		@Override
		public void run() {
			if (player.getRank().canTeleportBetweenWorlds()) {
				player.teleportWithHorse(loc);
				return;
			}

			World playerWorld = player.getWorld();
			String playerWorldName = playerWorld.getName();
			World locWorld = loc.getWorld();
			String locWorldName = locWorld.getName();

			if (playerWorldName.equalsIgnoreCase(locWorldName)) {
				player.teleportWithHorse(loc);

				PotionEffect ef = new PotionEffect(PotionEffectType.BLINDNESS, 60, 100);
				player.addPotionEffect(ef);
			} else {
				player.sendStringMessage(RED + "You can't teleport between worlds.");
			}
		}
	}

	private Tregmine plugin;

	public WarpCommand(Tregmine tregmine) {
		super(tregmine, "warp");
		plugin = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length == 0) {
			return false;
		}

		Server server = tregmine.getServer();
		String name = args[0];
		if (name.equalsIgnoreCase("irl")) {
			player.kickPlayer(plugin, "Welcome to IRL.");
			plugin.broadcast(new TextComponent(ChatColor.GOLD + "" + player.getChatName() + " found the IRL warp!"));
			return true;
		}

		Warp warp = null;
		try (IContext ctx = tregmine.createContext()) {
			IWarpDAO warpDAO = ctx.getWarpDAO();
			warp = warpDAO.getWarp(name, server);
			if (warp == null) {
				player.sendStringMessage("Warp not found!");
				LOGGER.info("[warp failed] + <" + player.getName() + "> " + name + " -- not found");
				return true;
			}

			ILogDAO logDAO = ctx.getLogDAO();
			logDAO.insertWarpLog(player, warp.getId());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		Location warpPoint = warp.getLocation();

		World world = warpPoint.getWorld();

		player.sendStringMessage(
				AQUA + "You started teleport to " + DARK_GREEN + name + AQUA + " in " + BLUE + world.getName() + ".");
		LOGGER.info("[warp] + <" + player.getName() + "> " + name + ":" + world.getName());

		player.setNoDamageTicks(200);

		Chunk chunk = world.getChunkAt(warpPoint);
		world.loadChunk(chunk);

		if (world.isChunkLoaded(chunk)) {
			long delay = player.getRank().getTeleportTimeout();

			player.sendStringMessage(AQUA + "You must now stand still and wait " + (delay / 20)
					+ " seconds for the stars to align, " + "allowing you to warp");

			BukkitScheduler scheduler = server.getScheduler();
			scheduler.scheduleSyncDelayedTask(tregmine, new WarpTask(player, warpPoint), delay);

		} else {
			player.sendStringMessage(RED + "Chunk failed to load. Please try to warp again");
		}

		return true;
	}
}
