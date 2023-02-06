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
import nl.rubixstudios.partneritems.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Djorr
 * @created 19/11/2022 - 15:38
 * @project PartnerItems
 */
public class PartnerItemCommand implements CommandExecutor {

    private final GrappleHarpoonController grappleHarpoonController;
    private final TntBomberController tntBomberController;
    private final BleedController bleedController;
    private final TeleportPortalController teleportPortalController;
    private final ReverseBowController reverseBowController;
    private final ParalyzeHoeController paralyzeHoeController;
    private final ForceFieldController forceFieldController;

    public PartnerItemCommand() {
        this.grappleHarpoonController = GrappleHarpoonController.getInstance();
        this.tntBomberController = TntBomberController.getInstance();
        this.bleedController = BleedController.getInstance();
        this.teleportPortalController = TeleportPortalController.getInstance();
        this.reverseBowController = ReverseBowController.getInstance();
        this.paralyzeHoeController = ParalyzeHoeController.getInstance();
        this.forceFieldController = ForceFieldController.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(Config.PARTNER_ITEM_PERMISSION)) {
            commandSender.sendMessage(ColorUtil.translate("&8&m---&f&m------------------------------------&8&m---"));
            commandSender.sendMessage(ColorUtil.translate(" &dPartnerItems &e&l" + PartnerItems.getInstance().getDescription().getVersion()));
            commandSender.sendMessage(ColorUtil.translate(" &fMade by: &bDjorr"));
            commandSender.sendMessage(ColorUtil.translate(" &fDiscord: &ehttps://discord.rubixstudios.nl/"));
            commandSender.sendMessage(ColorUtil.translate("&8&m---&f&m------------------------------------&8&m---"));
            return true;
        }

        if (args.length == 0) {
            commandSender.sendMessage(ColorUtil.translate("&7&m                                                                                "));
            commandSender.sendMessage(ColorUtil.translate("&dPartnerItems &8- &dCommands"));
            commandSender.sendMessage(ColorUtil.translate(""));
            commandSender.sendMessage(ColorUtil.translate("&8- &e/partneritem list | List all PartnerItems."));
            commandSender.sendMessage(ColorUtil.translate("&8- &e/partneritem give <player> <item> <amount> | Give PartnerItems."));
            commandSender.sendMessage(ColorUtil.translate(""));
            commandSender.sendMessage(ColorUtil.translate("&7&m                                                                                "));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (args.length != 1) {
                commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItemds &8» &cUsage: /partneritem list"));
                return true;
            }

            commandSender.sendMessage(ColorUtil.translate("&7&m                                                                                "));
            commandSender.sendMessage(ColorUtil.translate("&dPartnerItems &8- &dAvailable PartnerItems"));
            commandSender.sendMessage(ColorUtil.translate(""));
            commandSender.sendMessage(ColorUtil.translate("&8- &ableedbomb | Bleed Bomb"));
            commandSender.sendMessage(ColorUtil.translate("&8- &aforcefield | Forcefield"));
            commandSender.sendMessage(ColorUtil.translate("&8- &agrappleharpoon | Grapple Harpoon"));
            commandSender.sendMessage(ColorUtil.translate("&8- &aparalyzehoe | Paralyzed Hoe"));
            commandSender.sendMessage(ColorUtil.translate("&8- &areversebow | Reverse Bow"));
            commandSender.sendMessage(ColorUtil.translate("&8- &ateleportportal | Teleport Portal"));
            commandSender.sendMessage(ColorUtil.translate("&8- &atntbomber | TNT Bomber"));
            commandSender.sendMessage(ColorUtil.translate(""));
            commandSender.sendMessage(ColorUtil.translate("&7&m                                                                                "));
            return true;
        } else if (args[0].equalsIgnoreCase("give")) {
            if (args.length != 4) {
                commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItemds &8» &cUsage: /partneritem give <player> <item> <amount>"));
                return true;
            }

            final Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) return true;

            if (args[2] == null) return true;

            switch (args[2]) {
                case "bleedbomb": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.bleedController.getBleedBomb(amount) != null) {
                        targetPlayer.getInventory().addItem(this.bleedController.getBleedBomb(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_BLEED_BOMB_ITEM_NAME)
                        ));
                    }
                    break;
                }
                case "tearofblood": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.bleedController.getTearOfBlood(amount) != null) {
                        targetPlayer.getInventory().addItem(this.bleedController.getTearOfBlood(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_TEAR_OF_BLOOD_ITEM_NAME)
                        ));
                    }
                    break;
                }
                case "grappleharpoon": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.grappleHarpoonController.getGrappleHarpoon(amount) != null) {
                        targetPlayer.getInventory().addItem(this.grappleHarpoonController.getGrappleHarpoon(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_GRAPPLE_HARPOON_ITEM_NAME)
                        ));
                    }
                    break;
                }
                case "teleportportal": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.teleportPortalController.getTeleportPortal(amount) != null) {
                        targetPlayer.getInventory().addItem(this.teleportPortalController.getTeleportPortal(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_TELEPORT_PORTAL_ITEM_NAME)
                        ));
                    }
                    break;
                }
                case "tntbomber": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.tntBomberController.getTntBombers(amount) != null) {
                        targetPlayer.getInventory().addItem(this.tntBomberController.getTntBombers(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_TNT_BOMBER_ITEM_NAME)
                        ));
                    }
                    break;
                }
                case "reversebow": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.reverseBowController.getReverseBow(amount) != null) {
                        targetPlayer.getInventory().addItem(this.reverseBowController.getReverseBow(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_REVERSE_BOW_DISPLAY_NAME)
                        ));
                    }
                    break;
                }
                case "paralyzehoe": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.paralyzeHoeController.getParalyzeHoe(amount) != null) {
                        targetPlayer.getInventory().addItem(this.paralyzeHoeController.getParalyzeHoe(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_PARALYZED_HOE_ITEM_NAME)
                        ));
                    }
                    break;
                }
                case "forcefield": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.forceFieldController.getForceFieldItem(amount) != null) {
                        targetPlayer.getInventory().addItem(this.forceFieldController.getForceFieldItem(amount));
                        commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                                .replace("<amount>", "" + amount)
                                .replace("<itemName>", Config.PARTNER_ITEM_FORCE_FIELD_ITEM_NAME)
                        ));
                    }
                    break;
                }
                case "all": {
                    final int amount = Integer.parseInt(args[3]);
                    if (args[3] == null) return true;

                    if (this.bleedController.getBleedBomb(amount) != null)
                        targetPlayer.getInventory().addItem(this.bleedController.getBleedBomb(amount));
                    if (this.bleedController.getTearOfBlood(amount) != null)
                        targetPlayer.getInventory().addItem(this.bleedController.getTearOfBlood(amount));
                    if (this.grappleHarpoonController.getGrappleHarpoon(amount) != null)
                        targetPlayer.getInventory().addItem(this.grappleHarpoonController.getGrappleHarpoon(amount));
                    if (this.teleportPortalController.getTeleportPortal(amount) != null)
                        targetPlayer.getInventory().addItem(this.teleportPortalController.getTeleportPortal(amount));
                    if (this.tntBomberController.getTntBombers(amount) != null)
                        targetPlayer.getInventory().addItem(this.tntBomberController.getTntBombers(amount));
                    if (this.reverseBowController.getReverseBow(amount) != null)
                        targetPlayer.getInventory().addItem(this.reverseBowController.getReverseBow(amount));
                    if (this.paralyzeHoeController.getParalyzeHoe(amount) != null)
                        targetPlayer.getInventory().addItem(this.paralyzeHoeController.getParalyzeHoe(amount));
                    if (this.forceFieldController.getForceFieldItem(amount) != null)
                        targetPlayer.getInventory().addItem(this.forceFieldController.getForceFieldItem(amount));

                    commandSender.sendMessage(ColorUtil.translate("&d&lPartnerItems &8» &fYou have received &b<amount>x <itemName>&f."
                            .replace("<amount>", "" + amount)
                            .replace("<itemName>", "ALL")
                    ));
                    break;
                }
            }
        }
        return false;
    }
}
