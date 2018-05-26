package xyz.redslime.wafflebot.events;

import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.data.Data;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.util.MessageUtil;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by redslime on 28.03.2018
 */
public class ReadyEvent implements IListener<sx.blah.discord.handle.impl.events.ReadyEvent> {

    @Override
    public void handle(sx.blah.discord.handle.impl.events.ReadyEvent event) {
        // set module states
        try {
            for(IGuild g : Wafflebot.client.getGuilds()) {
                for(BotModule module : BotModule.modules) {
                    if(!module.isServerModule()) {
                        module.enable(g, module.getClass().getSimpleName().toLowerCase());
                    } else {
                        Data data = Wafflebot.data;

                        if(data.guildModules.get(g.getLongID()) != null) {
                            // data saved for this guild
                            if(data.guildModules.get(g.getLongID()).contains(module.getClass().getSimpleName().toLowerCase())) {
                                module.enable(g, module.getClass().getSimpleName().toLowerCase());
                            }
                        } else {
                            // no data saved for this guild
                            if(module.isActivatedDefault())
                                module.enable(g, module.getClass().getSimpleName().toLowerCase());
                        }
                    }
                }
            }
        } catch (Exception e) {
            MessageUtil.sendErrorReport(e, null);
        }
    }
}