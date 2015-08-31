package is.pinterjann.jaws.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import is.pinterjann.jaws.helper.SignalColor;
import is.pinterjann.jaws.model.WirelessNetwork;
import is.pinterjann.jaws.R;

public class NetworkAdapter extends BaseAdapter {

    private List<WirelessNetwork> networkList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Set<String> pinnedNetworks;

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        pinnedNetworks = sharedPreferences.getStringSet("pinned_networks", new HashSet<String>());

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
        final ImageButton btn_stick       = (ImageButton) convertView.findViewById(R.id.network_btn_stick);
        DonutProgress signalDonutProgress   = (DonutProgress)
                convertView.findViewById(R.id.network_donut_progress);


        final WirelessNetwork network = networkList.get(position);
        ssid.setText(network.getSsid());
        bssid.setText(network.getBssid());
        signal.setText(network.getSignal() + " dBm");
        signal.setTextColor(SignalColor.getColor(network.getSignal()));
        String channelTranslation = convertView.getResources().getString(R.string.network_channel);
        channel.setText(channelTranslation + ": " + network.getChannel());

        /* Set donut circle signal strength */
        if(network.getSignal() != 0) {
            signalDonutProgress.setProgress(WifiManager.calculateSignalLevel(network.getSignal(), 100) + 1);
            signalDonutProgress.setTextColor(signalDonutProgress.getFinishedStrokeColor());
        } else {
            signalDonutProgress.setProgress(0);
            signalDonutProgress.setTextColor(signalDonutProgress.getUnfinishedStrokeColor());
        }

        /* Check ESS */
        if(network.getSecurity().contains("ESS")) {
            cap_badge_ess.setVisibility(View.VISIBLE);
        } else {
            cap_badge_ess.setVisibility(View.INVISIBLE);
        }

        /* Check cryptography */
        if(network.getSecurity().contains("WPA2-")) {
            cap_badge_crypto.setImageResource(R.mipmap.cap_badge_wpa2);
        } else if (network.getSecurity().contains("WPA-")) {
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

        /* Update pinned/unpinned graphic */
        if(pinnedNetworks.contains(network.toString())) {
            btn_stick.setImageResource(R.drawable.ic_pin_pinned);
        } else {
            btn_stick.setImageResource(R.drawable.ic_pin_unpinned);
        }


        btn_stick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Due to a bug in Android, we need to make a copy of the set. Otherwise, the changes
                // to the set are not persistent.
                // See: https://code.google.com/p/android/issues/detail?id=27801
                Set<String> pinnedNetworksSet = new HashSet<>();
                pinnedNetworksSet.addAll(pinnedNetworks);


                if(!pinnedNetworksSet.contains(network.toString())) {
                    pinnedNetworksSet.add(network.toString());
                    editor.putStringSet("pinned_networks", pinnedNetworksSet);
                    editor.commit();
                    btn_stick.setImageResource(R.drawable.ic_pin_pinned);
                } else {
                    pinnedNetworksSet.remove(network.toString());
                    editor.putStringSet("pinned_networks", pinnedNetworksSet);
                    editor.commit();
                    btn_stick.setImageResource(R.drawable.ic_pin_unpinned);
                }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

}
