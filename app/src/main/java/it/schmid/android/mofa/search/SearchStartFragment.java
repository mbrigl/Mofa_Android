package it.schmid.android.mofa.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;

/**
 * Created by schmida on 09.12.14.
 */
public class SearchStartFragment extends Fragment {
    private static final String TAG = "SearchStartFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_frag_start, container, false);
        Button btnLastSprayOp = v.findViewById(R.id.btn_search_last_sprayop);
        Button btnSearchPest = v.findViewById(R.id.btn_search_pest);
        Button btnSearchFert = v.findViewById(R.id.btn_search_fert);
        Button btnDelArchive = v.findViewById(R.id.btn_del_arch);
        Button btnWorkHours = v.findViewById(R.id.btn_hours_overview);
        final CheckBox chkDelAllSended = v.findViewById(R.id.check_del_all_sended);
        TextView txtInfo = v.findViewById(R.id.textView);
        long numSprayEntries = DatabaseManager.getInstance().getNumSprayingEntries();
        Resources res = getResources();
        String infoText = String.format(res.getString(R.string.searchInfo), numSprayEntries);
        txtInfo.setText(infoText);
        btnLastSprayOp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Log.d(TAG, "btnLastSprayOP clicked, loading new fragment");
                Fragment landFragment = SearchLandFragment.newInstance(R.string.searchVQuarter, ActivityConstants.SEARCH_LAST_PEST);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.search_fragment_container, landFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        btnSearchPest.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Log.d(TAG, "btnSearchPest clicked, loading new fragment");
                Fragment pestFragment = SearchPestFragment.newInstance(R.string.searchPestSel, ActivityConstants.SEARCH_PEST);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.search_fragment_container, pestFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btnSearchFert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Fragment pestFragment = SearchPestFragment.newInstance(R.string.searchPestSel, ActivityConstants.SEARCH_FERT);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.search_fragment_container, pestFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btnWorkHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WorkerOverviewActivity.class));
            }
        });
        btnDelArchive.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String msgText;
                if (chkDelAllSended.isChecked()) {
                    msgText = getString(R.string.deleteallsendedmsg);
                } else {
                    msgText = getString(R.string.deletearchivemsg);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setTitle(R.string.delete_archive)
                        .setMessage(msgText)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.yesbutton, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (chkDelAllSended.isChecked()) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            final List<Work> workToDelete = DatabaseManager.getInstance().getAllSendedWorks();
                                            DatabaseManager.getInstance().batchDeleteAllOldSprayEntries(workToDelete);

                                        }
                                    }).start();
                                    Toast.makeText(getActivity(), R.string.deletetoastmsg, Toast.LENGTH_SHORT).show();
                                } else {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            final List<Work> workToDelete = DatabaseManager.getInstance().getOldSprayingWorks();
                                            DatabaseManager.getInstance().batchDeleteAllOldSprayEntries(workToDelete);

                                        }
                                    }).start();
                                    Toast.makeText(getActivity(), R.string.deletetoastmsg, Toast.LENGTH_SHORT).show();
                                }


                            }
                        })
                        .setNegativeButton(R.string.nobutton, null)                        //Do nothing on no
                        .show();
            }
        });
        return v;

    }
}
