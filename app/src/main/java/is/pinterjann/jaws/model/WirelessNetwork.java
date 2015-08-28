package is.pinterjann.jaws.model;

import java.util.Date;

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

    /* Builds a network from a given string produced by WirelessNetwork.toString() */
    public WirelessNetwork(String networkString) {

        // TODO: add some input validation

        String[] networkInfo = networkString.split(";");
        this.bssid = networkInfo[0];
        this.channel = Integer.parseInt(networkInfo[1]);
        this.security = networkInfo[2];
        this.ssid = networkInfo[3];

        this.signal = 0;
        this.timestamp = new Date().getTime();
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
    public boolean equals(Object otherNetwork) {
        if(otherNetwork instanceof  WirelessNetwork) {
            WirelessNetwork network = (WirelessNetwork) otherNetwork;
            if (getBssid().equals(network.getBssid()) &&
                    getSsid().equals(network.getSsid()) &&
                    getChannel() == network.getChannel() &&
                    getSecurity().equals(network.getSecurity())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Object otherNetwork) {
        return ((WirelessNetwork)otherNetwork).getSignal() - this.getSignal();
    }

    @Override
    public String toString() {
        return getBssid() + ";" + getChannel() + ";" + getSecurity() + ";" + getSsid();
    }

}
