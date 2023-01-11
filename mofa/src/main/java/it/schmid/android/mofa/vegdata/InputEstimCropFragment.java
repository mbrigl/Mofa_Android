package it.schmid.android.mofa.vegdata;


import android.content.res.Resources;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import it.schmid.android.mofa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputEstimCropFragment extends DialogFragment {
    private static final String TAG = "InputDoseDialogFragment";
    private static final String ARG_PARAM_AMOUNT = "param1";
    private static final String ARG_PARAM_SIZE = "param2";
    private static final String ARG_PARAM_TITLE = "param3";
    private TextView mSizeText;
    private NumberPicker mNumberPicker;
    private TextView mAmountLabel;
    private Button btnOk;
    private Button btnCancel;
    private int mAmount;
    private Double mSize;
    private String mTitle;
    private InputAmountDialogFragmentListener callback;
    public interface InputAmountDialogFragmentListener {
        void onFinishEditDialog(Integer amountInput);

    }
    public InputEstimCropFragment() {
        // Required empty public constructor
    }
    public static InputEstimCropFragment newInstance(Integer param1,Double param2,String param3) {
        InputEstimCropFragment fragment = new InputEstimCropFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_AMOUNT, param1);
        args.putDouble(ARG_PARAM_SIZE, param2);
        args.putString(ARG_PARAM_TITLE, param3);
        fragment.setArguments(args);
        return fragment;
    }
    public void setCallback(InputAmountDialogFragmentListener mCallback){
        callback = mCallback;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mAmount = getArguments().getInt(ARG_PARAM_AMOUNT);
            mSize = getArguments().getDouble(ARG_PARAM_SIZE);
            mTitle = getArguments().getString(ARG_PARAM_TITLE);
        }
        // callback = (InputAmountDialogFragmentListener) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().setTitle(mTitle);
        final NumberFormat nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("###.###");
        View v = inflater.inflate(R.layout.fragment_input_estim_crop, container, false);
        mSizeText = (TextView)v.findViewById(R.id.lbl_size);
        Resources res = getResources();
        String sizeText = String.format(res.getString(R.string.sizeInfo), mSize.toString());
        mSizeText.setText(sizeText);
        mAmountLabel = (TextView)v.findViewById(R.id.lbl_amount);
        mNumberPicker = (NumberPicker)v.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(140);
        mNumberPicker.setValue(mAmount);
        mNumberPicker.setWrapSelectorWheel(true);
        //Set a value change listener for NumberPicker
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                calcAmountForSize(newVal);
            }
        });
        btnOk = (Button)v.findViewById(R.id.ok_confirm_button);
        btnCancel = (Button)v.findViewById(R.id.cancel_confirm_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onFinishEditDialog(mNumberPicker.getValue());

                getDialog().dismiss();
            }
        });


        return v;

    }
    private void calcAmountForSize(int curValue){
        Double amount = curValue * mSize / 10000;
        String output = String.format(getResources().getString(R.string.amountCrop),amount.intValue());
        mAmountLabel.setText(output);
    }
}
