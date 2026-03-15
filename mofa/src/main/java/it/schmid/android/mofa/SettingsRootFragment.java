package it.schmid.android.mofa;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsRootFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_root, rootKey);
    }
}
