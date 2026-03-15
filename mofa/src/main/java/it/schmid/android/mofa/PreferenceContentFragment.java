package it.schmid.android.mofa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;


public class PreferenceContentFragment extends Fragment {
    private TextView checkUpdateOffline = null;
    private TextView checkDropbox = null;
    private TextView checkResetDropbox = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.prefscontent, parent, false);

        checkUpdateOffline = result.findViewById(R.id.updateofflinebox);
        checkDropbox = result.findViewById(R.id.dropbox);
        checkResetDropbox = result.findViewById(R.id.dropboxreset);
        return (result);
    }

    @Override
    public void onResume() {
        super.onResume();

        var prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        checkDropbox.setText(Boolean.valueOf(prefs.getBoolean("dropbox", false)).toString());
        checkResetDropbox.setText(Boolean.valueOf(prefs.getBoolean("dropboxreset", false)).toString());
        checkUpdateOffline.setText(Boolean.valueOf(prefs.getBoolean("updateofflinebox", false)).toString());
    }
}
