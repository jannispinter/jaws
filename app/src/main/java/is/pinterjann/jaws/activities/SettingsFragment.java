package is.pinterjann.jaws.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import is.pinterjann.jaws.R;


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
