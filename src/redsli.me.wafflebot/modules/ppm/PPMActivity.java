package redsli.me.wafflebot.modules.ppm;

import org.ocpsoft.prettytime.PrettyTime;
import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static redsli.me.wafflebot.data.HamzaPPM.*;

/**
 * Created by redslime on 01.04.2018
 */
@Module
public class PPMActivity extends CommandModule {

    public PPMActivity() {
        super("PPM Host Activity Checker Module", "Checks activity of all PPM Hosts", true, true);
        trigger("!checkactivity");
        limit(PPM_SERVER);
        setGuildOnly(true);
        setGuildFilter(PPM_SERVER);
        setShowInModulesList(false);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return min(event, ADMIN) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":arrows_counterclockwise: Loading..."));
        String result = "Last PPM = date of last message sent in announcements containing \"ppm\"\n\n";
        MessageHistory mh = event.getGuild().getChannelByID(ANNOUNCEMENTS).getFullMessageHistory();
        HashMap<IUser, Long> lastMessage = new HashMap<>();

        for(IUser user : event.getGuild().getUsersByRole(Wafflebot.client.getRoleByID(PPM_HOST))) {
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

        MessageUtil.editMessage(loading, EmbedPresets.success(result).withUserFooter(event));
    }
}