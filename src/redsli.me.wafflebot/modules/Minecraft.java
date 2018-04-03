package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.jsonresponses.Info;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.MinecraftHelper;
import redsli.me.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by redslime on 29.03.2018
 */
@Module
public class Minecraft extends CommandModule {

    public Minecraft() {
        super("Minecraft Server Checker Module", "Checks server status of a minecraft server", false, true);
        trigger("!minecraft");
        aliases("!server");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();
        String[] args = msg.split(" ");
        if(args.length == 2) {
            if(args[1].contains(".")) {
                IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":signal_strength: Checking..."));
                try {
                    Info info = MinecraftHelper.getInfo(args[1]);
                    WaffleEmbedBuilder embed = EmbedPresets.success().withUserFooter(event).withTitle("Server Status of " + args[1])
                            .appendField("Players", info.getPlayers().getOnline() + "/" + info.getPlayers().getMax(), true)
                            .appendField("Version", info.getVersion(), true)
                            .appendField("MOTD", info.getMotds().getClean(), false);
                    MessageUtil.editMessage(loading, embed);
                } catch (Exception e) {
                    MessageUtil.editMessage(loading, EmbedPresets.error("Server doesn't exist or is offline"));
                    if(!(e instanceof NullPointerException))
                        throw e;
                }
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("That doesn't look like a valid server address to me"));
        } else
            MessageUtil.sendMessage(event, EmbedPresets.error("Expected: " + args[0] + " [minecraft_server_address]"));
    }
}
