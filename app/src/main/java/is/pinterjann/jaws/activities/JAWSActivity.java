package is.pinterjann.jaws.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import is.pinterjann.jaws.R;
import is.pinterjann.jaws.adapter.NetworkAdapter;
import is.pinterjann.jaws.helper.FrequencyConverter;
import is.pinterjann.jaws.model.WirelessNetwork;

public class JAWSActivity extends AppCompatActivity {

    private WifiManager mWifiManager;
    private NetworkAdapter mNetworkAdapter;
    private BroadcastReceiver mWifiScanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaws);

        ListView listview = (ListView) findViewById(R.id.list);
        mNetworkAdapter = new NetworkAdapter();
        listview.setAdapter(mNetworkAdapter);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "WiFi was disabled, enabling wifi...",
                    Toast.LENGTH_LONG).show();
            mWifiManager.setWifiEnabled(true);
        }

        mWifiScanReceiver = new WifiScanReceiver();
        registerReceiver(mWifiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiScanReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jaw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Settings are not implemented yet",
                    Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> results = mWifiManager.getScanResults();
            List<WirelessNetwork> networkList = new ArrayList<WirelessNetwork>();
            for (android.net.wifi.ScanResult result : results) {
                WirelessNetwork network = new WirelessNetwork(result.BSSID, result.SSID,
                        FrequencyConverter.convert(result.frequency), result.level,
                        result.capabilities.toString(), new Date().getTime()
                );
                networkList.add(network);
            }
            Collections.sort(networkList);
            mNetworkAdapter.setNetworkList(networkList);

            // Request another scan from the OS
            mWifiManager.startScan();
        }
    }
}
