package nl.rubixstudios.partneritems.abilities.bleed.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Djorr
 * @created 20/11/2022 - 01:51
 * @project PartnerItems
 */

@Getter
public class BleedActivatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player damagedPlayer;
    private final boolean onlyFaction;

    public BleedActivatedEvent(Player damagedPlayer, boolean onlyFaction) {
        this.damagedPlayer = damagedPlayer;
        this.onlyFaction = onlyFaction;
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
