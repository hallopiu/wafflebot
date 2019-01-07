package xyz.redslime.wafflebot.modules;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import xyz.redslime.wafflebot.jsonresponses.Info;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.*;

/**
 * Created by redslime on 18.08.2018
 */
@Module
public class War extends CommandModule {

    private static final String STATE_REGEX = ".*In progress!.*|.*Starting soon!.*";
    private static final String MAP_REGEX = ".*Map:.*";
    private static final String MODE_REGEX = ".*Mode:.*";

    public War() {
        super("Mc-War Checker Module", "Checks server status of every Mc-War server", false, true);
        trigger("!war");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        MessageUtil.sendMessage(event, EmbedPresets.error("Disabled for now, sorry!\nUse !brawl to see player counts"));
//        IMessage loading = MessageUtil.sendMessage(event.getChannel(), EmbedPresets.loading(":signal_strength: Checking...").build());
//        new java.lang.Thread(() -> {
//            try {
//                WaffleEmbedBuilder eb = EmbedPresets.success("Mc-War Server Status", null);
//                for(int i = 1; i < 3; i++) {
//                    Info info = getWar(i);
//                    String motd = info.getMotds().getClean().replace("\n", "");
//                    String state = motd.replaceAll("> (.*).*\\*.*", "$1");
//                    String map = motd.replaceAll(".*Map: (.*) >.*", "$1");
//                    String mode = motd.replaceAll(".*\\* Mode: (.*)Map:.*", "$1");
//                    String desc = "Players: " + info.getPlayers().getOnline() + "/" + info.getPlayers().getMax() + "\n";
//                    if(motd.matches(STATE_REGEX))
//                        desc += "State: " + state;
//                    if(motd.matches(MAP_REGEX))
//                        desc += "\nMap: " + map;
//                    if(motd.matches(MODE_REGEX))
//                        desc += "\nMode: " + mode;
//                    if(info.getPlayers().getMax() == 0)
//                        desc = "Offline";
//                    eb.appendField("Mc-War " + i, desc, false);
//                }
//                MessageUtil.editMessage(loading, eb.withUserFooter(event.getAuthor(), event.getGuild()).build());
//            } catch (Exception e) {
//                if(e instanceof ArrayIndexOutOfBoundsException)
//                    MessageUtil.editMessage(loading, EmbedPresets.error(null, "Failed to get Mc-War server data, are they down?", null));
//                else {
//                    MessageUtil.editMessage(loading, EmbedPresets.error(null, e.getClass().getName(), null));
//                    MessageUtil.sendErrorReport(e, event);
//                }
//            }
//        }).start();
    }

    private Info getWar(int id) throws Exception {
        return MinecraftHelper.getInfo(id + ".mc-war.com");
    }
}
