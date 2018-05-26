package xyz.redslime.wafflebot.modules;

import sx.blah.discord.handle.obj.IGuild;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.DiscordHelper;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by redslime on 02.04.2018
 */
@Module
public class Config extends CommandModule {

    public static final String ARG_2 = "!config get";
    public static final String ARG_3 = "!config [ignore/unignore] [#channel]";
    public static final String ARG_4 = "!config setchannel [module] [#channel]";

    public Config() {
        super("Wafflebot Configuration Module", "Allows you to tell Wafflebot to (un)ignore specific channels and setting default channels for automated modules", true, false);
        trigger("!config");
        setShowInHelp(true);
        setGuildOnly(true);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return perm(event, Permissions.MANAGE_SERVER) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();
        String[] args = msg.split(" ");
        IGuild guild = event.getGuild();

        if(args.length == 2 && args[1].equalsIgnoreCase("get")) {
            WaffleEmbedBuilder embed = EmbedPresets.information().withTitle("Wafflebot configuration in " + event.getGuild().getName()).withThumbnail(event.getGuild().getIconURL())
                    .withTimestamp(System.currentTimeMillis()).withUserFooter(event);
            String customOutput = "";
            List<String> ignored = new ArrayList<>();
            String ignoredString = "";
            for(BotModule bm : BotModule.modules) {
                if(bm.isUsesOutputChannel() && bm.isActive(event.getGuild())) {
                    customOutput += bm.getName() + " [``" + bm.getClass().getSimpleName() + "``]: " + Wafflebot.data.getModuleChannel(event.getGuild(), bm).mention() + "\n";
                }
            }
            Wafflebot.data.baseConfigurations.stream().filter(m -> m.guild == event.getGuild().getLongID()).forEach(m -> {
                m.ignoreChannels.forEach(channel -> ignored.add(Wafflebot.client.getChannelByID(channel).mention()));
            });
            for(String i : ignored)
                ignoredString += i + "\n";

            embed.appendField("Output Channels", customOutput, false).appendField("Ignored Channels", ignoredString.equals("") ? "None" : ignoredString, false);

            MessageUtil.sendMessage(event, embed);
        } else if(args.length == 3) {
            if(DiscordHelper.isChannel(guild, args[2])) {
                IChannel channel = DiscordHelper.getChannel(guild, args[2]);
                switch (args[1].toLowerCase().trim()) {
                    case "ignore": {
                        Wafflebot.data.ignoreChannel(event.getGuild(), channel);
                        MessageUtil.sendMessage(event, EmbedPresets.success("Now ignoring everything in " + channel.mention() + "\nDo !config unignore " + channel.mention() + " to undo this").withUserFooter(event));
                        break;
                    }

                    case "unignore": {
                        Wafflebot.data.unignoreChannel(event.getGuild(), channel);
                        MessageUtil.sendMessage(event, EmbedPresets.success("No longer ignoring everything in " + channel.mention()).withUserFooter(event));
                        break;
                    }
                }
                Wafflebot.save();
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("\"" + args[2] + "\" is not a channel!\nExpected: " + ARG_3));
        } else if(args.length == 4 && args[1].equalsIgnoreCase("setchannel")) {
            if(BotModule.get(args[2]) != null) {
                if(BotModule.get(args[2]).isUsesOutputChannel()) {
                    if(DiscordHelper.isChannel(guild, args[3])) {
                        IChannel channel = DiscordHelper.getChannel(guild, args[3]);
                        Wafflebot.data.setModuleChannel(args[2], channel);
                        MessageUtil.sendMessage(event, EmbedPresets.success("Channel set!"));
                        Wafflebot.save();
                    } else
                        MessageUtil.sendMessage(event, EmbedPresets.error("\"" + args[3] + "\" is not a channel!\nExpected: " + ARG_4));
                } else
                    MessageUtil.sendMessage(event, EmbedPresets.error("You can't set a default channel for this module! [setchannel] defines a channel where modules that automatically send messages should post their messages in.\n" +
                            "To limit usage of other modules, you may [ignore] certain channels: !config ignore [#channel]")); //TODO: Why not also set a channel normal modules?
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("\"" + args[2] + "\" is not a module!\nExpected: " + ARG_4).withFooterText("Do !modules for a list of modules"));
        } else
            MessageUtil.sendMessage(event, EmbedPresets.error("Expected: " + ARG_2 + "\nExpected: " + ARG_3 + "\nExpected: " + ARG_4));
    }
}
