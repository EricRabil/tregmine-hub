package info.tregmine.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import info.tregmine.api.TregminePlayer;
import info.tregmine.zones.Lot;

public final class PlayerLotChangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled;
	private Location from;
	private Location to;
	private TregminePlayer player;
	private Lot oldLot;

	private Lot newLot;

	public PlayerLotChangeEvent(Location fromLoc, Location toLoc, TregminePlayer playerInvolved, Lot previousLot,
			Lot currentLot) {
		this.from = fromLoc;
		this.to = toLoc;
		this.player = playerInvolved;
		this.oldLot = previousLot;
		this.newLot = currentLot;
	}

	public Location getFrom() {
		return from;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Lot getNew() {
		return newLot;
	}

	public Lot getOld() {
		return oldLot;
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

	public void setNew(Lot value) {
		this.newLot = value;
	}

	public void setOld(Lot value) {
		this.oldLot = value;
	}

	public void setTo(Location newTo) {
		this.to = newTo;
	}
}
