package xyz.redslime.wafflebot.modules.cic;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;

import static xyz.redslime.wafflebot.modules.cic.CIC.*;

/**
 * @author redslime
 * @version 2018-10-20
 */
@Module
public class CICClear extends CommandModule {

    public CICClear() {
        super("cicclear", "cic meeting clearer", true, true);
        trigger("!clearmeetingtimes");
        limit(SERVER_ID);
        setGuildOnly(true);
        setGuildFilter(SERVER_ID);
        setShowInModulesList(false);
        setShowInHelp(false);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return role(event, MEMBER_ROLE) && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        Wafflebot.data.timeEntries.clear();
        Wafflebot.client.getChannelByID(CHANNEL_ID).bulkDelete();
        Wafflebot.save();
    }
}
