package is.pinterjann.jaws.helper;

import android.graphics.Color;
import android.net.wifi.WifiManager;

public class SignalColor {

    public static int getColor(int signal) {

        int signalPercent = WifiManager.calculateSignalLevel(signal, 5);

        /* carefully chosen pastel colors */
        switch (signalPercent) {
            case 4: /* fall through */
            case 3: return Color.rgb(0, 106, 78);
            case 2: return Color.rgb(255,179,71);
            case 1: return Color.rgb(255, 105, 97);
            case 0: return Color.rgb(194, 59, 34);

            default: return Color.BLACK;
        }

    }
}
