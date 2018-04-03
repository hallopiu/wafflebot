package redsli.me.wafflebot.util;

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
}
