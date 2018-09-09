package xyz.redslime.wafflebot.data;

import lombok.Builder;
import lombok.Data;
import xyz.redslime.wafflebot.modules.ctfcommunity.Strike;

/**
 * Created by redslime on 13.08.2018
 */
@Data
@Builder
public class PPMStrike {

    private long timestamp;
    private long striked;
    private long strikedBy;
    private Strike.StrikeTier tier;
    private String reason;

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > tier.getDuration();
    }

    public boolean isActive() {
        return !isExpired();
    }

    public long getExpirationTimestamp() {
        return timestamp + tier.getDuration();
    }
}
