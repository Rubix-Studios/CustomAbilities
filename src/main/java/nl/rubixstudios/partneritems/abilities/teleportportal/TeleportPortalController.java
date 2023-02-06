package nl.rubixstudios.partneritems.abilities.teleportportal;

import lombok.Getter;
import me.qiooip.lazarus.factions.Faction;
import me.qiooip.lazarus.factions.FactionsManager;
import me.qiooip.lazarus.factions.claim.ClaimManager;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.abilities.teleportportal.object.TeleportPortalObj;
import nl.rubixstudios.partneritems.abilities.teleportportal.task.TeleportPortalTask;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.SpecialTimer;
import nl.rubixstudios.partneritems.timer.timers.TeleportPortalTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.PlayerUtil;
import nl.rubixstudios.partneritems.util.check.BigCheckUtil;
import nl.rubixstudios.partneritems.util.item.ItemUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Djorr
 * @created 20/11/2022 - 01:48
 * @project PartnerItems
 */
@Getter
public class TeleportPortalController implements Listener {

    @Getter private static TeleportPortalController instance;

    private final List<TeleportPortalObj> teleportPortals;

    private final BukkitRunnable teleportPortalTask;

    public TeleportPortalController() {
        instance = this;

        this.teleportPortals = new ArrayList<>();

        this.teleportPortalTask = new TeleportPortalTask();
        this.teleportPortalTask.runTaskTimer(PartnerItems.getInstance(), 0L, 20L);

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    public void disable() {
        if (this.teleportPortalTask != null) this.teleportPortalTask.cancel();
        this.disableAllPortals();
    }

    private void disableAllPortals() {
        if (this.teleportPortals.isEmpty()) return;

        this.teleportPortals.forEach(teleportPortalObj -> teleportPortalObj.getLocation().getBlock().setType(Material.AIR));
        PartnerItems.getInstance().log("- &7Succesfully cleared &a" + this.teleportPortals.size() + " &7teleport portals.");

        this.teleportPortals.clear();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            final Block clickedBlock = event.getClickedBlock();
            if (clickedBlock.hasMetadata("teleportPortal")) {
                final TeleportPortalObj existingObject = this.getTeleportPortalByBlockAndFaction(event.getClickedBlock());
                if (existingObject != null) {
                    if (!existingObject.isPlayerInEnemyFaction(event.getPlayer())) return;

                    this.updatePlacedTime(event.getPlayer(), existingObject);
                    return;
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() == null) return;
            if (!NBTUtils.hasItemData(event.getItem(), "teleportPortal")) return;
            final Player player = event.getPlayer();
            if (BigCheckUtil.cantUseHere(player, "teleportportal")) {
                event.setCancelled(true);
                return;
            }

            final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
            if (specialTimer.isActive(player)) {
                player.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(player) + "s&7."));
                event.setCancelled(true);
                return;
            }

            final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(player);
            if (playerFaction == null) {
                player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou need to be in a faction."));
                event.setCancelled(true);
                return;
            }

            if (ClaimManager.getInstance().getFactionAt(event.getClickedBlock().getLocation()) == playerFaction) {
                player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou can only place this item in a enemy claim."));
                event.setCancelled(true);
                return;
            }

            final Block block = event.getClickedBlock();
            if (block == null) {
                player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou can only place this on the ground."));
                event.setCancelled(true);
                return;
            }

            if (block.getLocation().add(0, 1, 0).getBlock().getType() != Material.AIR) {
                player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou can only place this on the ground."));
                event.setCancelled(true);
                return;
            }

            final Faction enemyFaction = ClaimManager.getInstance().getFactionAt(block);
            if (!(enemyFaction instanceof PlayerFaction)) {
                player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou can only place this in an enemy claim."));
                event.setCancelled(true);
                return;
            }

            final PlayerFaction enemyPlayerFaction = FactionsManager.getInstance().getPlayerFactionByUuid(enemyFaction.getId());
            if (enemyPlayerFaction == null || ((PlayerFaction) enemyFaction).isFriendlyFire()) {
                player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cThis is not an enemy claim."));
                event.setCancelled(true);
                return;
            }

            final TeleportPortalTimer teleportPortalTimer = TimeController.getInstance().getTeleportPortalTimer();
            if (teleportPortalTimer.isActive(player)) {
                player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &cYou are on a cooldown for &l" + teleportPortalTimer.getTimeLeft(player) + "&cs!"));
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);

            ItemUtil.removeOneItem(player);

            final TeleportPortalObj teleportPortalObj = new TeleportPortalObj(block.getLocation(), playerFaction, enemyPlayerFaction, player.getUniqueId());
            teleportPortalObj.activatePortal();
            this.teleportPortals.add(teleportPortalObj);

            teleportPortalTimer.activate(player, Config.PARTNER_ITEM_TELEPORT_PORTAL_COOLDOWN);

            PlayerUtil.getPlayersInRangeOfLocation(teleportPortalObj.getLocation(), 10).forEach(inRangedPlayer -> {
                if (inRangedPlayer == player) return;

                player.playSound(inRangedPlayer.getLocation(), Sound.ANVIL_LAND, 0.2F, 0.2F);
            });
        }
    }

    public boolean hasEnoughPlayersWithArmorOn(PlayerFaction playerFaction, PlayerFaction enemyFaction) {
        final int enemiesInEnemyBase = (int) enemyFaction.getOnlinePlayers().stream().filter(player -> ClaimManager.getInstance().getFactionAt(player) != null
                        && ClaimManager.getInstance().getFactionAt(player).getId() == enemyFaction.getId()).collect(Collectors.toList())
                .stream().filter(this::hasPlayerFullArmorOn).count();


        final int alliesInEnemybase = (int) playerFaction.getOnlinePlayers().stream().filter(player -> ClaimManager.getInstance().getFactionAt(player) != null
                        && ClaimManager.getInstance().getFactionAt(player).getId() == enemyFaction.getId()).collect(Collectors.toList())
                .stream().filter(this::hasPlayerFullArmorOn).count();

        return alliesInEnemybase < enemiesInEnemyBase + 1;
    }

    private boolean hasPlayerFullArmorOn(Player player) {
        return player.getInventory().getHelmet() != null &&
                player.getInventory().getChestplate() != null &&
                player.getInventory().getLeggings() != null &&
                player.getInventory().getBoots() != null;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block == null) return;
        if (!block.hasMetadata("teleportPortal")) return;
        event.setCancelled(true);
    }


    public void updatePlacedTime(Player player, TeleportPortalObj teleportPortalObj) {
        teleportPortalObj.timesToHitBeforeDestroy--;
        player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &7You must hit this portal &c" + teleportPortalObj.timesToHitBeforeDestroy + " &7more times to destroy it."));

        if (teleportPortalObj.timesToHitBeforeDestroy == 0) {
            teleportPortalObj.deactivatePortal();
            player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &7You have destroyed this portal."));

            final Random random = new Random();
            if (random.nextInt(100) > Config.PARTNER_ITEM_TELEPORT_PORTAL_CHANCE) return;

            player.getInventory().addItem(this.getTeleportPortal(1));
            player.sendMessage(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + ColorUtil.translate(" &8» &7You have received a &bTeleport Portal &7item."));
        }
    }

    private TeleportPortalObj getTeleportPortalByBlockAndFaction(Block block) {
        return this.getTeleportPortals().stream().filter(teleportPortalObj -> teleportPortalObj.getLocation().equals(block.getLocation())).findFirst().orElse(null);
    }

    public TeleportPortalObj getTeleportPortalByFaction(PlayerFaction playerFaction) {
        return this.getTeleportPortals().stream().filter(teleportPortalObj -> teleportPortalObj.getPlayerFaction().getId().equals(playerFaction.getId())).findFirst().orElse(null);
    }

    public ItemStack getTeleportPortal(int amount) {
        final ItemStack itemStack = new ItemStack(Material.BEACON, amount);

        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Config.PARTNER_ITEM_TELEPORT_PORTAL_ITEM_NAME);

        final List<String> lore = new ArrayList<>(Config.PARTNER_ITEM_TELEPORT_PORTAL_ITEM_LORE);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        NBTUtils.setItemDataInt(itemStack, "teleportPortal", 0);

        return itemStack;
    }
}
