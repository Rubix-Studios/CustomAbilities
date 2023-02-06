package nl.rubixstudios.partneritems.api;

import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.timer.timers.*;

/**
 * @author Djorr
 * @created 23/11/2022 - 12:13
 * @project PartnerItems
 */
public class PartnerItemsAPI {

    // Timers

    /**
     * @returns the BleedBombTimer
     */
    public static BleedBombTimer getBleedBombTimer() {
        return TimeController.getInstance().getBleedBombTimer();
    }

    /**
     * @returns the ForceFieldTimer
     */
    public static FactionClaimPartnerItemTimer getFactionClaimPartnerItemTimer() {
        return TimeController.getInstance().getFactionClaimPartnerItemTimer();
    }

    /**
     * @returns the ForceFieldTimer
     */
    public static ForceFieldTimer getForceFieldTimer() {
        return TimeController.getInstance().getForceFieldTimer();
    }

    /**
     * @returns the ReverseBowTimer
     */
    public static GrappleTimer getGrappleTimer() {
        return TimeController.getInstance().getGrappleTimer();
    }

    /**
     * @returns the ReverseBowTimer
     */
    public static ParalyzedHoeTimer getParalyzedHoeTimer() {
        return TimeController.getInstance().getParalyzedHoeTimer();
    }

    /**
     * @returns the ReverseBowTimer
     */
    public static ReverseBowTimer getReverseBowTimer() {
        return TimeController.getInstance().getReverseBowTimer();
    }

    /**
     * @returns the ReverseBowTimer
     */
    public static SpecialTimer getSpecialTimer() {
        return TimeController.getInstance().getSpecialTimer();
    }

    /**
     * @returns the ReverseBowTimer
     */
    public static TearOfBloodTimer getTearOfBloodTimer() {
        return TimeController.getInstance().getTearOfBloodTimer();
    }

    /**
     * @returns the ReverseBowTimer
     */
    public static TeleportPortalTimer getTeleportPortalTimer() {
        return TimeController.getInstance().getTeleportPortalTimer();
    }

    /**
     * @returns the ReverseBowTimer
     */
    public static TNTBomberTimer getTntBomberTimer() {
        return TimeController.getInstance().getTntBomberTimer();
    }
}
