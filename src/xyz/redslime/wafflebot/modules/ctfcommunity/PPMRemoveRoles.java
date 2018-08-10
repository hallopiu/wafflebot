package xyz.redslime.wafflebot.modules.ctfcommunity;

import sx.blah.discord.util.RequestBuilder;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;

import static xyz.redslime.wafflebot.data.CTFCommunityDiscord.*;

/**
 * Created by redslime on 29.03.2018
 */
@Module
public class PPMRemoveRoles extends CommandModule {

    public PPMRemoveRoles() {
        super("PPM Role Remover Module", "Removes Red/Blue team roles. Made for Hamza's PPM server", true, true);
        trigger("!removeroles");
        limit(SERVER);
        setGuildOnly(true);
        setGuildFilter(SERVER);
        setShowInModulesList(false);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return role(event, PPM_HOST) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":arrows_counterclockwise: Removing roles"));
        IRole red = event.getGuild().getRoleByID(RED_TEAM);
        IRole blue = event.getGuild().getRoleByID(BLUE_TEAM);
        IRole playing = event.getGuild().getRoleByID(PLAYING);
        RequestBuilder builder = new RequestBuilder(Wafflebot.client);
        builder.shouldBufferRequests(true);
        builder.doAction(() -> {
            event.getGuild().getUsersByRole(red).forEach(user -> user.removeRole(red));
            event.getGuild().getUsersByRole(blue).forEach(user -> user.removeRole(blue));
            event.getGuild().getUsersByRole(playing).forEach(user -> user.removeRole(playing));
            return true;
        }).andThen(() -> {
            MessageUtil.editMessage(loading, EmbedPresets.success("Red/Blue Team & Playing Roles removed").withUserFooter(event));
            return true;
        }).onDiscordError(e -> {
            MessageUtil.sendErrorReport(e, event);
        }).onGeneralError(e -> {
            MessageUtil.sendErrorReport(e, event);
        }).onMissingPermissionsError(e -> {
            MessageUtil.sendErrorReport(e, event);
        }).execute();
    }
}