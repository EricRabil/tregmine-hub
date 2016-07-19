package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import info.tregmine.Tregmine;
import info.tregmine.api.Bank;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.UUIDFetcher;
import info.tregmine.database.DAOException;
import info.tregmine.database.IBankDAO;
import info.tregmine.database.IContext;
import info.tregmine.database.IZonesDAO;
import info.tregmine.quadtree.IntersectionException;
import info.tregmine.quadtree.Point;
import info.tregmine.quadtree.Rectangle;
import info.tregmine.zones.Lot;
import info.tregmine.zones.Zone;
import info.tregmine.zones.ZoneWorld;
import net.md_5.bungee.api.chat.TextComponent;

public class LotCommand extends AbstractCommand {
	public LotCommand(Tregmine tregmine) {
		super(tregmine, "lot");
	}

	public void createLot(TregminePlayer player, String[] args) {
		ZoneWorld world = tregmine.getWorld(player.getWorld());
		if (world == null) {
			return;
		}

		if (args.length < 3) {
			player.sendStringMessage("syntax: /lot create [name] [owner]");
			return;
		}

		Block tb1 = player.getZoneBlock1();

		Zone tzone = world.findZone(new Point(tb1.getX(), tb1.getZ()));

		String name = args[1] + "." + tzone.getName();

		if (world.lotExists(name)) {
			player.sendStringMessage(RED + "A lot named " + name + " does already exist.");
			return;
		}

		String playerName = args[2];

		TregminePlayer victim = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(playerName));
		if (victim == null) {
			player.sendStringMessage(RED + "Player " + playerName + " was not found.");
			return;
		}

		try (IContext ctx = tregmine.createContext()) {

			IZonesDAO dao = ctx.getZonesDAO();

			Block b1 = player.getZoneBlock1();
			Block b2 = player.getZoneBlock2();
			if (b1 == null || b2 == null) {
				player.sendStringMessage("Please select two corners");
				return;
			}

			Zone zone = world.findZone(new Point(b1.getX(), b1.getZ()));

			Zone.Permission perm = zone.getUser(player);
			if (perm != Zone.Permission.Owner) {
				player.sendStringMessage(
						RED + "You are not allowed to create lots in zone " + zone.getName() + " (" + perm + ").");
				return;
			}

			Zone checkZone = world.findZone(new Point(b2.getX(), b2.getZ()));

			// identity check. both lookups should return exactly the same
			// object
			if (zone != checkZone) {
				return;
			}

			Rectangle rect = new Rectangle(b1.getX(), b1.getZ(), b2.getX(), b2.getZ());

			Lot lot = new Lot();
			lot.setZoneId(zone.getId());
			lot.setRect(rect);
			lot.setName(args[1] + "." + zone.getName());
			lot.addOwner(victim);

			try {
				world.addLot(lot);
			} catch (IntersectionException e) {
				player.sendStringMessage(RED + "The specified rectangle intersects an existing lot.");
				return;
			}

			dao.addLot(lot);
			dao.addLotUser(lot.getId(), victim.getId());

			player.sendStringMessage(GREEN + "[" + zone.getName() + "] Lot " + args[1] + "." + zone.getName()
					+ " created for player " + playerName + ".");
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteLot(TregminePlayer player, String[] args) {
		ZoneWorld world = tregmine.getWorld(player.getWorld());
		if (world == null) {
			return;
		}

		if (args.length < 2) {
			player.sendStringMessage("syntax: /lot delete [name]");
			return;
		}

		String name = args[1];

		Lot lot = world.getLot(name);
		if (lot == null) {
			player.sendStringMessage(RED + "No lot named " + name + " found.");
			return;
		}

		Zone zone = tregmine.getZone(lot.getZoneId());
		Zone.Permission perm = zone.getUser(player);
		if (perm == Zone.Permission.Owner && zone.isCommunist()) {
			// Zone owners can do this in communist zones
		} else if (lot.isOwner(player)) {
			// Lot owners can always do it
		} else if (player.getRank().canModifyZones()) {
			// Admins etc.
		} else {
			player.sendStringMessage(RED + "You are not an owner of lot " + lot.getName() + ".");
			return;
		}

		try (IContext ctx = tregmine.createContext()) {
			IZonesDAO dao = ctx.getZonesDAO();

			dao.deleteLot(lot.getId());
			dao.deleteLotUsers(lot.getId());

			world.deleteLot(lot.getName());

			player.sendStringMessage(GREEN + lot.getName() + " has been deleted.");
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	public void flagLot(TregminePlayer player, String[] args) {
		ZoneWorld world = tregmine.getWorld(player.getWorld());
		if (world == null) {
			return;
		}

		if (args.length < 4) {
			player.sendStringMessage("syntax: /lot flag [name] [flag] [true/false]");
			return;
		}

		String name = args[1];

		Lot lot = world.getLot(name);
		if (lot == null) {
			player.sendStringMessage(RED + "No lot named " + name + " found.");
			return;
		}

		if (!lot.isOwner(player)) {
			player.sendStringMessage(RED + "Must be a lot owner!");
			return;
		}

		Lot.Flags flag = null;
		for (Lot.Flags i : Lot.Flags.values()) {
			if (args[2].equalsIgnoreCase(i.name())) {
				flag = i;
				break;
			}
		}

		if (flag == null) {
			player.sendStringMessage(RED + "Flag not found! Try the following:");

			for (Lot.Flags i : Lot.Flags.values()) {
				player.sendStringMessage(AQUA + i.name());
			}
			return;
		}

		if (flag == Lot.Flags.PVP && (player.getRank() != Rank.JUNIOR_ADMIN && player.getRank() != Rank.SENIOR_ADMIN)) {

			player.sendStringMessage(RED + "This is an admin only flag, Please contact an admin!");
			return;
		}

		boolean value = Boolean.valueOf(args[3]);
		boolean bank = false;

		if (flag == Lot.Flags.BANK) {
			bank = true;
			if (player.getRank().canEditBanks()) {
				if (value) {
					lot.setFlag(flag);
					player.sendStringMessage(GREEN + "Added flag: " + flag.name());
				} else {
					lot.removeFlag(flag);
					player.sendStringMessage(GREEN + "Removed flag: " + flag.name());
				}
			} else {
				player.sendStringMessage(ChatColor.RED + "Please see an admin to make a bank");
			}
		} else {
			if (value) {
				lot.setFlag(flag);
				player.sendStringMessage(GREEN + "Added flag: " + flag.name());
			} else {
				lot.removeFlag(flag);
				player.sendStringMessage(GREEN + "Removed flag: " + flag.name());
			}
		}

		Tregmine.LOGGER.info("Setting " + flag.name() + " to " + value + " for lot " + lot.getName());

		try (IContext ctx = tregmine.createContext()) {
			IZonesDAO dao = ctx.getZonesDAO();
			dao.updateLotFlags(lot);
			if (bank) {
				IBankDAO bDao = ctx.getBankDAO();
				Bank b = new Bank(lot.getId());
				if (value) {
					bDao.createBank(b);
				} else {
					bDao.deleteBank(b);
				}
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length == 0) {
			player.sendStringMessage(ChatColor.RED + "Incorrect usage! Try:");
			player.sendStringMessage(ChatColor.AQUA + "/lot create <lot name> <player>");
			player.sendStringMessage(ChatColor.AQUA + "/lot addowner <lot name> <player>");
			player.sendStringMessage(ChatColor.AQUA + "/lot delowner <lot name> <owner>");
			player.sendStringMessage(ChatColor.AQUA + "/lot delete <lot name>");
			player.sendStringMessage(ChatColor.AQUA + "/lot flag <lot name> <flag name> <true/false>");
			return true;
		}

		if ("create".equals(args[0])) {
			createLot(player, args);
			return true;
		} else if ("addowner".equals(args[0])) {
			setLotOwner(player, args);
			return true;
		} else if ("delowner".equals(args[0])) {
			setLotOwner(player, args);
			return true;
		} else if ("delete".equals(args[0])) {
			deleteLot(player, args);
			return true;
		} else if ("flag".equals(args[0])) {
			flagLot(player, args);
			return true;
		}

		return false;
	}

	public void setLotOwner(TregminePlayer player, String[] args) {
		ZoneWorld world = tregmine.getWorld(player.getWorld());
		if (world == null) {
			return;
		}

		if (args.length < 3) {
			player.sendStringMessage("syntax: /lot addowner [name] [player]");
			return;
		}

		String name = args[1];

		Lot lot = world.getLot(name);
		if (lot == null) {
			player.sendStringMessage(RED + "No lot named " + name + " found.");
			return;
		}

		Zone zone = tregmine.getZone(lot.getZoneId());
		Zone.Permission perm = zone.getUser(player);
		if (perm == Zone.Permission.Owner && zone.isCommunist()) {
			// Zone owners can do this in communist zones
		} else if (lot.isOwner(player)) {
			// Lot owners can always do it
		} else if (player.getRank().canModifyZones()) {
			// Admins etc.
		} else {
			player.sendStringMessage(RED + "You are not an owner of lot " + lot.getName() + ".");
			return;
		}

		// try partial matching
		List<TregminePlayer> candidates = tregmine.matchPlayer(args[2]);
		TregminePlayer candidate = null;
		if (candidates.size() != 1) {
			// try exact matching
			candidate = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(args[2]));
			if (candidate == null) {
				// give up
				player.sendStringMessage(RED + "Player " + args[2] + " was not found.");
				return;
			}
		} else {
			candidate = candidates.get(0);
		}

		try (IContext ctx = tregmine.createContext()) {
			IZonesDAO dao = ctx.getZonesDAO();

			if ("addowner".equals(args[0])) {

				if (lot.isOwner(candidate)) {
					player.sendSpigotMessage(new TextComponent(RED + ""), candidate.decideVS(player),
							new TextComponent(RED + " is already an owner of lot " + name + "."));
					return;
				} else {
					lot.addOwner(candidate);
					dao.addLotUser(lot.getId(), candidate.getId());
					player.sendSpigotMessage(new TextComponent(GREEN + ""), candidate.decideVS(player),
							new TextComponent(GREEN + " has been added as owner of " + lot.getName() + "."));
				}
			} else if ("delowner".equals(args[0])) {
				if (!lot.isOwner(candidate)) {
					player.sendSpigotMessage(new TextComponent(RED + ""), candidate.decideVS(player),
							new TextComponent(RED + " is not an owner of lot " + name + "."));
					return;
				} else {
					lot.deleteOwner(candidate);
					dao.deleteLotUser(lot.getId(), candidate.getId());

					player.sendSpigotMessage(new TextComponent(GREEN + ""), candidate.decideVS(player),
							new TextComponent(GREEN + " is no longer an owner of " + lot.getName() + "."));
				}
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
}
