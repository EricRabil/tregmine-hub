package info.tregmine.listeners;

import info.tregmine.Tregmine;

public class TregminePortalListener {
	private Tregmine plugin;

	// World vanillaWorld = Bukkit.getWorld("vanilla");
	//
	// World vanillaEndWorld = Bukkit.getWorld("vanilla_the_end");
	// World vanillaNether = Bukkit.getWorld("vanilla_nether");
	// World mainWorld = Bukkit.getWorld("world");
	// World mainWorldNether = Bukkit.getWorld("world_nether");
	// World mainWorldEnd = Bukkit.getWorld("world_the_end");
	public TregminePortalListener(Tregmine instance) {
		this.plugin = instance;
	}

	/*
	 * @EventHandler public void portalHandler(PlayerPortalEvent event) {
	 * 
	 * TeleportCause cause = event.getCause(); TregminePlayer player =
	 * plugin.getPlayer(event.getPlayer()); if (cause !=
	 * TeleportCause.NETHER_PORTAL && cause != TeleportCause.END_PORTAL && cause
	 * != TeleportCause.END_GATEWAY) { return; } event.setCancelled(true);
	 * switch (cause) { case NETHER_PORTAL: if (player.getWorld() ==
	 * vanillaWorld) { // Send to nether Location loc =
	 * vanillaNether.getSpawnLocation(); player.teleportWithHorse(loc); } else
	 * if (player.getWorld() == vanillaNether) { // Send home Location loc =
	 * vanillaWorld.getSpawnLocation(); player.teleportWithHorse(loc); } else if
	 * (player.getWorld() == mainWorld) { // Send to nether Location loc =
	 * mainWorldNether.getSpawnLocation(); player.teleportWithHorse(loc); } else
	 * if (player.getWorld() == mainWorldNether) { // Send home Location loc =
	 * mainWorld.getSpawnLocation(); player.teleportWithHorse(loc); } else { //
	 * This player shouldn't do that player.sendStringMessage(this.prefix(true)
	 * +
	 * "You are in an illegal world for that portal; Please contact an admin for assistance."
	 * ); break; } case END_PORTAL: if (player.getWorld() == vanillaWorld) { //
	 * Send to vanilla end Location loc = vanillaEndWorld.getSpawnLocation();
	 * player.teleportWithHorse(loc); } else if (player.getWorld() == mainWorld)
	 * { // Send to end Location loc = mainWorldEnd.getSpawnLocation();
	 * player.teleportWithHorse(loc); } else { // This player shouldn't do that
	 * player.sendStringMessage(this.prefix(true) +
	 * "You are in an illegal world for that portal; Please contact an admin for assistance."
	 * ); break; } case END_GATEWAY: if (player.getWorld() == vanillaEndWorld) {
	 * // Send to vanilla home Location loc = vanillaWorld.getSpawnLocation();
	 * player.teleportWithHorse(loc); } else if (player.getWorld() ==
	 * mainWorldEnd) { // Send home Location loc = mainWorld.getSpawnLocation();
	 * player.teleportWithHorse(loc); } else { // This player shouldn't do that
	 * player.sendStringMessage(this.prefix(true) +
	 * "You are in an illegal world for that portal; Please contact an admin for assistance."
	 * ); break; } case CHORUS_FRUIT: case COMMAND: case ENDER_PEARL: case
	 * PLUGIN: case SPECTATE: case UNKNOWN: }
	 * 
	 * }
	 * 
	 * private String prefix(boolean error) { if (error) { return ChatColor.RED
	 * + "[PORTAL] "; } else { return ChatColor.AQUA + "[PORTAL] "; } }
	 * 
	 * // // @EventHandler // public void portalHandler(PlayerMoveEvent event)
	 * // { // final TregminePlayer player =
	 * plugin.getPlayer(event.getPlayer()); // Block under =
	 * player.getLocation().subtract(0, 1, 0).getBlock(); // Block in =
	 * event.getTo().getBlock(); // // // Simply add another line changing
	 * frame, under, world and name to add a // new portal! (Similar to end
	 * portal) // } // // public void handlePortal(TregminePlayer player,
	 * Material underType, // Material frame, World newWorld, String worldName,
	 * Block in, Block under) // { // if (under.getType() != underType ||
	 * !in.isLiquid()) { // return; // } // // if ( !(frameCheck(player, -1, 3,
	 * -1, 3, frame) || // frameCheck(player, -1, 3, -2, 2, frame) || //
	 * frameCheck(player, -1, 3, -3, 1, frame) || // frameCheck(player, -2, 2,
	 * -1, 3, frame) || // frameCheck(player, -2, 2, -2, 2, frame) || //
	 * frameCheck(player, -2, 2, -3, 1, frame) || // frameCheck(player, -3, 1,
	 * -1, 3, frame) || // frameCheck(player, -3, 1, -2, 2, frame) || //
	 * frameCheck(player, -3, 1, -3, 1, frame))) { // return; // } // // if
	 * (player.getWorld().getName().equalsIgnoreCase(newWorld.getName())) { //
	 * Bukkit.getPluginManager().callEvent(new //
	 * TregminePortalEvent(player.getWorld(), Bukkit.getWorld("world"), //
	 * player)); //
	 * player.teleportWithHorse(Bukkit.getWorld("world").getSpawnLocation()); //
	 * player.sendStringMessage(ChatColor.GOLD + "[PORTAL] " + ChatColor.GREEN +
	 * // "Teleporting to main world!"); // } else { //
	 * Bukkit.getPluginManager().callEvent(new //
	 * TregminePortalEvent(player.getWorld(), newWorld, player)); //
	 * player.teleportWithHorse(newWorld.getSpawnLocation()); //
	 * player.sendStringMessage(ChatColor.GOLD + "[PORTAL] " + ChatColor.GREEN +
	 * // "Teleporting to " + worldName + " world!"); // } //
	 * player.setFireTicks(0); // } // // public boolean
	 * frameCheck(TregminePlayer p, int x1, int x2, int z1, int // z2, Material
	 * portal) // { // if( p.getLocation().add(x1, 0,
	 * 0).getBlock().getType().equals(portal) && // p.getLocation().add(0, 0,
	 * z1).getBlock().getType().equals(portal) && // p.getLocation().add(x2, 0,
	 * 0).getBlock().getType().equals(portal) && // p.getLocation().add(0, 0,
	 * z2).getBlock().getType().equals(portal)) { // return true; // } else { //
	 * return false; // } // }
	 * 
	 */
}
