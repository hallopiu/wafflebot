package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.module.BotModule;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.DiscordHelper;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.Utils;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.awt.*;
import java.util.List;

/**
 * Created by redslime on 31.03.2018
 */
@Module
public class Util extends CommandModule {

    public Util() {
        super("Util Module", "Special purposes module", true, false);
        setChannelFilter(429311133950935040L);
        setUserFilter(115834525329653760L);
        limit(194118228774092800L);
        setShowInModulesList(false);
        setShowInHelp(false);
        setHideCommandInModuleInfo(true);

        trigger("!cmslive");
        aliases("!bc", "!guilds");
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();
        String[] args = msg.split(" ");
        switch (args[0].toLowerCase()) {
            case "!cmslive": {
                for(IGuild g : Wafflebot.client.getGuilds()) {
                    if(BotModule.get("CMS").isActive(g)) {
                        IChannel channel = DiscordHelper.getDefaultChannel(g);
                        String message = Utils.connectArgs(args, 1);
                        MessageUtil.sendMessage(channel, new WaffleEmbedBuilder().withColor(new Color(211, 84, 84)).withDesc(message)
                                .withAuthorName("CTF Match Spotlight is now live!").withAuthorIcon("https://i.imgur.com/gd7ZPC8.jpg").withAuthorUrl("https://www.youtube.com/c/CTFMatchSpotlightLive"));
                    }
                }
                break;
            }

            case "!bc": {
                for(IGuild g : Wafflebot.client.getGuilds()) {
                    IChannel channel = DiscordHelper.getDefaultChannel(g);
                    String message = Utils.connectArgs(args, 1);
                    MessageUtil.sendMessage(channel, EmbedPresets.information().withDesc(message));
                }
                break;
            }

            case "!guilds": {
                List<IGuild> guilds = Wafflebot.client.getGuilds();
                WaffleEmbedBuilder embed = EmbedPresets.information().withTitle("Member in " + guilds.size() + " guilds");
                String body = "";
                for(IGuild guild : guilds) {
                    body += guild.getName() + ", owned by " + guild.getOwner().getName() + ", with " + guild.getTotalMemberCount() + " members\n";
                }
                embed.withDesc(body);
                MessageUtil.sendMessage(event, embed);
                break;
            }
        }
    }
}
