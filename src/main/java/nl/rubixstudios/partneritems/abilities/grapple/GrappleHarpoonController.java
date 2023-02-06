package nl.rubixstudios.partneritems.abilities.grapple;

import lombok.Getter;
import me.qiooip.lazarus.utils.item.ItemUtils;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.GrappleTimer;
import nl.rubixstudios.partneritems.timer.timers.SpecialTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.check.BigCheckUtil;
import nl.rubixstudios.partneritems.util.math.MathUtil;
import nl.rubixstudios.partneritems.util.item.ItemUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import nl.rubixstudios.partneritems.util.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Djorr
 * @created 20/11/2022 - 01:48
 * @project PartnerItems
 */

@Getter
public class GrappleHarpoonController implements Listener {

    @Getter private static GrappleHarpoonController instance;

    public GrappleHarpoonController() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    public void disable() {

    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof Arrow)) return;
        final Arrow arrow = (Arrow) event.getProjectile();

        if (!NBTUtils.hasItemData(event.getBow(), "grappleHarpoon")) return;
        final Player player = (Player) arrow.getShooter();
        if (BigCheckUtil.cantUseHere(player, "grapple")) return;

        final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
        if (specialTimer.isActive(player)) {
            player.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(player) + "s&7."));
            event.setCancelled(true);
            return;
        }

        if (NBTUtils.hasItemData(event.getBow(), "grappleUsages")) {
            if (NBTUtils.getItemDataInt(event.getBow(), "grappleUsages") == 0) {
                ItemUtil.removeOneItem(player);
                event.setCancelled(true);
                return;
            }
        }

        final GrappleTimer grappleTimer = TimeController.getInstance().getGrappleTimer();
        if (grappleTimer.isActive(player)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_GRAPPLE_HARPOON_DISPLAY_NAME + " &8» ") + ColorUtil.translate("&cYou are on a cooldown for &l<time>&cs!"
                    .replace("<time>", grappleTimer.getTimeLeft(player))));
            event.setCancelled(true);
            return;
        }

        grappleTimer.activate(player, Config.PARTNER_ITEM_GRAPPLE_HARPOON_COOLDOWN);

        arrow.setShooter(player);
        arrow.setMetadata("grappleHarpoon", new FixedMetadataValue(PartnerItems.getInstance(), true));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isOnGround() || arrow.isDead()) {
                    this.cancel();
                    return;
                }

                ParticleEffect.FIREWORKS_SPARK.display(0.5f, 0.5f, 0.5f, 0.1f, 2, arrow.getLocation(), 100);
            }
        }.runTaskTimer(PartnerItems.getInstance(), 1L, 1L);
    }

    private void cancelGrappleFire(Player player) {
        final GrappleTimer grappleTimer = TimeController.getInstance().getGrappleTimer();
        if (grappleTimer.isActive(player)) {
            grappleTimer.cancel(player);
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_GRAPPLE_HARPOON_DISPLAY_NAME + " &8» ") + ColorUtil.translate("&cCanceled, cause arrow is out of range!"));
        }
    }

    private ItemStack getItemStackFromPlayerInventoryWithUuid(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType() == Material.AIR) continue;

            if (NBTUtils.hasItemData(itemStack, "grappleHarpoonId")) {
                return itemStack;
            }
        }

        return null;
    }
    
    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();

        if (player.hasMetadata("grappleHarpoon")) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                player.removeMetadata("grappleHarpoon", PartnerItems.getInstance());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        final Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof Player)) return;
        if (arrow.isDead()) return;

        final Player shooter = (Player) arrow.getShooter();
        if (!arrow.hasMetadata("grappleHarpoon")) return;

        if (arrow.getLocation().distance(shooter.getLocation()) >= Config.PARTNER_ITEM_GRAPPLE_HARPOON_MAX_DISTANCE) {
            this.cancelGrappleFire(shooter);
            return;
        }

        shooter.setMetadata("grappleHarpoon", new FixedMetadataValue(PartnerItems.getInstance(), true));

        if (NBTUtils.hasItemData(shooter.getItemInHand(), "grappleUsages")) {
            int usages = NBTUtils.getItemDataInt(shooter.getItemInHand(), "grappleUsages") - 1;

            if (usages == 0) {
                ItemUtil.removeOneItem(shooter);
                return;
            }

            final ItemStack itemInHand = shooter.getItemInHand();
            NBTUtils.setItemDataInt(itemInHand, "grappleUsages", usages);

            itemInHand.setItemMeta(ItemUtil.getNewItemUsagesMeta(Config.PARTNER_ITEM_GRAPPLE_HARPOON_ITEM_LORE, shooter.getItemInHand(), usages, Config.PARTNER_ITEM_GRAPPLE_HARPOON_MAX_USAGES));
            shooter.updateInventory();
        }

        MathUtil.pullPlayerTowardLocation(shooter, arrow.getLocation(), 1.0);
        arrow.remove();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata("grappleHarpoon")) {
            player.removeMetadata("grappleHarpoon", PartnerItems.getInstance());
        }
    }

    public ItemStack getGrappleHarpoon(int amount) {
        final ItemStack item = new ItemStack(Material.BOW, amount);

        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Config.PARTNER_ITEM_GRAPPLE_HARPOON_ITEM_NAME);
        if (Config.PARTNER_ITEM_GRAPPLE_HARPOON_ITEM_ENCHANTED_GLOW) meta.addEnchant(ItemUtils.FAKE_GLOW, 2, true);


        final List<String> lore = new ArrayList<>();
        Config.PARTNER_ITEM_GRAPPLE_HARPOON_ITEM_LORE.forEach(line -> lore.add(line
                .replace("<currentUsages>", "" + Config.PARTNER_ITEM_GRAPPLE_HARPOON_MAX_USAGES)
                .replace("<maxUsages>", "" + Config.PARTNER_ITEM_GRAPPLE_HARPOON_MAX_USAGES)));

        meta.setLore(lore);
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);

        NBTUtils.setItemDataInt(item, "grappleHarpoon", 0);
        NBTUtils.setItemDataString(item, "grappleHarpoonId", UUID.randomUUID().toString());
        NBTUtils.setItemDataInt(item, "grappleUsages", Config.PARTNER_ITEM_GRAPPLE_HARPOON_MAX_USAGES);

        return item;
    }
}
