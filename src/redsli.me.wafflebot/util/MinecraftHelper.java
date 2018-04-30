package redsli.me.wafflebot.util;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.data.UUIDCacheItem;
import redsli.me.wafflebot.jsonresponses.Info;
import redsli.me.wafflebot.jsonresponses.MojangNameHistory;
import redsli.me.wafflebot.jsonresponses.MojangNameToUUID;

import javax.naming.NameNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Created by redslime on 29.03.2018
 */
public class MinecraftHelper {

    private static final String SERVER_INFO_URL = "https://use.gameapis.net/mc/query/info/";
    private static final String USER_UUID = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String USER_HISTORY = "https://api.mojang.com/user/profiles/%s/names";
    private static final String USER_SKIN = "http://cravatar.eu/helmavatar/%s/128.png";

    public static Info getInfo(String address) throws Exception {
        return Wafflebot.GSON.fromJson(Utils.readURL(new URL(SERVER_INFO_URL + address)), Info.class);
    }

    public static MinecraftUser getNameHistory(String name) throws Exception {
        UUIDCacheItem userData = Wafflebot.data.getLatestUuidCacheItem(name).orElse(fromName(name));
        List<MojangNameHistory> nameHistory = getHistory(userData.getUuid());
        return new MinecraftUser(userData.getName(), userData.getUuid(), nameHistory);
    }

    private static List<MojangNameHistory> getHistory(UUID uuid) throws Exception {
        return Wafflebot.GSON.fromJson(Utils.readURL(new URL(String.format(USER_HISTORY, uuid.toString().replace("-", "")))),
                new TypeToken<List<MojangNameHistory>>() {
                }.getType());
    }

    private static UUIDCacheItem fromName(String name) throws Exception {
        MojangNameToUUID data = Wafflebot.GSON.fromJson(Utils.readURL(new URL(USER_UUID + name)), MojangNameToUUID.class);
        if(data == null)
            throw new NameNotFoundException();
        UUIDCacheItem cacheItem = new UUIDCacheItem(data.getName(), data.getId(), System.currentTimeMillis());
        Wafflebot.data.uuids.add(cacheItem);
        Wafflebot.save();
        return cacheItem;
    }

    public static String getSkinUrl(UUID uuid) {
        return String.format(USER_SKIN, uuid.toString());
    }

    @Getter
    public static class MinecraftUser {
        private String name;
        private UUID uuid;
        private List<MojangNameHistory> names;

        public MinecraftUser(String name, UUID uuid, List<MojangNameHistory> names) {
            this.name = name;
            this.uuid = uuid;
            this.names = names;
        }
    }
}
