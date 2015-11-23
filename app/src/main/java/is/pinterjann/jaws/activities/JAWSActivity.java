package is.pinterjann.jaws.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import is.pinterjann.jaws.R;
import is.pinterjann.jaws.adapter.NetworkAdapter;
import is.pinterjann.jaws.helper.FrequencyConverter;
import is.pinterjann.jaws.model.WirelessNetwork;

public class JAWSActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private NetworkAdapter networkAdapter;
    private BroadcastReceiver wifiScanReceiver;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;

    private boolean isScanning = true;
    private boolean jawsAutoEnabledWifi = false;
    private boolean accessCoarseLocationPermissionGranted = true;

    private final int PERMISSION_COARSE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaws);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listview = (ListView) findViewById(R.id.list);
        networkAdapter = new NetworkAdapter();
        listview.setAdapter(networkAdapter);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        /* Fake networks for screenshots
        List<WirelessNetwork> networkList = new ArrayList<>();
        networkList.add(new WirelessNetwork("6E:AD:9E:53:02:B4", "Guest", 1, -80, "ESS OPEN", new Date().getTime()));
        networkList.add(new WirelessNetwork("54:4F:7C:33:6B:4F", "Telcom", 5, -77, "ESS OPEN", new Date().getTime()));
        networkList.add(new WirelessNetwork("CB:1A:07:DA:2D:EC", "Freifunk", 9, -58, "ESS OPEN", new Date().getTime()));
        networkList.add(new WirelessNetwork("60:B0:56:A7:95:07", "Free Manning",13, -57, "ESS WPA", new Date().getTime()));
        networkList.add(new WirelessNetwork("52:22:1E:E4:62:29", "Freedom", 100, -90, "ESS OPEN", new Date().getTime()));
        networkList.add(new WirelessNetwork("F8:19:B0:58:CA:96", "private", 36, -67, "ESS WPA2", new Date().getTime()));
        networkList.add(new WirelessNetwork("9D:44:E7:25:7A:CA", "JAWS 1", 40, -60, "ESS WEP", new Date().getTime()));
        networkList.add(new WirelessNetwork("69:40:1E:93:1F:D3", "JAWS 2", 120, -72, "ESS WPA", new Date().getTime()));
        networkList.add(new WirelessNetwork("7F:6D:12:88:D7:97", "JAWS 3", 1, -63, "ESS WPA2", new Date().getTime()));
        networkList.add(new WirelessNetwork("49:97:33:B9:25:4E", "JAWS 4", 5, -59, "ESS WPA2 WPS", new Date().getTime()));
        Collections.sort(networkList);
        networkAdapter.setNetworkList(networkList);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        */

        wifiScanReceiver = new WifiScanReceiver();
        registerReceiver(wifiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanning();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScanning();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiScanReceiver);
        stopScanning();

        if (sharedPreferences.getBoolean("switch_wifi_off_when_not_scanning", false) && jawsAutoEnabledWifi) {
            wifiManager.setWifiEnabled(false);
        }
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScanning() {

        isScanning = true;

        if (!wifiManager.isWifiEnabled()) {
            jawsAutoEnabledWifi = true;
            String toastTranslation = getResources().getString(R.string.toast_wifi_enabled);
            Toast.makeText(getApplicationContext(), toastTranslation,
                    Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        /* Request Android permissions for location if API level is 23 or higher */
        if(Build.VERSION.SDK_INT >= 23) {
            accessCoarseLocationPermissionGranted = false;
            requestPermissions();
        }

        /* Start AsyncTask to scan for networks in the background */
        new WifiScanAsyncTask().execute();

    }

    private void stopScanning() {
        isScanning = false;
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessCoarseLocationPermissionGranted = true;
                } else {
                    accessCoarseLocationPermissionGranted = false;
                }
            }
        }
    }

    public class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            List<WirelessNetwork> networkList = new ArrayList<>();
            for (android.net.wifi.ScanResult result : results) {
                WirelessNetwork network = new WirelessNetwork(result.BSSID, result.SSID,
                        FrequencyConverter.convert(result.frequency), result.level,
                        result.capabilities, new Date().getTime()
                );
                networkList.add(network);
            }

            /* Sort networks based on preferences */
            if (sharedPreferences.getString("network_order", "first").equals("first")) {
                Collections.sort(networkList);
            } else {
                Collections.sort(networkList, new Comparator<WirelessNetwork>() {
                    @Override
                    public int compare(WirelessNetwork lhs, WirelessNetwork rhs) {
                        return lhs.getSignal() - rhs.getSignal();
                    }
                });
            }

            /* Add pinned networks to the top of the list */
            Set<String> pinnedNetworks = sharedPreferences.getStringSet("pinned_networks", new HashSet<String>());
            for (String networkString : pinnedNetworks) {
                WirelessNetwork network = new WirelessNetwork(networkString);
                if (networkList.contains(network)) {
                    int position = networkList.indexOf(network);
                    WirelessNetwork origNetwork = networkList.get(position);
                    networkList.remove(position);
                    networkList.add(0, origNetwork);
                } else {
                    networkList.add(0, network);
                }
            }

            /* Disable loading animation */
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            networkAdapter.setNetworkList(networkList);
        }
    }

    public class WifiScanAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            while (isScanning && accessCoarseLocationPermissionGranted) {
                wifiManager.startScan();

                try {
                    int interval = Integer.parseInt(sharedPreferences.getString("scan_interval", "1000"));
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }
    }
}
