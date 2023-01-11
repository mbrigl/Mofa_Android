package it.schmid.android.mofa;



import it.schmid.android.mofa.adapter.QualitySpinnerAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.FruitQuality;
import it.schmid.android.mofa.model.Harvest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
@SuppressLint("ValidFragment")
public class HarvestDialogFragment extends DialogFragment{
	private static final String TAG="HarvestDialogFragment";
	private HarvestDialogListener harvestCallback;
	//layout variables
	private EditText mDocNrText;
	private EditText mAmountText;
	private EditText mBoxesText;
	private EditText mNotesText;
	private EditText mDateText;
	private EditText mPhText;
	private EditText mSugarText;
	private EditText mPhenText;
	private EditText mAcidText;
	private Spinner mQualitySpinner;
	private TableRow mTurnRow;
	//declaring the local variable for input values
	private Date mDate=null;
	private Integer mDocId=0;
	private Integer mAmount=0;
	private Integer mBoxes=0;
	private String mNotes=null;
	private Double mSugar=null;
	private Double mPhValue = null;
	private Double mPhenValue = null;
	private Double mAcid = null;
	private FruitQuality mQuality;
	private Boolean edit=false;
	private Integer mPass = 1;
	//buttons
	private Button mOkButton;
	private Button mCancelButton;
	//calendar variable
	Calendar myCalendar = Calendar.getInstance();
	//declaring interface for callback
	
	public interface HarvestDialogListener{
		void onFinishEditDialog(Integer docId, Integer amount, Integer boxes, String notes, Double sugar, Double phValue,
				Double phenValue, Double acid, Date mDate, FruitQuality mQuality, Integer pass);
	}
	//empty constructor
	public HarvestDialogFragment(){
		
		}
	//constructor for existing values
	public HarvestDialogFragment(Harvest currHar){
		this.mDocId= currHar.getId();
		this.mAmount=currHar.getAmount();
		this.mBoxes=currHar.getBoxes();
		this.mNotes=currHar.getNote();
		this.mSugar=currHar.getSugar();
		this.mPhValue=currHar.getPhValue();
		this.mPhenValue=currHar.getPhenol();
		this.mAcid=currHar.getAcid();
		this.mDate=currHar.getDate();
		this.mQuality=currHar.getFruitQuality();
		this.mPass=currHar.getPass();
		edit=true;
				
	}
	public void setHarvestCallback(HarvestDialogListener mharvestCallback){
		this.harvestCallback=mharvestCallback;
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	if (edit==false){
        		harvestCallback = (HarvestDialogListener) getTargetFragment();
        	}
        		
        	  
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement HarvestDialogListener interface");
        }
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		List<FruitQuality>qualityList = DatabaseManager.getInstance().getAllQualities();
		final QualitySpinnerAdapter adapter = new QualitySpinnerAdapter(qualityList, getActivity());
		
		View view = inflater.inflate(R.layout.dialog_harvest_frag,container);
		getDialog().setTitle(R.string.har_title);
		mDateText= (EditText)view.findViewById(R.id.edtxt_date);
		mDocNrText=(EditText)view.findViewById(R.id.edtxt_docnr);
		mAmountText = (EditText) view.findViewById(R.id.edtxt_amount);
		mQualitySpinner = (Spinner) view.findViewById(R.id.spinner_quality);
		mQualitySpinner.setAdapter(adapter);
		mNotesText = (EditText) view.findViewById(R.id.edtxt_notes);
		mBoxesText = (EditText) view.findViewById(R.id.edtxt_boxes);
		mSugarText = (EditText) view.findViewById(R.id.edtxt_sugar);
		mPhText = (EditText) view.findViewById(R.id.edtxt_pH);
		mPhenText = (EditText) view.findViewById(R.id.edtxt_phen);
		mAcidText = (EditText) view.findViewById(R.id.edtxt_acid);
		mOkButton = (Button) view.findViewById(R.id.ok_confirm_button);
        mCancelButton = (Button) view.findViewById(R.id.cancel_confirm_button);
		mTurnRow = (TableRow) view.findViewById(R.id.tableRow10);
		for (int i = 0; i < mTurnRow.getChildCount(); i++){
			final TextView passText = (TextView) mTurnRow.getChildAt(i);
			passText.setTag(Integer.valueOf(i));
			View.OnClickListener passClick = new View.OnClickListener()
	        {
	          public void onClick(View view)
	          {
	            int i = 1 + ((Integer)passText.getTag()).intValue();
	            updatePassView(i);
	            
	          }

			
	        };
	        passText.setOnClickListener(passClick);
		}
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
        mOkButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				readValues();
				if (mDocId==0){
					Toast.makeText(getActivity(),R.string.har_warning_id , Toast.LENGTH_LONG).show();
				}
				harvestCallback.onFinishEditDialog(mDocId,mAmount,mBoxes,mNotes,mSugar,mPhValue,mPhenValue,mAcid,mDate,mQuality,mPass);
				
				
	            getDialog().dismiss();
				
			}
		});
        mDateText.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //filling the Dialog with default values or existing ones
		 	String myFormat = "dd.MM.yyyy"; //In which you need put here
		    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);
        if (edit==false){ //new entry
        	Date date = new Date();
   		    mDateText.setText(sdf.format(date));
   		    
        }else{ //existing entry
        	mDateText.setText(sdf.format(mDate));
        	mDocNrText.setText(mDocId.toString());
        	mAmountText.setText(mAmount.toString());
        	mQualitySpinner.setSelection(adapter.getPosition(mQuality));
        	mNotesText.setText(mNotes);
        	mBoxesText.setText(mBoxes.toString());
        	if (mSugar!=null){
        		mSugarText.setText(mSugar.toString());
        	}
        	if (mPhValue!=null){
        		mPhText.setText(mPhValue.toString());
        	}
        	if (mPhenValue!=null){
        		mPhenText.setText(mPhenValue.toString());
        	}
        	if (mAcid!=null){
        		mAcidText.setText(mAcid.toString());
        	}
        	
        	
        }
        updatePassView(mPass); //setting the pass textView, default 1
		return view;
	}
	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

	    public void onDateSet(DatePicker view, int year, int monthOfYear,
	            int dayOfMonth) {
	        // TODO Auto-generated method stub
	        myCalendar.set(Calendar.YEAR, year);
	        myCalendar.set(Calendar.MONTH, monthOfYear);
	        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	        updateDateLabel();
	    }

	};
	 private void updateDateLabel() {

		    String myFormat = "dd.MM.yyyy"; //In which you need put here
		    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);

		    mDateText.setText(sdf.format(myCalendar.getTime()));
	 }
	/**
	 * assigning values from the input dialog fragment to the variables
	 */
	private void readValues(){
		String myFormat = "dd.MM.yyyy"; //In which you need put here
	    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);
	    mQuality = (FruitQuality)mQualitySpinner.getSelectedItem();
	    try {
			mDate = sdf.parse(mDateText.getText().toString());
		} catch (ParseException e1) {
			Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
		}
		if (TextUtils.isDigitsOnly(mDocNrText.getText())){
			try{
				mDocId = Integer.parseInt(mDocNrText.getText().toString());
			}catch (NumberFormatException e){
				Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
			}
		}
		if (TextUtils.isDigitsOnly(mAmountText.getText())&& (mAmountText.getText().toString().trim().length()!=0)){
			try{
				mAmount = Integer.parseInt(mAmountText.getText().toString());
			}catch (NumberFormatException e){
				Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
			}
		}
		if (isNumeric(mBoxesText.getText().toString()) && (mBoxesText.getText().toString().trim().length()!=0)){
			try{
				mBoxes = Integer.parseInt(mBoxesText.getText().toString());
			}catch (NumberFormatException e){
				Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
			}
		}
		if (!TextUtils.isEmpty(mNotesText.getText())){ //only if not empty
			mNotes = mNotesText.getText().toString();
		}
		if (!TextUtils.isEmpty(mSugarText.getText())){ //only if not empty
			try{
				mSugar = Double.parseDouble(mSugarText.getText().toString());
			} catch (NumberFormatException e){
				Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
			}
		}
		if (!TextUtils.isEmpty(mPhText.getText())){ //only if not empty
			try{
				mPhValue = Double.parseDouble(mPhText.getText().toString());
			} catch (NumberFormatException e){
				Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
			}
		}
		if (!TextUtils.isEmpty(mPhenText.getText())){ //only if not empty
			try{
				mPhenValue = Double.parseDouble(mPhenText.getText().toString());
			} catch (NumberFormatException e){
				Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
			}
		}
		if (!TextUtils.isEmpty(mAcidText.getText())){ //only if not empty
			try{
				mAcid = Double.parseDouble(mAcidText.getText().toString());
			} catch (NumberFormatException e){
				Toast.makeText(getActivity(),"Check Input", Toast.LENGTH_LONG).show();
			}
			
		}
	}
	private void updatePassView(int iChecked) {
		
		for (int x = 0; x < mTurnRow.getChildCount(); x++){
			TextView passText = (TextView) mTurnRow.getChildAt(x);
			if (iChecked==(x+1)){
				passText.setBackgroundColor(getResources().getColor(R.color.lightgreen));
			}else{
				passText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			}
		}
		mPass=iChecked;
	}
    private static boolean isNumeric(String str)
    {
        try
        {
           Integer i = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
