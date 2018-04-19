package redsli.me.wafflebot.data;

import lombok.Getter;
import org.ocpsoft.prettytime.PrettyTime;
import redsli.me.wafflebot.Wafflebot;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by redslime on 19.04.2018
 */
@Getter
public class Reminder {

    private long owner;
    private long channel;
    private long timestamp;
    private String message;
    private boolean sent;

    public Reminder(IUser owner, String message, long timestamp, IChannel channel) throws IOException {
        this.owner = owner.getLongID();
        this.message = message;
        this.timestamp = timestamp;
        this.channel = channel.getLongID();
        this.sent = false;

        Wafflebot.data.reminders.add(this);
        Wafflebot.save();
        schedule();
    }

    public static void initialize() {
        Wafflebot.data.reminders.removeIf(r -> r.getTimestamp() < System.currentTimeMillis() && r.sent);
        Wafflebot.data.reminders.forEach(Reminder::schedule);
    }

    public void schedule() {
        Timer t = new Timer();
        t.schedule(send(), new Date(timestamp));
    }

    public TimerTask send() {
        return new TimerTask() {
            @Override
            public void run() {
                MessageUtil.sendPM(Wafflebot.client.getUserByID(owner), EmbedPresets.reminder().withDesc(message)
                        .withFooterText("Scheduled in #" + Wafflebot.client.getChannelByID(channel).getName() + " / " + Wafflebot.client.getChannelByID(channel).getGuild().getName()));
                sent = true;
                try {
                    Wafflebot.save();
                } catch (IOException e) {
                    MessageUtil.sendErrorReport(e, null);
                }
            }
        };
    }
}