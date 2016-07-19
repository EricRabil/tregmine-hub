package info.tregmine.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import info.tregmine.api.TregminePlayer;
import info.tregmine.zones.Zone;

public final class PlayerZoneChangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled;
	private Location from;
	private Location to;
	private TregminePlayer player;
	private Zone oldZone;

	private Zone newZone;

	public PlayerZoneChangeEvent(Location fromLoc, Location toLoc, TregminePlayer playerInvolved, Zone previousZone,
			Zone currentZone) {
		this.from = fromLoc;
		this.to = toLoc;
		this.player = playerInvolved;
		this.oldZone = previousZone;
		this.newZone = currentZone;
	}

	public Location getFrom() {
		return from;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Zone getNew() {
		return newZone;
	}

	public Zone getOld() {
		return oldZone;
	}

	public TregminePlayer getPlayer() {
		return player;
	}

	public Location getTo() {
		return to;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	public void setFrom(Location newFrom) {
		this.from = newFrom;
	}

	public void setNew(Zone value) {
		this.newZone = value;
	}

	public void setOld(Zone value) {
		this.oldZone = value;
	}

	public void setTo(Location newTo) {
		this.to = newTo;
	}
}
