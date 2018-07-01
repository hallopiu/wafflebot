package xyz.redslime.wafflebot.util;

import com.sun.istack.internal.Nullable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import xyz.redslime.wafflebot.Wafflebot;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

/**
 * Created by redslime on 28.03.2018
 */
public class MessageUtil {

    public static final long OWNER = 115834525329653760L;

    public static IMessage sendMessage(IChannel channel, Object message) {
        if(message instanceof String)
            return RequestBuffer.request(() -> channel.sendMessage((String) message)).get();
        if(message instanceof EmbedObject)
            return RequestBuffer.request(() -> channel.sendMessage((EmbedObject) message)).get();
        if(message instanceof WaffleEmbedBuilder)
            return RequestBuffer.request(() -> channel.sendMessage(((WaffleEmbedBuilder) message).build())).get();
        return null;
    }

    public static IMessage sendMessage(IChannel channel, String content, EmbedObject embed) {
        return RequestBuffer.request(() -> channel.sendMessage(content, embed)).get();
    }

    public static IMessage sendMessage(MessageEvent event, Object message) {
        return sendMessage(event.getChannel(), message);
    }

    public static IMessage editMessage(IMessage message, Object contents) {
        if(contents instanceof String)
            return RequestBuffer.request(() -> message.edit((String) contents)).get();
        if(contents instanceof EmbedObject)
            return RequestBuffer.request(() -> message.edit((EmbedObject) contents)).get();
        if(contents instanceof WaffleEmbedBuilder)
            return RequestBuffer.request(() -> message.edit(((WaffleEmbedBuilder) contents).build())).get();
        return null;
    }

    public static IMessage sendPM(IUser user, Object message) {
        if(message instanceof String)
            return RequestBuffer.request(() -> user.getOrCreatePMChannel().sendMessage((String) message)).get();
        if(message instanceof EmbedObject)
            return RequestBuffer.request(() -> user.getOrCreatePMChannel().sendMessage((EmbedObject) message)).get();
        if(message instanceof WaffleEmbedBuilder)
            return RequestBuffer.request(() -> user.getOrCreatePMChannel().sendMessage(((WaffleEmbedBuilder) message).build())).get();
        return null;
    }

    public static void sendErrorReport(Exception e, @Nullable MessageEvent event) {
        e.printStackTrace();
        IUser redslime = Wafflebot.client.getUserByID(OWNER);
        String footer = null;
        if(event != null && event.getMessage() != null) {
            if(!event.getChannel().isPrivate())
                footer = "#" + event.getMessage().getChannel().getName() + " - " + event.getMessage().getGuild().getName();
            else
                footer = "@" + event.getAuthor().getName();
        }
        WaffleEmbedBuilder builder = EmbedPresets.error(e.getClass().getName(), ExceptionUtils.getStackTrace(e) + "", footer);
        if(event != null && event.getMessage() != null) {
            if(event.getAuthor().getNicknameForGuild(event.getGuild()) != null)
                builder.withAuthorName(event.getAuthor().getNicknameForGuild(event.getGuild()) + " (" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ")");
            else
                builder.withAuthorName(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
            builder.withAuthorIcon(event.getAuthor().getAvatarURL());
            builder.withThumbnail(event.getGuild().getIconURL());
        }
        sendPM(redslime, builder.build());
    }

    public static void deleteMessage(long messageId) {
        if(messageId != 0) {
            RequestBuffer.request(() -> Wafflebot.client.getMessageByID(messageId).delete());
        }
    }
}