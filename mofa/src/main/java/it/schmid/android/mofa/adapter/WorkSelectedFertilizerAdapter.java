package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.InputDoseDialogFragment;
import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.WorkEditSprayFragment;
import it.schmid.android.mofa.WorkEditTabActivity;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkSelectedFertilizerAdapter extends ArrayAdapter<SprayFertilizer>implements InputDoseDialogFragment.InputDoseDialogFragmentListener{
	
	private static final String TAG = "WorkSelectedFertilizerAdapter";
	Context context;
	int layoutResourceId;
	WorkEditSprayFragment fragment;
	List<SprayFertilizer> data=null;
	private SprayFertilizer currSprayFert;
	public WorkSelectedFertilizerAdapter(Context context,WorkEditSprayFragment fragment, int layoutResourceId, List<SprayFertilizer> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
		this.fragment=fragment;
		DatabaseManager.init(context);
		
		
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
        FertilizerHolder holder = null;
        if(row == null){
        	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new FertilizerHolder();
            holder.txtFertilizer = (TextView) row.findViewById(R.id.workpesticideLabel);
            holder.txtDose = (TextView) row.findViewById(R.id.workpesticideDoseLabel);
            holder.txtAmount = (TextView) row.findViewById(R.id.workpesticideAmountLabel);
            holder.delIcon = (ImageView)row.findViewById(R.id.delete_pesticide_icon);
            row.setTag(holder);
        }else{
        	holder = (FertilizerHolder)row.getTag();
        }
        final SprayFertilizer workFertilizer = data.get(position) ;
        final Fertilizer fertilizer = DatabaseManager.getInstance().getFertilizerWithId(workFertilizer.getFertilizer().getId());
        final Spraying spraying = DatabaseManager.getInstance().getSprayingWithId(workFertilizer.getSpraying().getId());
        String strFertilizer = fertilizer.getProductName(); //+ " " + workFertilizer.getDose() + " " + workFertilizer.getDose_amount();
        String strDose = workFertilizer.getDose().toString(); //+ " " + workPesticide.getDose_amount().toString();
        String strAmount = workFertilizer.getDose_amount().toString();
        holder.txtFertilizer.setText(strFertilizer);
        holder.txtDose.setText (strDose);
        holder.txtAmount.setText(strAmount);
        holder.delIcon.setImageResource(R.drawable.ic_action_delete_small);
        holder.delIcon.setClickable(true);
        holder.delIcon.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
            	showYesNoDeleteDialog(workFertilizer,position);
                              
            }
        });
        holder.txtFertilizer.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		InputDoseDialogFragment inputDoseDialog = new InputDoseDialogFragment(fertilizer,workFertilizer.getDose(),
						workFertilizer.getDose_amount(),fragment.getCurrentConc(),fragment.getCurrentWaterAmount(),fragment.getSumOfSize());
				inputDoseDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
				android.support.v4.app.FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
				inputDoseDialog.setCallback(WorkSelectedFertilizerAdapter.this);
				//inputDoseDialog.setTargetFragment( fm.,0);
				setCurrWorkFertilizer(workFertilizer); //saving the current element to a private variable
		        inputDoseDialog.show(fm, "fragment_input_dose");
        	}

			
        });
        return row;
	}
	static class FertilizerHolder{
		TextView txtFertilizer;
		TextView txtDose;
		TextView txtAmount;
        ImageView delIcon;
	}
	private void showYesNoDeleteDialog(final SprayFertilizer workFertilizer, final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialogdeletetitel);
		builder.setMessage(R.string.dialogdeletemsg);
				 
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		
		   public void onClick(DialogInterface dialog, int which) {
			   
			   // Deleting the entry
			   DatabaseManager.getInstance().deleteSprayFertilizer(workFertilizer);
               data.remove(data.get(position)); //removing the item form the list
               notifyDataSetChanged(); 
			   dialog.dismiss();
		   }
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		  
		   public void onClick(DialogInterface dialog, int which) {
		        // Do Nothing
		        dialog.dismiss();
		   }
		 
		});
		 
		 
		AlertDialog alert = builder.create();
		alert.show();
	}
	private void setCurrWorkFertilizer(SprayFertilizer f){
		this.currSprayFert= f;
	}
	private SprayFertilizer getCurrWorkFertilizer(){
		return currSprayFert;
	}
	public void onFinishEditDialog(Double doseInput, Double amountInput) {
		SprayFertilizer fert = getCurrWorkFertilizer();
		fert.setDose(doseInput);
		fert.setDose_amount(amountInput);
		DatabaseManager.getInstance().updateSprayFertilizer(fert);
		notifyDataSetChanged(); 
		
	}
}
