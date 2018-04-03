package redsli.me.wafflebot.events;

import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.util.DiscordHelper;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by redslime on 29.03.2018
 */
public class RokeReady implements IListener<ReadyEvent> {
    @Override
    public void handle(ReadyEvent event) {
        for(IGuild g : Wafflebot.client.getGuilds()) {
            MessageUtil.sendMessage(DiscordHelper.getDefaultChannel(g), "Hey everybody! This bot is shutting down as we speak. This is the last message ever from Lord Roke.\n" +
                    "As a replacement, **wafflebot** was created. It has almost the same functions and will be replacing me from here on. Invite wafflebot with this link to your server:" +
                    "<https://discordapp.com/api/oauth2/authorize?client_id=428575339749572608&permissions=2146954353&scope=bot>\n\nPlease don't go drink bleach due to this sad message,\nLove and waffles!");
        }
    }
}
