package it.schmid.android.mofa;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

@TargetApi(11)
public class WorkingJournalPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

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
        //Log.d("WorkingJournalPreferenceFragment", "calling listener");
        CheckBoxPreference dropboxReset = (CheckBoxPreference) getPreferenceScreen().findPreference("dropboxreset");
        //	Log.d("WorkingJournalPreferenceFragment", "Pref ASA ");
        if (dropboxReset.isChecked()) {
            Toast.makeText(getActivity(), R.string.dropboxresetmessage, Toast.LENGTH_LONG).show();
        }
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
