package redsli.me.wafflebot.jsonresponses;

import java.util.HashMap;
import java.util.Map;

public class Motds {

    private String ingame;
    private String html;
    private String clean;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getIngame() {
        return ingame;
    }

    public void setIngame(String ingame) {
        this.ingame = ingame;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getClean() {
        return clean;
    }

    public void setClean(String clean) {
        this.clean = clean;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
