package nl.rubixstudios.partneritems.abilities.paralyze;

import lombok.Getter;
import me.qiooip.lazarus.factions.FactionsManager;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.ParalyzedHoeTimer;
import nl.rubixstudios.partneritems.timer.timers.SpecialTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.check.BigCheckUtil;
import nl.rubixstudios.partneritems.util.item.ItemUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Djorr
 * @created 28/11/2022 - 18:30
 * @project PartnerItems
 */
public class ParalyzeHoeController implements Listener {

    @Getter private static ParalyzeHoeController instance;

    public ParalyzeHoeController() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    public void disable() {

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();

        if (!(event.getDamager() instanceof Player)) return;
        final Player damager = (Player) event.getDamager();

        final ItemStack itemInHand = damager.getItemInHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;
        if (!NBTUtils.hasItemData(itemInHand, "paralyzeHoe")) return;
        if (BigCheckUtil.cantUseHere(player, "paralyze")) return;
        if (FactionsManager.getInstance().getPlayerFaction(player) == FactionsManager.getInstance().getPlayerFaction(damager)) return;

        final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
        if (specialTimer.isActive(damager)) {
            damager.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(player) + "s&7."));
            return;
        }

        if (specialTimer.isActive(player)) {
            damager.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &cThis player is already paralyzed!"));
            return;
        }

        final ParalyzedHoeTimer paralyzedHoeTimer = TimeController.getInstance().getParalyzedHoeTimer();
        if (paralyzedHoeTimer.isActive(damager)) {
            damager.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou can't use this ability for another &l" + paralyzedHoeTimer.getTimeLeft(damager) + "&cs!"));
            return;
        }

        paralyzedHoeTimer.activate(damager, Config.PARTNER_ITEM_PARALYZED_HOE_COOLDOWN);

        if (NBTUtils.hasItemData(itemInHand, "paralyzeHoeUsages")) {
            int usages = NBTUtils.getItemDataInt(itemInHand, "paralyzeHoeUsages") - 1;

            if (usages == 0) {
                ItemUtil.removeOneItem(player);
                return;
            }

            NBTUtils.setItemDataInt(itemInHand, "paralyzeHoeUsages", usages);

            itemInHand.setItemMeta(ItemUtil.getNewItemUsagesMeta(Config.PARTNER_ITEM_PARALYZED_HOE_ITEM_LORE, itemInHand, usages, Config.PARTNER_ITEM_PARALYZED_HOE_MAX_USAGES));
            player.updateInventory();
        }

        specialTimer.activate(player, Config.PARTNER_ITEM_PARALYZED_HOE_DURATION);

        final Location location = player.getLocation();
        location.setYaw(location.getYaw() + 180);
        location.setPitch(90);
        player.teleport(location);
    }

    public ItemStack getParalyzeHoe(int amount) {
        final ItemStack itemStack = new ItemStack(Material.IRON_HOE, amount);

        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Config.PARTNER_ITEM_PARALYZED_HOE_ITEM_NAME);

        final List<String> lore = new ArrayList<>();
        Config.PARTNER_ITEM_PARALYZED_HOE_ITEM_LORE.forEach(line -> lore.add(line
                .replace("<currentUsages>", "" + Config.PARTNER_ITEM_PARALYZED_HOE_MAX_USAGES)
                .replace("<maxUsages>", "" + Config.PARTNER_ITEM_PARALYZED_HOE_MAX_USAGES)));
        itemMeta.setLore(lore);
        itemMeta.spigot().setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        NBTUtils.setItemDataInt(itemStack, "paralyzeHoe", 0);
        NBTUtils.setItemDataInt(itemStack, "paralyzeHoeUsages", Config.PARTNER_ITEM_PARALYZED_HOE_MAX_USAGES);

        return itemStack;
    }
}
