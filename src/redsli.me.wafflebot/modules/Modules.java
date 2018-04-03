package redsli.me.wafflebot.modules;

import redsli.me.wafflebot.module.BotModule;
import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import redsli.me.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Created by redslime on 28.03.2018
 */
@Module
public class Modules extends CommandModule {

    public static final String ON = ":ballot_box_with_check:";
    public static final String OFF = ":black_large_square:";

    public Modules() {
        super("Modules Overview Module", "Shows information about every module", true, false);
        trigger("!modules");
        setShowInHelp(true);
        setGuildOnly(true);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        WaffleEmbedBuilder builder = EmbedPresets.information().withTitle("wafflebot™ modules in " + event.getGuild().getName()).withThumbnail(event.getGuild().getIconURL())
                .withUserFooter(event.getAuthor(), event.getGuild())
                .appendField("Key", ON + " Enabled\n" + OFF + " Disabled", false)
                .appendField("Turning on/off a module", "!module [on/off] ``name``", false)
                .appendField("Getting more information about a module", "!module info ``name``", false);
        StringBuilder body = new StringBuilder();
        for(BotModule bm : BotModule.modules) {
            if(bm.isServerModule() && bm.isShowInModulesList())
                body.append(String.format("\n%s %s [``%s``]", bm.isActive(event.getGuild()) ? ON : OFF, bm.getName(), bm.getClass().getSimpleName()));
        }
        builder.withDesc(body.toString());
        MessageUtil.sendMessage(event.getChannel(), builder.build());
    }
}
