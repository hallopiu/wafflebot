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
public class CTFResults extends ChatListenerModule {

    public CTFResults() {
        super("CTF Match Results Module", "Sends a message with the latest matches results in #general or the default chat", false, true);
        trigger("player of the game");
        trigger("potg");
        setChannelFilter(222475330697428993L);
    }

    @Override
    public void mentioned(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();

        if(msg.toLowerCase().contains("won") && msg.toLowerCase().contains("against") && msg.toLowerCase().contains("-") && msg.toLowerCase().contains("brawl")) {
            for(IGuild g : Wafflebot.client.getGuilds()) {
                if(isActive(g) && !g.equals(event.getGuild())) {
                    MessageUtil.sendMessage(Wafflebot.data.getModuleChannel(g, this), EmbedPresets.information().withTitle(getName()).withDesc(msg).withTimestamp(System.currentTimeMillis()));
                }
            }
        }
    }
}
