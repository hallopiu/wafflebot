package xyz.redslime.wafflebot.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by redslime on 30.03.2018
 */
public class Utils {

    public static boolean isNumber(String s) {
        try {
            int i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String connectArgs(String[] args, int start) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            if(i >= start) {
                result.append(" ").append(args[i]);
            }
        }
        return result.toString();
    }

    public static String readURL(URL url) throws IOException {
        InputStream s = url.openStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(s, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = r.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String insertDashUUID(String uuid) {
        StringBuffer sb = new StringBuffer(uuid);
        sb.insert(8, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(13, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(18, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }

    public static long getLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    public static long getSecs(long duration) {
        return TimeUnit.MILLISECONDS.toSeconds(duration);
    }
}
