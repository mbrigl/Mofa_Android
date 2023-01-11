package it.schmid.android.mofa;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class InputPurchaseDialogFragment extends DialogFragment {
    private static final String PROD_PARAM = "prodParam";
    private static final String AMOUNT_PARAM = "amountParam";
    private static final String PRICE_PARAM = "priceParam";
    private String product;
    private double amount;
    private double price;
    private EditText mPriceText;
    private EditText mAmountText;
    private Button mOkButton;
    private Button mCancelButton;
    private OnInputPurchaseDialogListener mListener;

    public interface OnInputPurchaseDialogListener{

        void onInputPurchaseDialogInteraction(Double amount, Double price);
    }
    public InputPurchaseDialogFragment() {

    }
    public static InputPurchaseDialogFragment newInstance(String prodParam, Double amountParam, Double priceParam) {
        InputPurchaseDialogFragment fragment = new InputPurchaseDialogFragment();
        Bundle args = new Bundle();
        args.putString(PROD_PARAM, prodParam);
        args.putDouble(AMOUNT_PARAM, amountParam);
        args.putDouble(PRICE_PARAM, priceParam);
        fragment.setArguments(args);
        return fragment;
    }
    public void setCallback(OnInputPurchaseDialogListener mCallback){

        mListener = mCallback;
    }
    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getString(PROD_PARAM);
            amount = getArguments().getDouble(AMOUNT_PARAM);
            price = getArguments().getDouble(PRICE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_purchase_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAmountText = (EditText) view.findViewById(R.id.txt_purchase_amount);
        mPriceText = (EditText) view.findViewById(R.id.txt_purchase_price);
        mOkButton = (Button) view.findViewById(R.id.ok_confirm_button);
        mCancelButton =(Button) view.findViewById(R.id.cancel_confirm_button);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        mAmountText.setText(Double.toString(amount));
        mPriceText.setText(Double.toString(price));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @Override
    public void onDetach() {
        super.onDetach();
        //Log.d("onDetach ", "current json string " + createJsonFromMap());

        mListener = null;
    }
    private void callBack(){
        amount = Double.parseDouble(mAmountText.getText().toString());
        price = Double.parseDouble(mPriceText.getText().toString());
        mListener.onInputPurchaseDialogInteraction(amount,price);
        getDialog().dismiss();
    }
}
