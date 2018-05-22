package xyz.redslime.wafflebot.modules;

import org.ocpsoft.prettytime.PrettyTime;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;

import java.time.Instant;
import java.util.Date;

/**
 * Created by redslime on 28.03.2018
 */
@Module
public class Help extends CommandModule {

    public Help() {
        super("Help Module", "Shows basic Bot information and a list of commands", true, false);
        trigger("!help");
        setShowInHelp(true);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        WaffleEmbedBuilder builder = EmbedPresets.information().appendField("Creator", Wafflebot.client.getUserByID(115834525329653760L).mention(), true)
                .withDesc("I'm a module-based bot, you can enable or disable certain modules, dependant on your needs!\n" +
                        "All modules can be viewed with ``!modules``\n" +
                        "Members with \"Manage Server\" permissions can turn on/off modules")
                .withThumbnail("https://cdn.discordapp.com/avatars/428575339749572608/23c66d315ab662873495daef767d3067.png")
                .withTitle("Wafflebot Help")
                .appendField("Uptime", "Started " + new PrettyTime().format(new Date(Wafflebot.started)), true)
                .appendField("Commands", this.getCommands(event.getGuild(), true), true);
        if(!event.getChannel().isPrivate())
            if(!getCommands(event.getGuild(), false).trim().equals(""))
                builder.appendField("Module specific commands", getCommands(event.getGuild(), false), true).withTimestamp(Instant.now());
        else
            builder.appendField("Module specific commands (All enabled in PMs)", getPMCommands(), true);
        builder.appendField("Github", "https://github.com/hallopiu/wafflebot/", false);
        MessageUtil.sendMessage(event.getChannel(), builder.build());
    }

    private String getPMCommands() {
        String result = "";
        for(BotModule bm : BotModule.modules) {
            if(bm instanceof CommandModule) {
                CommandModule m = (CommandModule) bm;
                if(m.isShowInHelp() && m.isServerModule() && !m.isGuildOnly()) {
                    result += m.getCommand() + "\n";
                    if(!m.getAliases().isEmpty())
                        for(String alias : m.getAliases())
                            result += alias + "\n";
                }
            }
        }
        return result;
    }

    private String getCommands(IGuild g, boolean global) {
        String result = "";
        for(BotModule bm : BotModule.modules) {
            if(bm instanceof CommandModule) {
                CommandModule m = (CommandModule) bm;
                if(!global) {
                    if(m.isShowInHelp() && m.isActive(g) && m.isServerModule()) {
                        result += m.getCommand() + "\n";
                        if(!m.getAliases().isEmpty())
                            for(String alias : m.getAliases())
                                result += alias + "\n";
                    }
                } else {
                    if(m.isShowInHelp() && !m.isServerModule()) {
                        result += m.getCommand() + "\n";
                        if(!m.getAliases().isEmpty())
                            for(String alias : m.getAliases())
                                result += alias + "\n";
                    }
                }
            }
        }
        return result;
    }
}