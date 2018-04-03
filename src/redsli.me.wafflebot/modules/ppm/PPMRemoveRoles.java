package redsli.me.wafflebot.modules.ppm;

import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import static redsli.me.wafflebot.data.HamzaPPM.*;

/**
 * Created by redslime on 29.03.2018
 */
@Module
public class PPMRemoveRoles extends CommandModule {

    public PPMRemoveRoles() {
        super("PPM Role Remover Module", "Removes Red/Blue team roles. Made for Hamza's PPM server", true, true);
        trigger("!removeroles");
        limit(PPM_SERVER);
        setGuildOnly(true);
        setGuildFilter(PPM_SERVER);
        setShowInModulesList(false);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return min(event, PPM_HOST) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":arrows_counterclockwise: Removing roles"));
        IRole red = event.getGuild().getRoleByID(RED_TEAM);
        IRole blue = event.getGuild().getRoleByID(BLUE_TEAM);

        new Thread(() -> {
            try {
                for(IUser user : event.getGuild().getUsersByRole(red)) {
                    user.removeRole(red);
                    Thread.sleep(1000);
                }
                for(IUser user : event.getGuild().getUsersByRole(blue)) {
                    user.removeRole(blue);
                    Thread.sleep(1000);
                }

                MessageUtil.editMessage(loading, EmbedPresets.success("Red/Blue Team Roles removed").withUserFooter(event));
            } catch (Exception e) {
                MessageUtil.sendErrorReport(e, event);
            }
        }).start();
    }
}