package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.module.annotations.RequireOutputChannel;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.Utils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by redslime on 30.03.2018
 */
@Module
@RequireOutputChannel
public class CTFChangelog extends CommandModule {

    public CTFChangelog() {
        super("CTF Changelog Poster Module", "Sends a message whenever CTF is changed in #general or the default chat", false, true);
        setChannelFilter(429311133950935040L);
        setUserFilter(115834525329653760L);
        trigger("!update");
        setHideCommandInModuleInfo(true);
        setShowInHelp(false);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String message = Utils.connectArgs(event.getMessage().getContent().split(" "), 1);

        for(IGuild g : Wafflebot.client.getGuilds()) {
            if(isActive(g)) {
                MessageUtil.sendMessage(Wafflebot.data.getModuleChannel(event.getGuild(), this), EmbedPresets.information().withTitle(getName()).withDesc(message).withTimestamp(System.currentTimeMillis()));
            }
        }
    }
}
