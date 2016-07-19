package info.tregmine.zones;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;

import info.tregmine.quadtree.IntersectionException;
import info.tregmine.quadtree.Point;
import info.tregmine.quadtree.QuadTree;
import info.tregmine.quadtree.Rectangle;

public class ZoneWorld {
	private World world;
	private QuadTree<Zone> zonesLookup;
	private QuadTree<Lot> lotLookup;
	private Map<String, Zone> zoneNameLookup;
	private Map<String, Lot> lotNameLookup;

	public ZoneWorld(World world) {
		this.world = world;
		this.zonesLookup = new QuadTree<Zone>();
		this.zoneNameLookup = new TreeMap<String, Zone>(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareToIgnoreCase(b);
			}
		});
		this.lotLookup = new QuadTree<Lot>();
		this.lotNameLookup = new TreeMap<String, Lot>(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareToIgnoreCase(b);
			}
		});
	}

	public void addLot(Lot lot) throws IntersectionException {
		lotLookup.insert(lot.getRect(), lot);
		lotNameLookup.put(lot.getName(), lot);
	}

	public void addZone(Zone zone) throws IntersectionException {
		for (Rectangle rect : zone.getRects()) {
			zonesLookup.insert(rect, zone);
		}
		zoneNameLookup.put(zone.getName(), zone);
	}

	public void deleteLot(String name) {
		if (!lotNameLookup.containsKey(name)) {
			return;
		}

		lotNameLookup.remove(name);

		this.lotLookup = new QuadTree<Lot>(0);
		for (Lot lot : lotNameLookup.values()) {
			try {
				lotLookup.insert(lot.getRect(), lot);
			} catch (IntersectionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void deleteZone(String name) {
		if (!zoneNameLookup.containsKey(name)) {
			return;
		}

		zoneNameLookup.remove(name);

		this.zonesLookup = new QuadTree<Zone>(0);
		for (Zone zone : zoneNameLookup.values()) {
			try {
				for (Rectangle rect : zone.getRects()) {
					zonesLookup.insert(rect, zone);
				}
			} catch (IntersectionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Lot findLot(Location location) {
		Point p = new Point(location.getBlockX(), location.getBlockZ());
		return lotLookup.find(p);
	}

	public Lot findLot(Point p) {
		return lotLookup.find(p);
	}

	public Zone findZone(Location location) {
		Point p = new Point(location.getBlockX(), location.getBlockZ());
		return zonesLookup.find(p);
	}

	public Zone findZone(Point p) {
		return zonesLookup.find(p);
	}

	public Lot getLot(String name) {
		return lotNameLookup.get(name);
	}

	public String getName() {
		return world.getName();
	}

	public Zone getZone(String name) {
		return zoneNameLookup.get(name);
	}

	public boolean lotExists(String name) {
		return lotNameLookup.containsKey(name);
	}

	public boolean zoneExists(String name) {
		return zoneNameLookup.containsKey(name);
	}
}
