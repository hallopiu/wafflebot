package redsli.me.wafflebot.util;

import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.jsonresponses.Info;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by redslime on 29.03.2018
 */
public class MinecraftHelper {

    public static Info getInfo(String address) throws Exception {
        InputStream s = new URL("https://use.gameapis.net/mc/query/info/" + address).openStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(s, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = r.read()) != -1) {
            sb.append((char) cp);
        }
        return Wafflebot.GSON.fromJson(sb.toString(), Info.class);
    }
}
