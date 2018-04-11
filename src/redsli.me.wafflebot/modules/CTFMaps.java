package redsli.me.wafflebot.modules;

import com.google.gson.reflect.TypeToken;
import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.Utils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by redslime on 30.03.2018
 */
@Module
public class CTFMaps extends CommandModule {

    public static final File MAP_FILE = new File("maps.json");

    public CTFMaps() {
        super("CTF Map Finder Module", "Finds CTF maps by name or id", false, true);
        trigger("!findmap");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        boolean passes = true;
        String msg = event.getMessage().getContent();
        String[] args = msg.split(" ");
        if(args.length == 1) {
            passes = false;
            MessageUtil.sendMessage(event, EmbedPresets.error("Expected: !findmap [map_name/map_id]"));
        }
        if(!MAP_FILE.exists()) {
            passes = false;
            MessageUtil.sendMessage(event, EmbedPresets.error("Map file not found, can't check"));
        }
        return passes && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String request = Utils.connectArgs(event.getMessage().getContent().split(" "), 1).trim();

        BufferedReader reader = new BufferedReader(new FileReader(MAP_FILE));
        List<MapEntry> maps = Wafflebot.GSON.fromJson(reader, new TypeToken<List<MapEntry>>() {
        }.getType());
        reader.close();

        try {
            if(request.matches("([0-9]*)")) {
                int id = Integer.valueOf(request);
                MapEntry map = null;
                for(MapEntry m : maps) if(m.map_id == id) map = m;

                if(map != null) {
                    MessageUtil.sendMessage(event, EmbedPresets.information().withTimestamp(System.currentTimeMillis()).withUserFooter(event).withTitle("Found 1 map").withDesc(map.name + " (" + map.map_id + ")"));
                } else
                    MessageUtil.sendMessage(event, EmbedPresets.error("Map not found (searched for map id)"));
            } else {
                List<MapEntry> map = new ArrayList<>();
                for(MapEntry m : maps) if(m.name.toLowerCase().contains(request.toLowerCase())) map.add(m);

                if(!map.isEmpty()) {
                    String title = "";
                    for(MapEntry m : map) {
                        title += m.map_id + " (" + m.name + ")\n";
                    }
                    MessageUtil.sendMessage(event, EmbedPresets.information().withTimestamp(System.currentTimeMillis()).withUserFooter(event).withDesc(title).withTitle("Found " + map.size() + " map(s)"));
                } else
                    MessageUtil.sendMessage(event, EmbedPresets.error("Map not found (searched for map name)"));
            }
        } catch (IllegalArgumentException e) {
            MessageUtil.sendMessage(event, EmbedPresets.error("Way too many maps. Seriously, the message would be longer than Discord's character limit."));
        }
    }

    class MapEntry {
        int map_id;
        String name;
    }
}
