package xyz.redslime.wafflebot.util;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.obj.*;
import xyz.redslime.wafflebot.Wafflebot;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by redslime on 29.03.2018
 */
public class DiscordHelper {

    private static final String ROLE_REGEX = "<@&([0-9]*)>";
    private static final String ROLE_REGEX_FRONT = "@(.*)";
    private static final String USER_REGEX = "<@!?([0-9]*)>";
    private static final String USER_REGEX_FRONT = "@(.*)#([0-9]{4})";
    private static final String CHANNEL_REGEX = "<#([0-9]*)>";
    private static final String CHANNEL_REGEX_FRONT = "#(.*)";

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

    public static boolean isRole(IGuild guild, String role) {
        return getRole(guild, role) != null;
    }

    public static boolean isUser(IGuild guild, String user) {
        return getUser(guild, user) != null;
    }

    public static boolean isChannel(IGuild guild, String channel) {
        return getChannel(guild, channel) != null;
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
            if(Utils.getLong((String) channel) != -1L)
                return Wafflebot.client.getChannelByID(Utils.getLong((String) channel));
            if(((String) channel).trim().matches(CHANNEL_REGEX))
                return Wafflebot.client.getChannelByID(Long.parseLong(((String) channel).trim().replaceAll(CHANNEL_REGEX, "$1")));
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
            if(Utils.getLong((String) user) != -1L)
                return Wafflebot.client.getUserByID(Utils.getLong((String) user));
            if(((String) user).trim().matches(USER_REGEX))
                return Wafflebot.client.getUserByID(Long.parseLong(((String) user).replaceAll(USER_REGEX, "$1").trim()));
            if(((String) user).matches(USER_REGEX_FRONT)) {
                String name = ((String) user).replaceAll(USER_REGEX_FRONT, "$1");
                String discriminator = ((String) user).replaceAll(USER_REGEX_FRONT, "$2");
                for(IUser u : guild.getUsers()) {
                    if(u.getNicknameForGuild(guild) != null && u.getNicknameForGuild(guild).equalsIgnoreCase(name) && u.getDiscriminator().equalsIgnoreCase(discriminator))
                        return u;
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
            if(Utils.getLong((String) role) != -1L)
                return Wafflebot.client.getRoleByID(Utils.getLong((String) role));
            if(((String) role).equalsIgnoreCase("@everyone"))
                return guild.getEveryoneRole();
            if(((String) role).trim().matches(ROLE_REGEX))
                return Wafflebot.client.getRoleByID(Long.parseLong(((String) role).trim().replaceAll(ROLE_REGEX, "$1")));
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

    public static boolean hasRole(IUser user, IGuild guild, long role) {
        return user.getRolesForGuild(guild).stream().map(IRole::getLongID).anyMatch(l -> l == role);
    }

    public static boolean hasRole(long user, IGuild guild, List<Long> roles) {
        return Wafflebot.client.getUserByID(user).getRolesForGuild(guild).stream().map(IRole::getLongID).anyMatch(roles::contains);
    }

    public static void addToCache(IMessage msg) {
        if(msg.getChannel() instanceof Channel) {
            Channel c = (Channel) msg.getChannel();
            if(!c.messages.containsKey(msg))
                c.addToCache(msg);
        }
    }

    public static IRole getOrCreateRole(String roleName, IGuild guild) {
        return guild.getRoles().stream().filter(r -> r.getName().equals(roleName)).findFirst().orElseGet(() -> {
            IRole newRole = guild.createRole();
            newRole.edit(Color.LIGHT_GRAY, false, roleName, EnumSet.noneOf(Permissions.class), false);
            return newRole;
        });
    }
}
