package nl.rubixstudios.partneritems.abilities.bleed;

import lombok.Getter;
import me.qiooip.lazarus.factions.Faction;
import me.qiooip.lazarus.factions.FactionsManager;
import me.qiooip.lazarus.factions.claim.ClaimManager;
import me.qiooip.lazarus.factions.type.KothFaction;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import me.qiooip.lazarus.utils.Tasks;
import me.qiooip.lazarus.utils.item.ItemUtils;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.abilities.bleed.event.BleedActivatedEvent;
import nl.rubixstudios.partneritems.abilities.bleed.event.BleedStopEvent;
import nl.rubixstudios.partneritems.abilities.bleed.event.BombCooldownTimerEvent;
import nl.rubixstudios.partneritems.abilities.bleed.object.BleedBombObject;
import nl.rubixstudios.partneritems.abilities.bleed.task.BleedTask;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.BleedBombTimer;
import nl.rubixstudios.partneritems.timer.timers.SpecialTimer;
import nl.rubixstudios.partneritems.timer.timers.TearOfBloodTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.PlayerUtil;
import nl.rubixstudios.partneritems.util.check.BigCheckUtil;
import nl.rubixstudios.partneritems.util.item.ItemUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import nl.rubixstudios.partneritems.util.particles.ParticleUtil;
import nl.rubixstudios.partneritems.util.particles.particletrailbuilder.ParticleColor;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author Djorr
 * @created 20/11/2022 - 01:48
 * @project PartnerItems
 */

@Getter
public class BleedController implements Listener {

    @Getter private static BleedController instance;

    public final Map<UUID, Long> tearOfBloodMap;

    private final @Getter List<BleedBombObject> removalCache;
    public List<BleedBombObject> bleedBombs;

    private final BukkitRunnable bleedTask;

    public BleedController() {
        instance = this;

        this.tearOfBloodMap = new HashMap<>();

        this.bleedBombs = new ArrayList<>();
        this.removalCache = new ArrayList<>();

        this.bleedTask = new BleedTask();
        this.bleedTask.runTaskTimer(PartnerItems.getInstance(), 0L, 20L);

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    public void disable() {
        this.bleedTask.cancel();

        if (!this.bleedBombs.isEmpty()) {
            this.bleedBombs.forEach(bleedBombObject -> bleedBombObject.setActive(false));
            PartnerItems.getInstance().log("- &7Succesfully cleared &a" + this.bleedBombs.size() + " &7bleedbombs.");

            this.bleedBombs.clear();
        }

        if (!this.tearOfBloodMap.isEmpty()) {
            PartnerItems.getInstance().log("- &7Succesfully cleared &a" + this.tearOfBloodMap.size() + " &7tear of bloods.");
            this.tearOfBloodMap.clear();
        }
    }

    // Tear Of Blood

    @EventHandler
    private void onTryToEat(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null) return;
        if (!itemInHand.getType().equals(Material.SPIDER_EYE)) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!NBTUtils.hasItemData(itemInHand, "tearOfBlood")) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onTryToEat(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null) return;
        if (!itemInHand.getType().equals(Material.SPIDER_EYE)) return;
        if (!NBTUtils.hasItemData(itemInHand, "tearOfBlood")) return;

        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void OnHit(EntityDamageByEntityEvent event){
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;


        final Player victim = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();
        final UUID victimUUID = victim.getUniqueId();
        double damage = event.getDamage();

        final long currentTime = System.currentTimeMillis();

        if (this.tearOfBloodMap.containsKey(victimUUID)){
            final double newDamage = calculateExtraDamage(victim, event.getDamage(), Config.PARTNER_ITEM_TEAR_OF_BLOOD_DAMAGE);
            event.setDamage(newDamage);
        } else if (NBTUtils.hasItemData(damager.getItemInHand(), "tearOfBlood")) {
            if (BigCheckUtil.cantUseHere(damager, "tearOfBlood")) return;

            final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(victim);
            if (playerFaction == null) return;

            if (this.isAtKoth(victim)) return;
            if (this.arePlayersTeamMates(victim, damager)) return;

            final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
            if (specialTimer.isActive(damager)) {
                damager.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(damager) + "s&7."));
                return;
            }

            final TimeController timerManager = TimeController.getInstance();

            final TearOfBloodTimer tearOfBloodTimer = timerManager.getTearOfBloodTimer();
            if (tearOfBloodTimer.isActive(damager)) {
                damager.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TEAR_OF_BLOOD_DISPLAY_NAME + " &8» ") + ColorUtil.translate("&cYou are on a cooldown for &l" + tearOfBloodTimer.getTimeLeft(damager) + "&cs!"));
                return;
            }

            timerManager.getTearOfBloodTimer().activate(damager, Config.PARTNER_ITEM_TEAR_OF_BLOOD_COOLDOWN);

            ItemUtil.removeOneItem(damager);

            final BleedActivatedEvent bleedActivatedEvent = new BleedActivatedEvent(damager, false);
            Bukkit.getPluginManager().callEvent(bleedActivatedEvent);

            Tasks.syncLater(() -> {
                final BleedStopEvent bleedStopEvent = new BleedStopEvent(damager);
                Bukkit.getPluginManager().callEvent(bleedStopEvent);
            }, Config.PARTNER_ITEM_TEAR_OF_BLOOD_DURATION * 20L);

            this.tearOfBloodMap.put(victimUUID, currentTime);

            final double newDamage = calculateExtraDamage(victim, damage, Config.PARTNER_ITEM_TEAR_OF_BLOOD_COOLDOWN);
            event.setDamage(newDamage);
        }
    }

    public boolean isAtKoth(Player player){
        final Faction factionAt = ClaimManager.getInstance().getFactionAt(player.getLocation());
        if (factionAt == null) return false;
        return factionAt instanceof KothFaction;
    }

    public boolean arePlayersTeamMates(Player player1, Player player2){
        final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(player1);
        final PlayerFaction playerFaction2 = FactionsManager.getInstance().getPlayerFaction(player2);
        if (playerFaction == null || playerFaction2 == null) return false;

        return playerFaction.equals(playerFaction2);
    }

    public double calculateExtraDamage(final Player victim, final double damage, final double percentage) {
        ParticleUtil.runColoredParticlesCubeAtPlayer(victim, 10, 145, 0, 0, Effect.COLOURED_DUST);

        return percentage * damage;
    }

    public ItemStack getTearOfBlood(int amount) {
        final ItemStack item = new ItemStack(Material.SPIDER_EYE, amount);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Config.PARTNER_ITEM_TEAR_OF_BLOOD_ITEM_NAME);
        if (Config.PARTNER_ITEM_TEAR_OF_BLOOD_ITEM_ENCHANTED_GLOW) meta.addEnchant(ItemUtils.FAKE_GLOW, 2, true);

        final List<String> lore = new ArrayList<>();
        Config.PARTNER_ITEM_TEAR_OF_BLOOD_ITEM_LORE.forEach(line -> lore.add(line.replace("<duration>", "" + Config.PARTNER_ITEM_TEAR_OF_BLOOD_DURATION)));
        meta.setLore(lore);
        item.setItemMeta(meta);

        NBTUtils.setItemDataInt(item, "tearOfBlood", 0);

        return item;
    }

    // Bleed Bomb

    /**
     * Removes the bleed Bomb
     * @param bleedBombObject the Smoke Bomb
     */
    public void removeBleedBomb(final BleedBombObject bleedBombObject) {
        bleedBombObject.getItem().remove();
        removalCache.add(bleedBombObject);
    }

    public void spawnParticlesAtEachBleedBomb(final BleedBombObject bleedBombObject) {
        final int range = bleedBombObject.getBleedRange(); // Range of spawning smokes from the point the smokebomb lands
        this.spawnRandomParticlesInsideBleedArea(bleedBombObject, range);

        this.spawnParticlesOnBleedBombDroppedLocation(bleedBombObject);
    }

    /**
     * Spawn random particles inside the smoke area
     * @param bleedBombObject the Smoke Bomb
     * @param range the range
     */
    public void spawnRandomParticlesInsideBleedArea(final BleedBombObject bleedBombObject, int range) {
        final org.bukkit.util.Vector pointVector = this.getRandomOffsetWithinRangeAndHeight(range, true);
        final Location randomPointInRangeFromImpactPosition = bleedBombObject.getLandLoc().clone().add(pointVector); // Calculate random point for particles
        ParticleUtil.runColoredParticles(randomPointInRangeFromImpactPosition, 5, 255, 0, 0, Effect.COLOURED_DUST); // spawn random red particles inside range
    }


    /**
     * Get random offset within range an height
     * @param range the range
     * @param positiveY true or false
     * @return returns vector
     */
    public org.bukkit.util.Vector getRandomOffsetWithinRangeAndHeight(final double range, boolean positiveY) {
        final double yModifier = positiveY ? 1 : Math.random() > 5 ? 1 : -1;
        final double xOffset = (Math.random() > 5 ? 1 : -1) * Math.random() * range;
        final double yOffset = yModifier * Math.random() * range;
        final double zOffset = (Math.random() > 5 ? 1 : -1) * Math.random() * range;

        return new org.bukkit.util.Vector(xOffset, yOffset, zOffset);
    }

    /**
     * Spawn particles around the Smoke Bomb
     * @param bleedBombObject the smoke bomb
     */
    public void spawnParticlesOnBleedBombDroppedLocation(final BleedBombObject bleedBombObject) {
        final double range = 0.5;

        final Vector bleedBombParticleVector = this.getRandomOffsetWithinRangeAndHeight(range, true);
        final Location bleedBombLoc = bleedBombObject.getLandLoc().clone().add(bleedBombParticleVector); // The point where the smokeball has been thrown at.
        ParticleUtil.runColoredParticles(bleedBombLoc, 5, 255, 0, 0, Effect.COLOURED_DUST); // Spawn random red dust particles at bomb loc
    }

    public boolean isPlayerInRange(final BleedBombObject bleedBombObject, final Player player){
        // Check if player is in range the smoke point
        return (!this.arePlayersTeamMates(bleedBombObject.getShooter(), player) && bleedBombObject.getLandLoc().distance(player.getLocation()) < bleedBombObject.getBleedRange())
                || bleedBombObject.getLandLoc().distance(player.getLocation()) < bleedBombObject.getBleedRange();
    }

    public ItemStack getBleedBomb(int amount) {
        final ItemStack item = new ItemStack(Material.BLAZE_POWDER, amount);

        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Config.PARTNER_ITEM_BLEED_BOMB_DISPLAY_NAME);

        final List<String> lore = new ArrayList<>();
        Config.PARTNER_ITEM_BLEED_BOMB_ITEM_LORE.forEach(line -> lore.add(line
                        .replace("<duration>", "" + Config.PARTNER_ITEM_BLEED_BOMB_DURATION)
                        .replace("<range>", "" + Config.PARTNER_ITEM_BLEED_BOMB_BLEED_RANGE)
        ));
        meta.setLore(lore);

        if (Config.PARTNER_ITEM_BLEED_BOMB_ITEM_ENCHANTED_GLOW) meta.addEnchant(ItemUtils.FAKE_GLOW, 2, true);
        item.setItemMeta(meta);

        NBTUtils.setItemDataInt(item, "bleedBomb", 0);

        return item;
    }

    private BukkitTask checkIfBombIsOnGround(Player dropper, Item bomb) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!bomb.isOnGround()) return;
                if (!hasBombMeta(bomb)) return;

                final BleedBombObject bleedBombObject = new BleedBombObject(dropper, bomb);
                getBleedBombs().add(bleedBombObject);

                cancel();
            }
        }.runTaskTimer(PartnerItems.getInstance(), 0L, 20L);
    }

    public void onBombPickup(PlayerPickupItemEvent event) {
        final Item bomb = event.getItem();
        if (!hasBombMeta(bomb)) return;
        event.setCancelled(true);
    }

    public void onMergeBombOnGround(ItemMergeEvent event) {
        final Item bomb = event.getEntity();
        if (!hasBombMeta(bomb)) return;
        event.setCancelled(true);
    }

    private boolean hasBombMeta(Item Bomb) {
        return Bomb.hasMetadata("bleedBomb") && Bomb.getMetadata("bleedBomb").get(0).asBoolean(); // Check if the fireball has the meta above
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemInHand = player.getItemInHand();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        if (itemInHand.getType() == null) return;
        if (!NBTUtils.hasItemData(itemInHand, "bleedBomb")) return;
        if (BigCheckUtil.cantUseHere(player, "bleedbomb")) return;
        event.setCancelled(true);

        final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
        if (specialTimer.isActive(player)) {
            player.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(player) + "s&7."));
            return;
        }

        final BleedBombTimer bleedBombTimer = TimeController.getInstance().getBleedBombTimer();
        if (TimeController.getInstance().getBleedBombTimer().isActive(player)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_BLEED_BOMB_DISPLAY_NAME + " &8» ") + ColorUtil.translate("&cYou are on a cooldown for &l" + bleedBombTimer.getTimeLeft(player) + "&cs!"));
            event.setCancelled(true);
            return;
        }

        final BombCooldownTimerEvent bombCooldownTimerEvent = new BombCooldownTimerEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(bombCooldownTimerEvent);
        if (bombCooldownTimerEvent.isCancelled()) return;

        ItemUtil.removeOneItem(event.getPlayer());
        bleedBombTimer.activate(event.getPlayer(), Config.PARTNER_ITEM_BLEED_BOMB_COOLDOWN);

        final ItemStack bombItem = this.getBleedBomb(1);
        final Item bomb = Bukkit.getWorld(player.getWorld().getName()).dropItemNaturally(player.getEyeLocation(), bombItem); // Drop smokeBomb

        final Vector vector = player.getLocation().getDirection(); // Give an direction for shooting the fireball.
        bomb.setVelocity(vector);
        bomb.setMetadata("bleedBomb", new FixedMetadataValue(PartnerItems.getInstance(), true)); // Add metadata to bomb to reconize is easier
        bomb.setMetadata("customItemDrop", new FixedMetadataValue(PartnerItems.getInstance(), true)); // Add metadata to bomb to reconize is easier

        this.checkIfBombIsOnGround(player, bomb);

        PlayerUtil.getPlayersInRangeOfLocation(bomb.getLocation(), 10).forEach(playerInRange -> {
            if (playerInRange == player) return;

            playerInRange.playSound(bomb.getLocation(), Sound.PORTAL, 0.2F, 0.2F);
        });
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        onBombPickup(event);
    }

    @EventHandler
    public void onPickUp(ItemMergeEvent event) {
        onMergeBombOnGround(event);
    }

    // General Events

    @EventHandler
    private void onActivated(BleedActivatedEvent event) {
        final Player damagedPlayer = event.getDamagedPlayer();
        if (damagedPlayer == null) return;

        ParticleUtil.runColoredParticlesCubeAtPlayer(damagedPlayer, 10, 145, 0, 0, Effect.COLOURED_DUST);
        damagedPlayer.playSound(damagedPlayer.getLocation(), Sound.CAT_HISS, 1f, 1f);

        if (event.isOnlyFaction()) {
            getPlayersInFactionOf(damagedPlayer).forEach(player -> {
                if (player == damagedPlayer) return;

                player.sendMessage(ColorUtil.translate(""));
                player.sendMessage(ColorUtil.translate("&4&lBleed &8» &4Bleed damage &chas been &aactivated&c!"));
                player.sendMessage(ColorUtil.translate("&4&lBleed &8» &cYour doing &4extra damage &cnow!"));
                player.sendMessage(ColorUtil.translate(""));
            });
            return;
        }

        damagedPlayer.sendMessage(ColorUtil.translate(""));
        damagedPlayer.sendMessage(ColorUtil.translate("&4&lBleed &8» &4Bleed damage &chas been &aactivated&c!"));
        damagedPlayer.sendMessage(ColorUtil.translate("&4&lBleed &8» &cYour doing &4extra damage &cnow!"));
        damagedPlayer.sendMessage(ColorUtil.translate(""));
    }

    public List<Player> getPlayersInFactionOf(Player player) {
        final List<Player> players = new ArrayList<>();
        final PlayerFaction faction = FactionsManager.getInstance().getPlayerFaction(player);

        if (faction == null) return players;

        final List<Player> playersInDistanceOfPlayer = this.getPlayersInDistanceOf(player, 5);

        for (Player onlinePlayer : playersInDistanceOfPlayer) {
            if (FactionsManager.getInstance().getPlayerFaction(onlinePlayer) == null) continue;
            if (!FactionsManager.getInstance().getPlayerFaction(onlinePlayer).getId().equals(faction.getId())) continue;

            players.add(onlinePlayer);
        }

        return players;
    }

    public List<Player> getPlayersInDistanceOf(Player player, int range) {
        final List<Player> players = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == null) continue;
            if (onlinePlayer.getLocation().distance(player.getLocation()) > range) continue;

            players.add(onlinePlayer);
        }

        return players;
    }

    @EventHandler
    private void onStop(BleedStopEvent event) {
        final Player damagedPlayer = event.getDamagedPlayer();
        if(damagedPlayer == null) return;

        damagedPlayer.sendMessage(ColorUtil.translate(""));
        damagedPlayer.sendMessage(ColorUtil.translate("&4&lBleed &8» &4Bleed damage &chas been &4deactivated&c!"));
        damagedPlayer.sendMessage(ColorUtil.translate(""));
    }


}
