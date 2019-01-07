package xyz.redslime.wafflebot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import xyz.redslime.wafflebot.data.Data;
import xyz.redslime.wafflebot.data.Reminder;
import xyz.redslime.wafflebot.data.StrikeReminder;
import xyz.redslime.wafflebot.events.JoinEvent;
import xyz.redslime.wafflebot.events.MessageEvent;
import xyz.redslime.wafflebot.events.ReadyEvent;
import xyz.redslime.wafflebot.module.BotModule;
import xyz.redslime.wafflebot.module.annotations.Module;
import xyz.redslime.wafflebot.module.annotations.RequireOutputChannel;

import java.io.*;

/**
 * Created by redslime on 28.03.2018
 */
public class Wafflebot {

    public static final File DATA_FILE = new File("data.json");
    public static final Gson GSON = new GsonBuilder().create();

    public static IDiscordClient client;
    public static long started;
    public static Data data;

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException {
        client = createClient();
        register();
        load();
        started = System.currentTimeMillis();
        removeShit();
    }

    private static void load() throws IOException {
        if(!DATA_FILE.exists())
            DATA_FILE.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
        Data loadedData = GSON.fromJson(br, Data.class);
        br.close();
        if(loadedData != null)
            data = loadedData;
        else
            data = new Data();

        Reminder.initialize();
        StrikeReminder.initialize();
    }

    public static void save() throws IOException {
        if(!DATA_FILE.exists())
            DATA_FILE.createNewFile();

        BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE));
        bw.write(GSON.toJson(data));
        bw.close();
    }

    private static void register() throws IllegalAccessException, InstantiationException {
        // register events
        client.getDispatcher().registerListener(new MessageEvent());
        client.getDispatcher().registerListener(new ReadyEvent());
        client.getDispatcher().registerListener(new JoinEvent());

        // register modules
        Reflections reflections = new Reflections("xyz.redslime.wafflebot.modules");
        for(Class<?> o : reflections.getTypesAnnotatedWith(Module.class))
            o.newInstance();

        for(Class<?> o : reflections.getTypesAnnotatedWith(RequireOutputChannel.class)) {
            BotModule.get(o.getSimpleName()).setUsesOutputChannel(true);
            System.out.println("Module " + o.getSimpleName() + " requires an output channel");
        }
    }

    private static void removeShit() {
        // these files are generated when you exit the process inside a screen with ctrl + c
        // cba deleting them manually, so this is a nice automatic workaround
        String regex = "(hs_err_pid[0-9]*.log)";
        for(File f : new File(".").listFiles()) {
            if(f.getName().matches(regex))
                f.delete();
        }
    }

    private static IDiscordClient createClient() { // Returns a new instance of the Discord client
        try {
            File token = new File("token.txt");
            BufferedReader reader = new BufferedReader(new FileReader(token));
            ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
            clientBuilder.withToken(reader.readLine()); // Adds the login info to the builder
            reader.close();

            return clientBuilder.login(); // Creates the client instance and logs the client in
        } catch (Exception e) { // This is thrown if there was a problem building the client
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
}