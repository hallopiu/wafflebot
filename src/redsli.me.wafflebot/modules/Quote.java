package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.module.ReactModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.DiscordHelper;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by redslime on 29.03.2018
 */
@Module
public class Quote extends ReactModule {

    public static final String REACT_NAME = "quote";

    public Quote() {
        super("Discord Quote Module", "Allows you to quote a message by reacting with :speech_left: :speech_balloon: :quote: on the message you want to quote", false, true);
        trigger(REACT_NAME, "🗨", "🗨️", "💬", "👁️");
    }

    @Override
    public void onReact(ReactionEvent reactionEvent) throws Exception {
        WaffleEmbedBuilder embed = new WaffleEmbedBuilder().withColor(DiscordHelper.getHighestRoleColor(reactionEvent))
                .withAuthorName(reactionEvent.getMessage().getAuthor().getName()).withAuthorIcon(reactionEvent.getMessage().getAuthor().getAvatarURL())
                .withDesc(reactionEvent.getMessage().getContent())
                .withFooterIcon(reactionEvent.getUser().getAvatarURL())
                .withFooterText("Quoted by " + reactionEvent.getUser().getName())
                .withTimestamp(reactionEvent.getMessage().getTimestamp());
        if(reactionEvent.getUser().getNicknameForGuild(reactionEvent.getGuild()) != null)
            embed.withFooterText("Quoted by " + reactionEvent.getUser().getNicknameForGuild(reactionEvent.getGuild()));
        if(reactionEvent.getMessage().getAttachments() != null && !reactionEvent.getMessage().getAttachments().isEmpty()) {
            reactionEvent.getMessage().getAttachments().stream().map(IMessage.Attachment::getUrl).forEach(embed::withImage);
        }
        MessageUtil.sendMessage(reactionEvent, embed);
    }


}
