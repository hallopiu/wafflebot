package xyz.redslime.wafflebot.modules;

import xyz.redslime.wafflebot.jsonresponses.MojangNameHistory;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.MinecraftHelper;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import javax.naming.NameNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by redslime on 30.04.2018
 */
@Module
public class Namehistory extends CommandModule {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd @ HH:mm z");

    public Namehistory() {
        super("Minecraft Name History Lookup Module", "Look up a player's name history", false, true);
        trigger("!name");
        aliases("!lookup", "!namehistory");
        setUsage("!name [minecraft_name]");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        String[] args = event.getMessage().getContent().split(" ");
        if(args.length != 2) {
            MessageUtil.sendMessage(event, EmbedPresets.error(getUsage()));
            return false;
        }
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String name = event.getMessage().getContent().split(" ")[1];
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":signal_strength: Loading..."));
        try {
            MinecraftHelper.MinecraftUser user = MinecraftHelper.getNameHistory(name);
            WaffleEmbedBuilder embed = EmbedPresets.success("Name history of " + user.getName(), null)
                    .withUserFooter(event)
                    .withThumbnail(MinecraftHelper.getSkinUrl(user.getUuid()));
            for(MojangNameHistory mnh : user.getNames()) {
                embed.appendField(mnh.getName(), mnh.getChangedToAt() != 0 ? DATE_FORMAT.format(new Date(mnh.getChangedToAt())) : "First name", false);
            }
            MessageUtil.editMessage(loading, embed);
        } catch (Exception e) {
            MessageUtil.editMessage(loading, EmbedPresets.error(e instanceof NameNotFoundException ? "Minecraft name not found!" : e + ""));
            if(!(e instanceof NameNotFoundException))
                throw e;
        }
    }
}
