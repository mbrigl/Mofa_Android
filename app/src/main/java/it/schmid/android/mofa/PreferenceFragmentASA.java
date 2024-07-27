package it.schmid.android.mofa;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@TargetApi(11)
public class PreferenceFragmentASA extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int res = getActivity().getResources().getIdentifier(getArguments().getString("resource"),
                "xml",
                getActivity().getPackageName());
        addPreferencesFromResource(res);

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
