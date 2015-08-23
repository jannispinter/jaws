package is.pinterjann.jaws.helper;

public class FrequencyConverter {

    /**
     * Takes a frequency in MHz and converts it to the corresponding WiFi channel number
     * @param frequency
     * @return channel
     */
    public static int convert(int frequency) {

        switch(frequency) {
            /* 2.4 GHz Band */
            case 2412: return 1;
            case 2417: return 2;
            case 2422: return 3;
            case 2427: return 4;
            case 2432: return 5;
            case 2437: return 6;
            case 2442: return 7;
            case 2447: return 8;
            case 2452: return 9;
            case 2457: return 10;
            case 2462: return 11;
            case 2467: return 12;
            case 2472: return 13;
            case 2484: return 14;

            /* 5 GHz Band */
            case 5180: return 36;
            case 5200: return 40;
            case 5220: return 44;
            case 5240: return 48;
            case 5260: return 52;
            case 5280: return 56;
            case 5300: return 60;
            case 5320: return 64;
            case 5500: return 100;
            case 5520: return 104;
            case 5540: return 108;
            case 5560: return 112;
            case 5580: return 116;
            case 5600: return 120;
            case 5620: return 124;
            case 5640: return 128;
            case 5660: return 132;
            case 5680: return 136;
            case 5700: return 140;
            case 5745: return 149;
            case 5765: return 153;
            case 5785: return 157;
            case 5805: return 161;
            case 5825: return 165;

            /* Other frequencies are not implemented yet */
            default: return 0;
        }
    }

}
