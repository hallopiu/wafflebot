package xyz.redslime.wafflebot.util;

import xyz.redslime.wafflebot.Wafflebot;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import xyz.redslime.wafflebot.data.HamzaPPM;

/**
 * Created by redslime on 29.03.2018
 */
public class DiscordHelper {

    public static final String ROLE_REGEX = "<@&([0-9]*)>";
    public static final String USER_REGEX = "<@!?([0-9]*)>";
    public static final String CHANNEL_REGEX = "<#([0-9]*)>";

    public static IChannel getDefaultChannel(IGuild guild) {
        IChannel result = null;
        for(IChannel c : guild.getChannelsByName("general")) {
            result = c;
        }
        if(result == null) {
            for(IChannel c : guild.getChannelsByName("main")) {
                result = c;
            }
        }
        if(result == null)
            result = guild.getDefaultChannel();
        return result;
    }

    public static boolean isRole(String role) {
        return role.matches(ROLE_REGEX);
    }

    public static boolean isUser(String user) {
        return user.matches(USER_REGEX);
    }

    public static boolean isChannel(String channel) {
        return channel.matches(CHANNEL_REGEX);
    }

    public static IRole getRole(String role) {
        return Wafflebot.client.getGuildByID(HamzaPPM.PPM_SERVER).getRoleByID(Long.parseLong(role.replaceAll(ROLE_REGEX, "$1").trim()));
    }

    public static IUser getUser(String user) {
        return Wafflebot.client.getUserByID(Long.parseLong(user.replaceAll(USER_REGEX, "$1").trim()));
    }

    public static IChannel getChannel(String channel) {
        return Wafflebot.client.getChannelByID(Long.parseLong(channel.replaceAll(CHANNEL_REGEX, "$1").trim()));
    }

    public static int getHighestRoleColor(ReactionEvent e) {
        int pos = 0;
        int rgb = 0;
        for(IRole r : e.getMessage().getAuthor().getRolesForGuild(e.getGuild())) {
            if(r.getPosition() > pos || rgb == 0) {
                pos = r.getPosition();
                rgb = r.getColor().getRGB();
            }
        }
        return rgb;
    }
}
