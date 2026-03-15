package it.schmid.android.mofa;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.File;

public class WorkingJournalPreferenceFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        CheckBoxPreference offlineUpdate = findPreference("updateOffline");
        CheckBoxPreference dropbox = findPreference("dropbox");
        CheckBoxPreference dropboxReset = findPreference("dropboxreset");
        ListPreference backendSoftware = findPreference("listBackendFormat");
        ListPreference encodePref = findPreference("listFormat");

        if (offlineUpdate != null && offlineUpdate.isChecked()) {
            if (isSdPresent()) {
                createSdFolderStruct();
            } else {
                Toast.makeText(requireActivity(), "SD-Card not present or ready", Toast.LENGTH_LONG).show();
            }
        }
        if (dropbox != null && dropbox.isChecked()) {
            if (offlineUpdate != null) offlineUpdate.setChecked(false);
        }
        if (backendSoftware != null && "1".equals(backendSoftware.getValue())) {
            if (dropbox != null) dropbox.setChecked(true);
            if (encodePref != null) encodePref.setValue("2");
        }
        if (dropboxReset != null && dropboxReset.isChecked()) {
            Toast.makeText(requireActivity(), R.string.dropboxresetmessage, Toast.LENGTH_LONG).show();
        }
    }

    private void createSdFolderStruct() {
        if (isSdPresent()) {
            File direct = new File(requireActivity().getExternalFilesDir(null) + MofaConstants.PATH);
            if (!direct.exists()) direct.mkdir();
            new File(requireActivity().getExternalFilesDir(null) + MofaConstants.IMPORT + "/land").mkdirs();
            new File(requireActivity().getExternalFilesDir(null) + MofaConstants.EXPORT).mkdirs();
            new File(requireActivity().getExternalFilesDir(null) + MofaConstants.IMPORT + "/worker").mkdirs();
            new File(requireActivity().getExternalFilesDir(null) + MofaConstants.IMPORT + "/vquarter").mkdirs();
            new File(requireActivity().getExternalFilesDir(null) + MofaConstants.IMPORT + "/task").mkdirs();
            new File(requireActivity().getExternalFilesDir(null) + MofaConstants.IMPORT + "/machine").mkdirs();
        }
    }

    private boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
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
