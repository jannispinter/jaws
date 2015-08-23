package is.pinterjann.jaws.model;

public class WirelessNetwork implements Comparable {
    private String bssid;
    private String ssid;
    private int channel;
    private int signal;
    private String security;
    private long timestamp;

    public WirelessNetwork(String bssid, String ssid, int channel, int signal, String security, long timestamp) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.channel = channel;
        this.signal = signal;
        this.security = security;
        this.timestamp = timestamp;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Object otherNetwork) {
        return ((WirelessNetwork)otherNetwork).getSignal() - this.getSignal();
    }
}
