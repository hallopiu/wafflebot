package xyz.redslime.wafflebot.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.TriConsumer;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Created by redslime on 29.06.2018
 */
public class SetupFlow<T> {

    private static List<SetupFlow> flows = new ArrayList<>();

    @Getter
    private T owner;
    private Queue<Step> steps;
    private List<String> triggers;
    private Map<String, Consumer<SetupFlow<T>>> rules;
    private long channel;
    private long user;
    private boolean deletePreviousStep;
    @Setter
    private long lastSetupMsg;
    private long lastUserMsg;

    public SetupFlow(T owner, IChannel channel, IUser user) {
        this.owner = owner;
        this.steps = new LinkedBlockingQueue<>();
        this.triggers = new ArrayList<>();
        this.channel = channel.getLongID();
        this.user = user.getLongID();
        this.rules = new HashMap<>();
    }

    public static void check(MessageReceivedEvent event) {
        for(int i = 0, flowsSize = flows.size(); i < flowsSize; i++) {
            SetupFlow flow = flows.get(i);
            if(flow.channel == event.getChannel().getLongID() && flow.user == event.getAuthor().getLongID()) {
                if(flow.rules.containsKey(event.getMessage().getContent().toLowerCase())) {
                    ((Consumer<SetupFlow>) flow.rules.get(event.getMessage().getContent().toLowerCase())).accept(flow.getInstance());
                }
                if(flows.contains(flow) && flow.triggers.isEmpty() || flow.triggers.contains(event.getMessage().getContent().toLowerCase())) {
                    flow.lastUserMsg = event.getMessageID();
                    flow.getCurrentStep().triConsumer.accept(flow.owner, flow.getInstance(), event.getMessage());
                }
            }
        }
    }

    public void start() {
        flows.add(this);
        steps.peek().consumer.accept(getInstance());
    }

    public SetupFlow<T> addRule(String trigger, Consumer<SetupFlow<T>> consumer) {
        rules.put(trigger, consumer);
        return this;
    }

    public SetupFlow<T> removeRule(String trigger) {
        rules.remove(trigger);
        return this;
    }

    public SetupFlow<T> andThen(Consumer<SetupFlow<T>> consumer, TriConsumer<T, SetupFlow<T>, IMessage> step) {
        steps.add(new Step(consumer, step));
        return this;
    }

    public boolean nextStep() {
        steps.remove();
        if(steps.peek() != null) {
            if(deletePreviousStep) {
                MessageUtil.deleteMessage(lastUserMsg);
                MessageUtil.deleteMessage(lastSetupMsg);
            }
            steps.peek().consumer.accept(getInstance());
            return true;
        }
        return false;
    }

    public SetupFlow<T> trigger(String... triggers) {
        for(String trigger : triggers)
            this.triggers.add(trigger.toLowerCase());
        return this;
    }

    public SetupFlow<T> deletePreviousStep() {
        deletePreviousStep = true;
        return this;
    }

    public void sentMessage(IMessage msg) {
        lastSetupMsg = msg.getLongID();
    }

    public void end() {
        flows.remove(this);
    }

    public SetupFlow<T> getInstance() {
        return this;
    }

    private Step getCurrentStep() {
        return steps.peek();
    }

    public class Step {
        Consumer<SetupFlow<T>> consumer;
        TriConsumer<T, SetupFlow<T>, IMessage> triConsumer;

        Step(Consumer<SetupFlow<T>> consumer, TriConsumer<T, SetupFlow<T>, IMessage> triConsumer) {
            this.consumer = consumer;
            this.triConsumer = triConsumer;
        }
    }
}
