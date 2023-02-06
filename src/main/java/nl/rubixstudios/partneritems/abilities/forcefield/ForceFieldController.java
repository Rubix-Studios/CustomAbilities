package nl.rubixstudios.partneritems.abilities.forcefield;

import lombok.Getter;
import me.qiooip.lazarus.factions.FactionsManager;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.ForceFieldTimer;
import nl.rubixstudios.partneritems.timer.timers.SpecialTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.check.BigCheckUtil;
import nl.rubixstudios.partneritems.util.item.ItemUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import nl.rubixstudios.partneritems.util.particles.ParticleEffect;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tokeee
 * @created 28/11/2022 - 18:38
 * @project PartnerItems
 */
public class ForceFieldController implements Listener {

    @Getter private static ForceFieldController instance;

    @Getter private final List<ForceField> forceFields = new ArrayList<>();

    @Getter private BukkitRunnable forceFieldRunnable;
    @Getter private final float minimumForceAmplifier = 0.2F;
    @Getter private final float maximumForce = 3;
    @Getter private final float minimumParticleAmplifier = 0.2F;
    @Getter private final float minimumRadiusAmplifier = 0.3f;
    @Getter private final float timeAfterWarmup = 1f;
    @Getter private final float warmupTime = 5f;
    @Getter private final List<Material> raycastFilter = new ArrayList<>();


    public ForceFieldController() {
        instance = this;
        forceFieldRunnable = forceFieldHandler();
        forceFieldRunnable.runTaskTimer(PartnerItems.getInstance(), 0L, 0L);

        initializeRaycastFilter();
        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    // initialize raycastfilter
    private void initializeRaycastFilter() {
        raycastFilter.add(Material.AIR);

        for (int i = 0; i < 8; i++) {
            raycastFilter.add(new ItemStack(Material.getMaterial(44), (byte)i).getType());
        }

        for (int i = 0; i < 5; i++) {
            raycastFilter.add(new ItemStack(Material.getMaterial(126), (byte)i).getType());
        }

        raycastFilter.add(new ItemStack(Material.getMaterial(182), (byte)0).getType());
    }

    public void disable() {

    }

    @Getter private final float particleScaling = 2F;
    @Getter private final short maxParticleAmount = 2;

    private BukkitRunnable forceFieldHandler() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                // make a copy of forcefields list
                List<ForceField> copiedForceField = new ArrayList<>(getForceFields());
                for (ForceField forceField : copiedForceField) {
                    // get player from forcefield
                    final Player player = forceField.getPlayer();

                    // get current progress from forcefield
                    final float warmUpProgress = forceField.getWarmUpProgress();

                    final short maxRadius = forceField.getRadius();

                    float circleRadius = 1;

                    int circlePoints = 20;

                    if (warmUpProgress >= 1){
                        if (!forceField.initializedVelocityTimeStamp()) {
                            forceField.setVelocityStartTimeStamp(System.currentTimeMillis());
                        }

                        final float velocityProgress = forceField.getVelocityProgress();

                        final float normalizedRadius = (velocityProgress * maxRadius) / maxRadius;
                        final double radiusEasing = easeInSine(normalizedRadius) * maxRadius;

                        if (velocityProgress >= 1) {
                            forceFields.remove(forceField);
                        }

                        int easedRadius = (int) Math.round(radiusEasing);

                        circleRadius = easedRadius;

                        circlePoints *= easedRadius;

                        circlePoints += 20;

                        force(forceField, easedRadius);
                    }



                    // get a list of circe locations with player location
                    final List<Location> circleLocations = getCircle(player.getLocation(), circleRadius, circlePoints);

                    final double particleEasing = easeOutQuad(warmUpProgress);
                    // loop through circle locations and spawn white particles
                    for (Location location : circleLocations) {

                        // calculate particleCount and must not be 0
                       int particleCount = (int) Math.round(maxParticleAmount * Math.max(particleEasing, minimumParticleAmplifier));
                       if (particleCount <= 0) particleCount = 1;

                        ParticleEffect.CLOUD.display(0.1F, 0.1F, 0.1F, 0.1F, particleCount, location, forceField.getRadius() + 4);
                    }
                }
            }
        };
    }

    // calculate easeInSine
    private double easeInSine(double x) {
        return 1 - Math.cos((x * Math.PI) / 2);
    }


    //raycast between two locations
    private boolean raycast(Location start, Location end) {
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        for (double i = 0; i < distance; i += 0.1) {
            Location loc = start.clone().add(direction.clone().multiply(i));
            if (!raycastFilter.contains(loc.getBlock().getType())) {
                return false;
            }
        }
        return true;
    }

    private boolean isPlayerAnAlly(ForceField forceField, Entity entity) {
        if (FactionsManager.getInstance().getPlayerFaction(forceField.getPlayer().getUniqueId()) == null) return false;

        final Player player = (Player) entity;
        if (FactionsManager.getInstance().getPlayerFaction(player.getUniqueId()) == null) return false;
        if (!FactionsManager.getInstance().getPlayerFaction(forceField.getPlayer()).getMembers().containsKey(player.getUniqueId())) return false;

        return true;
    }

    private void force(ForceField forceField, int radius) {
        // get nearby entities from force field player with radius

        final List<Entity> nearbyEntities = forceField.getPlayer().getNearbyEntities(radius, radius, radius).stream().filter(entity ->
                entity instanceof Player && !isPlayerAnAlly(forceField, entity)).collect(Collectors.toList());

        // loop through nearby entities
        // calculate direction between player and entity
        // apply extra force to entity based on distance
        // the lower the distance the higher the force
        // the higher the distance the lower the force
        nearbyEntities.forEach(entity -> {
            if (!raycast(forceField.getPlayer().getLocation().clone().add(0,1.8,0), entity.getLocation().clone().add(0,1.8,0))) return;

            // get direction between player and entity
            final Vector direction = entity.getLocation().toVector().clone().subtract(forceField.getPlayer().getLocation().toVector().clone());

            // get distance between player and entity
            final double distance = direction.length();

            // get force based on distance with minimum of minimum force amplifier
            final double forceAmplifier = Math.max(minimumForceAmplifier, 1 - (distance / radius));

            // 8 / 10 = 0.8

            // get normalized direction
            final Vector normalizedDirection = direction.normalize();

            normalizedDirection.setY(Math.max(0.2, normalizedDirection.getY()));

            // maximum force * forceamplifier
            final double force = maximumForce * forceAmplifier;

            final Vector forceVector = clampVector(normalizedDirection.multiply(force));

            // apply force to entity
            entity.setVelocity(forceVector);
        });
    }

    // return a vector clamped between -3.99 and 3.99
    private Vector clampVector(Vector vector) {
        return new Vector(Math.max(-3.99, Math.min(3.99, vector.getX())), Math.max(-3.99, Math.min(3.99, vector.getY())), Math.max(-3.99, Math.min(3.99, vector.getZ())));
    }

    // calculate easeOutQuad from https://easings.net/#easeOutQuad
    public double easeOutQuad(float x) {
       return  1 - (1 - x) * (1 - x);
    }

    // calculate a circle around a player
    public List<Location> getCircle(Location center, double radius, int amount) {
        List<Location> locations = new ArrayList<>();
        double increment = (2 * Math.PI) / amount;
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(center.getWorld(), x, center.getY() + 0.2, z));
        }
        return locations;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack itemInHand = event.getItem();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;
        if (!NBTUtils.hasItemData(itemInHand, "forceField")) return;
        if (BigCheckUtil.cantUseHere(player, "forcefield")) return;

        final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
        if (specialTimer.isActive(player)) {
            player.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(player) + "s&7."));
            event.setCancelled(true);
            return;
        }

        final ForceFieldTimer forceFieldTimer = TimeController.getInstance().getForceFieldTimer();
        if (forceFieldTimer.isActive(player)) {
            player.sendMessage(Config.PARTNER_ITEM_FORCE_FIELD_DISPLAY_NAME + ColorUtil.translate("&cYou can't use this item for another &e" + forceFieldTimer.getTimeLeft(player) + "s&c."));
            return;
        }

        forceFieldTimer.activate(player, Config.PARTNER_ITEM_FORCE_FIELD_COOLDOWN);
        ItemUtil.removeOneItem(player);

        // create forcefield object and use player in constructor then add to list
        final ForceField forceField = new ForceField(player, warmupTime);
        getForceFields().add(forceField);
    }


    public ItemStack getForceFieldItem(int amount) {
        final ItemStack itemStack = new ItemStack(Material.NETHER_STAR, amount);

        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Config.PARTNER_ITEM_FORCE_FIELD_ITEM_NAME);

        final List<String> lore = new ArrayList<>(Config.PARTNER_ITEM_FORCE_FIELD_ITEM_LORE);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        NBTUtils.setItemDataInt(itemStack, "forceField", 1);

        return itemStack;
    }
}
