package nl.rubixstudios.partneritems.abilities.teleportportal.command;

import me.qiooip.lazarus.factions.FactionsManager;
import me.qiooip.lazarus.factions.claim.Claim;
import me.qiooip.lazarus.factions.claim.ClaimManager;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import me.qiooip.lazarus.timer.TimerManager;
import me.qiooip.lazarus.timer.scoreboard.CombatTagTimer;
import nl.rubixstudios.partneritems.abilities.teleportportal.TeleportPortalController;
import nl.rubixstudios.partneritems.abilities.teleportportal.object.TeleportPortalObj;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.util.ColorUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Djorr
 * @created 21/11/2022 - 19:58
 * @project PartnerItems
 */
public class PortalCommand implements CommandExecutor {

    private final TeleportPortalController teleportPortalController;

    public PortalCommand() {
        this.teleportPortalController = TeleportPortalController.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) return true;
        final Player player = (Player) commandSender;

        if (args.length != 0) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &8» &eUsage: /portal"));
            return true;
        }

        final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &8» &cYou are not in a faction."));
            return true;
        }

        final TeleportPortalObj teleportPortalObj = this.teleportPortalController.getTeleportPortalByFaction(playerFaction);
        if (teleportPortalObj == null) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &8» &cThere is no teleport portal active."));
            return true;
        }

        final PlayerFaction enemyFaction = FactionsManager.getInstance().getPlayerFactionByUuid(ClaimManager.getInstance().getFactionAt(teleportPortalObj.getLocation()).getId());
        if (!this.teleportPortalController.hasEnoughPlayersWithArmorOn(playerFaction, enemyFaction)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TNT_BOMBER_DISPLAY_NAME + " &7» &cA maximum of the total amount of enemies online - 1 can teleport."));
            return true;
        }

        if (!teleportPortalObj.isPlayerInRange(player)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &8» &cYou must be in &b<amount> &crange of the portal."
                    .replace("<amount>", "" + Config.PARTNER_ITEM_TELEPORT_PORTAL_TELEPORT_RANGE)
            ));
            return true;
        }

        final CombatTagTimer combatTagTimer = TimerManager.getInstance().getCombatTagTimer();
        if (combatTagTimer.isActive(player)) {
            player.sendMessage(ColorUtil.translate(Config.PARTNER_ITEM_TELEPORT_PORTAL_DISPLAY_NAME + " &8» &cYou can't do this in combat."));
            return true;
        }

        teleportPortalObj.handleTeleport(player);
        return false;
    }
}
