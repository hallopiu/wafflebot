package xyz.redslime.wafflebot.modules;

import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.SSHelper;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Module
public class CTFSS extends CommandModule {

    public CTFSS() {
        super("CTF Match Spreadsheet Module", "Gets information from the spreadsheet for you", false, true);
        trigger("!ss");
        aliases("!spreadsheet");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        String[] args = event.getMessage().getContent().split(" ");

        if(args.length == 1) {
            MessageUtil.sendMessage(event, EmbedPresets.error("Expected: !ss [all/past/upcoming/now]"));
            return false;
        }
        if(args.length > 1) {
            if(!(args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("past") || args[1].equalsIgnoreCase("upcoming") || args[1].equalsIgnoreCase("now"))) {
                MessageUtil.sendMessage(event, EmbedPresets.error("Expected: !ss [all/past/upcoming/now]"));
                return false;
            }
        }
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":arrow_heading_down: Fetching data..."));
        try {
            List<SSHelper.Match> matches = SSHelper.getMatches();
            switch (event.getMessage().getContent().split(" ")[1].toLowerCase()) {
                case "upcoming": {
                    matches = matches.stream().filter(m -> m.begin.getTime() >= Date.from(Instant.now()).getTime()).collect(Collectors.toList());
                    break;
                }

                case "past": {
                    matches = matches.stream().filter(m -> m.begin.getTime() <= Date.from(Instant.now()).getTime()).collect(Collectors.toList());
                    break;
                }

                case "now": {
                    matches.clear();
                    SSHelper.Match now = SSHelper.getNow();
                    if(now != null)
                        matches.add(now);
                    else {
                        SSHelper.Match next = SSHelper.getNext();
                        String info = "\n";
                        if(next != null)
                            info += "Next match on " + next.parent.getDay() + ", " + next.parent.getTimeEST() + " EST";
                        MessageUtil.editMessage(loading, EmbedPresets.success("The match server should be empty" + info).withUserFooter(event));
                        return;
                    }
                }
            }

            WaffleEmbedBuilder result = EmbedPresets.success().withUserFooter(event);
            int fields = 0;
            for(SSHelper.Match match : matches) {
                SimpleDateFormat date = new SimpleDateFormat("MMMM d");
                date.setTimeZone(TimeZone.getTimeZone("EST"));

                SimpleDateFormat time = new SimpleDateFormat("h:mma");
                time.setTimeZone(TimeZone.getTimeZone("EST"));

                if(fields != 0 && fields % 25 == 0) {
                    loading.delete();
                    MessageUtil.sendMessage(event, result);
                    result.clearFields();
                } else
                    result.appendField(match.name, match.parent.getDay() + ", " + date.format(match.begin) + "\n" + time.format(match.begin) + " - " + time.format(match.end) + " EST", false);
                fields++;
            }
            if(fields < 25)
                MessageUtil.editMessage(loading, result);
            else
                MessageUtil.sendMessage(event, result);
        } catch (Exception e) {
            MessageUtil.editMessage(loading, EmbedPresets.error(e + ""));
            e.printStackTrace();
            throw e;
        }
    }
}