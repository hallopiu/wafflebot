package redsli.me.wafflebot.jsonresponses;

import java.util.HashMap;
import java.util.Map;

public class ServerPlayers {

    private Integer online;
    private Integer max;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}