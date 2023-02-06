package nl.rubixstudios.partneritems.abilities.teleportportal.task;

import nl.rubixstudios.partneritems.abilities.teleportportal.TeleportPortalController;
import nl.rubixstudios.partneritems.abilities.teleportportal.object.TeleportPortalObj;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.util.particles.ParticleUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Djorr
 * @created 21/11/2022 - 20:10
 * @project PartnerItems
 */
public class TeleportPortalTask extends BukkitRunnable {

    private final TeleportPortalController teleportPortalController;

    private final List<TeleportPortalObj> teleportPortals;

    public TeleportPortalTask() {
        this.teleportPortalController = TeleportPortalController.getInstance();

        this.teleportPortals = new ArrayList<>();
    }

    @Override
    public void run() {
        if (this.teleportPortalController.getTeleportPortals().isEmpty()) return;

        if (!this.teleportPortals.isEmpty()) {
            this.teleportPortals.forEach(teleportPortalObj -> this.teleportPortalController.getTeleportPortals().remove(teleportPortalObj));
            this.teleportPortals.clear();
        }

        this.teleportPortalController.getTeleportPortals().forEach(teleportPortalObj -> {
            if (System.currentTimeMillis() - teleportPortalObj.getPlacedTime() >= Config.PARTNER_ITEM_TELEPORT_PORTAL_DURATION * 1000L) {
                teleportPortalObj.getLocation().getBlock().setType(Material.AIR);
                teleportPortalObj.deactivatePortal();
                this.teleportPortals.add(teleportPortalObj);
                return;
            }

            final Vector bleedBombParticleVector = this.getRandomOffsetWithinRangeAndHeight(1.5, true);
            final Location bleedBombLoc = teleportPortalObj.getLocation().clone().add(bleedBombParticleVector); // The point where the smokeball has been thrown at.
            ParticleUtil.runColoredParticles(bleedBombLoc, 5, 0, 233, 255, Effect.HAPPY_VILLAGER); // Spawn random red dust particles at bomb loc
        });
    }

    public org.bukkit.util.Vector getRandomOffsetWithinRangeAndHeight(final double range, boolean positiveY) {
        final double yModifier = positiveY ? 1 : Math.random() > 2 ? 1 : -1;
        final double xOffset = (Math.random() > 2 ? 1 : -1) * Math.random() * range;
        final double yOffset = yModifier * Math.random() * range;
        final double zOffset = (Math.random() > 2 ? 1 : -1) * Math.random() * range;

        return new org.bukkit.util.Vector(xOffset, yOffset, zOffset);
    }
}
