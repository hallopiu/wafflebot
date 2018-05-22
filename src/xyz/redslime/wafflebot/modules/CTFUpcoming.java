package xyz.redslime.wafflebot.modules;

import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.ChatListenerModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.module.annotations.RequireOutputChannel;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by redslime on 29.03.2018
 */
@Module
@RequireOutputChannel
public class CTFUpcoming extends ChatListenerModule {

    public CTFUpcoming() {
        super("CTF Upcoming Matches Module", "Informs you about upcoming matches every thursday", false, true);
        trigger("Match");
        setUserFilter(115834525329653760L);
        setChannelFilter(222475330697428993L);
    }

    @Override
    public void mentioned(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();

        if(msg.toLowerCase().contains("friday") || msg.toLowerCase().contains("saturday") || msg.toLowerCase().contains("sunday") || msg.toLowerCase().contains("no upcoming matches")) {
            if(msg.toLowerCase().contains(" est")) {
                for(IGuild g : Wafflebot.client.getGuilds()) {
                    if(isActive(g) && !g.equals(event.getGuild())) {
                        msg = msg.replace("<@&222473706532765696>", "");
                        msg = msg.replace("<@&222473075461980160>", "");
                        msg = msg.replace("<@&222473223961444353>", "");
                        msg = msg.replace("<@&444965592617844746>", "");
                        MessageUtil.sendMessage(Wafflebot.data.getModuleChannel(g, this), EmbedPresets.information().withTitle(getName()).withDesc(msg).withTimestamp(System.currentTimeMillis()));
                    }
                }
            }
        }
    }
}