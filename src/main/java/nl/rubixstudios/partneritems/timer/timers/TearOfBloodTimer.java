package nl.rubixstudios.partneritems.timer.timers;

import me.qiooip.lazarus.config.Language;
import me.qiooip.lazarus.timer.type.PlayerTimer;
import me.qiooip.lazarus.timer.type.ScoreboardTimer;
import me.qiooip.lazarus.utils.Color;
import me.qiooip.lazarus.utils.StringUtils;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.util.ColorUtil;
import org.bukkit.entity.Player;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Djorr
 * @created 20/11/2022 - 02:07
 * @project PartnerItems
 */

public class TearOfBloodTimer extends PlayerTimer implements ScoreboardTimer {
    String placeholder = " " + Config.PARTNER_ITEM_TEAR_OF_BLOOD_DISPLAY_NAME + ": &f";

    public TearOfBloodTimer(ScheduledExecutorService executor) {
        super(executor, "TearOfBloodTimer", Config.PARTNER_ITEM_TEAR_OF_BLOOD_COOLDOWN);
        this.setFormat(StringUtils.FormatType.MILLIS_TO_SECONDS);
        this.setExpiryMessage(ColorUtil.translate(Config.PARTNER_ITEM_TEAR_OF_BLOOD_DISPLAY_NAME + " &8» ") + Color.translate("&7 Your cooldown has been expired!"));
    }

    public String getPlaceholder() {
        return Color.translate(this.placeholder);
    }

    public String getScoreboardEntry(Player player) {
        return this.getTimeLeft(player) + "s";
    }

    public void setPlaceholder(String string) {
        this.placeholder = string;
    }
}