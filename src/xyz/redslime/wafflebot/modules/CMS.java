package xyz.redslime.wafflebot.modules;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.ChatListenerModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.module.annotations.RequireOutputChannel;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;

import java.util.function.Function;

/**
 * Created by redslime on 29.03.2018
 */
@Module
@RequireOutputChannel
public class CMS extends ChatListenerModule {

    private static final Function<String, String> THUMBNAIL = (s) -> String.format("https://img.youtube.com/vi/%s/0.jpg", s);
    private static final Function<String, String> REGEX = (s) -> s.replaceAll(".*/(.*)", "$1");

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
                    MessageUtil.sendMessage(Wafflebot.data.getModuleChannel(g, this),
                            EmbedPresets.information().withTitle(getName()).withDesc(msg)
                                    .withTimestamp(System.currentTimeMillis())
                                    .withThumbnail(THUMBNAIL.apply(REGEX.apply(msg))));
                }
            }
        }
    }
}