package nl.rubixstudios.partneritems.timer.timers;

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
 * @created 28/11/2022 - 19:11
 * @project PartnerItems
 */
public class SpecialTimer extends PlayerTimer implements ScoreboardTimer {
    String placeholder = " &b&lSpecial&7: &f";

    public SpecialTimer(ScheduledExecutorService executor) {
        super(executor, "SpecialTimer", Config.PARTNER_ITEM_PARALYZED_HOE_DURATION);
        this.setFormat(StringUtils.FormatType.MILLIS_TO_SECONDS);
        this.setExpiryMessage(ColorUtil.translate("&b&lSpecial &8»") + Color.translate("&7 Your cooldown has been expired!"));
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