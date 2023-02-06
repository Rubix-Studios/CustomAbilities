package nl.rubixstudios.partneritems.command;

import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.abilities.bleed.BleedController;
import nl.rubixstudios.partneritems.abilities.forcefield.ForceFieldController;
import nl.rubixstudios.partneritems.abilities.grapple.GrappleHarpoonController;
import nl.rubixstudios.partneritems.abilities.paralyze.ParalyzeHoeController;
import nl.rubixstudios.partneritems.abilities.reversebow.ReverseBowController;
import nl.rubixstudios.partneritems.abilities.teleportportal.TeleportPortalController;
import nl.rubixstudios.partneritems.abilities.tntbomber.TntBomberController;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.timer.timers.TNTBomberTimer;
import nl.rubixstudios.partneritems.timer.timers.TeleportPortalTimer;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.item.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Djorr
 * @created 28/11/2022 - 19:25
 * @project PartnerItems
 */
public class PartnerItemsCommand implements CommandExecutor, Listener {

    private final BleedController bleedController;
    private final ForceFieldController forceFieldController;
    private final GrappleHarpoonController grappleHarpoonController;
    private final ParalyzeHoeController paralyzeHoeController;
    private final ReverseBowController reverseBowController;
    private final TeleportPortalController teleportPortalController;
    private final TntBomberController tntBomberController;

    public PartnerItemsCommand() {
        this.bleedController = BleedController.getInstance();
        this.forceFieldController = ForceFieldController.getInstance();
        this.grappleHarpoonController = GrappleHarpoonController.getInstance();
        this.paralyzeHoeController = ParalyzeHoeController.getInstance();
        this.reverseBowController = ReverseBowController.getInstance();
        this.teleportPortalController = TeleportPortalController.getInstance();
        this.tntBomberController = TntBomberController.getInstance();

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ColorUtil.translate("&cOnly players can execute this command!"));
            return true;
        }

        if (!commandSender.hasPermission(Config.PARTNER_ITEM_PERMISSION)) {
            commandSender.sendMessage(ColorUtil.translate("&8&m---&f&m------------------------------------&8&m---"));
            commandSender.sendMessage(ColorUtil.translate(" &dPartnerItems &e&l" + PartnerItems.getInstance().getDescription().getVersion()));
            commandSender.sendMessage(ColorUtil.translate(" &fMade by: &bDjorr"));
            commandSender.sendMessage(ColorUtil.translate(" &fDiscord: &ehttps://discord.rubixstudios.nl/"));
            commandSender.sendMessage(ColorUtil.translate("&8&m---&f&m------------------------------------&8&m---"));
            return true;
        }

        final Player player = (Player) commandSender;
        player.openInventory(this.getPartnerItemsGui());
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(ColorUtil.translate("&dPartnerItems"))) return;

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        if (!NBTUtils.hasItemData(clickedItem, "partnerItemPreview")) return;
        event.setCancelled(true);

        this.giveClickedItemToPlayer(player, clickedItem);
    }

    private void giveClickedItemToPlayer(Player player, ItemStack clickedItem) {
        if (NBTUtils.hasItemData(clickedItem, "bleedBomb")) {
            player.getInventory().addItem(this.bleedController.getBleedBomb(1));
        } else if (NBTUtils.hasItemData(clickedItem, "tearOfBlood")) {
            player.getInventory().addItem(this.bleedController.getTearOfBlood(1));
        } else if (NBTUtils.hasItemData(clickedItem, "forceField")) {
            player.getInventory().addItem(this.forceFieldController.getForceFieldItem(1));
        } else if (NBTUtils.hasItemData(clickedItem, "grappleHarpoon")) {
            player.getInventory().addItem(this.grappleHarpoonController.getGrappleHarpoon(1));
        } else if (NBTUtils.hasItemData(clickedItem, "paralyzeHoe")) {
            player.getInventory().addItem(this.paralyzeHoeController.getParalyzeHoe(1));
        } else if (NBTUtils.hasItemData(clickedItem, "reverseBow")) {
            player.getInventory().addItem(this.reverseBowController.getReverseBow(1));
        } else if (NBTUtils.hasItemData(clickedItem, "teleportPortal")) {
            player.getInventory().addItem(this.teleportPortalController.getTeleportPortal(1));
        } else if (NBTUtils.hasItemData(clickedItem, "tntBomber")) {
            player.getInventory().addItem(this.tntBomberController.getTntBombers(1));
        }

        player.updateInventory();
    }

    private Inventory getPartnerItemsGui() {
        final List<ItemStack> partnerItems = this.getPartnerItems();

        final Inventory partnerItemsGui = Bukkit.createInventory(null, this.getGuiSize(partnerItems.size()), ColorUtil.translate("&dPartnerItems"));
        partnerItems.forEach(partnerItemsGui::addItem);

        return partnerItemsGui;
    }

    private List<ItemStack> getPartnerItems() {
        final List<ItemStack> itemStacks = new ArrayList<>();

        itemStacks.add(this.bleedController.getBleedBomb(1));
        itemStacks.add(this.bleedController.getTearOfBlood(1));
        itemStacks.add(this.forceFieldController.getForceFieldItem(1));
        itemStacks.add(this.grappleHarpoonController.getGrappleHarpoon(1));
        itemStacks.add(this.paralyzeHoeController.getParalyzeHoe(1));
        itemStacks.add(this.reverseBowController.getReverseBow(1));
        itemStacks.add(this.teleportPortalController.getTeleportPortal(1));
        itemStacks.add(this.tntBomberController.getTntBombers(1));

        itemStacks.forEach(itemStack -> NBTUtils.setItemDataInt(itemStack, "partnerItemPreview", 1));

        return itemStacks;
    }

    private int getGuiSize(int size) {
        if (size <= 9) {
            return 9;
        } else if (size <= 18) {
            return 18;
        } else if (size <= 27) {
            return 27;
        } else if (size <= 36) {
            return 36;
        } else if (size <= 45) {
            return 45;
        } else if (size <= 54) {
            return 54;
        } else {
            return 54;
        }
    }
}
