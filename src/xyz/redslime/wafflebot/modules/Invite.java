package xyz.redslime.wafflebot.modules;

import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Created by redslime on 29.03.2018
 */
@Module
public class Invite extends CommandModule {

    public Invite() {
        super("Invite Module", "Sends you an invite link to add wafflebot to your Discord server!", true, false);
        trigger("!invite");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        MessageUtil.sendMessage(event, "Invite me with this link: <https://discordapp.com/api/oauth2/authorize?client_id=428575339749572608&permissions=2146954353&scope=bot> \nSee you on the other side!");
    }
}
