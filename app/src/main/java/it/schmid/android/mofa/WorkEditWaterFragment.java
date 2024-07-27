package it.schmid.android.mofa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Global;

/**
 * Created by schmida on 09.04.16.
 */
public class WorkEditWaterFragment extends Fragment {
    private static final String TAG = "WorkEditWaterFragment";
    private static final String GLOBALTYP = "Irrigation";
    private int mWorkId = 0;
    private int irrigationType = 1;
    private SeekBar seekAmount;
    private SeekBar seekDuration;
    private TableRow mIrrTypeRow;
    private TextView txtAmount;
    private TextView txtDuration;
    private TextView txtTotal;
    private TextView txtIrrDesc;
    private Button confirmButton;
    private Double irrAmount = 0.00;
    private Double irrDuration = 0.00;
    private Double irrTotaleRel = 0.00;
    private boolean newEntry = true;
    private Global irrData;
    WorkEditTabActivity parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager.init(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentActivity = (WorkEditTabActivity) getActivity();
        mWorkId = parentActivity.getWorkId();
        View view = inflater.inflate(R.layout.work_edit_irrigation, container, false);
        mIrrTypeRow = (TableRow) view.findViewById(R.id.tableRow1);
        seekAmount = (SeekBar) view.findViewById(R.id.seekBarAmount);
        seekAmount.setMax(40);
        seekAmount.setProgress(1);
        seekDuration = (SeekBar) view.findViewById(R.id.seekBarDuration);
        seekDuration.setMax(24);
        txtAmount = (TextView) view.findViewById(R.id.txtIrrAmountValue);
        txtDuration = (TextView) view.findViewById(R.id.txtDurationValue);
        txtTotal = (TextView) view.findViewById(R.id.txtIrrTotal);
        txtIrrDesc = (TextView) view.findViewById(R.id.txtImageDesc);
        confirmButton = (Button) view.findViewById(R.id.work_save_button);
        seekAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                irrAmount = ((double) progress / 10);
                txtAmount.setText(irrAmount.toString());
                refreshTotal(irrAmount, irrDuration);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                irrDuration = ((double) progress * 0.5);
                txtDuration.setText(irrDuration.toString());
                refreshTotal(irrAmount, irrDuration);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                getActivity().finish();
            }
        });
        populateFields();
        return view;
    }

    public void populateFields() {

        if (mWorkId != 0) {

            if (DatabaseManager.getInstance().getGlobalbyWorkIdAndIrrigation(mWorkId, GLOBALTYP).size() != 0) {

                irrData = DatabaseManager.getInstance().getGlobalbyWorkId(mWorkId).get(0);
                newEntry = false;
                if (irrData.getData() != null) {
                    readJSON(irrData.getData());
                }
                txtAmount.setText(irrAmount.toString());
                Double seekAmountPos = irrAmount * 10;
                seekAmount.setProgress((seekAmountPos).intValue());
                txtDuration.setText(irrDuration.toString());
                Double seekDurPos = irrDuration / 0.5;
                seekDuration.setProgress(seekDurPos.intValue());

            } else {
                irrAmount = getDefaultValue(mWorkId); //read from preferences the last saved entry for this land
                txtAmount.setText(irrAmount.toString());
                Double seekAmountPos = irrAmount * 10;
                seekAmount.setProgress((seekAmountPos).intValue());
            }

        }
        refreshTotal(irrAmount, irrDuration);
        for (int i = 0; i < mIrrTypeRow.getChildCount(); i++) {
            final ImageButton irrBtn = (ImageButton) mIrrTypeRow.getChildAt(i);
            irrBtn.setTag(Integer.valueOf(i));
            View.OnClickListener irrClick = new View.OnClickListener() {
                public void onClick(View view) {
                    int i = 1 + ((Integer) irrBtn.getTag()).intValue();

                    Log.d(TAG, "[irrBtnClicked] i  = " + i);
                    updateIrrigationView(i);
                }


            };
            irrBtn.setOnClickListener(irrClick);
        }
        updateIrrigationView(irrigationType);
    }

    private void saveData() {
        if (newEntry) {
            irrData = new Global();
            irrData.setWorkId(mWorkId);
            irrData.setTypeInfo(GLOBALTYP);
            irrData.setData(createJSON());
            DatabaseManager.getInstance().addGlobal(irrData);
        } else {
            irrData.setData(createJSON());
            DatabaseManager.getInstance().updateGlobal(irrData);
        }
        setDefaultValue(mWorkId, irrAmount);
    }

    private String createJSON() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("irramount", irrAmount);
            jsonObj.put("irrduration", irrDuration);
            irrTotaleRel = Math.round(irrTotaleRel * 100.0) / 100.0;
            jsonObj.put("irrtotale", irrTotaleRel);
            jsonObj.put("irrtype", irrigationType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    private void readJSON(String data) {
        try {
            JSONObject jsonObj = new JSONObject(data);
            if (jsonObj.has("irramount")) {
                irrAmount = Util.getJSONDouble(jsonObj, "irramount");
            }
            if (jsonObj.has("irrduration")) {
                irrDuration = Util.getJSONDouble(jsonObj, "irrduration");
            }
            if (jsonObj.has("irrtype")) {
                irrigationType = Util.getJSONInt(jsonObj, "irrtype");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setDefaultValue(int workId, Double value) {
        String key = "Irr" + DatabaseManager.getInstance().getFirstLandIdForIrrigation(workId); //creating key for irr
        SharedPreferences prefs = getActivity().getSharedPreferences(PathConstants.ID, Context.MODE_PRIVATE);
        prefs.edit().putLong(key, Double.doubleToLongBits(value)).apply();
        //Toast.makeText(getActivity(),DatabaseManager.getInstance().getFirstLandIdForIrrigation(workId),Toast.LENGTH_LONG).show();
    }

    private Double getDefaultValue(int workId) {
        String key = "Irr" + DatabaseManager.getInstance().getFirstLandIdForIrrigation(workId); //creating key for irr
        SharedPreferences prefs = getActivity().getSharedPreferences(PathConstants.ID, Context.MODE_PRIVATE);
        Double value = Double.longBitsToDouble(prefs.getLong(key, 0));
        return value;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Work_ID", mWorkId);
        Log.d(TAG, "onSaveInstanceState in WorkEditWaterFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        mWorkId = parentActivity.getWorkId();
        Log.d(TAG, "[onResume] CurrWorkID= " + mWorkId);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "[onPause] Save Data");
        //saveData();
    }

    private void updateIrrigationView(int irrNumber) {

        for (int x = 0; x < mIrrTypeRow.getChildCount(); x++) {
            ImageButton irrBtn = (ImageButton) mIrrTypeRow.getChildAt(x);
            if (irrNumber == (x + 1)) {
                irrBtn.setBackgroundColor(getResources().getColor(R.color.deepskyblue));
                updateIrrDesc(irrNumber);
            } else {
                irrBtn.setBackgroundColor(getResources().getColor(R.color.skyblue));
            }
        }
        irrigationType = irrNumber;
        //mPass=iChecked;
    }

    private void updateIrrDesc(int irrNumber) {
        String imgDesc;
        switch (irrNumber) {
            case ActivityConstants.DRYIRRIGATION:
                imgDesc = (getResources().getString(R.string.irrDry));
                break;
            case ActivityConstants.FROSTIRRIGATION:
                imgDesc = (getResources().getString(R.string.irrFrost));
                break;
            case ActivityConstants.DRIPIRRIGATION:
                imgDesc = (getResources().getString(R.string.irrDrip));
                break;
            default:
                imgDesc = "";
                break;
        }
        txtIrrDesc.setText(imgDesc);
    }

    private void refreshTotal(Double amount, Double duration) {
        irrTotaleRel = amount * duration;
        txtTotal.setText(String.format("%.2f mm", irrTotaleRel));
    }
}
