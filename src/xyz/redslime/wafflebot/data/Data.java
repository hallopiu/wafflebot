package xyz.redslime.wafflebot.data;

import xyz.redslime.wafflebot.Wafflebot;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.util.DiscordHelper;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.IOException;
import java.util.*;

/**
 * Created by redslime on 29.03.2018
 */
public class Data {

    public HashMap<Long, List<String>> guildModules = new HashMap<>();
    public List<BaseConfiguration> baseConfigurations = new ArrayList<>();
    public List<ModuleConfiguration> moduleConfigurations = new ArrayList<>();
    public List<Reminder> reminders = new ArrayList<>();
    public List<UUIDCacheItem> uuids = new ArrayList<>();

    public void addModule(IGuild g, String moduleName) throws IOException {
        long id = g.getLongID();
        List<String> modules = guildModules.containsKey(id) ? guildModules.get(id) : new ArrayList<>();
        if(!modules.contains(moduleName))
            modules.add(moduleName);
        guildModules.put(id, modules);
        Wafflebot.save();
    }

    public void removeModule(IGuild g, String moduleName) throws IOException {
        long id = g.getLongID();
        List<String> modules = guildModules.containsKey(id) ? guildModules.get(id) : new ArrayList<>();
        if(modules.contains(moduleName))
            modules.remove(moduleName);
        guildModules.put(id, modules);
        Wafflebot.save();
    }

    public void setModuleChannel(String module, IChannel channel) throws Exception {
        Optional<ModuleConfiguration> conf = moduleConfigurations.stream().filter(m -> m.module.equalsIgnoreCase(module)).filter(m -> m.guild == channel.getGuild().getLongID()).findFirst();

        ModuleConfiguration c = new ModuleConfiguration();
        c.module = conf.map(moduleConfiguration2 -> moduleConfiguration2.module).orElse(module);
        c.channel = channel.getLongID();
        c.guild = conf.map(moduleConfiguration1 -> moduleConfiguration1.guild).orElseGet(() -> channel.getGuild().getLongID());

        conf.ifPresent(moduleConfiguration -> moduleConfigurations.remove(moduleConfiguration));
        moduleConfigurations.add(c);
    }

    public IChannel getModuleChannel(IGuild guild, BotModule module) throws Exception {
        if(moduleConfigurations.stream().anyMatch(m -> module.getClass().getSimpleName().equalsIgnoreCase(m.module) && guild.getLongID() == m.guild)) {
            return Wafflebot.client.getChannelByID(moduleConfigurations.stream()
                    .filter(m -> module.getClass().getSimpleName().equalsIgnoreCase(m.module))
                    .filter(m -> guild.getLongID() == m.guild)
                    .findAny().get().channel);
        }
        return DiscordHelper.getDefaultChannel(guild);
    }

    public void ignoreChannel(IGuild guild, IChannel channel) throws Exception {
        Optional<BaseConfiguration> conf = baseConfigurations.stream().filter(m -> m.guild == guild.getLongID()).findFirst();

        BaseConfiguration c = new BaseConfiguration();
        c.guild = guild.getLongID();
        if(conf.isPresent())
            if(!conf.get().ignoreChannels.contains(channel.getLongID())) {
                conf.get().ignoreChannels.add(channel.getLongID());
                c.ignoreChannels = conf.get().ignoreChannels;
            } else {
                c.ignoreChannels = new ArrayList<>();
                c.ignoreChannels.add(channel.getLongID());
            }
        else {
            c.ignoreChannels = new ArrayList<>();
            c.ignoreChannels.add(channel.getLongID());
        }

        conf.ifPresent(s -> baseConfigurations.remove(s));
        baseConfigurations.add(c);
    }

    public void unignoreChannel(IGuild guild, IChannel channel) throws Exception {
        Optional<BaseConfiguration> conf = baseConfigurations.stream().filter(m -> m.guild == guild.getLongID()).findFirst();

        BaseConfiguration c = new BaseConfiguration();
        c.guild = guild.getLongID();
        if(conf.isPresent())
            if(conf.get().ignoreChannels.contains(channel.getLongID())) {
                conf.get().ignoreChannels.remove(channel.getLongID());
                c.ignoreChannels = conf.get().ignoreChannels;
            }

        conf.ifPresent(s -> baseConfigurations.remove(s));
        baseConfigurations.add(c);
    }

    public boolean isIgnored(IGuild guild, IChannel channel) throws Exception {
        if(baseConfigurations.stream().anyMatch(m -> m.guild == guild.getLongID())) {
            BaseConfiguration c = baseConfigurations.stream().filter(m -> m.guild == guild.getLongID()).findFirst().get();
            return c.ignoreChannels.contains(channel.getLongID());
        } else
            return false;
    }

    public Optional<UUIDCacheItem> getLatestUuidCacheItem(String name) {
        return uuids.stream().filter(i -> i.name.equalsIgnoreCase(name) && !i.isOutdated()).findFirst();
    }
}