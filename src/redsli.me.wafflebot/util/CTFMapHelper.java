package redsli.me.wafflebot.util;

import com.google.gson.reflect.TypeToken;
import redsli.me.wafflebot.Wafflebot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by redslime on 03.04.2018
 */
public class CTFMapHelper {

    public static final File MAP_FILE = new File("maps.json");

    public static String getMapByID(int id) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(MAP_FILE));
        List<MapEntry> maps = Wafflebot.GSON.fromJson(reader, new TypeToken<List<MapEntry>>() {
        }.getType());
        reader.close();

        return maps.stream().filter(m -> m.map_id == id).findFirst().map(mapEntry -> mapEntry.name).orElse(null);
    }

    public static List<Integer> getAllIds() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(MAP_FILE));
        List<MapEntry> maps = Wafflebot.GSON.fromJson(reader, new TypeToken<List<MapEntry>>() {
        }.getType());
        reader.close();
        List<Integer> ids = new ArrayList<>();
        maps.forEach(m -> ids.add(m.map_id));
        return ids;
    }

    class MapEntry {
        int map_id;
        String name;
    }
}
