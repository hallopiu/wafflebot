package xyz.redslime.wafflebot.modules.ctfcommunity;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;
import xyz.redslime.wafflebot.data.CTFCommunityDiscord;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.modules.Util;
import xyz.redslime.wafflebot.util.DiscordHelper;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.Utils;

import java.util.stream.Collectors;

/**
 * Created by redslime on 23.07.2018
 */
@Module
public class Flag extends CommandModule {

    public Flag() {
        super("Country Flag Module", "Gives country flag roles", true, true);
        trigger("!country");
        aliases("!flag");
        limit(CTFCommunityDiscord.SERVER);
        setGuildOnly(true);
        setGuildFilter(CTFCommunityDiscord.SERVER);
        setShowInModulesList(false);
        setUsage("!country [country_name/country_tag]");
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        if(event.getMessage().getContent().split(" ").length < 2) {
            MessageUtil.sendMessage(event, EmbedPresets.error(getUsage()));
            return false;
        }
        return super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String country = Utils.connectArgs(event.getMessage().getContent().split(" "), 1);
        Emoji emoji = EmojiManager.getAll().stream()
                .filter(e -> e.getTags().contains("flag"))
                .filter(e -> e.getTags().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(country.toLowerCase())
                        || e.getAliases().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(country.toLowerCase()))
                .findFirst().orElse(null);

        if(emoji != null) {
            RequestBuffer.request(() -> {
                String roleName = emoji.getUnicode();
                IUser user = event.getAuthor();
                IRole role = DiscordHelper.getOrCreateRole(roleName, event.getGuild());

                //check for old country roles and remove
                user.getRolesForGuild(event.getGuild()).stream()
                        .filter(r -> !r.getName().equals(roleName))
                        .filter(r -> EmojiManager.getByUnicode(r.getName()) != null)
                        .filter(r -> EmojiManager.getByUnicode(r.getName()).getTags().contains("flag"))
                        .forEach(user::removeRole);

                user.addRole(role);
                MessageUtil.sendMessage(event, EmbedPresets.success("Role " + roleName + " given!"));
            });
        } else
            MessageUtil.sendMessage(event, EmbedPresets.error("Couldn't find country! (Searched for " + country + ")"));
    }
}
