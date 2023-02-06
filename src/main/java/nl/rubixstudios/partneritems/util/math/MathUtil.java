package nl.rubixstudios.partneritems.util.math;

import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.util.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MathUtil {

    public static void pullPlayerTowardLocation(Player player, Location loc, double multiply){
        Location entityLoc = player.getLocation();

        Vector boost = player.getVelocity();
        boost.setY(0.3);
        player.setVelocity(boost);

        player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 0.2F, 0.2F);

        Bukkit.getScheduler().scheduleSyncDelayedTask(PartnerItems.getInstance(), () -> {
            double g = -0.08;
            double t = loc.distance(entityLoc);
            double v_x = (1.0+0.07*t) * (loc.getX()-entityLoc.getX())/t;
            double v_y = (1.0+0.03*t) * (loc.getY()-entityLoc.getY())/t -0.5*g*t;
            double v_z = (1.0+0.07*t) * (loc.getZ()-entityLoc.getZ())/t;

            Vector v = player.getVelocity();
            v.setX(v_x);
            v.setY(v_y);
            v.setZ(v_z);
            v.multiply(multiply);
            player.setVelocity(v);
        }, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnGround()) {
                    this.cancel();
                    return;
                }

                ParticleEffect.CLOUD.display(0.5f, 0.5f, 0.5f, 0.1f, 2, player.getLocation(), 100);
                player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 0.2f, 0.2f);
            }
        }.runTaskTimer(PartnerItems.getInstance(), 1L, 1L);
    }
}