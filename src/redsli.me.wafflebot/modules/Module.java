package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.module.BotModule;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by redslime on 29.03.2018
 */
@redsli.me.wafflebot.module.annotations.Module
public class Module extends CommandModule {

    public Module() {
        super("Module Controller Module", "Turn on/off or view information about a module", true, false);
        trigger("!module");
        setShowInHelp(true);
        setGuildOnly(true);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String msg = event.getMessage().getContent();
        String[] args = msg.split(" ");
        if(args.length > 2) {
            BotModule module = BotModule.get(args[2]);
            if(module != null) {
                switch (args[1].toLowerCase()) {
                    case "enable":
                    case "on": {
                        if(perm(event, Permissions.MANAGE_SERVER)) {
                            module.enable(event.getGuild(), args[2].toLowerCase());
                            MessageUtil.sendMessage(event.getChannel(), EmbedPresets.success(module.getName() + " enabled!").withUserFooter(event));
                        }
                        break;
                    }
                    case "disable":
                    case "off": {
                        if(perm(event, Permissions.MANAGE_SERVER)) {
                            module.disable(event.getGuild(), args[2].toLowerCase());
                            MessageUtil.sendMessage(event.getChannel(), EmbedPresets.success(module.getName() + " disabled!").withUserFooter(event));
                        }
                        break;
                    }
                    case "info":
                    case "?":
                    case "help": {
                        WaffleEmbedBuilder embed = EmbedPresets.information().withTitle(module.isActive(event.getGuild()) ? Modules.ON + " " + module.getName() : Modules.OFF + " " + module.getName())
                                .withDesc(module.getDescription())
                                .withFooterText("!module on " + module.getClass().getSimpleName() + " to enable").withUserFooter(event);
                        if(module instanceof CommandModule) {
                            if(!((CommandModule) module).isHideCommandInModuleInfo()) {
                                embed.appendField("Command", ((CommandModule) module).getCommand(), true);
                                if(((CommandModule) module).hasAliases())
                                    embed.appendField("Aliases", ((CommandModule) module).getAliasesHuman(), true);
                            }
                        }
                        MessageUtil.sendMessage(event.getChannel(), embed);
                        break;
                    }
                    case "setchannel": {

                    }
                }
            } else
                MessageUtil.sendMessage(event.getChannel(), EmbedPresets.error("Module not found").withFooterText("Do !modules for a list of modules"));
        } else
            MessageUtil.sendMessage(event.getChannel(), EmbedPresets.error("Expected: !module [on/off/info/help] [module_name]").withFooterText("Do !modules for a list of modules"));
    }
}