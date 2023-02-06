package nl.rubixstudios.partneritems.abilities.tntbomber.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author Djorr
 * @created 19/11/2022 - 14:05
 * @project PartnerItems
 */

@Getter
@Setter
@AllArgsConstructor
public class BlockBombData {

    private final Location location;
    private final Material material;
    private final byte data;

    private boolean placedBack;

    private long explodeTime;
}
