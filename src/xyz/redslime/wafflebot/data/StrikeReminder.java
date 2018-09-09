package xyz.redslime.wafflebot.data;

import lombok.Getter;
import org.ocpsoft.prettytime.PrettyTime;
import sx.blah.discord.handle.obj.IUser;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by redslime on 09.09.2018
 */
@Getter
public class StrikeReminder {

    private PPMStrike strike;
    private boolean sent;

    public StrikeReminder(PPMStrike strike) {
        this.strike = strike;

        Wafflebot.data.strikeReminders.add(this);
        try {
            Wafflebot.save();
        } catch (IOException e) {
            MessageUtil.sendErrorReport(e, null);
        }
        schedule();
    }

    public static void initialize() {
        Wafflebot.data.strikeReminders.removeIf(r -> r.getStrike().getExpirationTimestamp() < System.currentTimeMillis() && r.sent);
        Wafflebot.data.strikeReminders.forEach(StrikeReminder::schedule);
    }

    public void schedule() {
        Timer t = new Timer();
        t.schedule(send(), new Date(strike.getExpirationTimestamp()));
    }

    public TimerTask send() {
        return new TimerTask() {
            @Override
            public void run() {
                IUser striked = Wafflebot.client.getUserByID(strike.getStriked());
                IUser strikedBy = Wafflebot.client.getUserByID(strike.getStrikedBy());
                String ago = new PrettyTime().format(new Date(strike.getTimestamp()));

                MessageUtil.sendMessage(Wafflebot.client.getChannelByID(CTFCommunityDiscord.PPM_HOSTS_CHAT),
                        EmbedPresets.information().withTitle(":zap: Strike expired")
                                .withDesc(strike.getTier().getEmoji() + " " + striked.mention() + ", striked by " + strikedBy.mention() + ", " + ago + "\n" +
                                        "Reason: " + strike.getReason())
                                .withTimestamp(System.currentTimeMillis())
                                .build());
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
