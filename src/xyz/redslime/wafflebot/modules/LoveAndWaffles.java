package xyz.redslime.wafflebot.modules;

import xyz.redslime.wafflebot.module.ChatListenerModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Created by redslime on 28.03.2018
 */
@Module
public class LoveAndWaffles extends ChatListenerModule {

    public LoveAndWaffles() {
        super("Love and Waffles Echo Module", "Repeats \"Love and waffles!\" in chat", true, true);
        trigger("love and waffles", "love & waffles", "<@428575339749572608>", "hugs and pugs", "hugs & pugs");
    }

    @Override
    public void mentioned(MessageReceivedEvent event) throws Exception {
        MessageUtil.sendMessage(event.getChannel(), event.getMessage().getContent().toLowerCase().contains("pugs") ? "Hugs and pugs!" : "Love and waffles!");
    }
}