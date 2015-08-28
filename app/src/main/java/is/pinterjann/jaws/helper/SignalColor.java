package is.pinterjann.jaws.helper;

import android.graphics.Color;
import android.net.wifi.WifiManager;

public class SignalColor {

    public static int BOTTLE_GREEN      = Color.rgb(0, 106, 78);
    public static int PASTEL_ORANGE     = Color.rgb(255,179,71);
    public static int PASTEL_RED        = Color.rgb(255, 105, 97);
    public static int PASTEL_RED_DARK   = Color.rgb(194, 59, 34);

    public static int getColor(int signal) {

        /* in case the network is out of range */
        if(signal == 0) {
            return PASTEL_RED_DARK;
        }

        int signalPercent = WifiManager.calculateSignalLevel(signal, 5);

        /* carefully chosen pastel colors */
        switch (signalPercent) {
            case 3:  return BOTTLE_GREEN;
            case 2:  return PASTEL_ORANGE;
            case 1:  return PASTEL_RED;
            case 0:  return PASTEL_RED_DARK;
            default: return BOTTLE_GREEN;
        }
    }
}
