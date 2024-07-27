package it.schmid.android.mofa;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import it.schmid.android.mofa.adapter.WorkProductAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.InputDoseDialogFragmentListener;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Purchase;
import it.schmid.android.mofa.model.PurchaseFertilizer;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.Spraying;


public class WorkSelectFertilizerFragment extends Fragment implements InputDoseDialogFragmentListener, InputPurchaseDialogFragment.OnInputPurchaseDialogListener {
    private static final String TAG = "WorkSelectFertilizerActivity";
    private ListView mFertilizerLvWithFilter;
    private EditText mSearchEdt;
    private Button mClearBtn;
    private Button closeButton;
    private WorkProductAdapter<Fertilizer> mFertilizerAdapter;
    private TextWatcher mSearchTw;
    private int sprayId;
    private int purchaseId;
    private Fertilizer currProd;
    private Double concentration;
    private Double wateramount;
    private Double size;
    private int callingActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager.init(getActivity());
        Bundle bundle = getActivity().getIntent().getExtras();
        callingActivity = getActivity().getIntent().getIntExtra("Calling_Activity", 0);
        switch (callingActivity) {
            case ActivityConstants.WORK_SPRAYING_ACTIVITY:
                if (null != bundle && bundle.containsKey("Spray_ID")) {
                    sprayId = bundle.getInt("Spray_ID");
                }
                if (null != bundle && bundle.containsKey("Size")) {
                    size = bundle.getDouble("Size");
                }
                break;
            case ActivityConstants.PURCHASING_ACTIVITY:
                if (null != bundle && bundle.containsKey("Purchase_ID")) {
                    purchaseId = bundle.getInt("Purchase_ID");
                }
                break;
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pesticide_list, container, false);
        mFertilizerLvWithFilter = (ListView) view.findViewById(R.id.list_view_pesticide);
        mSearchEdt = (EditText) view.findViewById(R.id.txt_search_pesticide);
        mClearBtn = (Button) view.findViewById(R.id.clear_search);
        closeButton = (Button) view.findViewById(R.id.btnclose);
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mSearchEdt.setText("");
            }

        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        if (callingActivity == ActivityConstants.WORK_SPRAYING_ACTIVITY) {
            getSprayInfos();
        }


        prepAdapter();
        setTextWatcher();
        mSearchEdt.addTextChangedListener(mSearchTw);
        return view;
    }

    private void prepAdapter() {
        List<Fertilizer> fertilizerList = DatabaseManager.getInstance().getAllFertilizersOrderByName();
        mFertilizerAdapter = new WorkProductAdapter<Fertilizer>(getActivity(), R.layout.pesticide_row, fertilizerList);
        mFertilizerLvWithFilter.setAdapter(mFertilizerAdapter);
        mFertilizerLvWithFilter.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                final Fertilizer fertilizer = (Fertilizer) mFertilizerAdapter.getItem(position);
                if (callingActivity == ActivityConstants.WORK_SPRAYING_ACTIVITY) {
                    showDoseDialog(fertilizer);
                }
                if (callingActivity == ActivityConstants.PURCHASING_ACTIVITY) {
                    MofaApplication app = MofaApplication.getInstance();
                    String backEndSoftware = app.getBackendSoftware();
                    if (Integer.parseInt(backEndSoftware) == 1) { //special case ASA
                        showPurchaseDialogASA(fertilizer);
                    } else {
                        showPurchaseDialog(fertilizer);
                    }

                }

            }

        });
    }

    private void getSprayInfos() {
        Spraying currSpray = DatabaseManager.getInstance().getSprayingWithId(sprayId);
        concentration = currSpray.getConcentration();
        wateramount = currSpray.getWateramount();
    }

    private void showDoseDialog(Fertilizer fertilizer) {
        currProd = fertilizer;
        //FragmentManager fm = getActivity().getSupportFragmentManager();
        InputDoseDialogFragment inputDoseDialog = new InputDoseDialogFragment(fertilizer, concentration, wateramount, size);
        inputDoseDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        inputDoseDialog.setTargetFragment(this, 0);
        inputDoseDialog.show(getFragmentManager(), "fragment_input_dose");
    }

    private void showPurchaseDialog(final Fertilizer fertilizer) {
        currProd = fertilizer;
        PromptDialogKeyboard dlg = new PromptDialogKeyboard(getActivity(), R.string.title,
                R.string.enter_amount, 1.0) {
            @Override
            public boolean onOkClicked(Double input) {
                // do something
                //Log.d(TAG, "showDialog: " + input);
                try {
                    savePurchaseProduct(purchaseId, fertilizer.getId(), input, "");

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true; // true = close dialog

            }


        };
        dlg.show();
    }

    private void showPurchaseDialogASA(final Fertilizer fertilizer) {
        currProd = fertilizer;
        InputPurchaseDialogFragment purchaseDialogFragment = InputPurchaseDialogFragment.newInstance(fertilizer.getProductName(), 1.0, 0.0);
        purchaseDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        purchaseDialogFragment.setCallback(this);
        purchaseDialogFragment.setTargetFragment(this, 0);
        purchaseDialogFragment.show(getFragmentManager(), "fragment_input_purchase");
    }

    private void setTextWatcher() {
        mSearchTw = new TextWatcher() {

            public void afterTextChanged(Editable s) {
                mFertilizerAdapter.getFilter().filter(s);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

        };
    }

    public void onFinishEditDialog(Double doseHl, Double amount) {
        //	Log.d(TAG,"[onFinishEditDialog] Current doseHl =" + doseHl);
        try {
            saveState((Math.round(doseHl * 100.0) / 100.0), (Math.round(amount * 100.0) / 100.0));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveState(Double doseHl, Double amount) throws SQLException {
        //Log.d(TAG,"[saveState] Saving fertilizer treatment");
        List<SprayFertilizer> currSprayFertilizer = DatabaseManager.getInstance().getSprayFertilizerBySprayIdAndByFertilizerId(sprayId, currProd.getId());
        if (currSprayFertilizer.size() == 0) {
            //Log.d(TAG,"[saveState] New Entry");
            SprayFertilizer sprayProduct = new SprayFertilizer();
            Spraying curSpray = DatabaseManager.getInstance().getSprayingWithId(sprayId);
            sprayProduct.setSpraying(curSpray);
            sprayProduct.setFertilizer(currProd);
            sprayProduct.setDose(doseHl);
            sprayProduct.setDose_amount(amount);
            DatabaseManager.getInstance().addSprayFertilizer(sprayProduct);
        } else {
            //Log.d(TAG,"[saveState] Updating Entry");
            SprayFertilizer currSprayFert = currSprayFertilizer.get(0);
            currSprayFert.setDose(doseHl);
            currSprayFert.setDose_amount(amount);
            DatabaseManager.getInstance().updateSprayFertilizer(currSprayFert);
        }
    }

    private void savePurchaseProduct(int purchaseId, Integer fertId,
                                     Double input, String data) throws SQLException {
        List<PurchaseFertilizer> currPurFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseIdAndByFertilizerId(purchaseId, fertId);
        Purchase p = DatabaseManager.getInstance().getPurchaseWithId(purchaseId);
        if (currPurFert.size() == 0) {
            //Log.d(TAG,"[savePurchaseProduct] New Entry");
            PurchaseFertilizer newPurFert = new PurchaseFertilizer();
            newPurFert.setProduct(currProd);
            newPurFert.setPurchase(p);
            newPurFert.setAmount(input);
            newPurFert.setData(data);
            DatabaseManager.getInstance().addPurchaseFertilizer(newPurFert);
        } else {
            //Log.d(TAG,"[savePurchaseProduct] Updating Entry" );
            PurchaseFertilizer updatePurFert = currPurFert.get(0);
            updatePurFert.setAmount(input);
            updatePurFert.setData(data);
            DatabaseManager.getInstance().updatePurchaseFertilizer(updatePurFert);
        }
    }

    @Override
    public void onInputPurchaseDialogInteraction(Double amount, Double price) {
        JSONObject object = new JSONObject();
        try {
            // Add the id to the json
            object.put("price", price);
            // Create a json array
            savePurchaseProduct(purchaseId, currProd.getId(), amount, object.toString());
        } catch (JSONException e) {
            // Handle impossible error
            e.printStackTrace();
        } catch (SQLException e) {
            Toast.makeText(getActivity(), "Error in saving data", Toast.LENGTH_LONG).show();
        }

    }
}
