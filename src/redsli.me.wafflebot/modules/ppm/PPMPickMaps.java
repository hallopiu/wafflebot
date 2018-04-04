package redsli.me.wafflebot.modules.ppm;

import redsli.me.wafflebot.module.CommandModule;
import redsli.me.wafflebot.module.annotations.Module;
import redsli.me.wafflebot.util.CTFMapHelper;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static redsli.me.wafflebot.data.HamzaPPM.PPM_SERVER;

/**
 * Created by redslime on 03.04.2018
 */
@Module
public class PPMPickMaps extends CommandModule {

    public static final int[] popular = new int[]{424, 1323, 369399, 1196, 331935, 362115, 206110, 123520, 214857, 256, 24573, 381337, 369228, 138168, 370442, 377015, 18991};
    public static final int[] rotation = new int[]{254, 370, 424, 558, 852, 1196, 1323, 1570, 1575, 2848, 4118, 17833, 18991, 23787, 40067, 61286, 123520, 138168,
            178357, 205923, 206110, 214857, 220473, 245468, 287203, 320452, 321972, 325556, 327940, 329797, 331935, 336987, 344355, 345222, 345296, 362115, 369228,
            369399, 370442, 371262, 376577, 376623, 377015, 378708, 380335, 380956, 381337, 381351, 382785};

    public PPMPickMaps() {
        super("PPM Map Picker Module", "Picks 3 random maps. Made for Hamza's PPM server", true, true);
        trigger("!pickmaps");
        limit(PPM_SERVER);
        setGuildOnly(true);
        setGuildFilter(PPM_SERVER);
        setShowInModulesList(false);
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
            if(args[1].equalsIgnoreCase("all")) {
                Integer[] all = CTFMapHelper.getAllIds().toArray(new Integer[CTFMapHelper.getAllIds().size()]);
                String maps = pick(Arrays.stream(all).mapToInt(i -> i).toArray());
                MessageUtil.sendMessage(event, EmbedPresets.success("Picked 3 random maps", maps).withUserFooter(event));
            } else if(args[1].equalsIgnoreCase("popular") || args[1].equalsIgnoreCase("rotation")) {
                boolean p = args[1].equalsIgnoreCase("popular");
                String maps = pick(p ? popular : rotation);
                MessageUtil.sendMessage(event, EmbedPresets.success(p ? "Picked 3 random popular maps" : "Picked 3 random maps from rotation", maps).withUserFooter(event));
            } else if(args[1].equalsIgnoreCase("list")) {
                String popular = "";
                String rotation = "";

                for(int id : PPMPickMaps.popular)
                    popular += CTFMapHelper.getMapByID(id) + " (" + id + ")\n";
                for(int id : PPMPickMaps.rotation)
                    rotation += CTFMapHelper.getMapByID(id) + " (" + id + ")\n";

                MessageUtil.sendMessage(event, EmbedPresets.information().withUserFooter(event).withDesc("**Popular Maps:**\n" + popular + "\n**Maps in rotation: **\n" + rotation));
            } else
                MessageUtil.sendMessage(event, EmbedPresets.error("Expected: !pickmaps [popular/rotation/all]"));
        } else
            MessageUtil.sendMessage(event, EmbedPresets.error("Expected: !pickmaps [popular/rotation/all]"));
    }

    private String pick(int[] rotation) throws Exception {
        String maps = "";
        List<Integer> picked = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            int map = rotation[new Random().nextInt(rotation.length)];
            if(CTFMapHelper.getMapByID(map) != null && !picked.contains(map)) {
                picked.add(map);
                maps += CTFMapHelper.getMapByID(map) + " (" + map + ")\n";
            } else
                i--;
        }
        return maps;
    }
}
