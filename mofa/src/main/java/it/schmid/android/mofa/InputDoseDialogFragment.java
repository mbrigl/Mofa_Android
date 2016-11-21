package it.schmid.android.mofa;

import it.schmid.android.mofa.interfaces.ProductInterface;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


@SuppressLint("ValidFragment")
public class InputDoseDialogFragment  extends DialogFragment implements OnEditorActionListener{
	private static final String TAG = "InputDoseDialogFragment";
	private TextView mSizeText;
	private TextView mAmountProHa;
	private EditText mDoseHlText;
	private EditText mAmountText;
	private Button mOkButton;
	private Button mCancelButton;
	private ProductInterface mPesticide;
	private Double mConc;
	private Double mWaterAmount;
	private Double mSize;
	private InputDoseDialogFragmentListener callback;
	private Integer focus = 0; //checking which field is active to calculate the right amount on closing the dialog
	private Double mAmount;
	private Double mDose;
	private Boolean edit=false;
	public interface InputDoseDialogFragmentListener {
		void onFinishEditDialog(Double doseInput, Double amountInput);

	}
	public InputDoseDialogFragment() {
		
	}
	public InputDoseDialogFragment(ProductInterface pesticide,Double conc, Double water,Double size) {
		this.mPesticide=pesticide;
		this.mConc=conc;
		this.mWaterAmount=water;
		this.mSize=size;
	}
//	constructor for existing entries, to modify the dose or amount
	public InputDoseDialogFragment(ProductInterface pesticide,Double dose, Double doseAmount, Double conc, Double water,Double size){
		this.mPesticide=pesticide;
		this.mConc=conc;
		this.mWaterAmount=water;
		this.mDose=dose;
		this.mAmount=doseAmount;
		this.mSize=size;
		this.edit=true;
	}
	public void setCallback(InputDoseDialogFragmentListener mCallback){
		callback = mCallback;
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
        	if (edit==false){
        		callback = (InputDoseDialogFragmentListener) getTargetFragment();
        	}
        	
        	
            
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogFragmentListener interface");
        }
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_input_dose, container);

		mSizeText=(TextView) view.findViewById(R.id.lbl_size);
		Resources res = getResources();
		String sizeText = String.format(res.getString(R.string.sizeInfo), mSize.toString());
		mSizeText.setText(sizeText);
		mAmountProHa =(TextView) view.findViewById(R.id.lbl_amount_ha_value);
        mDoseHlText = (EditText) view.findViewById(R.id.txt_dose_hl);
        mAmountText = (EditText) view.findViewById(R.id.txt_dose_total);
        mOkButton = (Button) view.findViewById(R.id.ok_confirm_button);
        mCancelButton = (Button) view.findViewById(R.id.cancel_confirm_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
        mOkButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (focus==0){
	            	mAmountText.setText((calcAmount(mWaterAmount,mConc))); //recalculate the amount
	            }else{
	            	mDoseHlText.setText((calcDose(mWaterAmount,mConc))); //recalculate the dose
	            }
				SharedPreferences sharedPreferences = PreferenceManager .getDefaultSharedPreferences(getActivity());

				boolean showPestInfos = sharedPreferences.getBoolean("showPestInfos",false);
				//Log.d("showpestInfo", "value = " + showPestInfos);
				//TODO showing constraints on pesticide
				if (mPesticide.showInfo()== 1 && showPestInfos) { //only for pesticides
					PestInfoDialog infoDialog = PestInfoDialog.newInstance(mPesticide.getId());
				 	infoDialog.show(getFragmentManager(),"DialogFragment");
				}
//
				callback.onFinishEditDialog(mDose,mAmount);
				
				
	            getDialog().dismiss();
				
			}
		});
        getDialog().setTitle(mPesticide.getProductName());
        	if (edit==false){ //only the case for new selection
        		if (mPesticide.getDefaultDose()!=null){
            		//java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
            		NumberFormat nf = NumberFormat.getInstance(Locale.US);
					//new DecimalFormat("####.000", DecimalFormatSymbols.getInstance(Locale.US));
            		//DecimalFormat nf = new DecimalFormat("#.###");
					((DecimalFormat) nf).applyPattern("###.###");
					mDoseHlText.setText (nf.format(mPesticide.getDefaultDose()));
            		mAmountText.setText((calcAmount(mWaterAmount,mConc)));
            	}
        	}else{ //editing the selected product
        		NumberFormat nf = NumberFormat.getInstance(Locale.US);
        		mDoseHlText.setText (nf.format(mDose));
        		mAmountText.setText(nf.format(mAmount));
				calcAmountProHa(mAmount);
        	}
        	
        	// Show soft keyboard automaticallybi
            mDoseHlText.requestFocus();
            mDoseHlText.setOnEditorActionListener(this);
            mAmountText.setOnEditorActionListener(this);
            mDoseHlText.setOnFocusChangeListener(new OnFocusChangeListener() { //listener to calculate the amount
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus){
						mAmountText.setText((calcAmount(mWaterAmount,mConc)));
						Log.d(TAG,"[mDoseHlText.setOnFocusChange] " + "calculating with " + mWaterAmount +"," + mConc);
					}else{
						//Toast.makeText(getActivity(), "focus==0", Toast.LENGTH_LONG).show();
						focus=0;
					}
						
					
				}
			});
            
            mAmountText.setOnFocusChangeListener(new OnFocusChangeListener() { //listener to calculate the dose
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus){
						mDoseHlText.setText((calcDose(mWaterAmount,mConc)));
						Log.d(TAG,"[mAmountText.setOnFocusChange] " + "calculating with " + mWaterAmount +"," + mConc);
					}else{
						focus=1;
					}
					
				}
			});
        return view;

	}
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			
            if (focus==0){
            	mAmountText.setText((calcAmount(mWaterAmount,mConc))); //recalculate the amount
            }else{
            	mDoseHlText.setText((calcDose(mWaterAmount,mConc))); //recalculate the dose
            }
            	
			callback.onFinishEditDialog(mDose,mAmount);
			
            this.dismiss();
            return true;
        }
        return false;
    }

	private String calcAmount(Double wateramount, Double conc){
		mAmount=0.00;
		mDose=0.00;

		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		((DecimalFormat) nf).applyPattern("###.###");
		try {
			mDose = nf.parse(mDoseHlText.getText().toString()).doubleValue();
			mAmount = (mDose*wateramount*conc);
			calcAmountProHa(mAmount);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return nf.format(mAmount);
	}	
	private String calcDose(Double wateramount,Double conc){
		mAmount=0.000;
		mDose=0.000;
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		((DecimalFormat) nf).applyPattern("###.###");
		try {
			mAmount = nf.parse(mAmountText.getText().toString()).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mDose = mAmount/(wateramount*conc);
		return nf.format(mDose);
	}
	private void calcAmountProHa(Double amount){
		Double amountProHa=0.00;
		if(mSize!= 0){
			amountProHa =amount/mSize*10000 ;
			amountProHa = (double)Math.round(amountProHa * 100) / 100;
			mAmountProHa.setText(amountProHa.toString());
		}

	}
	
	
	

}
