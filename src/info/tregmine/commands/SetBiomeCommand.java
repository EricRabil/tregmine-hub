package info.tregmine.commands;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class SetBiomeCommand extends AbstractCommand {

	protected Tregmine plugin;

	protected int x, minX, maxX;

	protected int z, minZ, maxZ;

	public SetBiomeCommand(Tregmine tregmine) {
		super(tregmine, "setbiome");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canSetBiome()) {
			return true;
		}

		Block b1 = player.getFillBlock1();
		Block b2 = player.getFillBlock2();

		if (b1 == null || b2 == null) {
			player.sendStringMessage(RED + "You must select 2 corners!");
			return true;
		}

		World world = b1.getLocation().getWorld();

		maxX = Math.max(b1.getLocation().getBlockX(), b2.getLocation().getBlockX());
		minX = Math.min(b1.getLocation().getBlockX(), b2.getLocation().getBlockX());
		x = minX;

		maxZ = Math.max(b1.getLocation().getBlockZ(), b2.getLocation().getBlockZ());
		minZ = Math.min(b1.getLocation().getBlockZ(), b2.getLocation().getBlockZ());
		z = minZ;

		if (args.length != 1) {
			player.sendStringMessage(RED + "Correct usage: /setbiome <biome>");
			player.sendStringMessage(RED + "For a list of availiable biomes type /setbiome help");
			return true;
		}

		if (args[0].equalsIgnoreCase("help")) {
			player.sendStringMessage(GREEN + "Availiable Biomes:");
			player.sendStringMessage(GRAY + "Beach, Desert, Desert_Hills, Extreme_Hills, Forest, Forest_Hills");
			player.sendStringMessage(GRAY + "Frozen_Ocean, Frozen_River, Hell, Ice_Mountains, Ice_Plains");
			player.sendStringMessage(GRAY + "Jungle, Jungle_Hills, Mushroom, Mushroom_Shore, Ocean, Plains");
			player.sendStringMessage(GRAY + "River, Sky, Small_Mountains, Swampland, Taiga, Taiga_Hills");
		} else {
			try {
				for (int x = minX; x <= maxX; x++) {
					for (int z = minZ; z <= maxZ; z++) {
						world.getBlockAt(x, 0, z).setBiome(Biome.valueOf(args[0].toUpperCase()));
					}
				}
				player.sendStringMessage(GREEN + "Biome successfully changed to " + args[0].toLowerCase() + "!");
			} catch (Exception error) {
				player.sendStringMessage(RED + "An error has occured, Please type " + GRAY + "/setbiome help");
				player.sendStringMessage(RED + "for a list of valid biomes.");

			}
		}

		int minChunkX = (int) Math.floor((double) minX / 16);
		int maxChunkX = (int) Math.floor((double) maxX / 16);
		int minChunkZ = (int) Math.floor((double) minZ / 16);
		int maxChunkZ = (int) Math.floor((double) maxZ / 16);

		for (int x = minChunkX; x <= maxChunkX; x++) {
			for (int z = minChunkZ; z <= maxChunkZ; z++) {
				world.refreshChunk(x, z);
			}
		}

		return true;
	}
}
