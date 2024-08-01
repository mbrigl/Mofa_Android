package it.schmid.android.mofa;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

import it.schmid.android.mofa.HarvestDialogFragment.HarvestDialogListener;
import it.schmid.android.mofa.adapter.WorkHarvestAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.FruitQuality;
import it.schmid.android.mofa.model.Harvest;


public class WorkEditHarvestFragment extends Fragment implements HarvestDialogListener {
    private static final String TAG = "WorkEditHarvestFragment";
    private int mworkId = 0;
    private Button confirmButton;
    private ImageButton btnAddHarvestDoc;
    private ListView lstHarvestDoc;
    WorkEditTabActivity parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager.init(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = (WorkEditTabActivity) getActivity();
        mworkId = parentActivity.getWorkId();
        Log.d(TAG, "[onCreateView] CurrWorkID= " + mworkId);
        View view = inflater.inflate(R.layout.work_edit_harvest, container, false);
        confirmButton = view.findViewById(R.id.work_save_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getActivity();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();

            }

        });
        //btnAddHarvestDoc = (ImageButton) view.findViewById(R.id.work_add_harvest_doc);
        lstHarvestDoc = view.findViewById(R.id.harvest_list_doc);
//			btnAddHarvestDoc.setOnClickListener(new View.OnClickListener() { //event when user adds a new transport document
//				public void onClick(View v) {
//					Log.d(TAG, "Showing the input dialog of transport document");
//					showHarvestDialog();
//					}
//			});
        FloatingActionButton myFab = view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showHarvestDialog();
            }
        });
        fillHarvestList();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Work_ID", mworkId);
        Log.d(TAG, "onSaveInstanceState in  WorkHarvestFragment");

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mworkId = parentActivity.getWorkId();
        Log.d(TAG, "[onResume] CurrWorkID= " + mworkId);
        //populateFields(mworkId);
    }


    private void showHarvestDialog() {
        HarvestDialogFragment harvestDialog = new HarvestDialogFragment();
        harvestDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        harvestDialog.setTargetFragment(this, 0);
        harvestDialog.show(getFragmentManager(), "harvest_dialog");
    }

    private void fillHarvestList() {
        List<Harvest> harvestList = DatabaseManager.getInstance().getHarvestListbyWorkId(mworkId);
        Log.d(TAG, "Number of harvest entries of current work" + harvestList.size());
        WorkHarvestAdapter adapter = new WorkHarvestAdapter(getActivity(), R.layout.harvest_row, harvestList);
        lstHarvestDoc.setAdapter(adapter);
    }

    //callback of the dialog
    public void onFinishEditDialog(Integer docId, Integer amount,
                                   Integer boxes, String notes, Double sugar, Double phValue,
                                   Double phenValue, Double acid, Date hDate, FruitQuality fQuality, Integer pass) {
        Log.d(TAG, "[onFinishEditDialog] - Callback from harvest dialog");
        saveData(docId, amount, boxes, notes, sugar, phValue, phenValue, acid, hDate, fQuality, pass);
    }

    public void saveData(Integer docId, Integer amount, Integer boxes, String notes
            , Double sugar, Double phValue, Double phenValue, Double acid, Date hDate, FruitQuality fQuality, Integer pass) {
        Harvest har = DatabaseManager.getInstance().getHarvestWithId(docId);
        if (har == null) { //new entry
            Harvest newHar = new Harvest();
            newHar.setId(docId);
            newHar.setWork(DatabaseManager.getInstance().getWorkWithId(mworkId));
            newHar.setDate(hDate);
            newHar.setAmount(amount);
            newHar.setBoxes(boxes);
            newHar.setPass(pass);
            newHar.setFruitQuality(fQuality);
            if (!isNull(notes)) {
                newHar.setNote(notes);
            }
            if (!isNull(sugar)) {
                newHar.setSugar(sugar);
            }
            if (!isNull(phValue)) {
                newHar.setPhValue(phValue);
            }
            if (!isNull(phenValue)) {
                newHar.setPhenol(phenValue);
            }
            if (!isNull(acid)) {
                newHar.setAcid(acid);
            }
            DatabaseManager.getInstance().addHarvest(newHar);
        } else {//existing one, update the record


        }
        fillHarvestList();
    }

    private Boolean isNull(Object arg) {
        return arg == null;
    }

}
