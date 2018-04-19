package redsli.me.wafflebot.util;

import com.sun.istack.internal.Nullable;

import java.awt.*;

/**
 * Created by redslime on 28.03.2018
 */
public class EmbedPresets {

    public static final Color ERROR = Color.RED;
    public static final Color SUCCESS = Color.GREEN;
    public static final Color LOADING = Color.ORANGE;
    public static final Color INFORMATION = Color.CYAN;
    public static final Color BROADCAST = Color.MAGENTA;
    public static final Color REMINDER = Color.PINK;

    public static WaffleEmbedBuilder error(@Nullable String header, @Nullable String body, @Nullable String footer) {
        WaffleEmbedBuilder builder = new WaffleEmbedBuilder().withColor(ERROR).withTimestamp(System.currentTimeMillis());
        if(header != null) builder.withTitle(":x: " + header);
        else builder.withTitle(":x: An error occurred!");
        if(body != null) builder.withDesc(body);
        if(footer != null) builder.withFooterText(footer);
        return builder;
    }

    public static WaffleEmbedBuilder error(@Nullable String header, @Nullable String body) {
        return error(header, body, null);
    }

    public static WaffleEmbedBuilder error(@Nullable String body) {
        return error(null, body, null);
    }

    public static WaffleEmbedBuilder success(@Nullable String header, @Nullable String body, @Nullable String footer) {
        WaffleEmbedBuilder builder = new WaffleEmbedBuilder().withColor(SUCCESS).withTimestamp(System.currentTimeMillis());
        if(header != null) builder.withTitle(":white_check_mark: " + header);
        else builder.withTitle(":white_check_mark: Success!");
        if(body != null) builder.withDesc(body);
        if(footer != null) builder.withFooterText(footer);
        return builder;
    }

    public static WaffleEmbedBuilder success(@Nullable String header, @Nullable String body) {
        return success(header, body, null);
    }

    public static WaffleEmbedBuilder success(@Nullable String body) {
        return success(null, body, null);
    }

    public static WaffleEmbedBuilder success() {
        return success(null);
    }

    public static WaffleEmbedBuilder loading(@Nullable String header) {
        return new WaffleEmbedBuilder().withColor(LOADING).withTitle(header == null ? ":arrows_counterclockwise: Loading" : header);
    }

    public static WaffleEmbedBuilder information() {
        return new WaffleEmbedBuilder().withColor(INFORMATION);
    }

    public static WaffleEmbedBuilder reminder() {
        return new WaffleEmbedBuilder().withColor(REMINDER).withTitle(":alarm_clock: Reminder");
    }

    public static WaffleEmbedBuilder broadcast() {
        return new WaffleEmbedBuilder().withColor(BROADCAST);
    }
}
