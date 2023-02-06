package nl.rubixstudios.partneritems.abilities.bleed.object;

import lombok.Getter;
import lombok.Setter;
import nl.rubixstudios.partneritems.data.Config;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class BleedBombObject {
    private final Item item;
    private final Player shooter;
    private final Location landLoc;
    private final int bleedRange;
    private final List<UUID> bleedPlayers;
    private final long timeThrown;

    private @Setter boolean isActive = false;

    public BleedBombObject(Player shooter, Item item) {
        this.shooter = shooter;

        this.bleedRange = Config.PARTNER_ITEM_BLEED_BOMB_BLEED_RANGE;
        this.bleedPlayers = new ArrayList<>();

        this.timeThrown = System.currentTimeMillis();

        this.item = item;
        this.landLoc = item.getLocation();

    }

    public void addBleedPlayer(Player player) {
        if (this.bleedPlayers.contains(player.getUniqueId())) return;
        this.bleedPlayers.add(player.getUniqueId());
    }

    public void removeBleedPlayer(Player player) {
        if (!this.bleedPlayers.contains(player.getUniqueId())) return;
        this.bleedPlayers.remove(player.getUniqueId());
    }

    public boolean hasBleedPlayer(Player player){
        return bleedPlayers.contains(player.getUniqueId());
    }


}
