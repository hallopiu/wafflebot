package xyz.redslime.wafflebot.modules;

import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by redslime on 28.03.2018
 */
@Module
public class Brawl extends CommandModule {

    private static final Map<String, String> servers = new HashMap<>();

    static {
        servers.put("br_brawl_com", "Battle Royale");
        servers.put("ctfmatch_brawl_com", "Capture the Flag (Match)");
        servers.put("lobby_brawl_com", "Lobby");
        servers.put("mc_hg_com", "Hardcore Games");
        servers.put("mc_war_com", "MC-War");
        servers.put("mc_warz_com", "MC-WarZ");
        servers.put("mcctf_com", "Capture the Flag");
        servers.put("minecraftbuild_com", "Build");
        servers.put("minecraftparty_com", "Minecraft Party");
        servers.put("raid_mcpvp_com", "Raid");
        servers.put("test_brawl_com", "Test");
        servers.put("wildwest_brawl_com", "Wild West");
        servers.put("kit_brawl_com", "KitBrawl");
    }

    public Brawl() {
        super("Brawl Checker Module", "Checks current Brawl.com playercount per server.", false, true);
        trigger("!brawl");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String json = getJSONStatus();
        String servers = "";
        String total = "";
        for(Map.Entry<String, Integer> entry : getServerValues(json).entrySet()) {
            if(!entry.getKey().equalsIgnoreCase("total"))
                servers += "\n:small_blue_diamond: **" + entry.getKey() + "** - " + entry.getValue();
            else
                total = "\n:small_orange_diamond: **Total** - " + entry.getValue();
        }
        MessageUtil.sendMessage(event.getChannel(), servers + total);
    }

    private String getJSONStatus() throws Exception {
        InputStream s = null;
        s = new URL("http://www.brawl.com/data/playerCount.json").openStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(s, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = r.read()) != -1) sb.append((char) cp);
        return sb.toString();
    }

    private Map<String, Integer> getServerValues(String s) {
        // honestly just ignore this part this is horrible code copy pasted from 2016. too lazy to do it properly.
        s = s.replace("{", "");
        s = s.replace("}", "");
        s = s.replace('"', ' ');
        SortedMap<String, Integer> serverCount = new TreeMap<>();
        String tmp = "";
        int i = 1;
        for(String pairs : s.split(",")) {
            for(String values : pairs.split(":")) {
                if(i == 1) {
                    tmp = values;
                    i++;
                } else {
                    serverCount.put(this.servers.containsKey(tmp.trim()) ? this.servers.get(tmp.trim()) : tmp.trim(), Integer.valueOf(values));
                    i = 1;
                }
            }
        }
        return serverCount;
    }
}
