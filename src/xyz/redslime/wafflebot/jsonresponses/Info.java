package xyz.redslime.wafflebot.jsonresponses;

public class Info {

    private Boolean status;
    private String hostname;
    private Integer port;
    private Integer ping;
    private String version;
    private String protocol;
    private ServerPlayers players;
    private Motds motds;
    private String favicon;
    private String hover;
    private boolean cached;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPing() {
        return ping;
    }

    public void setPing(Integer ping) {
        this.ping = ping;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public ServerPlayers getPlayers() {
        return players;
    }

    public void setPlayers(ServerPlayers players) {
        this.players = players;
    }

    public Motds getMotds() {
        return motds;
    }

    public void setMotds(Motds motds) {
        this.motds = motds;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

}