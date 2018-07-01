package xyz.redslime.wafflebot.module;

import lombok.Getter;
import lombok.Setter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import xyz.redslime.wafflebot.Wafflebot;
import sx.blah.discord.handle.obj.IGuild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by redslime on 28.03.2018
 */
@Setter
@Getter
public abstract class BotModule {

    public static List<BotModule> modules = new ArrayList<>();

    String name;
    String description;
    boolean activatedDefault;
    boolean serverModule;
    List<Long> guilds;
    boolean guildOnly;
    long guildLimit;
    boolean usesOutputChannel;
    boolean showInModulesList;
    Consumer<MessageReceivedEvent> initialRun;

    public BotModule(String name, String description, boolean activatedDefault, boolean serverModule) {
        this.name = name;
        this.description = description;
        this.activatedDefault = activatedDefault;
        this.serverModule = serverModule;
        this.guilds = new ArrayList<>();
        this.showInModulesList = true;

        modules.add(this);
        System.out.println("Module " + name + " initialized");
    }

    public static BotModule get(String moduleName) {
        for(BotModule m : modules)
            if(m.name.equalsIgnoreCase(moduleName) || m.getClass().getSimpleName().equalsIgnoreCase(moduleName))
                return m;
        return null;
    }

    public void disable(IGuild guild) throws IOException {
        String name = this.getClass().getSimpleName();
        System.out.println("Disabled module " + name + " in guild " + guild.getName() + " (" + guild.getLongID() + ")");
        guilds.remove(guild.getLongID());
        Wafflebot.data.removeModule(guild, name.toLowerCase());
    }

    public boolean enable(IGuild guild) throws IOException {
        String name = this.getClass().getSimpleName();
        if(guildLimit != 0)
            if(guildLimit != guild.getLongID())
                return false;
        System.out.println("Enabled module " + name + " in guild " + guild.getName() + " (" + guild.getLongID() + ")");
        guilds.add(guild.getLongID());
        Wafflebot.data.addModule(guild, name.toLowerCase());
        return true;
    }

    public void limit(long guildId) {
        guildLimit = guildId;
    }

    public boolean isActive(IGuild guild) {
        return guilds.contains(guild.getLongID());
    }
}