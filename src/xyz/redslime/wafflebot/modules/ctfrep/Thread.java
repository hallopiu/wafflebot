package xyz.redslime.wafflebot.modules.ctfrep;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.ocpsoft.prettytime.PrettyTime;
import xyz.redslime.wafflebot.data.CTFRep;
import xyz.redslime.wafflebot.module.CommandModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.util.EmbedPresets;
import xyz.redslime.wafflebot.util.MessageUtil;
import xyz.redslime.wafflebot.util.Utils;
import xyz.redslime.wafflebot.util.WaffleEmbedBuilder;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.net.URL;
import java.util.Optional;

import static xyz.redslime.wafflebot.data.CTFRep.REP_SERVER;

/**
 * Created by redslime on 06.04.2018
 */
@Module
public class Thread extends CommandModule {

    public static final String RSS_OFFICIAL_TEAMS = "http://www.brawl.com/forums/299/index.rss";

    public Thread() {
        super("CTF Team Thread Module", "Finds the team thread of the given name", true, true);
        trigger("!thread");
        limit(REP_SERVER);
        setGuildOnly(true);
        setGuildFilter(REP_SERVER);
        setShowInModulesList(false);
    }

    @Override
    public boolean verify(MessageReceivedEvent event) throws Exception {
        boolean pass = false;
        if(event.getMessage().getContent().split(" ").length >= 2) {
            pass = true;
            if(event.getMessage().getContent().split(" ")[1].length() < 2) {
                pass = false;
                MessageUtil.sendMessage(event, EmbedPresets.error("Invalid team name or tag"));
            }
        } else
            MessageUtil.sendMessage(event, EmbedPresets.error("Expected: !thread [team_name]"));
        return pass && super.verify(event);
    }

    @Override
    public void onUse(MessageReceivedEvent event) throws Exception {
        String request = Utils.connectArgs(event.getMessage().getContent().split(" "), 1);
        IMessage loading = MessageUtil.sendMessage(event, EmbedPresets.loading(":mag: Searching..."));

        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(RSS_OFFICIAL_TEAMS)));
            Optional<SyndEntry> entry = feed.getEntries().stream().filter(e -> titleMatches(request, e.getTitleEx().getValue())).findAny();

            if(entry.isPresent()) {
                SyndEntry team = entry.get();
                WaffleEmbedBuilder embed = EmbedPresets.success("Thread created by " + team.getAuthor().replaceAll("\\((.*)\\)", "$1") + "\n" +
                        "Last Post: " + new PrettyTime().format(team.getPublishedDate()) + "\n" +
                        "Link: " + team.getLink()).withUserFooter(event).withAuthorName(team.getTitleEx().getValue()).withAuthorUrl(team.getLink()).withTitle(null);

                MessageUtil.editMessage(loading, embed);
            } else
                MessageUtil.editMessage(loading, EmbedPresets.error("Nothing found"));
        } catch (Exception e) {
            MessageUtil.editMessage(loading, EmbedPresets.error(e + ""));
            throw e;
        }
    }

    private boolean titleMatches(String request, String title) {
        if(request.length() <= 4) {
            String name = CTFRep.TEAM_TAGS.get(request.toLowerCase().trim());
            if(name != null)
                return title.toLowerCase().trim().contains(name.toLowerCase().trim());
        }
        return title.toLowerCase().trim().contains(request.toLowerCase().trim());
    }
}
