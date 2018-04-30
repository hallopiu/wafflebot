package redsli.me.wafflebot.data;

import lombok.Getter;
import redsli.me.wafflebot.util.Utils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by redslime on 30.04.2018
 */
@Getter
public class UUIDCacheItem {

    String name;
    UUID uuid;
    long timestamp;

    public UUIDCacheItem(String name, String uuid, long timestamp) {
        this.name = name;
        this.uuid = UUID.fromString(Utils.insertDashUUID(uuid));
        this.timestamp = timestamp;
    }

    public boolean isOutdated() {
        return System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10) > 0;
    }
}
