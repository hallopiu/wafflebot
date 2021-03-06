package xyz.redslime.wafflebot.modules.ctfcommunity;

import org.ocpsoft.prettytime.PrettyTime;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.data.CTFCommunityDiscord;
import xyz.redslime.wafflebot.data.PPMStrike;
import xyz.redslime.wafflebot.data.StrikeReminder;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by redslime on 13.08.2018
 */
@Module
public class Strike extends CommandModule {

    public Strike() {
        super("PPM Strike Module", "Allows PPM Hosts to restrict people", true, true);
        trigger("!strike");
        aliases("!strikes");
        limit(CTFCommunityDiscord.SERVER);
        setGuildOnly(true);
        setGuildFilter(CTFCommunityDiscord.SERVER);
        setShowInModulesList(false);
        setUsage("!strike list\n!strike @user [reason]\n!strike info @user\n!strike remove @user");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event) && role(event, CTFCommunityDiscord.PPM_HOST);
//        return super.verify(event) && event.getAuthor().getLongID() == 115834525329653760L;
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();
        String[] args = msg.split(" ");

        if(args.length >= 3) {
            if(DiscordHelper.isUser(event.getGuild(), args[1])) {
                IUser striked = DiscordHelper.getUser(event.getGuild(), args[1]);
                IUser strikedBy = event.getAuthor();
                String reason = Utils.connectArgs(args, 2);
                StrikeTier tier = getNextStrikeTier(striked.getLongID());
                PPMStrike strike = PPMStrike.builder()
                        .striked(striked.getLongID())
                        .strikedBy(strikedBy.getLongID())
                        .reason(reason)
                        .timestamp(System.currentTimeMillis())
                        .tier(tier)
                        .build();
                new StrikeReminder(strike);
                Wafflebot.data.strikes.add(strike);
                Wafflebot.save();

                MessageUtil.sendMessage(event, EmbedPresets.success("Striked " + striked.mention() + "!\nReason: " + reason));
                MessageUtil.sendMessage(Wafflebot.client.getChannelByID(CTFCommunityDiscord.STRIKE_ANNOUNCEMENT),
                        new WaffleEmbedBuilder().withTitle(":zap: Player Striked")
                                .withDesc(striked.mention() + " has been striked.\n" +
                                        "Reason: " + reason + "\n" +
                                        "This is their " + tier.name().toLowerCase() + " strike")
                                .withColor(Color.YELLOW)
                                .withTimestamp(System.currentTimeMillis())
                                .build());

                return;
            }
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("!strikes")) {
                buildStrikesList(s -> true, event).forEach(e -> MessageUtil.sendMessage(event, e));
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("Expected: !strike list\n!strike @user [reason]\n!strike info @user\n!strike remove @user"));
        } else if(args.length == 2) {
            if(args[1].equalsIgnoreCase("list")) {
                buildStrikesList(s -> true, event).forEach(e -> MessageUtil.sendMessage(event, e));
            }
            if(args[0].equalsIgnoreCase("!strikes") && DiscordHelper.isUser(event.getGuild(), args[1])) {
                IUser striked = DiscordHelper.getUser(event.getGuild(), args[1]);
                buildStrikesList(s -> s.getStriked() == striked.getLongID(), event).forEach(e -> MessageUtil.sendMessage(event, e));
            }
        } else if(args.length == 3) {
            if(DiscordHelper.isUser(event.getGuild(), args[2])) {
                IUser striked = DiscordHelper.getUser(event.getGuild(), args[2]);
                if(args[1].equalsIgnoreCase("info")) {
                    buildStrikesList(s -> s.getStriked() == striked.getLongID(), event).forEach(e -> MessageUtil.sendMessage(event, e));
                } else if(args[1].equalsIgnoreCase("remove")) {
                    List<PPMStrike> remove = new ArrayList<>(getStrikes(s -> s.getStriked() == striked.getLongID() && !s.isExpired()));
                    Wafflebot.data.strikes.removeAll(remove);
                    Wafflebot.save();

                    remove.forEach(strike -> {
                        MessageUtil.sendMessage(event, EmbedPresets.success("Removed strike:", "Reason: " + strike.getReason() + "\n" +
                                "Given by: " + Wafflebot.client.getUserByID(strike.getStrikedBy()).mention()));
                    });

                    if(remove.isEmpty())
                        MessageUtil.sendMessage(event, EmbedPresets.error("No strikes found to remove"));
                }
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("No user provided\n" + getUsage()));
        }
    }

    public static List<PPMStrike> getStrikes(Predicate<PPMStrike> predicate) {
        return Wafflebot.data.strikes.stream().filter(predicate).collect(Collectors.toList());
    }

    public StrikeTier getNextStrikeTier(long striked) {
        Optional<PPMStrike> previousStrike = getStrikes(s -> s.getStriked() == striked).stream()
                .max(Comparator.comparing(PPMStrike::getTier));
        if(previousStrike.isPresent()) {
            return previousStrike.get().getTier().getNextLevel();
        } else
            return StrikeTier.FIRST;
    }

    public List<EmbedObject> buildStrikesList(Predicate<PPMStrike> predicate, MessageReceivedEvent event) {
        List<String> activeSb = new ArrayList<>();
        List<String> expiredSb = new ArrayList<>();
        List<EmbedObject> embeds = new ArrayList<>();

        getStrikes(predicate).stream().sorted(Comparator.comparing(PPMStrike::getTier)).forEach(strike -> {
            IUser striked = Wafflebot.client.getUserByID(strike.getStriked());
            IUser strikedBy = Wafflebot.client.getUserByID(strike.getStrikedBy());
            String ago = new PrettyTime().format(new Date(strike.getTimestamp()));

            if(striked == null || strikedBy == null)
                return;

            if(!strike.isExpired())
                activeSb.add(strike.getTier().getEmoji() + " " + striked.mention() + ", striked by " + strikedBy.mention() + ", " + ago);
            else
                expiredSb.add(strike.getTier().getEmoji() + " " + striked.mention() + ", striked by " + strikedBy.mention() + ", " + ago);
        });

        String active = activeSb.isEmpty() ? "None." : String.join("\n", activeSb);

        embeds.add(new WaffleEmbedBuilder()
                .withTitle(":zap: Strikes:")
                .appendField("Active strikes:", active, false)
                .withColor(Color.YELLOW)
                .withUserFooter(event)
                .build());

        String expired = "";
        int maxSize = 1024;
        int currentSize = 0;

        for(String line : expiredSb) {
            if(currentSize + line.length() + 2 < maxSize) {
                expired += line + "\n";
                currentSize += line.length() + 2;
            } else {
                embeds.add(new WaffleEmbedBuilder()
                        .withTitle(":zap: Strikes:")
                        .appendField("Expired strikes:", expired, false)
                        .withColor(Color.YELLOW)
                        .withUserFooter(event)
                        .build());
                expired = "";
                currentSize = 0;
            }
        }
        embeds.add(new WaffleEmbedBuilder()
                .withTitle(":zap: Strikes:")
                .appendField("Expired strikes:", expired, false)
                .withColor(Color.YELLOW)
                .withUserFooter(event)
                .build());

        return embeds;
    }

    public static enum StrikeTier {
        FIRST(2),
        SECOND(5),
        THIRD(14);

        int length;

        StrikeTier(int length) {
            this.length = length;
        }

        public long getDuration() {
            return TimeUnit.DAYS.toMillis(length);
        }

        public String getEmoji() {
            String emoji = "";
            switch (this) {
                case FIRST:
                    emoji = ":one:";
                    break;
                case SECOND:
                    emoji = ":two:";
                    break;
                case THIRD:
                    emoji = ":three:";
                    break;
            }
            return emoji;
        }

        public StrikeTier getNextLevel() {
            switch (this) {
                case FIRST:
                    return SECOND;
                case SECOND:
                    return THIRD;
                case THIRD:
                    return THIRD;
            }
            return FIRST;
        }
    }
}
