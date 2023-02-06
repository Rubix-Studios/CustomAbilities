package nl.rubixstudios.partneritems.timer.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import me.qiooip.lazarus.timer.type.SystemTimer;
import me.qiooip.lazarus.utils.StringUtils;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Djorr
 * @created 23/11/2022 - 09:34
 * @project PartnerItems
 */
public class FactionClaimPartnerItemTimer extends SystemTimer {

    private final Table<UUID, String, ScheduledFuture<?>> factionsOnCooldown;

    public FactionClaimPartnerItemTimer(ScheduledExecutorService executor) {
        super(executor, "FactionClaimPartnerItemTimer", 0, true);

        this.factionsOnCooldown = HashBasedTable.create();
        this.setFormat(StringUtils.FormatType.MILLIS_TO_SECONDS);
    }

    @Override
    public void disable() {
        this.factionsOnCooldown.values().forEach(future -> future.cancel(true));
        this.factionsOnCooldown.clear();
    }

    public void activate(PlayerFaction playerFaction, int delay, String partnerItemNbt) {
        this.factionsOnCooldown.put(playerFaction.getId(), partnerItemNbt, this.scheduleExpiry(playerFaction.getId(), partnerItemNbt, delay));
    }

    public void cancel(UUID factionId, String partnerItemNbt) {
        if (!this.isActive(factionId, partnerItemNbt)) return;

        this.factionsOnCooldown.remove(factionId, partnerItemNbt).cancel(true);
    }

    public boolean isActive(UUID factionId, String partnerItemNbt) {
        return this.factionsOnCooldown.contains(factionId, partnerItemNbt);
    }

    public long getCooldown(UUID factionId, String partnerItemNbt) {
        return this.factionsOnCooldown.get(factionId, partnerItemNbt).getDelay(TimeUnit.MILLISECONDS);
    }

    public String getTimeLeft(UUID factionId, String partnerItemNbt) {
        return StringUtils.formatTime(this.getCooldown(factionId, partnerItemNbt), this.format);
    }

    public String getDynamicTimeLeft(UUID factionId, String partnerItemNbt) {
        long remaining = this.getCooldown(factionId, partnerItemNbt);

        if(remaining < 3_600_000L) {
            return StringUtils.formatTime(remaining, StringUtils.FormatType.MILLIS_TO_MINUTES);
        } else {
            return StringUtils.formatTime(remaining, StringUtils.FormatType.MILLIS_TO_HOURS);
        }
    }



    private ScheduledFuture<?> scheduleExpiry(UUID uuid, String cooldown, int delay) {
        return this.executor.schedule(() -> {
            try {
                this.factionsOnCooldown.remove(uuid, cooldown);
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }, delay, TimeUnit.SECONDS);
    }
}