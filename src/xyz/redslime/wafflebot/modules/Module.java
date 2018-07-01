package xyz.redslime.wafflebot.modules;

import sx.blah.discord.handle.obj.IGuild;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by redslime on 29.03.2018
 */
@xyz.redslime.wafflebot.module.annotations.Module
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
        IGuild guild = event.getGuild();
        String[] args = msg.split(" ");
        if(args.length > 2) {
            BotModule module = BotModule.get(args[2]);
            boolean all = args[2].equalsIgnoreCase("all") || args[2].equals("*");
            if(module != null || all) {
                switch (args[1].toLowerCase()) {
                    case "enable":
                    case "on": {
                        if(perm(event, Permissions.MANAGE_SERVER)) {
                            if(all) {
                                for(BotModule m : BotModule.modules)
                                    if(m.isServerModule() && m.isShowInModulesList()) {
                                        m.enable(guild);
                                        if(m.getInitialRun() != null)
                                            m.getInitialRun().accept(event);
                                    }
                                MessageUtil.sendMessage(event.getChannel(), EmbedPresets.success("All available modules enabled!").withUserFooter(event));
                            } else {
                                if(module.enable(guild)) {
                                    MessageUtil.sendMessage(event.getChannel(), EmbedPresets.success(module.getName() + " enabled!").withUserFooter(event));
                                    if(module.getInitialRun() != null)
                                        module.getInitialRun().accept(event);
                                } else
                                    MessageUtil.sendMessage(event, EmbedPresets.error("You can't enable this module here!"));
                            }
                        }
                        break;
                    }
                    case "disable":
                    case "off": {
                        if(perm(event, Permissions.MANAGE_SERVER)) {
                            if(all) {
                                for(BotModule m : BotModule.modules)
                                    if(m.isServerModule() && m.isShowInModulesList())
                                        m.disable(guild);
                                MessageUtil.sendMessage(event.getChannel(), EmbedPresets.success("All modules disabled!").withUserFooter(event));
                            } else {
                                module.disable(guild);
                                MessageUtil.sendMessage(event.getChannel(), EmbedPresets.success(module.getName() + " disabled!").withUserFooter(event));
                            }
                        }
                        break;
                    }
                    case "info":
                    case "?":
                    case "help": {
                        if(all)
                            return;
                        WaffleEmbedBuilder embed = EmbedPresets.information().withTitle(module.isActive(guild) ? Modules.ON + " " + module.getName() : Modules.OFF + " " + module.getName())
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
                }
            } else
                MessageUtil.sendMessage(event.getChannel(), EmbedPresets.error("Module not found").withFooterText("Do !modules for a list of modules"));
        } else
            MessageUtil.sendMessage(event.getChannel(), EmbedPresets.error("Expected: !module [on/off/info/help] [module_name]").withFooterText("Do !modules for a list of modules"));
    }
}