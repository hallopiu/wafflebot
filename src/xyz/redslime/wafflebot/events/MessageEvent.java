package xyz.redslime.wafflebot.events;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.util.RequestBuffer;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.data.WaffleEvent;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.module.ChatListenerModule;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.ReactModule;
import xyz.redslime.wafflebot.modules.Config;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import xyz.redslime.wafflebot.util.SetupFlow;

import java.util.List;

/**
 * Created by redslime on 28.03.2018
 */
public class MessageEvent extends Event implements IListener<sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent> {

    @Override
    public void handle(sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent event) {
        try {
            if(event instanceof MessageReceivedEvent)
                SetupFlow.check((MessageReceivedEvent) event);
            if(event instanceof ReactionEvent)
                WaffleEvent.checkReaction(event.getMessage(), ((ReactionEvent) event).getReaction(), ((ReactionEvent) event).getUser());
            if(event instanceof MessageDeleteEvent) {
                List<WaffleEvent> events = Wafflebot.data.events;
                for(int i = 0; i < events.size(); i++) {
                    WaffleEvent e = events.get(i);
                    if(e.getAnnouncementMessage() == event.getMessage()) {
                        e.delete();
                    }
                }
            }
            for(BotModule bm : BotModule.modules) {
                if(!event.getChannel().isPrivate())
                    if(Wafflebot.data.isIgnored(event.getGuild(), event.getChannel()) && !(bm instanceof Config))
                        continue;
                if(event instanceof MessageReceivedEvent) {
                    if(bm instanceof CommandModule) {
                        CommandModule cm = (CommandModule) bm;
                        if(isCommand(cm, event.getMessage().getContent())) {
                            if(event.getChannel().isPrivate() && cm.isGuildOnly()) {
                                event.getMessage().reply("This command can't be used in PMs");
                                continue;
                            }
                            if(!event.getChannel().isPrivate())
                                if(!cm.isActive(event.getGuild()))
                                    continue;
                            if(!cm.verify((MessageReceivedEvent) event))
                                continue;
                            cm.onUse((MessageReceivedEvent) event);
                        }
                    }
                    if(bm instanceof ChatListenerModule) {
                        ChatListenerModule clm = (ChatListenerModule) bm;
                        if(!clm.verify((MessageReceivedEvent) event))
                            continue;
                        for(String trigger : clm.getTriggers())
                            if(event.getMessage().getContent().toLowerCase().contains(trigger.toLowerCase()) && clm.isActive(event.getGuild()))
                                clm.mentioned((MessageReceivedEvent) event);
                    }
                }
                if(event instanceof ReactionEvent) {
                    if(bm instanceof ReactModule) {
                        ReactModule rm = (ReactModule) bm;
                        if(bm.isGuildOnly() && event.getChannel().isPrivate())
                            continue;
                        if(!rm.getReacts().contains(((ReactionEvent) event).getReaction().getEmoji().getName()))
                            continue;
                        if(bm.isActive(event.getGuild())) {
                            if(rm.isRemoveReact()) {
                                RequestBuffer.request(() -> event.getMessage().removeReaction(((ReactionEvent) event).getUser(), ((ReactionEvent) event).getReaction()));
                                if(((ReactionEvent) event).getReaction().getCount() == 0)
                                    continue;
                            }
                            rm.onReact((ReactionEvent) event);
                        }
                    }
                }
            }
        } catch (Exception e) {
            MessageUtil.sendErrorReport(e, event);
        }
    }

    private boolean isCommand(CommandModule module, String msg) {
        return module.getCommand().equalsIgnoreCase(msg.split(" ")[0]) || module.getCommand().equalsIgnoreCase(msg.split("\n")[0])
                || module.getAliases().stream().anyMatch(c -> msg.split(" ")[0].equalsIgnoreCase(c))
                || module.getAliases().stream().anyMatch(c -> msg.split("\n")[0].equalsIgnoreCase(c));
    }
}
