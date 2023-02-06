package nl.rubixstudios.partneritems.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Djorr
 * @created 28/11/2022 - 22:24
 * @project PartnerItems
 */
public class PlayerUtil {

    public static List<Player> getPlayersInRangeOfLocation(Location location, int range) {
        return location.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(location) <= range).collect(Collectors.toList());
    }
}
