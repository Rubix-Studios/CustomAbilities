package nl.rubixstudios.partneritems.abilities.bleed.task;

import me.qiooip.lazarus.factions.FactionsManager;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import nl.rubixstudios.partneritems.abilities.bleed.BleedController;
import nl.rubixstudios.partneritems.abilities.bleed.event.BleedActivatedEvent;
import nl.rubixstudios.partneritems.abilities.bleed.event.BleedStopEvent;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.util.particles.ParticleUtil;
import nl.rubixstudios.partneritems.util.particles.particletrailbuilder.ParticleColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author Djorr
 * @created 21/11/2022 - 18:40
 * @project PartnerItems
 */
public class BleedTask extends BukkitRunnable {

    private final BleedController bleedController;

    public final List<UUID> tearRemovalCache;

    public BleedTask() {
        this.bleedController = BleedController.getInstance();

        this.tearRemovalCache = new ArrayList<>();
    }

    @Override
    public void run() {
        if (!this.bleedController.getRemovalCache().isEmpty()) {
            this.bleedController.getRemovalCache().forEach(bleedBombObject -> {
                getPlayersInFactionOf(bleedBombObject.getShooter()).forEach(member -> {
                    final BleedStopEvent bleedStopEvent = new BleedStopEvent(member);
                    Bukkit.getPluginManager().callEvent(bleedStopEvent);
                });

                this.bleedController.getBleedBombs().remove(bleedBombObject);
            });
            this.bleedController.getRemovalCache().clear();
        }

        if (!this.tearRemovalCache.isEmpty()) {
            this.tearRemovalCache.forEach(uuid -> {
                if (Bukkit.getPlayer(uuid) != null) {
                    final BleedStopEvent bleedStopEvent = new BleedStopEvent(Bukkit.getPlayer(uuid));
                    Bukkit.getPluginManager().callEvent(bleedStopEvent);
                }

                this.bleedController.getTearOfBloodMap().remove(uuid);
            });
            this.tearRemovalCache.clear();
        }

        if (!this.bleedController.getBleedBombs().isEmpty()) {
            this.bleedController.getBleedBombs().forEach(bleedBombObject -> {
                bleedBombObject.setActive(true);
                this.bleedController.spawnParticlesAtEachBleedBomb(bleedBombObject);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getLocation().getWorld().equals(bleedBombObject.getLandLoc().getWorld())) return;

                    if (!this.bleedController.isPlayerInRange(bleedBombObject, player)) {
                        if (bleedBombObject.getBleedPlayers().contains(player.getUniqueId())) {
                            bleedBombObject.getBleedPlayers().remove(player.getUniqueId());

                            final BleedStopEvent bleedStopEvent = new BleedStopEvent(bleedBombObject.getShooter());
                            Bukkit.getPluginManager().callEvent(bleedStopEvent);
                        }
                        continue;
                    }

                    final UUID playerId = player.getUniqueId();
                    if (bleedBombObject.getBleedPlayers().contains(playerId)) continue;
                    bleedBombObject.getBleedPlayers().add(player.getUniqueId());

                    final BleedActivatedEvent bleedActivateEvent = new BleedActivatedEvent(player, true);
                    Bukkit.getPluginManager().callEvent(bleedActivateEvent);
                }

                final boolean afterTime = System.currentTimeMillis() - bleedBombObject.getTimeThrown() >= Config.PARTNER_ITEM_BLEED_BOMB_DURATION * 1000L; // If the thrown time is higher then timeAfterRemoval
                if (afterTime) {  // If the thrown time is higher then timeAfterRemoval
                    bleedBombObject.setActive(false);
                    this.bleedController.removeBleedBomb(bleedBombObject);
                }
            });
        }

        if (!this.bleedController.getTearOfBloodMap().isEmpty()) {
            this.bleedController.getTearOfBloodMap().forEach(((uuid, aLong) -> {
                if (uuid == null || aLong == null) return;

                if (System.currentTimeMillis() - aLong >= Config.PARTNER_ITEM_TEAR_OF_BLOOD_DURATION * 1000L) {
                    this.tearRemovalCache.add(uuid);
                }
            }));
        }
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


}
