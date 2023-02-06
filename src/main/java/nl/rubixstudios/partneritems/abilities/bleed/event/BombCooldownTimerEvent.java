package nl.rubixstudios.partneritems.abilities.bleed.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Djorr
 * @created 21/11/2022 - 19:12
 * @project PartnerItems
 */
public class BombCooldownTimerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final @Getter
    Player player;

    private boolean cancelled;

    public BombCooldownTimerEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
