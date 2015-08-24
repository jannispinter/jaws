package is.pinterjann.jaws.adapter;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.List;

import is.pinterjann.jaws.helper.SignalColor;
import is.pinterjann.jaws.model.WirelessNetwork;
import is.pinterjann.jaws.R;

public class NetworkAdapter extends BaseAdapter {

    private List<WirelessNetwork> networkList = new ArrayList<>();

    public List<WirelessNetwork> getNetworkList() {
        return networkList;
    }

    public void setNetworkList(List<WirelessNetwork> networkList) {
        this.networkList = networkList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return networkList.size();
    }

    @Override
    public Object getItem(int position) {
        return networkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.network_row, null);
        }

        TextView ssid               = (TextView) convertView.findViewById(R.id.network_ssid);
        TextView bssid              = (TextView) convertView.findViewById(R.id.network_bssid);
        TextView signal             = (TextView) convertView.findViewById(R.id.network_signal);
        TextView channel            = (TextView) convertView.findViewById(R.id.network_channel);
        ImageView cap_badge_ess     = (ImageView) convertView.findViewById(R.id.cap_badge_ess);
        ImageView cap_badge_crypto  = (ImageView) convertView.findViewById(R.id.cap_badge_crypto);
        ImageView cap_badge_wps     = (ImageView) convertView.findViewById(R.id.cap_badge_wps);
        DonutProgress signalDonutProgress   = (DonutProgress)
                convertView.findViewById(R.id.network_donut_progress);


        WirelessNetwork network = networkList.get(position);
        ssid.setText(network.getSsid());
        bssid.setText(network.getBssid());
        signal.setText(network.getSignal() + " dBm");
        signal.setTextColor(SignalColor.getColor(network.getSignal()));
        signalDonutProgress.setProgress(WifiManager.calculateSignalLevel(network.getSignal(), 100));
        channel.setText("Channel: " + network.getChannel());

        /* Check ESS */
        if(network.getSecurity().contains("ESS")) {
            cap_badge_ess.setVisibility(View.VISIBLE);
        } else {
            cap_badge_ess.setVisibility(View.INVISIBLE);
        }

        /* Check cryptography */
        if(network.getSecurity().contains("WPA2")) {
            cap_badge_crypto.setImageResource(R.mipmap.cap_badge_wpa2);
        } else if (network.getSecurity().contains("WPA")) {
            cap_badge_crypto.setImageResource(R.mipmap.cap_badge_wpa);
        } else if(network.getSecurity().contains("WEP")) {
            cap_badge_crypto.setImageResource(R.mipmap.cap_badge_wep);
        } else {
            cap_badge_crypto.setImageResource(R.mipmap.cap_badge_open);
        }

        /* Check WPS */
        if(network.getSecurity().contains("WPS")) {
            cap_badge_wps.setVisibility(View.VISIBLE);
        } else {
            cap_badge_wps.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
