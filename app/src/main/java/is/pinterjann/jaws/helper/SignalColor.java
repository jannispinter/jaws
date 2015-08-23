package is.pinterjann.jaws.helper;

import android.graphics.Color;
import android.net.wifi.WifiManager;

public class SignalColor {

    public static int getColor(int signal) {

        int signalPercent = WifiManager.calculateSignalLevel(signal, 100);

        // Make sure that there is no division by zero
        if(signalPercent == 0) {
            signalPercent = 1;
        }

        int red = 255-(255/(100/signalPercent));
        int green = 255/(100/signalPercent);
        int blue = 0;

        return Color.rgb(red, green, blue);

    }
}
