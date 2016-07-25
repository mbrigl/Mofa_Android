package it.schmid.android.mofa;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by schmida on 06.03.16.
 */
public class InputAmountSoilFertFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private static final String TAG = "InputDoseDialogFragment";
    private static final String ARG_PARAM_AMOUNT = "param1";
    private static final String ARG_PARAM_PRODUCT = "param2";
    private static final String ARG_PARAM_SIZE = "param3";
    private TextView mSizeText;
    private EditText mAmountText;
    private EditText mAmountHaText;
    private Button btnOk;
    private Button btnCancel;
    private int focus = 0;
    private String mTitle;
    private Double mAmount;
    private Double mAmountHa;
    private Double mSize;
    private Boolean edit=false;
    private InputAmountDialogFragmentListener callback;
    public interface InputAmountDialogFragmentListener {
        void onFinishEditDialog(Double amountInput);

    }
    //constructors
    public InputAmountSoilFertFragment(){

    }
    public static InputAmountSoilFertFragment newInstance(Double param1,String param2, Double param3) {
        InputAmountSoilFertFragment fragment = new InputAmountSoilFertFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM_AMOUNT, param1);
        args.putString(ARG_PARAM_PRODUCT, param2);
        args.putDouble(ARG_PARAM_SIZE, param3);
        fragment.setArguments(args);
        return fragment;
    }
    public void setCallback(InputAmountDialogFragmentListener mCallback){
        callback = mCallback;
    }
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_PARAM_PRODUCT);
            mAmount = getArguments().getDouble(ARG_PARAM_AMOUNT);
            mSize = getArguments().getDouble(ARG_PARAM_SIZE);
        }
       // callback = (InputAmountDialogFragmentListener) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final NumberFormat nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("###.###");
        View v = inflater.inflate(R.layout.fragment_input_soilfert,container,false);
        mSizeText = (TextView)v.findViewById(R.id.lbl_size);
        Resources res = getResources();
        String sizeText = String.format(res.getString(R.string.sizeInfo), mSize.toString());
        mSizeText.setText(sizeText);
        mAmountText = (EditText)v.findViewById(R.id.txt_amount_total);
        mAmountHaText = (EditText)v.findViewById(R.id.txt_amount_ha);
        btnOk = (Button)v.findViewById(R.id.ok_confirm_button);
        btnCancel = (Button)v.findViewById(R.id.cancel_confirm_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (focus==0){
                    try {
                        calcAmountProHa(nf.parse(mAmountText.getText().toString()).doubleValue());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    try{
                        calcAmount(nf.parse(mAmountHaText.getText().toString()).doubleValue());
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
                }
                try {
                    callback.onFinishEditDialog(nf.parse(mAmountText.getText().toString()).doubleValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getDialog().dismiss();

            }
        });
        getDialog().setTitle(mTitle);
        // Show soft keyboard automaticallybi
        mAmountText.requestFocus();
        mAmountText.setOnEditorActionListener(this);
        mAmountHaText.setOnEditorActionListener(this);
        mAmountText.setOnFocusChangeListener(new View.OnFocusChangeListener() { //listener to calculate the amount pro ha
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        calcAmountProHa(nf.parse(mAmountText.getText().toString()).doubleValue());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "[mAmountText.setOnFocusChange] ");
                } else {
                    //Toast.makeText(getActivity(), "focus==0", Toast.LENGTH_LONG).show();
                    focus = 0;
                }


            }
        });

        mAmountHaText.setOnFocusChangeListener(new View.OnFocusChangeListener() { //listener to calculate the dose
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        calcAmount(nf.parse(mAmountHaText.getText().toString()).doubleValue());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "[mAmountHaText.setOnFocusChange] ");
                } else {
                    focus = 1;
                }

            }
        });
        mAmountText.setText(mAmount.toString());
        calcAmountProHa(mAmount);
        return v;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == i) {

            if (focus==0){
                calcAmountProHa(mAmount); //recalculate the amount pro ha
            }else{
               calcAmount(mAmountHa); //recalculate the amount
            }

            callback.onFinishEditDialog(mAmount);

            this.dismiss();
            return true;
        }
        return false;

    }
    private void calcAmount(Double amountHa){
        Double amount = 0.00;
        if (mSize != 0) {
            amount = amountHa/10000*mSize;
            amount = (double)Math.round(amount * 100) / 100;
            mAmount = amount;
            mAmountText.setText(amount.toString());
        }

    }
    private void calcAmountProHa(Double amount){
        Double amountProHa=0.00;
        if(mSize!= 0){
            amountProHa =amount/mSize*10000 ;
            amountProHa = (double)Math.round(amountProHa * 100) / 100;
            mAmountHa = amountProHa;
            mAmountHaText.setText(amountProHa.toString());
        }

    }
}
