package xyz.redslime.wafflebot.events;

import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.util.DiscordHelper;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;

import java.awt.*;
import java.io.IOException;

/**
 * Created by redslime on 29.03.2018
 */
public class JoinEvent implements IListener<GuildCreateEvent> {

    @Override
    public void handle(GuildCreateEvent event) {
        long id = event.getGuild().getLongID();

        // Bot joined the server while running
        if(!Wafflebot.data.guildModules.containsKey(id)) {
            WaffleEmbedBuilder embed = new WaffleEmbedBuilder().withColor(Color.ORANGE)
                    .withTitle(":wave: Hello, I'm wafflebot!")
                    .withDesc("Thanks for adding me to your Discord server!")
                    .appendField("Getting Started", "I'm a module-based bot, you can enable or disable certain modules, dependant on your needs!\n" +
                            "All modules can be viewed with ``!modules``\n" +
                            "Members with \"Manage Server\" permissions can turn on/off modules", false)
                    .appendField("Help, Ideas, Suggestions, Comments", "Contact redslime#8503", false);
            MessageUtil.sendMessage(DiscordHelper.getDefaultChannel(event.getGuild()), embed);

            String end = "Love and waffles!";
            if(event.getGuild().getUsers().stream().anyMatch(user -> user.getLongID() == 194384999305314305L)) {
                end = "I also see you used <@194384999305314305> before. To use the same features, activate the ``Brawl`` and ``CTF`` module.\n" + end;
            }
            MessageUtil.sendMessage(DiscordHelper.getDefaultChannel(event.getGuild()), end);

            for(BotModule bm : BotModule.modules) {
                if(bm.isActivatedDefault()) {
                    try {
                        bm.enable(event.getGuild());
                    } catch (IOException e) {
                        MessageUtil.sendErrorReport(e, null);
                    }
                }
            }
        }
    }
}
