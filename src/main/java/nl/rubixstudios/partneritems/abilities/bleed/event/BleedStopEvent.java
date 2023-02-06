package nl.rubixstudios.partneritems.abilities.bleed.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Djorr
 * @created 20/11/2022 - 01:51
 * @project PartnerItems
 */
public class BleedStopEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player damagedPlayer;

    public BleedStopEvent(Player damagedPlayer) {
        this.damagedPlayer = damagedPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getDamagedPlayer(){ return this.damagedPlayer; }
}
