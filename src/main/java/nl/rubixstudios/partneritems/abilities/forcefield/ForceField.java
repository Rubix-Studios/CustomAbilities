package nl.rubixstudios.partneritems.abilities.forcefield;

import lombok.Getter;
import lombok.Setter;
import nl.rubixstudios.partneritems.data.Config;
import org.bukkit.entity.Player;

/**
 * @author Tokeee
 * @created 28/11/2022 - 18:49
 * @project PartnerItems
 */
public class ForceField {
    @Getter
    private final short radius = (short) Config.PARTENR_ITEM_FORCE_FIELD_SHOOT_RANGE;

    @Getter
    private float warmUpInSeconds = Config.PARTNER_ITEM_FORCE_FIELD_WARM_UP;

    @Getter
    private final long warmUpTimeStamp = System.currentTimeMillis();

    @Getter @Setter
    private long velocityStartTimeStamp = -1;

    @Getter private final float velocityDurationInSeconds = 0.65F;


   @Getter
   private final Player player;

    public ForceField(final Player player) {
        this.player = player;
    }
    public ForceField(final Player player, float warmupTime) {
        this.player = player;
        this.warmUpInSeconds = warmupTime;
    }



    // calculate the currentTime - timestamp in a value between 0 and 1
    public float getWarmUpProgress() {
        return (System.currentTimeMillis() - warmUpTimeStamp) / (warmUpInSeconds * 1000f);
    }

    // initialize the velocityStartTimeStamp
    public boolean initializedVelocityTimeStamp() {
        return velocityStartTimeStamp != -1;
    }

    public float getWarmUpProgressWithOffset(float offset) {
        return (System.currentTimeMillis() - warmUpTimeStamp) / ((warmUpInSeconds + offset) * 1000f);
    }

    // calculate the currentTime - velocityTimeStamp in a value between 0 and 1
    public float getVelocityProgress() {
        return (System.currentTimeMillis() - velocityStartTimeStamp) / (velocityDurationInSeconds * 1000f);
    }


}
