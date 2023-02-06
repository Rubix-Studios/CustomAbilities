package nl.rubixstudios.partneritems.util.particles.particletrailbuilder;

import lombok.Getter;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.util.particles.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ParticleTrailManager {
    private final @Getter List<ParticleTrailObject> arrows;
    private final @Getter BukkitRunnable setupTask;
    private final @Getter List<ParticleTrailObject> removalCache;

    public ParticleTrailManager(){
        this.arrows = new ArrayList<>();
        this.removalCache = new ArrayList<>();
        this.setupTask = setupTasks();
        this.setupTask.runTaskTimer(PartnerItems.getInstance(), 0L, 0L);
    }

    public void disable() {
        this.setupTask.cancel();

        this.arrows.clear();
        this.removalCache.clear();
    }

    private BukkitRunnable setupTasks() {
        final int timeAfterRemoval = 7; // 7 sec

        return new BukkitRunnable() {
            @Override
            public void run() {

                if (!removalCache.isEmpty()) {
                    removalCache.forEach(arrows::remove);
                }

                if (arrows.isEmpty()) return;
                arrows.forEach((p) -> {

                    final Arrow arrow = p.getArrow();
                    if (arrow == null){
                        System.out.println("!!! ARROW IS NULL !!! ParticleTrailManager.java line 41 CustomEnchants --Tokeee");
                        removeParticleTrail(p);
                        return;
                    }

                    final Location loc = arrow.getLocation();
                    ParticleUtil.runColoredParticles(loc, 1, p.getRed(), p.getGreen(), p.getBlue(), p.getEffect());

                    boolean afterTime = p.getTimeThrown() + 1000 * timeAfterRemoval < System.currentTimeMillis();

                    if (arrow.isDead() || !arrow.isValid() || arrow.isOnGround()){ afterTime = true; }
                    if (afterTime) {
                        removeParticleTrail(p);
                    }
                });
            }
        };
    }

    private void removeParticleTrail(final ParticleTrailObject particleTrailObject) {
        removalCache.add(particleTrailObject);
    }
}
