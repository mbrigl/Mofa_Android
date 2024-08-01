package it.schmid.android.mofa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class PreferenceContentFragment extends Fragment {
    private TextView checkResetDropbox = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.prefscontent, parent, false);

        checkResetDropbox = result.findViewById(R.id.dropboxreset);
        return (result);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        checkResetDropbox.setText(Boolean.valueOf(prefs.getBoolean("dropboxreset", false)).toString());
    }
}
