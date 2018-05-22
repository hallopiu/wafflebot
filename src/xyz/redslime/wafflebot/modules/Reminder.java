package xyz.redslime.wafflebot.modules;

import org.ocpsoft.prettytime.PrettyTime;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.Utils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by redslime on 19.04.2018
 */
@Module
public class Reminder extends CommandModule {

    private static final HashMap<Character, TimeUnit> units = new HashMap<>();

    static {
        units.put('s', TimeUnit.SECONDS);
        units.put('m', TimeUnit.MINUTES);
        units.put('h', TimeUnit.HOURS);
        units.put('d', TimeUnit.DAYS);
    }

    public Reminder() {
        super("Reminder Module", "Schedules a reminder as a private message", true, false);
        trigger("!remind");
        setUsage("!remind [number + s/m/h/d] [message]");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        String[] args = event.getMessage().getContent().split(" ");

        if(args.length < 3) {
            MessageUtil.sendMessage(event, EmbedPresets.error(getUsage()));
            return false;
        } else {
            if(!units.keySet().contains(args[1].charAt(args[1].length() - 1))) {
                MessageUtil.sendMessage(event, EmbedPresets.error(getUsage()));
                return false;
            }
        }
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        try {
            String[] args = event.getMessage().getContent().split(" ");
            String message = Utils.connectArgs(args, 2);
            TimeUnit unit = units.get(args[1].charAt(args[1].length() - 1));
            int number = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
            long timestamp = System.currentTimeMillis() + unit.toMillis(number);

            new xyz.redslime.wafflebot.data.Reminder(event.getAuthor(), message, timestamp, event.getChannel());
            MessageUtil.sendMessage(event, EmbedPresets.success("Sending you the reminder in " + new PrettyTime().format(new Date(timestamp))));
        } catch (Exception e) {
            MessageUtil.sendMessage(event, EmbedPresets.error(e + ""));
            throw e;
        }
    }
}
