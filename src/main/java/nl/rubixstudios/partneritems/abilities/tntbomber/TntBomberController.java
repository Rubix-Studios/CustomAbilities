package nl.rubixstudios.partneritems.abilities.tntbomber;

import lombok.Getter;
import me.qiooip.lazarus.factions.Faction;
import me.qiooip.lazarus.factions.FactionsManager;
import me.qiooip.lazarus.factions.claim.ClaimManager;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.abilities.tntbomber.data.BomberData;
import nl.rubixstudios.partneritems.abilities.tntbomber.task.BomberTask;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.FactionClaimPartnerItemTimer;
import nl.rubixstudios.partneritems.timer.timers.SpecialTimer;
import nl.rubixstudios.partneritems.timer.timers.TNTBomberTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.PlayerUtil;
import nl.rubixstudios.partneritems.util.check.BigCheckUtil;
import nl.rubixstudios.partneritems.util.item.ItemUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Djorr
 * @created 19/11/2022 - 14:01
 * @project PartnerItems
 */

@Getter
public class TntBomberController implements Listener {

    @Getter private static TntBomberController instance;

    private final List<BomberData> bomberDatas;

    private final BukkitRunnable bomberTask;

    public TntBomberController() {
        instance = this;

        this.bomberDatas = new ArrayList<>();

        this.bomberTask = new BomberTask();
        this.bomberTask.runTaskTimer(PartnerItems.getInstance(), 0L, 10L);

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    public void disable() {
        if (this.bomberTask != null) this.bomberTask.cancel();
        if (this.bomberDatas.isEmpty()) return;

        this.bomberDatas.forEach(BomberData::placeAllBlocksBack);
        PartnerItems.getInstance().log("- &7Succesfully cleared &a" + this.bomberDatas.size() + " &7tnt bombers.");

        this.bomberDatas.clear();
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!NBTUtils.hasItemData(event.getItemInHand(), "tntBomber")) return;

        final Player player = event.getPlayer();
        if (BigCheckUtil.cantUseHere(player, "bomber")) {
            event.setCancelled(true);
            return;
        }

        final Block block = event.getBlock();
        if (block == null) return;

        final SpecialTimer specialTimer = TimeController.getInstance().getSpecialTimer();
        if (specialTimer.isActive(player)) {
            player.sendMessage(Config.PARTNER_ITEM_PARALYZED_HOE_DISPLAY_NAME + ColorUtil.translate(" &8» &7You are still paralyzed for &c" + specialTimer.getTimeLeft(player) + "s&7."));
            event.setCancelled(true);
            return;
        }

        final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &cYou need to be in a faction to use this item."));
            event.setCancelled(true);
            return;
        }

        final PlayerFaction enemyFaction = FactionsManager.getInstance().getPlayerFactionByUuid(ClaimManager.getInstance().getFactionAt(block.getLocation()).getId());
        if (enemyFaction == null) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &cYou can only use this in a &eenemy &cfaction claim."));
            event.setCancelled(true);
            return;
        }

        if (!hasEnoughPlayersWithArmorOn(playerFaction, enemyFaction)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &cNot &4enough players &care &4available &cin this faction."));
            event.setCancelled(true);
            return;
        }

        if (isPlayerNakedAroundBomber(block.getLocation(), enemyFaction)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &cYou can't bomb a naked player in the faction."));
            event.setCancelled(true);
            return;
        }

        final FactionClaimPartnerItemTimer factionClaimPartnerItemTimer = TimeController.getInstance().getFactionClaimPartnerItemTimer();
        if (factionClaimPartnerItemTimer.isActive(enemyFaction.getId(), "tntbomber")) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &cThe enemy faction is on tnt bomber cooldown."));
            event.setCancelled(true);
            return;
        }

        final TNTBomberTimer tntBomberTimer = TimeController.getInstance().getTntBomberTimer();
        if (this.anyFactionMembersCooldown(playerFaction, tntBomberTimer)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &8» ") + ColorUtil.translate("&cYour faction is on a cooldown for &l" + this.getTimeLeftOfTheFactionMember(playerFaction, tntBomberTimer) + "&cs!"));
            event.setCancelled(true);
            return;
        }

        ItemUtil.removeOneItem(player);

        block.setType(Material.AIR);

        final TNTPrimed tntPrimed = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
        tntPrimed.setFuseTicks(100);
        tntPrimed.setCustomName(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME);
        tntPrimed.setMetadata("tntBomber", new FixedMetadataValue(PartnerItems.getInstance(), true));

        tntBomberTimer.activate(event.getPlayer(), Config.PARTNER_ITEM_TNT_BOMBER_COOLDOWN);
        factionClaimPartnerItemTimer.activate(enemyFaction, Config.PARTNER_ITEM_TNT_BOMBER_COOLDOWN, "tntBomber");

        enemyFaction.getOnlinePlayers().forEach(online -> {
            online.sendMessage(ColorUtil.translate(""));
            online.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &4&l !!!WARNING!!!"));
            online.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &4 Someone has placed a " + Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &4on your claim!"));
            online.sendMessage(ColorUtil.translate(""));

            online.playSound(online.getLocation(), Sound.DOOR_CLOSE, 1f, 1f);
        });
    }

    private boolean isPlayerNakedAroundBomber(Location location, PlayerFaction enemyFaction) {
        return enemyFaction.getOnlinePlayers().stream().anyMatch(player ->
                player.getLocation().distance(location) < Config.PARTNER_ITEM_TNT_BOMBER_RADIUS_NAKED_ENEMIES
                && !this.hasPlayerFullArmorOn(player));
    }

    private boolean anyFactionMembersCooldown(PlayerFaction playerFaction, TNTBomberTimer tntBomberTimer) {
        return playerFaction.getOnlinePlayers().stream().anyMatch(tntBomberTimer::isActive);
    }

    private String getTimeLeftOfTheFactionMember(PlayerFaction playerFaction, TNTBomberTimer tntBomberTimer) {
        return playerFaction.getOnlinePlayers().stream()
                .filter(tntBomberTimer::isActive)
                .findFirst()
                .map(tntBomberTimer::getTimeLeft)
                .orElse("0s");
    }

    private boolean isEnemyFactionClaim(PlayerFaction playerFaction, Location location) {
        final Faction factionClaim = ClaimManager.getInstance().getFactionAt(location);
        if (factionClaim == null) return false;

        final PlayerFaction enemyFaction = FactionsManager.getInstance().getPlayerFactionByUuid(factionClaim.getId());
        if (enemyFaction == null) return false;

        return !playerFaction.getId().equals(enemyFaction.getId());
    }

    private boolean hasEnoughPlayersWithArmorOn(PlayerFaction playerFaction, PlayerFaction enemyFaction) {
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
    private void onBomberTntPrime(ExplosionPrimeEvent event) {
        final Entity entity = event.getEntity();
        if (entity == null) return;
        if (!entity.hasMetadata("tntBomber")) return;

        event.setFire(false);
        event.setRadius(10.0F);
    }

    @EventHandler
    private void onBomberTntExplode(EntityExplodeEvent event) {
        if (event.blockList() == null) return;
        if (!event.getEntity().hasMetadata("tntBomber")) return;

        final TNTPrimed primed = (TNTPrimed) event.getEntity();
        if (primed != null) this.filterAllBlocksBelowTnt(event.blockList(), primed.getLocation().getBlockY());

        final BomberData bomberData = new BomberData();

        for (int i = 0; i < event.blockList().size(); i++) {
            final Block block = event.blockList().get(i);
            block.getDrops().clear();
            bomberData.addNewBlockData(block, bomberData.getEffectedBlocks().size());

            PlayerUtil.getPlayersInRangeOfLocation(block.getLocation(), 10).forEach(player -> {
                if (player == null) return;

                player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0);
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.2F, 0.2F);
            });
        }

        this.bomberDatas.add(bomberData);
    }

    private void filterAllBlocksBelowTnt(List<Block> blocks, int yBlock) {
        final List<Block> copiedBlocks = new ArrayList<>(blocks);

        for (Block block : copiedBlocks) {
            if (block.getLocation().getBlockY() > yBlock) continue;

            blocks.remove(block);
        }
    }

    public ItemStack getTntBombers(int amount){
        final ItemStack itemStack = new ItemStack(Material.TNT, amount);

        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME);

        final List<String> lore = new ArrayList<>();
        Config.PARTNER_ITEM_TNT_BOMBER_ITEM_LORE.forEach(line -> lore.add(line
                .replace("<time>", "" + Config.PARTNER_ITEM_TNT_BOMBER_TIME_BEFORE_PLACE_BACK)
        ));
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        NBTUtils.setItemDataInt(itemStack, "tntBomber", 0);

        return itemStack;
    }
}
