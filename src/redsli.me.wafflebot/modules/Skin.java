package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by redslime on 04.04.2018
 */
@Module
public class Skin extends CommandModule {

    public static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/7f5b17459ee7449db669a3fe4ff41c7f";

    public Skin() {
        super("Minecraft Skin Fetcher Module", "Sends you a link of puhdgy's current skin", false, true);
        trigger("!getskin");
        setUserFilter(116275810649767937L);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        InputStream s = new URL(PROFILE_URL).openStream();
        InputStreamReader isr = new InputStreamReader(s, Charset.forName("UTF-8"));
        BufferedReader r = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = r.read()) != -1) {
            sb.append((char) cp);
        }
        String data = sb.toString();
        MessageUtil.sendMessage(event, data);
    }
}
