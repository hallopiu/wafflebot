package redsli.me.wafflebot.modules.ppm;

import redsli.me.wafflebot.data.HamzaPPM;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.DiscordHelper;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

import static redsli.me.wafflebot.data.HamzaPPM.PPM_HOST;
import static redsli.me.wafflebot.data.HamzaPPM.PPM_SERVER;

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

        new Thread(() -> {
            try {
                IRole currentTeam = null;
                List<String> skippedLines = new ArrayList<>();
                int rolesSet = 0;
                for(String line : lines) {
                    if(line.toLowerCase().startsWith("!setroles") || line.trim().equalsIgnoreCase(""))
                        continue;

                    if(DiscordHelper.isRole(line.trim())) {
                        currentTeam = DiscordHelper.getRole(line);
                        continue;
                    }

                    if(DiscordHelper.isUser(line.trim())) {
                        IUser u = DiscordHelper.getUser(line);
                        if(u != null && HamzaPPM.isTeamRole(currentTeam) && !u.hasRole(currentTeam)) {
                            u.addRole(currentTeam);
                            rolesSet++;
                            Thread.sleep(1000);
                        } else
                            skippedLines.add(line);
                        continue;
                    }

                    skippedLines.add(line);
                }

                if(rolesSet > 0) {
                    String body = rolesSet + " roles set!";

                    if(!skippedLines.isEmpty()) {
                        body += "\n\nSkipped " + skippedLines.size() + " line(s):";
                        for(String l : skippedLines)
                            body += "\n\"" + l + "\"";
                    }

                    WaffleEmbedBuilder embed = EmbedPresets.success(body).withUserFooter(event);
                    MessageUtil.editMessage(loading, embed);
                } else
                    MessageUtil.editMessage(loading, EmbedPresets.error("No users found, did you tag them?"));
            } catch (Exception e) {
                MessageUtil.editMessage(loading, EmbedPresets.error(e.getClass().getName()));
                MessageUtil.sendErrorReport(e, event);
            }
        }).start();
    }
}
