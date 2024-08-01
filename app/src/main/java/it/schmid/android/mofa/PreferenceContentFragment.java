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
    private TextView text = null;
    private TextView list = null;
    private TextView listbackend = null;
    private TextView checkUpdateOffline = null;
    private TextView checkResetDropbox = null;
    private TextView checkShowPestInfos = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.prefscontent, parent, false);

        text = result.findViewById(R.id.text);
        list = result.findViewById(R.id.list);
        listbackend = result.findViewById(R.id.listbackend);
        checkUpdateOffline = result.findViewById(R.id.updateofflinebox);
        checkResetDropbox = result.findViewById(R.id.dropboxreset);
        checkShowPestInfos = result.findViewById(R.id.showPestInfos);
        return (result);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        text.setText(prefs.getString("text", "<unset>"));
        list.setText(prefs.getString("list", "<unset>"));
        listbackend.setText(prefs.getString("listbackend", "<unset>"));

        checkResetDropbox.setText(Boolean.valueOf(prefs.getBoolean("dropboxreset", false)).toString());
        checkShowPestInfos.setText(Boolean.valueOf(prefs.getBoolean("showPestInfos", false)).toString());
        checkUpdateOffline.setText(Boolean.valueOf(prefs.getBoolean("updateofflinebox", false)).toString());
    }
}
