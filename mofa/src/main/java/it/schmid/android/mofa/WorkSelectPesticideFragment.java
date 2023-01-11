package it.schmid.android.mofa;


import it.schmid.android.mofa.adapter.WorkProductAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.InputDoseASANewFragmentListener;
import it.schmid.android.mofa.interfaces.InputDoseDialogFragmentListener;
import it.schmid.android.mofa.interfaces.ShowInfoInterface;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.Purchase;
import it.schmid.android.mofa.model.PurchasePesticide;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.Wirkung;

import java.sql.SQLException;
import java.util.List;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class WorkSelectPesticideFragment extends Fragment implements InputDoseDialogFragmentListener,InputDoseASANewFragmentListener,ShowInfoInterface,InputPurchaseDialogFragment.OnInputPurchaseDialogListener{
	private static final String TAG = "WorkSelectPesticideActivity";
	private ListView mPesticideLvWithFilter;
    private EditText mSearchEdt;
    private Button mClearBtn;
    private Button closeButton;
    private WorkProductAdapter<Pesticide> mPesticideAdapter;
    private TextWatcher mSearchTw;
    private int sprayId;
    private int purchaseId;
    private Pesticide currProd;
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
        	if (null!=bundle && bundle.containsKey("Spray_ID")) {
                sprayId = bundle.getInt("Spray_ID");
                }
			if (null!=bundle && bundle.containsKey("Size")) {
				size = bundle.getDouble("Size");
			}
            break;
        case ActivityConstants.PURCHASING_ACTIVITY:
        	if (null!=bundle && bundle.containsKey("Purchase_ID")) {
                purchaseId = bundle.getInt("Purchase_ID");

                }
            break;
        }
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pesticide_list, container, false);
		mPesticideLvWithFilter=(ListView)view.findViewById(R.id.list_view_pesticide);
		mSearchEdt=(EditText) view.findViewById(R.id.txt_search_pesticide);
		mClearBtn=(Button) view.findViewById(R.id.clear_search);
		closeButton=(Button)view.findViewById(R.id.btnclose);
		mClearBtn.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view) {
				mSearchEdt.setText("");
			}
		});
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		if (callingActivity==ActivityConstants.WORK_SPRAYING_ACTIVITY){
			getSprayInfos();
		}
		
		prepAdapter();
		setTextWatcher();
		mSearchEdt.addTextChangedListener(mSearchTw);
		return view;
	}

	private void prepAdapter() {
		List<Pesticide> pesticideList = DatabaseManager.getInstance().getAllPesticidesOrderByName();
		mPesticideAdapter = new WorkProductAdapter<Pesticide>(getActivity(),R.layout.pesticide_row,pesticideList,this);
		mPesticideLvWithFilter.setAdapter(mPesticideAdapter);
		mPesticideLvWithFilter.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				final Pesticide pesticide = (Pesticide) mPesticideAdapter.getItem(position);
				if (callingActivity==ActivityConstants.WORK_SPRAYING_ACTIVITY){
					MofaApplication app = MofaApplication.getInstance();
					if (app.newAsaVersion()){
						// new ASA Version (> Ver 16)
						showDoseDialogNew(pesticide);
					}else {
						showDoseDialog (pesticide); //default case
					}

				}
				if (callingActivity==ActivityConstants.PURCHASING_ACTIVITY){
					MofaApplication app = MofaApplication.getInstance();
					String backEndSoftware = app.getBackendSoftware();
					if (Integer.parseInt(backEndSoftware)==1) { //special case ASA
						showPurchaseDialogASA(pesticide);
					}else {
						showPurchaseDialog (pesticide);
					}

				}
			}
			
		});
	}
	private void getSprayInfos(){
		Spraying currSpray = DatabaseManager.getInstance().getSprayingWithId(sprayId);
		concentration = currSpray.getConcentration();
		wateramount = currSpray.getWateramount();
	}
	private void showDoseDialog(Pesticide pesticide) {
        currProd = pesticide;
		//FragmentManager fm = getActivity().getSupportFragmentManager();
        InputDoseDialogFragment inputDoseDialog = new InputDoseDialogFragment(pesticide,concentration,wateramount,size);
		inputDoseDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        inputDoseDialog.setTargetFragment(this, 0);
		inputDoseDialog.show(getFragmentManager(), "fragment_input_dose");
    }
	private void showDoseDialogNew(Pesticide pesticide) {
		currProd = pesticide;
		//FragmentManager fm = getActivity().getSupportFragmentManager();
		InputDoseDialogFragmentNewVer inputDoseDialog = new InputDoseDialogFragmentNewVer(pesticide,concentration,wateramount,size);
		inputDoseDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
		inputDoseDialog.setTargetFragment(this, 0);
		inputDoseDialog.show(getFragmentManager(), "fragment_input_dose");
	}
	private void showPurchaseDialog(final Pesticide pesticide){
		currProd = pesticide;
		PromptDialogKeyboard dlg = new PromptDialogKeyboard(getActivity(), R.string.title,
				R.string.enter_amount, 1.0) {
			@Override
			public boolean onOkClicked(Double input) {
				// do something

				try {
					savePurchaseProduct(purchaseId, pesticide.getId(), input,"");
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true; // true = close dialog

			}

			
		};
		dlg.show();
	}
	private void showPurchaseDialogASA(final Pesticide pesticide){
		currProd = pesticide;
		InputPurchaseDialogFragment purchaseDialogFragment = InputPurchaseDialogFragment.newInstance(pesticide.getProductName(),1.0,0.0);
		purchaseDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
		purchaseDialogFragment.setCallback(this);
		purchaseDialogFragment.setTargetFragment(this,0);
		purchaseDialogFragment.show(getFragmentManager(),"fragment_input_purchase");
	}
    public void showInfoDialog(){

    }
	private void setTextWatcher(){
		 mSearchTw=new TextWatcher() {

			public void afterTextChanged(Editable s) {
				mPesticideAdapter.getFilter().filter(s);
				
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
		//Log.d(TAG,"[onFinishEditDialog] Current doseHl =" + (Math.round(doseHl*1000.0)/1000.0) + "Amount = " + (Math.round(amount*1000.0)/1000.0));
		try {
			//DecimalFormat twoDForm = new DecimalFormat("#.##");
		    //return Double.valueOf(twoDForm.format(d));
			saveState((Math.round(doseHl*1000.0)/1000.0),(Math.round(amount*1000.0)/1000.0));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFinishEditDialog(Double doseHl, Double amount, Wirkung w) {
		//Log.d(TAG,"[onFinishEditDialog] Current doseHl =" + (Math.round(doseHl*1000.0)/1000.0) + "Amount = " + (Math.round(amount*1000.0)/1000.0));
		try {
			//DecimalFormat twoDForm = new DecimalFormat("#.##");
			//return Double.valueOf(twoDForm.format(d));
			saveStateNewVer((Math.round(doseHl*1000.0)/1000.0),(Math.round(amount*1000.0)/1000.0),w);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveState(Double doseHl, Double amount)throws SQLException{
		//Log.d(TAG,"[saveState] Saving pesticide treatment");
		List<SprayPesticide> currSprayPesticide = DatabaseManager.getInstance().getSprayPesticideBySprayIdAndByPesticideId(sprayId, currProd.getId());
		if (currSprayPesticide.size() == 0) {
			//Log.d(TAG,"[saveState] New Entry");
			SprayPesticide sprayProduct = new SprayPesticide();
			Spraying curSpray = DatabaseManager.getInstance().getSprayingWithId(sprayId);
			sprayProduct.setSpraying(curSpray);
			sprayProduct.setPesticide(currProd);
			sprayProduct.setDose(doseHl);
			sprayProduct.setDose_amount(amount);
			DatabaseManager.getInstance().addSprayPesticide(sprayProduct);
		}else{
			//Log.d(TAG,"[saveState] Updating Entry");
			SprayPesticide currSprayPest = currSprayPesticide.get(0);
			currSprayPest.setDose(doseHl);
			currSprayPest.setDose_amount(amount);
			DatabaseManager.getInstance().updateSprayPesticide(currSprayPest);
		}
	}
	private void saveStateNewVer(Double doseHl, Double amount, Wirkung w)throws SQLException{
		//Log.d(TAG,"[saveState] Saving pesticide treatment");
		List<SprayPesticide> currSprayPesticide = DatabaseManager.getInstance().getSprayPesticideBySprayIdAndByPesticideId(sprayId, currProd.getId());
		if (currSprayPesticide.size() == 0) {
			//Log.d(TAG,"[saveState] New Entry");
			SprayPesticide sprayProduct = new SprayPesticide();
			Spraying curSpray = DatabaseManager.getInstance().getSprayingWithId(sprayId);
			sprayProduct.setSpraying(curSpray);
			sprayProduct.setPesticide(currProd);
			sprayProduct.setDose(doseHl);
			sprayProduct.setReason(w.toString());
			sprayProduct.setPeriodCode(w.getEinsatzperCode());
			sprayProduct.setDose_amount(amount);
			DatabaseManager.getInstance().addSprayPesticide(sprayProduct);
		}else{
			//Log.d(TAG,"[saveState] Updating Entry");
			SprayPesticide currSprayPest = currSprayPesticide.get(0);
			currSprayPest.setDose(doseHl);
			currSprayPest.setDose_amount(amount);
			currSprayPest.setReason(w.toString());
			currSprayPest.setPeriodCode(w.getEinsatzperCode());
			DatabaseManager.getInstance().updateSprayPesticide(currSprayPest);
		}
	}
	private void savePurchaseProduct(int purchaseId, Integer pestId,
			Double input, String data) throws SQLException {
		List<PurchasePesticide> currPurPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseIdAndByPesticideId(purchaseId, pestId);
		Purchase p= DatabaseManager.getInstance().getPurchaseWithId(purchaseId);
		if (currPurPest.size()==0){
			//Log.d(TAG,"[savePurchaseProduct] New Entry");
			PurchasePesticide newPurPest = new PurchasePesticide();
			newPurPest.setProduct(currProd);
			newPurPest.setPurchase(p);
			newPurPest.setAmount(input);
			newPurPest.setData(data);
			DatabaseManager.getInstance().addPurchasePesticide(newPurPest);
		}else{
			//Log.d(TAG,"[savePurchaseProduct] Updating Entry" );
			PurchasePesticide updatePurPest = currPurPest.get(0);
			updatePurPest.setAmount(input);
			updatePurPest.setData(data);
			DatabaseManager.getInstance().updatePurchasePesticide(updatePurPest);
		}
	}


    public void showInfos(Integer pestId) {
		MofaApplication app = MofaApplication.getInstance();
		if (app.newAsaVersion()){
			PestInfoDialogASA infoDialog = PestInfoDialogASA.newInstance(pestId);
			infoDialog.show(getFragmentManager(),"DialogFragment");
		}else {
			PestInfoDialog infoDialog = PestInfoDialog.newInstance(pestId);
			infoDialog.show(getFragmentManager(),"DialogFragment");
		}

    }


	@Override
	public void onInputPurchaseDialogInteraction(Double amount, Double price) {
		JSONObject object = new JSONObject();
		try {
			Log.d("WorkSelPestFrag","Callback from InputPurchaseDialogFragment");
			// Add the id to the json
			object.put("price", price);
			// Create a json array
			savePurchaseProduct(purchaseId, currProd.getId(), amount,object.toString());
		} catch (JSONException e) {
			// Handle impossible error
			e.printStackTrace();
		} catch (SQLException e) {
			Toast.makeText(getActivity(),"Error in saving data",Toast.LENGTH_LONG).show();
		}

	}
}
