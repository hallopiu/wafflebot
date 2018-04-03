package redsli.me.wafflebot.module;

import lombok.Getter;
import lombok.Setter;
import redsli.me.wafflebot.util.EmbedPresets;
import redsli.me.wafflebot.util.MessageUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by redslime on 28.03.2018
 */
@Getter
@Setter
public abstract class CommandModule extends BotModule {

    String command;
    List<String> aliases = new ArrayList<>();
    boolean showInHelp;
    long guildFilter;
    long userFilter;
    long channelFilter;
    boolean hideCommandInModuleInfo;

    public CommandModule(String name, String description, boolean activatedDefault, boolean serverModule) {
        super(name, description, activatedDefault, serverModule);
        showInHelp = serverModule;
    }

    public void trigger(String cmd) {
        command = cmd;
    }

    public void aliases(String... a) {
        aliases.addAll(Arrays.asList(a));
    }

    public boolean hasAliases() {
        return !aliases.isEmpty();
    }

    public String getAliasesHuman() {
        String result = "";
        for(String a : aliases) {
            result += a + ", ";
        }
        return result.substring(0, result.length() - 2);
    }

    protected boolean perm(MessageEvent event, Permissions perm) {
        boolean hasPerm = event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(perm);
        if(!hasPerm)
            MessageUtil.sendMessage(event, EmbedPresets.error("You can't do this!", perm.name() + " permissions required!"));
        return hasPerm;
    }

    protected boolean min(MessageEvent event, long role) {
        IRole rle = event.getGuild().getRoleByID(role);
        int pos = rle.getPosition();
        boolean hasPerm = event.getAuthor().getRolesForGuild(event.getGuild()).stream().anyMatch(r -> r.getPosition() >= pos);
        if(!hasPerm)
            MessageUtil.sendMessage(event, EmbedPresets.error("You can't do this!", "Role " + rle.getName() + " or higher required!"));
        return hasPerm;
    }

    protected boolean role(MessageEvent event, long role) {
        IRole rle = event.getGuild().getRoleByID(role);
        boolean hasPerm = event.getAuthor().getRolesForGuild(event.getGuild()).stream().anyMatch(r -> r == rle);
        if(!hasPerm)
            MessageUtil.sendMessage(event, EmbedPresets.error("You can't do this!", "Role " + rle.getName() + " or higher required!"));
        return hasPerm;
    }

    public boolean verify(MessageReceivedEvent event) throws Exception {
        if(guildFilter != 0)
            if(event.getGuild().getLongID() != guildFilter)
                return false;
        if(userFilter != 0)
            if(event.getAuthor().getLongID() != userFilter)
                return false;
        if(channelFilter != 0)
            if(event.getChannel().getLongID() != channelFilter)
                return false;
        return true;
    }

    @Override
    public boolean isActive(IGuild guild) {
        if(guildFilter == 0)
            return super.isActive(guild);
        else
            return super.isActive(guild) && guild.getLongID() == guildFilter;
    }

    public abstract void onUse(MessageReceivedEvent event) throws Exception;
}
