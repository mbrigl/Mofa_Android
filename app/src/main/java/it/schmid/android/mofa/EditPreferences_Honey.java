package it.schmid.android.mofa;

import android.preference.PreferenceActivity;

import java.util.List;


public class EditPreferences_Honey extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.prference_headers, target);

    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return WorkingJournalPreferenceFragment.class.getName().equals(fragmentName);
    }
}
