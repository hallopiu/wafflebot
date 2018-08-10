package xyz.redslime.wafflebot.modules.ctfcommunity;

import org.ocpsoft.prettytime.PrettyTime;
import sx.blah.discord.handle.obj.IChannel;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;
import xyz.redslime.wafflebot.data.CTFCommunityDiscord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by redslime on 01.04.2018
 */
@Module
public class PPMActivity extends CommandModule {

    public PPMActivity() {
        super("PPM Host Activity Checker Module", "Checks activity of all PPM Hosts", true, true);
        trigger("!checkactivity");
        aliases("!adminactivity");
        limit(CTFCommunityDiscord.SERVER);
        setGuildOnly(true);
        setGuildFilter(CTFCommunityDiscord.SERVER);
        setShowInModulesList(false);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return role(event, CTFCommunityDiscord.SERVER_ADMIN) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":arrows_counterclockwise: Loading... This may take several minutes"));
        HashMap<IUser, Long> lastMessage = new HashMap<>();
        new Thread(() -> {
            String result = "";
            if(event.getMessage().getContent().toLowerCase().startsWith("!checkactivity")) {
                result = "Last PPM = date of last message sent in announcements containing \"ctfcommunity\"\n\n";
                MessageHistory mh = event.getGuild().getChannelByID(CTFCommunityDiscord.ANNOUNCEMENTS).getFullMessageHistory();
                for(IUser user : event.getGuild().getUsersByRole(Wafflebot.client.getRoleByID(CTFCommunityDiscord.PPM_HOST))) {
                    for(IMessage message : mh.asArray()) {
                        if(message.getAuthor().getLongID() == user.getLongID() && message.getContent().toLowerCase().contains("ppm")) {
                            long timestamp = message.getTimestamp().toEpochMilli();
                            if(lastMessage.containsKey(user)) {
                                if(lastMessage.get(user) < timestamp)
                                    lastMessage.put(user, timestamp);
                            } else
                                lastMessage.put(user, timestamp);
                        }
                    }
                    if(lastMessage.containsKey(user))
                        result += user.mention() + "'s last PPM: " + new PrettyTime().format(new Date(lastMessage.get(user))) + " (" + new SimpleDateFormat("YYYY-MM-dd").format(new Date(lastMessage.get(user))) + ")\n";
                    else
                        result += user.mention() + "'s last PPM: unknown\n";
                }
            } else {
                result = "Last message in any chat\n\n";
                List<IUser> admins = event.getGuild().getUsersByRole(Wafflebot.client.getRoleByID(CTFCommunityDiscord.SERVER_ADMIN));
                for(IChannel c : event.getGuild().getChannels()) {
                    MessageHistory mh = c.getMessageHistoryTo(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(60));
                    for(IMessage m : mh.asArray()) {
                        if(admins.contains(m.getAuthor())) {
                            long timestamp = m.getTimestamp().toEpochMilli();
                            if(lastMessage.containsKey(m.getAuthor())) {
                                if(lastMessage.get(m.getAuthor()) < timestamp)
                                    lastMessage.put(m.getAuthor(), timestamp);
                            } else
                                lastMessage.put(m.getAuthor(), timestamp);
                        }
                    }
                }
                for(IUser admin : admins) {
                    if(lastMessage.containsKey(admin))
                        result += admin.mention() + "'s last message: " + new PrettyTime().format(new Date(lastMessage.get(admin))) + " (" + new SimpleDateFormat("YYYY-MM-dd").format(new Date(lastMessage.get(admin))) + ")\n";
                    else
                        result += admin.mention() + "'s last message: longer than 2 months ago\n";
                }
            }
            MessageUtil.editMessage(loading, EmbedPresets.success(result).withUserFooter(event));
        }).start();

    }
}