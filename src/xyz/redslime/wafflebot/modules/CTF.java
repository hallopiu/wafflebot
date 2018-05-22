package xyz.redslime.wafflebot.modules;

import xyz.redslime.wafflebot.jsonresponses.Info;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.SSHelper;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import xyz.redslime.wafflebot.util.MinecraftHelper;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by redslime on 28.03.2018
 */
@Module
public class CTF extends CommandModule {

    public CTF() {
        super("CTF Checker Module", "Checks server status of every CTF server", false, true);
        trigger("!ctf");
        aliases("!capturetheflag");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        IMessage loading = MessageUtil.sendMessage(event.getChannel(), EmbedPresets.loading(":signal_strength: Checking...").build());
        new Thread(() -> {
            try {
                WaffleEmbedBuilder eb = EmbedPresets.success("CTF Server Status", null);
                String match = "";
                SSHelper.Match m = SSHelper.getNow();
                if(m != null)
                    match = "(" + m.name + ")";
                for(int i = 1; i < 5; i++) {
                    Info info = getCTF(i);
                    eb.appendField(i != 4 ? "CTF " + i : "CTF Match " + match, "Players: " + info.getPlayers().getOnline() + "/" + info.getPlayers().getMax() + "\n" +
                            "State: " + humanCTF(info.getMotds().getClean()).split("/")[0].replace("> ", "") + "\n" +
                            "Map: " + humanCTF(info.getMotds().getClean()).split("/")[1].substring(2), false);
                }
                MessageUtil.editMessage(loading, eb.withUserFooter(event.getAuthor(), event.getGuild()).build());
            } catch (Exception e) {
                MessageUtil.sendErrorReport(e, event);
                MessageUtil.editMessage(loading, EmbedPresets.error(null, e.getClass().getName(), null));
            }
        }).start();
    }

    private Info getCTF(int id) throws Exception {
        return MinecraftHelper.getInfo(id == 4 ? "1.ctfmatch.brawl.com" : id + ".mcctf.com");
    }

    private String humanCTF(String input) {
        input = input.replace("\n", " / ").replace("*", "").replace("vN0Q3B", "");
        int slashes = 0;
        for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if(Character.toString(c).equalsIgnoreCase("/")) {
                slashes++;
                if(slashes == 2) {
                    input = input.substring(0, i);
                }
            }
        }
        return input;
    }
}