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
    public static final String ROLE_REGEX_FRONT = "@(.*)";
    public static final String USER_REGEX = "<@!?([0-9]*)>";
    public static final String USER_REGEX_FRONT = "@(.*)#([0-9]{4})";
    public static final String CHANNEL_REGEX = "<#([0-9]*)>";
    public static final String CHANNEL_REGEX_FRONT = "#(.*)";

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
        return role.matches(ROLE_REGEX) || role.matches(ROLE_REGEX_FRONT);
    }

    public static boolean isUser(String user) {
        return user.matches(USER_REGEX) || user.matches(USER_REGEX_FRONT);
    }

    public static boolean isChannel(String channel) {
        return channel.matches(CHANNEL_REGEX) || channel.matches(CHANNEL_REGEX_FRONT);
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

    public static IChannel getChannel(IGuild guild, Object channel) {
        if(channel instanceof Long)
            return Wafflebot.client.getChannelByID((Long) channel);
        if(channel instanceof String) {
            IChannel possibleResult = getChannel((String) channel);
            if(possibleResult != null)
                return possibleResult;
            if(((String) channel).matches(CHANNEL_REGEX_FRONT)) {
                for(IChannel c : guild.getChannels()) {
                    if(c.getName().equalsIgnoreCase(((String) channel).replaceAll(CHANNEL_REGEX_FRONT, "$1")))
                        return c;
                }
            }
            for(IChannel c : guild.getChannels()) {
                if(c.getName().equalsIgnoreCase((String) channel))
                    return c;
            }
        }
        return null;
    }

    public static IUser getUser(IGuild guild, Object user) {
        if(user instanceof Long)
            return Wafflebot.client.getUserByID((Long) user);
        if(user instanceof String) {
            IUser possibleUser = getUser((String) user);
            if(possibleUser != null)
                return possibleUser;
            if(((String) user).matches(USER_REGEX_FRONT)) {
                String name = ((String) user).replaceAll(USER_REGEX_FRONT, "$1");
                String discriminator = ((String) user).replaceAll(USER_REGEX_FRONT, "$2");
                for(IUser u : guild.getUsers()) {
                    if(u.getName().equalsIgnoreCase(name) && u.getDiscriminator().equalsIgnoreCase(discriminator))
                        return u;
                }
            }
            for(IUser u : guild.getUsers()) {
                if(u.getName().equalsIgnoreCase((String) user))
                    return u;
            }
        }
        return null;
    }

    public static IRole getRole(IGuild guild, Object role) {
        if(role instanceof Long)
            return Wafflebot.client.getRoleByID((Long) role);
        if(role instanceof String) {
            if(((String) role).equalsIgnoreCase("@everyone"))
                return guild.getEveryoneRole();
            IRole possibleRole = getRole((String) role);
            if(possibleRole != null)
                return possibleRole;
            if(((String) role).matches(ROLE_REGEX_FRONT)) {
                for(IRole r : guild.getRoles()) {
                    if(r.getName().equalsIgnoreCase(((String) role).replaceAll(ROLE_REGEX_FRONT, "$1")))
                        return r;
                }
            }
            for(IRole r : guild.getRoles()) {
                if(r.getName().equalsIgnoreCase((String) role))
                    return r;
            }
        }
        return null;
    }
}
