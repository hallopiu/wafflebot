package xyz.redslime.wafflebot.module;

import lombok.Getter;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by redslime on 28.03.2018
 */
@Getter
public abstract class ReactModule extends BotModule {

    List<String> reacts = new ArrayList<>();

    public ReactModule(String name, String description, boolean activatedDefault, boolean serverModule) {
        super(name, description, activatedDefault, serverModule);
    }

    public abstract void onReact(ReactionEvent reactionEvent) throws Exception;

    public void trigger(String... emojiName) {
        reacts.addAll(Arrays.asList(emojiName));
    }

    public void triggerAll() {
        reacts = null;
    }
}
