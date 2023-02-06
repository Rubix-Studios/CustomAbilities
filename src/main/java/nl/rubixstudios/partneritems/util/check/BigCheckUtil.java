package nl.rubixstudios.partneritems.util.check;

import me.qiooip.lazarus.Lazarus;
import me.qiooip.lazarus.factions.Faction;
import me.qiooip.lazarus.factions.FactionsManager;
import me.qiooip.lazarus.factions.claim.ClaimManager;
import me.qiooip.lazarus.factions.type.*;
import nl.rubixstudios.partneritems.abilities.bleed.BleedController;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Djorr
 * @created 09/12/2022 - 00:11
 * @project PartnerItems
 */
public class BigCheckUtil {

    public static boolean cantUseHere(Player player, String ability) {
        if (ability.equals("bleedbomb")) {
            if (!BleedController.getInstance().getBleedBombs().isEmpty() && BleedController.getInstance().getBleedBombs().stream().anyMatch(bleedBomb -> bleedBomb.getLandLoc().distance(player.getLocation()) < 5)) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cThere is already an active bleed bomb nearby!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_BLEED_BOMB_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_BLEED_BOMB_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_BLEED_BOMB_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_BLEED_BOMB_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }
        } else if (ability.equals("tearofblood")) {
            if (!BleedController.getInstance().getBleedBombs().isEmpty() && BleedController.getInstance().getBleedBombs().stream().anyMatch(bleedBomb -> bleedBomb.getLandLoc().distance(player.getLocation()) < 5)) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cThere is an active bleed bomb nearby!"));
                return true;
            }

            if (getPlayersInRangeOfByPlayer(player, 5).stream().anyMatch(bleedPlayer -> BleedController.getInstance().getTearOfBloodMap().containsKey(bleedPlayer.getUniqueId()))) {
                final String factionMember = getPlayersInRangeOfByPlayer(player, 5).stream().filter(bleedPlayer -> BleedController.getInstance().getTearOfBloodMap().containsKey(bleedPlayer.getUniqueId())).findFirst().get().getName();
                if (factionMember != null) {
                    player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cThere is already an active tear of blood on &4" + factionMember + "&c!"));
                    return true;
                } else {
                    player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cThere is already an active tear of blood nearby!"));
                    return true;
                }
            }

            if (!Config.PARTNER_ITEM_TEAR_OF_BLOOD_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_TEAR_OF_BLOOD_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_TEAR_OF_BLOOD_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_TEAR_OF_BLOOD_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }

        } else if (ability.equals("forcefield")) {
            if (!Config.PARTNER_ITEM_FORCE_FIELD_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_FORCE_FIELD_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_FORCE_FIELD_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_FORCE_FIELD_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }

        } else if (ability.equals("grapple")) {
            if (!Config.PARTNER_ITEM_GRAPPLE_HARPOON_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_GRAPPLE_HARPOON_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_GRAPPLE_HARPOON_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_GRAPPLE_HARPOON_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }

        } else if (ability.equals("reverse")) {
            if (!Config.PARTNER_ITEM_REVERSE_BOW_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_REVERSE_BOW_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_REVERSE_BOW_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_REVERSE_BOW_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }

        } else if (ability.equals("paralyze")) {
            if (!Config.PARTNER_ITEM_PARALYZED_HOE_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_PARALYZED_HOE_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_PARALYZED_HOE_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_PARALYZED_HOE_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }

        } else if (ability.equals("teleportportal")) {
            if (!Config.PARTNER_ITEM_TELEPORT_PORTAL_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_TELEPORT_PORTAL_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_TELEPORT_PORTAL_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_TELEPORT_PORTAL_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }

        } else if (ability.equals("bomber")) {
            if (!Config.PARTNER_ITEM_TNT_BOMBER_ENABLE_ON_WILDERNESS && ClaimManager.getInstance().getFactionAt(player) instanceof WildernessFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &2Wilderness&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_TNT_BOMBER_ENABLE_ON_WARZONE && ClaimManager.getInstance().getFactionAt(player) instanceof WarzoneFaction) {
                return true;
            }

            if (!Config.PARTNER_ITEM_TNT_BOMBER_ENABLE_ON_KOTH && ClaimManager.getInstance().getFactionAt(player) instanceof KothFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &4KOTH&c!"));
                return true;
            }

            if (!Config.PARTNER_ITEM_TNT_BOMBER_ENABLE_ON_CONQUEST && ClaimManager.getInstance().getFactionAt(player) instanceof ConquestFaction) {
                player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in the &3Conquest&c!"));
                return true;
            }
        }
        
        if (ClaimManager.getInstance().getFactionAt(player).isSafezone()) {
            player.sendMessage(ColorUtil.translate("&d&lPartnerItem &8» &cYou can't use this item in a &aSafezone&c!"));
            return true;
        }

        return false;
    }

    public static List<Player> getPlayersInRangeOfByPlayer(Player player, int range) {
        final List<Player> players = new ArrayList<>();

        final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(player);

        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (target == player) continue;
            if (playerFaction != null && !playerFaction.getMembers().containsKey(target.getUniqueId())) continue;
            if (player.getLocation().distance(target.getLocation()) > range) continue;

            players.add(target);
        }

        return players;
    }
}
