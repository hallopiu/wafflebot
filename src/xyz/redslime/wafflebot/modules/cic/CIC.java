package xyz.redslime.wafflebot.modules.cic;

import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.DiscordHelper;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author redslime
 * @version 2018-10-20
 */
@Module
public class CIC extends CommandModule {

    public static final long SERVER_ID = 489270848230785028L;
    //    public static final long SERVER_ID = 194118228774092800L;
    public static final long CHANNEL_ID = 503298425803112461L;
    //    public static final long CHANNEL_ID = 503295317060616205L;
    public static final long MEMBER_ROLE = 489274937769459712L;
//    public static final long MEMBER_ROLE = 194507643027587072L;

    private static LinkedHashMap<String, String> days = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> times = new LinkedHashMap<>();

    static {
        days.put("Friday", ":regional_indicator_f:");
        days.put("Saturday", ":regional_indicator_s:");
        days.put("Sunday", ":cross:");

        times.put("1pm EST", ":one:");
        times.put("2pm EST", ":two:");
        times.put("3pm EST", ":three:");
        times.put("4pm EST", ":four:");
        times.put("5pm EST", ":five:");
    }

    public CIC() {
        super("CIC", "CIC Meeting planner", true, true);
        trigger("!setupmeetingtimes");
        limit(SERVER_ID);
        setGuildOnly(true);
        setGuildFilter(SERVER_ID);
        setShowInModulesList(false);
        setShowInHelp(false);

        setInit(() -> {
            Wafflebot.client.getChannelByID(CHANNEL_ID).getFullMessageHistory().forEach(m -> {
                if(!m.getReactions().isEmpty()) {
                    m.getReactions().forEach(r -> r.getUsers().forEach(u -> handleReaction(m, null, u)));
                }
            });
            Wafflebot.data.timeEntries.forEach(te -> updateMessage(te.listMessageId));
        });
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return role(event, MEMBER_ROLE) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        IMessage msg = MessageUtil.sendMessage(event, EmbedPresets.loading("Setting up..."));
        IChannel c = Wafflebot.client.getChannelByID(CHANNEL_ID);

        IMessage listMsg = MessageUtil.sendMessage(c, EmbedPresets.information().withTitle("Meeting Time List").withDesc("React on all times that works for you"));

        c.pin(listMsg);
        for(Map.Entry<String, String> day : days.entrySet()) {
            for(Map.Entry<String, String> time : times.entrySet()) {
                String dayTime = day.getValue() + time.getValue() + " " + day.getKey() + " / " + time.getKey();
                IMessage m = MessageUtil.sendMessage(c, dayTime);
                RequestBuffer.request(() -> m.addReaction(getEmoji()));
                TimeEntry.builder().dayTime(dayTime)
                        .messageId(m.getLongID()).users(new ArrayList<>()).listMessageId(listMsg.getLongID()).build().add();
            }
        }
        MessageUtil.editMessage(msg, EmbedPresets.success("Done").build());
        try {
            Wafflebot.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleReaction(IMessage m, IReaction reaction, IUser reactionUser) {
        if(m.getGuild().getLongID() != SERVER_ID)
            return;

        Wafflebot.data.timeEntries.stream().filter(e -> e.getMessageId() == m.getLongID())
                .filter(e -> reactionUser != Wafflebot.client.getOurUser())
                .filter(e -> DiscordHelper.hasRole(reactionUser, m.getGuild(), MEMBER_ROLE))
                .forEach(e -> {
                    if(m.getReactionByEmoji(getEmoji()).getUserReacted(reactionUser)) {
                        if(!e.users.contains(reactionUser.getLongID())) {
                            e.users.add(reactionUser.getLongID());
                            updateMessage(e.getListMessageId());
                        }
                    } else {
                        e.users.remove(reactionUser.getLongID());
                        updateMessage(e.getListMessageId());
                    }
                });
    }

    private static ReactionEmoji getEmoji() {
        return ReactionEmoji.of("✅");
    }

    private static List<TimeEntry> getTimeEntriesByListMessage(long id) {
        return Wafflebot.data.timeEntries.stream().filter(e -> e.getListMessageId() == id).collect(Collectors.toList());
    }

    private static void updateMessage(long listMessageId) {
        IMessage msg = Wafflebot.client.getChannelByID(CHANNEL_ID).fetchMessage(listMessageId);

        if(msg != null) {
            WaffleEmbedBuilder e = EmbedPresets.information().withTitle("Meeting Time List");
//            StringBuilder sb = new StringBuilder("");
            getTimeEntriesByListMessage(listMessageId).forEach(timeEntry -> {
                if(!timeEntry.getUsers().isEmpty()) {
                    e.appendField(timeEntry.getDayTime(), getUsers(timeEntry.getUsers()), false);
//                    sb.append(timeEntry.getDayTime()).append("\n").append(getUsers(timeEntry.getUsers())).append("\n");
                }
            });
//            e.withDesc(sb.toString());
            MessageUtil.editMessage(msg, e.build());
        }
    }

    private static String getUsers(List<Long> users) {
        return Strings.join(users.stream().map(e -> Wafflebot.client.getUserByID(e).mention()).collect(Collectors.toList()), ' ');
    }

    @Data
    @Builder
    public static class TimeEntry {
        private String dayTime;
        private long messageId;
        private long listMessageId;
        private List<Long> users;

        public void add() {
            Wafflebot.data.timeEntries.add(this);
        }

        public void remove() {
            Wafflebot.data.timeEntries.remove(this);
        }
    }
}
