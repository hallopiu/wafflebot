package xyz.redslime.wafflebot.data;

import java.util.HashMap;

/**
 * Created by redslime on 06.04.2018
 */
public class CTFRep {

    public static final long REP_SERVER = 212077498480066560L;
    public static final HashMap<String, String> TEAM_TAGS = new HashMap<>();

    static {
        TEAM_TAGS.put("adv", "The Adventurer");
        TEAM_TAGS.put("tc", "The Crusaders");
        TEAM_TAGS.put("tj", "The Jailors");
        TEAM_TAGS.put("bk", "The Black Knights");
        TEAM_TAGS.put("in", "Insurgence");
        TEAM_TAGS.put("df", "Delta Force");
        TEAM_TAGS.put("tbf", "The Blue Flaggers");
        TEAM_TAGS.put("tt", "The Templars");
        TEAM_TAGS.put("scr", "Scorch");
        TEAM_TAGS.put("anni", "Annihilation");
    }
}
