package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.InputDoseDialogFragment;
import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.WorkEditSprayFragment;
import it.schmid.android.mofa.WorkEditTabActivity;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class WorkSelectedPesticideAdapter extends ArrayAdapter<SprayPesticide>implements InputDoseDialogFragment.InputDoseDialogFragmentListener{
	
	private static final String TAG = "WorkPesticideAdapter";
	Context context;
	WorkEditSprayFragment fragment;
	int layoutResourceId;
	List<SprayPesticide> data=null;
	private SprayPesticide p;
	
	
	
	public WorkSelectedPesticideAdapter(Context context,WorkEditSprayFragment fragment, int layoutResourceId, List<SprayPesticide> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.fragment = fragment;
		this.data = data;
		
		DatabaseManager.init(context);
		
		
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
        PesticideHolder holder = null;
        if(row == null){
        	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PesticideHolder();
            holder.txtPesticide = (TextView) row.findViewById(R.id.workpesticideLabel);
            holder.txtDose = (TextView) row.findViewById(R.id.workpesticideDoseLabel);
            holder.txtAmount = (TextView) row.findViewById(R.id.workpesticideAmountLabel);
            holder.delIcon = (ImageView)row.findViewById(R.id.delete_pesticide_icon);
            row.setTag(holder);
        }else{
        	holder = (PesticideHolder)row.getTag();
        }
       final SprayPesticide workPesticide = data.get(position) ;
        final Pesticide pesticide = DatabaseManager.getInstance().getPesticideWithId(workPesticide.getPesticide().getId());
     //   final Spraying spraying = DatabaseManager.getInstance().getSprayingWithId(workPesticide.getSpraying().getId());
        String strPesticide = pesticide.getProductName(); // + " " + workPesticide.getDose() + " " + workPesticide.getDose_amount();
        String strDose = workPesticide.getDose().toString(); //+ " " + workPesticide.getDose_amount().toString();
        String strAmount = workPesticide.getDose_amount().toString();
        holder.txtPesticide.setText(strPesticide);
        holder.txtDose.setText (strDose);
        holder.txtAmount.setText(strAmount);
        holder.delIcon.setImageResource(R.drawable.ic_action_delete_small);
        holder.delIcon.setClickable(true);
        holder.delIcon.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
            	showYesNoDeleteDialog(workPesticide,position);
                }
        });
        holder.txtPesticide.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				InputDoseDialogFragment inputDoseDialog = new InputDoseDialogFragment(pesticide,workPesticide.getDose(),
						workPesticide.getDose_amount(),fragment.getCurrentConc(),fragment.getCurrentWaterAmount());
				android.support.v4.app.FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
				inputDoseDialog.setCallback(WorkSelectedPesticideAdapter.this);
				//inputDoseDialog.setTargetFragment( fm.,0);
				setCurrPesticide(workPesticide); //saving the current element to a private variable
		        inputDoseDialog.show(fm, "fragment_input_dose");
				
			}
		});
        return row;
	}
	
	static class PesticideHolder{
		TextView txtPesticide;
		TextView txtDose;
		TextView txtAmount;
        ImageView delIcon;
	}
	private void showYesNoDeleteDialog(final SprayPesticide workPesticide, final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialogdeletetitel);
		builder.setMessage(R.string.dialogdeletemsg);
				 
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		
		   public void onClick(DialogInterface dialog, int which) {
		       
			   DatabaseManager.getInstance().deleteSprayPesticide(workPesticide);
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
	public void onFinishEditDialog(Double doseInput, Double amountInput) {
		SprayPesticide pest = getCurrPesticide();
		pest.setDose(Math.round(doseInput*100.0)/100.0);
		pest.setDose_amount(Math.round(amountInput*100.0)/100.0);
		DatabaseManager.getInstance().updateSprayPesticide(pest);
		notifyDataSetChanged(); 
		
	}
	private void setCurrPesticide(SprayPesticide p){
		this.p = p;
	}
	private SprayPesticide getCurrPesticide(){
		return p;
	}
}
