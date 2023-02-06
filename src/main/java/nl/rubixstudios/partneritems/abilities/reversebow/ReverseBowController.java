package nl.rubixstudios.partneritems.abilities.reversebow;

import lombok.Getter;
import me.qiooip.lazarus.factions.FactionsManager;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.ReverseBowTimer;
import nl.rubixstudios.partneritems.timer.timers.SpecialTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.check.BigCheckUtil;
import nl.rubixstudios.partneritems.util.item.ItemUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import nl.rubixstudios.partneritems.util.particles.particletrailbuilder.ParticleTrailBuilder;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Djorr
 * @created 28/11/2022 - 13:13
 * @project PartnerItems
 */

@Getter
public class ReverseBowController implements Listener {

    @Getter private static ReverseBowController instance;

    public ReverseBowController() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    public void disable() {

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;

        final ItemStack itemInHand = event.getItem();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;
        if (!NBTUtils.hasItemData(itemInHand, "reverseBow")) return;
        if (BigCheckUtil.cantUseHere(player, "reverse")) return;
        event.setCancelled(true);

        final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
        if (specialTimer.isActive(player)) {
            player.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(player) + "s&7."));
            return;
        }

        final ReverseBowTimer reverseBowTimer = TimeController.getInstance().getReverseBowTimer();
        if (reverseBowTimer.isActive(player)) {
            player.sendMessage(Config.PARTNER_ITEM_REVERSE_BOW_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou can't use this ability for another &l" + reverseBowTimer.getTimeLeft(player) + "&cs!"));
            return;
        }

        reverseBowTimer.activate(player, Config.PARTNER_ITEM_REVERSE_BOW_COOLDOWN);

        final Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1.5D)), player.getLocation().getDirection().clone(), 2.0F, 0.0F);
        arrow.setShooter(player);
        arrow.setMetadata("reverseBow", new FixedMetadataValue(PartnerItems.getInstance(), true));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isOnGround() || arrow.isDead()) {
                    this.cancel();
                    return;
                }

                new ParticleTrailBuilder(arrow)
                        .setEffect(Effect.COLOURED_DUST)
                        .setRed(255)
                        .setGreen(0)
                        .setBlue(255)
                        .build();
            }
        }.runTaskTimer(PartnerItems.getInstance(), 1L, 1L);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!NBTUtils.hasItemData(event.getBow(), "reverseBow")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        final Arrow arrow = (Arrow) event.getEntity();

        if (!arrow.hasMetadata("reverseBow")) return;
        arrow.remove();
    }

    @EventHandler
    public void onDamageByArrow(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) return;
        final Arrow arrow = (Arrow) event.getDamager();

        if (!(arrow.getShooter() instanceof Player)) return;
        final Player shooter = (Player) arrow.getShooter();

        if (!(event.getEntity() instanceof Player)) return;
        final Player victim = (Player) event.getEntity();

        if (!arrow.hasMetadata("reverseBow")) return;
        if (FactionsManager.getInstance().getPlayerFaction(victim) == FactionsManager.getInstance().getPlayerFaction(shooter)) return;


        if (arrow.getShooter() == victim) {
            final ReverseBowTimer reverseBowTimer = TimeController.getInstance().getReverseBowTimer();
            if (reverseBowTimer.isActive(victim)) {
                reverseBowTimer.cancel(victim);
                victim.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_REVERSE_BOW_DISPLAY_NAME + " &8» &cCanceled, because arrow hit yourself!"));
                return;
            }
            return;
        }


        if (NBTUtils.hasItemData(shooter.getItemInHand(), "reverseBowUsages")) {
            int usages = NBTUtils.getItemDataInt(shooter.getItemInHand(), "reverseBowUsages") - 1;

            if (usages == 0) {
                ItemUtil.removeOneItem(shooter);
                return;
            }

            NBTUtils.setItemDataInt(shooter.getItemInHand(), "reverseBowUsages", usages);

            shooter.getItemInHand().setItemMeta(ItemUtil.getNewItemUsagesMeta(Config.PARTNER_ITEM_REVERSE_BOW_ITEM_LORE, shooter.getItemInHand(), usages, Config.PARTNER_ITEM_REVERSE_BOW_MAX_USAGES));
            shooter.updateInventory();
        }

        final Location location = victim.getLocation();
        location.setYaw(location.getYaw() + 180);
        victim.teleport(location);
        victim.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_REVERSE_BOW_DISPLAY_NAME + " &8» &cYou have been reversed&c!"));

        victim.playSound(victim.getLocation(), Sound.CAT_HISS, 0.3F, 0.3F);
    }

    public ItemStack getReverseBow(int amount) {
        final ItemStack reverseBow = new ItemStack(Material.BOW, amount);

        final ItemMeta reverseBowMeta = reverseBow.getItemMeta();
        reverseBowMeta.setDisplayName(Config.PARTNER_ITEM_REVERSE_BOW_ITEM_NAME);

        final List<String> lore = new ArrayList<>();
        Config.PARTNER_ITEM_REVERSE_BOW_ITEM_LORE.forEach(line -> lore.add(line
                .replace("<currentUsages>", "" + Config.PARTNER_ITEM_REVERSE_BOW_MAX_USAGES)
                .replace("<maxUsages>", "" + Config.PARTNER_ITEM_REVERSE_BOW_MAX_USAGES)));
        reverseBowMeta.setLore(lore);

        reverseBowMeta.spigot().setUnbreakable(true);
        reverseBow.setItemMeta(reverseBowMeta);

        NBTUtils.setItemDataInt(reverseBow, "reverseBow", 0);
        NBTUtils.setItemDataInt(reverseBow, "reverseBowUsages", Config.PARTNER_ITEM_REVERSE_BOW_MAX_USAGES);

        return reverseBow;
    }
}
