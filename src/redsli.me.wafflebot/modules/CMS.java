package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.module.ChatListenerModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.module.annotations.RequireOutputChannel;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by redslime on 29.03.2018
 */
@Module
@RequireOutputChannel
public class CMS extends ChatListenerModule {

    public CMS() {
        super("CTF Match Spotlight Module", "Sends you a message when a stream is scheduled or is live in #general or the default chat", false, true);
        trigger("Stream at");
        setChannelFilter(222472853327446018L);
        setUserFilter(115834525329653760L);
    }

    @Override
    public void mentioned(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();

        if(msg.toLowerCase().contains("est") && msg.toLowerCase().contains("https://youtu.be")) {
            for(IGuild g : Wafflebot.client.getGuilds()) {
                if(isActive(g) && !g.equals(event.getGuild())) {
                    MessageUtil.sendMessage(Wafflebot.data.getModuleChannel(event.getGuild(), this), EmbedPresets.information().withTitle(getName()).withDesc(msg).withTimestamp(System.currentTimeMillis()));
                }
            }
        }
    }
}