package xyz.redslime.wafflebot.data;

import lombok.Data;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.util.DiscordHelper;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by redslime on 29.06.2018
 */
@Data
public class WaffleEvent {

    private String name;
    private String description;
    private List<Long> roles = new ArrayList<>();
    private List<Long> tagRoles = new ArrayList<>();
    private long announceChannel;
    private long listChannel;
    private String signUpEmoji;
    private String rejectEmoji;
    private long timestamp;
    private List<UserEntry> playing = new ArrayList<>();
    private List<UserEntry> notPlaying = new ArrayList<>();
    private long giveRole;
    private long announcementMessage;
    private long listMessage;

    public static void checkReaction(IMessage m, IReaction reaction, IUser reactionUser) {
        String emoji = reaction.getEmoji().getName();
        Wafflebot.data.events.stream().filter(e -> (e.signUpEmoji != null && e.signUpEmoji.equals(emoji)) || (e.rejectEmoji != null && e.rejectEmoji.equals(emoji)))
                .filter(e -> e.announcementMessage == m.getLongID() && reactionUser != Wafflebot.client.getOurUser())
                .filter(e -> {
                    if(!e.roles.isEmpty()) {
                        return DiscordHelper.hasRole(reactionUser.getLongID(), m.getGuild(), e.roles);
                    } else return true;
                })
                .forEach(e -> {
                    boolean playing = e.signUpEmoji.equals(emoji);
                    e.updateParticipantsList(reactionUser, playing);
                });
    }

    public void save() throws IOException {
        if(!Wafflebot.data.events.contains(this))
            Wafflebot.data.events.add(this);
        Wafflebot.save();
    }

    public void delete() {
        Wafflebot.data.events.remove(this);
        try {
            Wafflebot.save();
            RequestBuffer.request(() -> getListMessage().delete());
        } catch (IOException e) {
            MessageUtil.sendErrorReport(e, null);
        }
    }

    public void init() throws IOException {
        timestamp = System.currentTimeMillis();
        IMessage announcement = MessageUtil.sendMessage(Wafflebot.client.getChannelByID(announceChannel), mentionRoles(), buildAnnouncement());
        if(getPlayingEmoji() != null)
            RequestBuffer.request(() -> announcement.addReaction(getPlayingEmoji()));
        if(getNotPlayingEmoji() != null)
            RequestBuffer.request(() -> announcement.addReaction(getNotPlayingEmoji()));
        announcementMessage = announcement.getLongID();
        IMessage list = MessageUtil.sendMessage(Wafflebot.client.getChannelByID(listChannel), buildList());
        listMessage = list.getLongID();
        save();
    }

    public IMessage getAnnouncementMessage() {
        if(Wafflebot.client.getMessageByID(announcementMessage) != null)
            return Wafflebot.client.getMessageByID(announcementMessage);
        return RequestBuffer.request(() -> Wafflebot.client.getChannelByID(announceChannel).fetchMessage(announcementMessage)).get();
    }

    private IMessage getListMessage() {
        if(Wafflebot.client.getMessageByID(listMessage) != null)
            return Wafflebot.client.getMessageByID(listMessage);
        return RequestBuffer.request(() -> Wafflebot.client.getChannelByID(listChannel).fetchMessage(listMessage)).get();
    }

    private void updateParticipantsList(IUser changed, boolean playing) {
        new Thread(() -> {
            if(playing) {
                if(getAnnouncementMessage().getReactionByEmoji(getPlayingEmoji()).getUsers().contains(changed)) {
                    this.playing.add(new UserEntry(changed));
                    if(getGiveRole() != null)
                        RequestBuffer.request(() -> changed.addRole(getGiveRole()));
                } else {
                    this.playing.stream().filter(e -> e.user == changed.getLongID()).findFirst().ifPresent(this.playing::remove);
                    if(getGiveRole() != null)
                        RequestBuffer.request(() -> changed.removeRole(getGiveRole()));
                }
            } else {
                if(getAnnouncementMessage().getReactionByEmoji(getNotPlayingEmoji()).getUsers().contains(changed))
                    this.notPlaying.add(new UserEntry(changed));
                else
                    this.notPlaying.stream().filter(e -> e.user == changed.getLongID()).findFirst().ifPresent(this.notPlaying::remove);
            }

            MessageUtil.editMessage(getListMessage(), buildList());
            try {
                Wafflebot.save();
            } catch (IOException e) {
                MessageUtil.sendErrorReport(e, null);
            }
        }).start();
    }

    private String mentionRoles() {
        if(tagRoles == null)
            return " ";
        return String.join(" ", tagRoles.stream().map(m -> Wafflebot.client.getRoleByID(m).mention()).collect(Collectors.toList()));
    }

    private EmbedObject buildAnnouncement() {
        WaffleEmbedBuilder e = new WaffleEmbedBuilder().withTitle(name).withDesc(description)
                .withThumbnail(Wafflebot.client.getChannelByID(announceChannel).getGuild().getIconURL());
        if(signUpEmoji != null)
            e.appendDescription("\n\nReact with " + signUpEmoji + " to play");
        if(rejectEmoji != null)
            e.appendDescription("\nReact with " + rejectEmoji + " if you can't play");
        return e.build();
    }

    private EmbedObject buildList() {
        WaffleEmbedBuilder e = new WaffleEmbedBuilder().withTitle(name).withDesc(description).withFooterText("Last updated").withTimestamp(System.currentTimeMillis());
        if(!getPlaying().equalsIgnoreCase("nobody"))
            e.appendField(signUpEmoji + " Playing (" + getPlayingUsers().size() + ")", getPlaying(), false);
        if(!getNotPlaying().equalsIgnoreCase("nobody"))
            e.appendField(rejectEmoji + " Not playing (" + getNotPlayingUsers().size() + ")", getNotPlaying(), false);
        return e.build();
    }

    private List<IUser> getPlayingUsers() {
        List<UserEntry> users = this.playing;
        if(roles != null && !roles.isEmpty())
            users = users.stream().filter(u -> DiscordHelper.hasRole(u.user, getGuild(), roles)).collect(Collectors.toList());
        users.sort(Comparator.comparingLong(o -> o.timestamp));
        return users.stream().map(u -> Wafflebot.client.getUserByID(u.user)).collect(Collectors.toList());
    }

    private String getPlaying() {
        if(signUpEmoji == null)
            return "Nobody";
        if(getAnnouncementMessage() == null || getAnnouncementMessage().getReactionByEmoji(getPlayingEmoji()) == null)
            return "Nobody";
        List<IUser> users = getPlayingUsers();
        if(users.isEmpty() || (users.size() == 1 && users.get(0) == Wafflebot.client.getOurUser()))
            return "Nobody";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < users.size(); i++) {
            IUser user = users.get(i);
            if(!user.equals(Wafflebot.client.getOurUser())) {
                builder.append(i + 1).append(". ").append(user.mention()).append("\n");
            }
        }
        return builder.toString();
    }

    private List<IUser> getNotPlayingUsers() {
        List<UserEntry> users = this.notPlaying;
        if(roles != null && !roles.isEmpty())
            users = users.stream().filter(u -> DiscordHelper.hasRole(u.user, getGuild(), roles)).collect(Collectors.toList());
        users.sort(Comparator.comparingLong(o -> o.timestamp));
        return users.stream().map(u -> Wafflebot.client.getUserByID(u.user)).collect(Collectors.toList());
    }

    private String getNotPlaying() {
        if(rejectEmoji == null)
            return "Nobody";
        if(getAnnouncementMessage() == null || getAnnouncementMessage().getReactionByEmoji(getNotPlayingEmoji()) == null)
            return "Nobody";
        List<IUser> users = getNotPlayingUsers();
        if(users.isEmpty() || (users.size() == 1 && users.get(0) == Wafflebot.client.getOurUser()))
            return "Nobody";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < users.size(); i++) {
            IUser user = users.get(i);
            if(!user.equals(Wafflebot.client.getOurUser())) {
                builder.append(i + 1).append(". ").append(user.mention()).append("\n");
            }
        }
        return builder.toString();
    }

    private IGuild getGuild() {
        return getAnnouncementMessage().getGuild();
    }

    private ReactionEmoji getPlayingEmoji() {
        return ReactionEmoji.of(signUpEmoji);
    }

    private ReactionEmoji getNotPlayingEmoji() {
        return ReactionEmoji.of(rejectEmoji);
    }

    private IRole getGiveRole() {
        if(this.giveRole == 0)
            return null;
        return RequestBuffer.request(() -> Wafflebot.client.getRoleByID(this.giveRole)).get();
    }

    @Override
    public String toString() {
        return "**Name:** " + name + "\n" +
                "**Description:** " + description + "\n" +
                "**Allowed roles:** " + String.join(", ", roles.stream().map(m -> Wafflebot.client.getRoleByID(m).getName()).collect(Collectors.toList())) + "\n" +
                "**Role for players:** " + (getGiveRole() != null ? getGiveRole().getName() : "None") + "\n" +
                "**Announcement channel:** " + Wafflebot.client.getChannelByID(announceChannel).mention() + "\n" +
                "**Participants list channel:** " + Wafflebot.client.getChannelByID(listChannel).mention() + "\n" +
                "**Sign up emoji:** " + signUpEmoji + "\n" +
                "**Reject emoji:** " + rejectEmoji;
    }

    public class UserEntry {
        long user;
        long timestamp;

        UserEntry(IUser user) {
            this.user = user.getLongID();
            this.timestamp = System.currentTimeMillis();
        }
    }
}