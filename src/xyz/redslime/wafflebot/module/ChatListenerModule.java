package xyz.redslime.wafflebot.module;

import lombok.Getter;
import lombok.Setter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by redslime on 28.03.2018
 */
@Setter
@Getter
public abstract class ChatListenerModule extends BotModule {

    List<String> triggers = new ArrayList<>();
    long guildFilter;
    long channelFilter;
    long userFilter;

    public ChatListenerModule(String name, String description, boolean activatedDefault, boolean serverModule) {
        super(name, description, activatedDefault, serverModule);
    }

    public abstract void mentioned(MessageReceivedEvent event) throws Exception;

    public boolean verify(MessageReceivedEvent event) throws Exception {
        boolean verified = true;
        if(getChannelFilter() != 0)
            if(getChannelFilter() != event.getChannel().getLongID())
                verified = false;
        if(getUserFilter() != 0)
            if(getUserFilter() != event.getAuthor().getLongID())
                verified = false;
        if(getGuildFilter() != 0)
            if(getGuildFilter() != event.getGuild().getLongID())
                verified = false;

        return verified;
    }

    public void trigger(String... trigger) {
        triggers.addAll(Arrays.asList(trigger));
    }
}
