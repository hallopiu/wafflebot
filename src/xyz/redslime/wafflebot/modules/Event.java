package xyz.redslime.wafflebot.modules;

import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import xyz.redslime.wafflebot.data.CTFCommunityDiscord;
import xyz.redslime.wafflebot.data.WaffleEvent;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by redslime on 29.06.2018
 */
@Module
public class Event extends CommandModule {

    private static final String DOC_URL = "https://github.com/hallopiu/wafflebot/wiki/Events";
    private static List<IUser> users = new ArrayList<>();

    public Event() {
        super("Event Module", "Simple creation of events and managing participants\nFull documentation: " + DOC_URL, false, true);
        trigger("!event");
        setInitialRun((event) -> MessageUtil.sendMessage(event, EmbedPresets.success(getName(), "Hello there. Read the documentation to learn how to set up events: " + DOC_URL)));
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        if(users.contains(event.getAuthor())) {
            MessageUtil.sendMessage(event, EmbedPresets.error("You're already creating a new event! Do ``!cancel`` to abort the current event creation"));
            return false;
        }
        if(CTFCommunityDiscord.SERVER == event.getGuild().getLongID() && DiscordHelper.hasRole(event.getAuthor(), event.getGuild(), CTFCommunityDiscord.PPM_HOST)) {
            return super.verify(event);
        }
        if(!role0(event, "event manager") && !perm0(event, Permissions.ADMINISTRATOR)) {
            MessageUtil.sendMessage(event, EmbedPresets.error("You can't do this!", "Role 'event manager' or Administrator permissions required"));
            return false;
        }
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        SetupFlow<WaffleEvent> setup = new SetupFlow<>(new WaffleEvent(), event.getChannel(), event.getAuthor());
        users.add(event.getAuthor());
        setup.addRule("!cancel", (setupFlow) -> {
            setupFlow.end();
            MessageUtil.sendMessage(event, EmbedPresets.success("Aborted event setup"));
            users.remove(event.getAuthor());
        });
        setup.deletePreviousStep();
        setup.andThen((sf) -> {
            EmbedObject embed = EmbedPresets.success("Started Event Setup", "See the full documentation for this command here: " + DOC_URL + "\n" +
                    "Say !cancel to abort").appendField("Next:", "Enter the event name", false).build();
            sf.sentMessage(MessageUtil.sendMessage(event, embed));
        }, (waffleEvent, setupFlow, message) -> {
            waffleEvent.setName(message.getContent());
            setupFlow.nextStep();
        }).andThen((sf) -> sendUpdate(event, "Enter the event description (date, time, additional information, etc)", sf), (waffleEvent, setupFlow, message) -> {
            waffleEvent.setDescription(message.getContent());
            setupFlow.nextStep();
        }).andThen((sf) -> sendUpdate(event, "Enter roles that may sign up or ``any`` (separate roles with ,)", sf), (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("any")) {
                waffleEvent.getRoles().add(event.getGuild().getEveryoneRole().getLongID());
                setupFlow.nextStep();
                return;
            }
            List<String> failed = new ArrayList<>();
            String msg = message.getContent();
            msg = msg.replace(", ", ",");
            for(String role : msg.split(",")) {
                IRole r = DiscordHelper.getRole(event.getGuild(), role);
                if(r != null)
                    waffleEvent.getRoles().add(r.getLongID());
                else
                    failed.add(role);
            }
            if(failed.isEmpty())
                setupFlow.nextStep();
            else
                MessageUtil.sendMessage(event, EmbedPresets.error("Failed to find these roles: " + String.join(", ", failed) + ". Try again."));
        }).andThen((sf) -> sendUpdate(event, "Enter channel name where the event announcement should be posted or ``here``", sf), (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("here")) {
                waffleEvent.setAnnounceChannel(message.getChannel().getLongID());
                setupFlow.nextStep();
                return;
            }
            IChannel channel = DiscordHelper.getChannel(event.getGuild(), message.getContent());
            if(channel != null) {
                waffleEvent.setAnnounceChannel(channel.getLongID());
                setupFlow.nextStep();
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("Failed to find channel. Try again."));
        }).andThen((sf) -> sendUpdate(event, "Enter channel name where the list of participants should be posted or ``here``", sf), (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("here")) {
                waffleEvent.setListChannel(message.getChannel().getLongID());
                setupFlow.nextStep();
                return;
            }
            IChannel channel = DiscordHelper.getChannel(event.getGuild(), message.getContent());
            if(channel != null) {
                waffleEvent.setListChannel(channel.getLongID());
                setupFlow.nextStep();
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("Failed to find channel. Try again."));
        }).andThen((sf) -> sendUpdate(event, "Enter roles that should be tagged in the announcement, ``everyone`` for everyone or ``none``", sf), (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("everyone")) {
                waffleEvent.getTagRoles().add(message.getGuild().getEveryoneRole().getLongID());
                setupFlow.nextStep();
                return;
            }
            if(message.getContent().equalsIgnoreCase("none")) {
                waffleEvent.setTagRoles(null);
                setupFlow.nextStep();
                return;
            }
            List<String> failed = new ArrayList<>();
            String msg = message.getContent();
            msg = msg.replace(", ", ",");
            for(String role : msg.split(",")) {
                IRole r = DiscordHelper.getRole(event.getGuild(), role);
                if(r != null)
                    waffleEvent.getTagRoles().add(r.getLongID());
                else
                    failed.add(role);
            }
            if(failed.isEmpty())
                setupFlow.nextStep();
            else
                MessageUtil.sendMessage(event, EmbedPresets.error("Failed to find these roles: " + String.join(", ", failed) + ". Try again."));
        }).andThen((sf) -> sendUpdate(event, "Enter role to give to players that sign up or ``none`` (role must exist already)", sf), (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("none")) {
                waffleEvent.setGiveRole(0);
                setupFlow.nextStep();
                return;
            }
            IRole r = DiscordHelper.getRole(event.getGuild(), message.getContent());
            if(r != null) {
                waffleEvent.setGiveRole(r.getLongID());
                setupFlow.nextStep();
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("Failed to find role. Try again."));
        }).andThen((sf) -> sendUpdate(event, "Enter emoji to sign up or ``none``", sf), (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("none")) {
                waffleEvent.setSignUpEmoji(null);
                setupFlow.nextStep();
                return;
            }
            if(EmojiManager.isEmoji(message.toString())) {
                waffleEvent.setSignUpEmoji(message.toString());
                setupFlow.nextStep();
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("Failed to find emoji. Try again."));
        }).andThen((sf) -> sendUpdate(event, "Enter emoji to reject participation or ``none``", sf), (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("none")) {
                waffleEvent.setRejectEmoji(null);
                setupFlow.nextStep();
                return;
            }
            if(EmojiManager.isEmoji(message.toString())) {
                waffleEvent.setRejectEmoji(message.toString());
                setupFlow.nextStep();
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("Failed to find emoji. Try again."));
        }).andThen((sf) -> {
            MessageUtil.sendMessage(event, EmbedPresets.success("Alright, you're all set!", "Do ``!confirm`` to confirm or ``!cancel`` to abort")
                    .appendField("Summary:", setup.getOwner().toString(), false).build());
        }, (waffleEvent, setupFlow, message) -> {
            if(message.getContent().equalsIgnoreCase("!confirm")) {
                users.remove(event.getAuthor());
                setupFlow.end();
                try {
                    waffleEvent.save();
                    waffleEvent.init();
                } catch (IOException e) {
                    MessageUtil.sendMessage(event, EmbedPresets.error("Damn! Something went wrong when trying to save the event. I'm afraid you have to start all over again :("));
                    MessageUtil.sendErrorReport(e, event);
                }
            } else if(message.getContent().equalsIgnoreCase("!abort")) {
                users.remove(event.getAuthor());
                setupFlow.end();
                MessageUtil.sendMessage(event, EmbedPresets.success("Aborted."));
            }
        }).start();
    }

    private void sendUpdate(MessageReceivedEvent event, String text, SetupFlow setupFlow) {
        setupFlow.sentMessage(MessageUtil.sendMessage(event, new WaffleEmbedBuilder().withColor(Color.GREEN).withTitle("Next:").withDesc(text).withFooterText("!cancel to abort").build()));
    }
}
