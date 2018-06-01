package xyz.redslime.wafflebot.modules.ppm;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.RequestBuilder;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.data.HamzaPPM;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static xyz.redslime.wafflebot.data.HamzaPPM.PPM_HOST;
import static xyz.redslime.wafflebot.data.HamzaPPM.PPM_SERVER;

/**
 * Created by redslime on 30.03.2018
 */
@Module
public class PPMSetRoles extends CommandModule {

    public PPMSetRoles() {
        super("PPM Role Setter Module", "Sets Red/Blue team roles. Made for Hamza's PPM server", true, true);
        trigger("!setroles");
        limit(PPM_SERVER);
        setGuildFilter(PPM_SERVER);
        setGuildOnly(true);
        setShowInModulesList(false);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return min(event, PPM_HOST) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();
        String[] lines = msg.split("\n");
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":arrows_counterclockwise: Setting roles..."));
        IGuild guild = event.getGuild();

        AtomicReference<IRole> currentTeam = new AtomicReference<>();
        List<String> skippedLines = new ArrayList<>();
        AtomicInteger rolesSet = new AtomicInteger();
        RequestBuilder builder = new RequestBuilder(Wafflebot.client);
        builder.shouldBufferRequests(true);
        builder.doAction(() -> {
            for(String line : lines) {
                if(line.toLowerCase().startsWith("!setroles") || line.trim().equalsIgnoreCase(""))
                    continue;

                if(DiscordHelper.isRole(guild, line)) {
                    currentTeam.set(DiscordHelper.getRole(guild, line));
                    continue;
                }

                if(DiscordHelper.isUser(guild, line)) {
                    IUser u = DiscordHelper.getUser(guild, line);
                    if(HamzaPPM.isTeamRole(currentTeam.get()) && !u.hasRole(currentTeam.get())) {
                        u.addRole(currentTeam.get());
                        rolesSet.getAndIncrement();
                        continue;
                    }
                }

                if(!(DiscordHelper.isRole(guild, line) || DiscordHelper.isUser(guild, line)))
                    skippedLines.add(line);
            }
            return true;
        }).andThen(() -> {
            if(rolesSet.get() > 0) {
                StringBuilder body = new StringBuilder(rolesSet + " roles set!");

                if(!skippedLines.isEmpty()) {
                    body.append("\n\nSkipped ").append(skippedLines.size()).append(" line(s):");
                    for(String l : skippedLines)
                        body.append("\n").append(l);
                }

                WaffleEmbedBuilder embed = EmbedPresets.success(body.toString()).withUserFooter(event);
                MessageUtil.editMessage(loading, embed);
            } else {
                if(skippedLines.isEmpty())
                    MessageUtil.editMessage(loading, EmbedPresets.error("No users found, did you tag them?"));
                else {
                    StringBuilder sl = new StringBuilder();
                    for(String l : skippedLines)
                        sl.append("\n").append(l);
                    MessageUtil.editMessage(loading, EmbedPresets.error("No users found, did you tag them?\nSkipped line(s):" + sl.toString()));
                }
            }
            return true;
        }).onDiscordError(e -> {
            MessageUtil.editMessage(loading, EmbedPresets.error(e.getClass().getName()));
            MessageUtil.sendErrorReport(e, event);
        }).onGeneralError(e -> {
            MessageUtil.editMessage(loading, EmbedPresets.error(e.getClass().getName()));
            MessageUtil.sendErrorReport(e, event);
        }).onMissingPermissionsError(e -> {
            MessageUtil.editMessage(loading, EmbedPresets.error(e.getClass().getName()));
            MessageUtil.sendErrorReport(e, event);
        }).execute();
    }
}
