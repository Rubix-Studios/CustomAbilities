package nl.rubixstudios.partneritems.timer;

import lombok.Getter;
import me.qiooip.lazarus.timer.TimerManager;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.timer.timers.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * @author Djorr
 * @created 20/11/2022 - 02:08
 * @project PartnerItems
 */

@Getter
public class TimeController implements Listener {

    @Getter private static TimeController instance;

    private TearOfBloodTimer tearOfBloodTimer;
    private BleedBombTimer bleedBombTimer;
    private GrappleTimer grappleTimer;
    private TeleportPortalTimer teleportPortalTimer;
    private TNTBomberTimer tntBomberTimer;
    private FactionClaimPartnerItemTimer factionClaimPartnerItemTimer;

    private ForceFieldTimer forceFieldTimer;
    private ReverseBowTimer reverseBowTimer;
    private ParalyzedHoeTimer paralyzedHoeTimer;
    private SpecialTimer specialTimer;

    public TimeController() {
        instance = this;

        this.initializeTimers();

        Bukkit.getPluginManager().registerEvents(this, PartnerItems.getInstance());
    }

    public void disable() {
        if (this.tearOfBloodTimer != null) this.tearOfBloodTimer.disable();
        if (this.bleedBombTimer != null) this.bleedBombTimer.disable();
        if (this.grappleTimer != null) this.grappleTimer.disable();
        if (this.teleportPortalTimer != null) this.teleportPortalTimer.disable();
        if (this.tntBomberTimer != null) this.tntBomberTimer.disable();
        if (this.factionClaimPartnerItemTimer != null) this.factionClaimPartnerItemTimer.disable();
        if (this.forceFieldTimer != null) this.forceFieldTimer.disable();
        if (this.reverseBowTimer != null) this.reverseBowTimer.disable();
        if (this.paralyzedHoeTimer != null) this.paralyzedHoeTimer.disable();
        if (this.specialTimer != null) this.specialTimer.disable();
    }

    private void initializeTimers() {
        final TimerManager timerManager = TimerManager.getInstance();

        timerManager.getScoreboardTimers().add(this.tearOfBloodTimer = new TearOfBloodTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.bleedBombTimer = new BleedBombTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.grappleTimer = new GrappleTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.teleportPortalTimer = new TeleportPortalTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.tntBomberTimer = new TNTBomberTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.forceFieldTimer = new ForceFieldTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.reverseBowTimer = new ReverseBowTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.paralyzedHoeTimer = new ParalyzedHoeTimer(timerManager.getExecutor()));
        timerManager.getScoreboardTimers().add(this.specialTimer = new SpecialTimer(timerManager.getExecutor()));

        this.factionClaimPartnerItemTimer = new FactionClaimPartnerItemTimer(timerManager.getExecutor());
    }
}
