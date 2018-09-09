package xyz.redslime.wafflebot.data;

import com.sun.istack.internal.Nullable;
import sx.blah.discord.handle.obj.IRole;

/**
 * Created by redslime on 30.03.2018
 */
public class CTFCommunityDiscord {

    public static final long PPM_HOST = 284125257042886666L;
    public static final long SERVER_ADMIN = 470916141888307200L;
    public static final long RED_TEAM = 276530235581792259L;
    public static final long BLUE_TEAM = 276534140625616897L;
    public static final long SERVER = 276518289289773067L;
    public static final long ANNOUNCEMENTS = 309172630814982156L;
    public static final long PLAYING = 463443216134569989L;
    public static final long STRIKE_ANNOUNCEMENT = 276518289289773067L;
    public static final long PPM_HOSTS_CHAT = 313870688727597058L;

    public static boolean isTeamRole(@Nullable IRole role) {
        if(role == null)
            return false;
        return role.getLongID() == RED_TEAM || role.getLongID() == BLUE_TEAM;
    }
}
