package nl.rubixstudios.partneritems.abilities.teleportportal.object;

import lombok.Getter;
import lombok.Setter;
import me.qiooip.lazarus.factions.Faction;
import me.qiooip.lazarus.factions.claim.Claim;
import me.qiooip.lazarus.factions.claim.ClaimManager;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Djorr
 * @created 21/11/2022 - 19:56
 * @project PartnerItems
 */

@Getter
@Setter
public class TeleportPortalObj {

    private final Location location;
    private final PlayerFaction playerFaction;
    private final PlayerFaction enemyFaction;
    private final UUID placedByPlayer;

    private long placedTime;
    public int timesToHitBeforeDestroy;

    public TeleportPortalObj(Location location, PlayerFaction playerFaction, PlayerFaction enemyFaction, UUID playerId) {
        this.location = location.add(new Vector(0, 1, 0));
        this.playerFaction = playerFaction;
        this.enemyFaction = enemyFaction;
        this.placedByPlayer = playerId;

        this.placedTime = System.currentTimeMillis();
        this.timesToHitBeforeDestroy = Config.PARTNER_ITEM_TELEPORT_PORTAL_HITS_NEEDED;
    }

    public void activatePortal() {
        final Block block = this.location.getBlock();
        block.setType(Material.BEACON);
        block.setMetadata("teleportPortal", new FixedMetadataValue(PartnerItems.getInstance(), true));

        this.sendActivatedMessage();
    }

    private void sendActivatedMessage() {
        this.playerFaction.getMembers().forEach(((uuid, factionPlayer) -> {
            if (factionPlayer.getPlayer() == null) return;

            factionPlayer.sendMessage(ColorUtil.translate(""));
            factionPlayer.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &7» &a<name> &bactivated a Portal in &f<enemyFaction>&b!"
                    .replace("<name>", Bukkit.getOfflinePlayer(placedByPlayer).getName())
                    .replace("<enemyFaction>", enemyFaction.getName())
            ));
            factionPlayer.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &7» &bUse this Portal with &f/portal&b!"));
            factionPlayer.sendMessage(ColorUtil.translate(""));
        }));
    }

    public void deactivatePortal() {
        final Block block = this.location.getBlock();

        for (int i = 0; i < 5; i++) {
            block.getWorld().getBlockAt(block.getLocation().getBlockX(), block.getLocation().getBlockY() - i, block.getLocation().getBlockZ());
            if (block.getType() == Material.BEACON) block.setType(Material.AIR);
        }
        block.setType(Material.AIR);

        this.sendDeactivatedMessage();
    }

    public void sendDeactivatedMessage() {
        this.playerFaction.getMembers().forEach(((uuid, factionPlayer) -> {
            if (factionPlayer.getPlayer() == null) return;

            factionPlayer.sendMessage(ColorUtil.translate(""));
            factionPlayer.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &7» &bThe &fteleport portal &bin &f<enemyFaction>&b"
                    .replace("<enemyFaction>", enemyFaction.getName())
            ));
            factionPlayer.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &7» &bhas been been &6deactivated&b!"));
            factionPlayer.sendMessage(ColorUtil.translate(""));
        }));
    }

    public void handleTeleport(Player player) {
        final List<Player> playersInEnemyClaimOfFaction = this.getAmountOfPlayersOfFactionInEnemyClaim();
        if (playersInEnemyClaimOfFaction.size() > this.enemyFaction.getOnlineMemberCount()) {
            player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cThe maximum teleport count has been reached."));
            return;
        }

        player.teleport(this.location.add(0, 0.5, 0));
        player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &7» &aYou have been teleported to the portal!"));
    }

    private Location getFirstSafePosition(Claim claim) {
        int maxX = claim.getMaxX() < 0 ? claim.getMinZ() - 2 : claim.getMaxX() + 2;
        int maxZ = claim.getMaxZ() < 0 ? claim.getMinZ() - 2 : claim.getMaxZ() + 2;
        int y = claim.getWorld().getHighestBlockYAt(maxX, maxZ);

        return new Location(claim.getWorld(), maxX, y, maxZ);
    }

    private List<Player> getAmountOfPlayersOfFactionInEnemyClaim() {
        return this.playerFaction.getOnlinePlayers().stream().filter(player ->
                ClaimManager.getInstance().getFactionAt(player) != null && ClaimManager.getInstance().getFactionAt(player).getId() == this.enemyFaction.getId())
                .collect(Collectors.toList());
    }

    public boolean isPlayerInEnemyFaction(Player player) {
        return this.enemyFaction.getMembers().keySet().stream().anyMatch(uuid -> player.getUniqueId().equals(uuid));
    }

    public boolean isPlayerInRange(Player player) {
        return this.isPlayerInEnemyClaim(player) || this.getLocation().distance(player.getLocation()) < Config.PARTNER_ITEM_TELEPORT_PORTAL_TELEPORT_RANGE;
    }

    private boolean isPlayerInEnemyClaim(Player player) {
        return ClaimManager.getInstance().getFactionAt(player).getId() == this.enemyFaction.getId();
    }

}
