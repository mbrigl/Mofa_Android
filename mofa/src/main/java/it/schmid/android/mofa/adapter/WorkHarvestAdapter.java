package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.HarvestDialogFragment;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.HarvestDialogFragment.HarvestDialogListener;
import it.schmid.android.mofa.WorkEditHarvestFragment;
import it.schmid.android.mofa.adapter.WorkSelectedPesticideAdapter.PesticideHolder;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.FruitQuality;
import it.schmid.android.mofa.model.Harvest;
import it.schmid.android.mofa.model.SprayPesticide;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.widget.Toast;

public class WorkHarvestAdapter extends ArrayAdapter<Harvest>implements HarvestDialogListener{
	private static final String TAG = "WorkHarvestAdapter";
	Context context;
	int layoutResourceId;
	private Harvest h;
	List<Harvest>data=null;
	public WorkHarvestAdapter(Context context,int layoutResourceId, List<Harvest> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
		DatabaseManager.init(context);
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final String DATE_FORMAT = "dd.MM.yyyy";
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		View row = convertView;
        HarvestHolder holder = null;
        if(row == null){
        	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new HarvestHolder();
            holder.txtHarvestDate = (TextView) row.findViewById(R.id.txt_date);
            holder.txtHarvestId = (TextView) row.findViewById(R.id.txt_docnr);
            holder.txtHarvestAmount = (TextView) row.findViewById(R.id.txt_weight);
            holder.txtHarvestCategory =(TextView) row.findViewById(R.id.txt_category);
            holder.txtHarvestBoxes = (TextView) row.findViewById(R.id.txt_boxes);
            holder.txtHarvestNotes = (TextView) row.findViewById(R.id.txt_notes);
            holder.delIcon = (ImageView)row.findViewById(R.id.delete_icon);
            
            row.setTag(holder);
        }else{
        	holder = (HarvestHolder)row.getTag();
        }
        final Harvest currHarvest = data.get(position);
        String hDate = dateFormat.format(currHarvest.getDate());
        Integer hNumber = currHarvest.getId();
        Integer hWeight = currHarvest.getAmount();
        String hCat = currHarvest.getFruitQuality().getQuality();
        Integer hBoxes = currHarvest.getBoxes();
        String hNotes = currHarvest.getNote();
        //Defininig click-listener for all textviews in this list
        OnClickListener txtClick = new OnClickListener() {
			public void onClick(View v) {
				HarvestDialogFragment harvestDialog = new HarvestDialogFragment(currHarvest);
				android.support.v4.app.FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
				harvestDialog.setHarvestCallback(WorkHarvestAdapter.this);
				setCurrHarvest(currHarvest);
				harvestDialog.show(fm, "fragmentdialog_harvest");
			}
		};
		holder.txtHarvestDate.setText(hDate);
        holder.txtHarvestDate.setOnClickListener(txtClick);	
        
        holder.txtHarvestId.setText(hNumber.toString());
        holder.txtHarvestId.setOnClickListener(txtClick);
        if (hWeight!=0){
        	holder.txtHarvestAmount.setText(hWeight.toString());
        }
        holder.txtHarvestAmount.setOnClickListener(txtClick);
        holder.txtHarvestCategory.setText(hCat);
        holder.txtHarvestCategory.setOnClickListener(txtClick);
        if (hBoxes!=0){
        	holder.txtHarvestBoxes.setText(hBoxes.toString());
        	}
        holder.txtHarvestBoxes.setOnClickListener(txtClick);
        if (hNotes!=null){
        	holder.txtHarvestNotes.setText(hNotes);
        }
        holder.delIcon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showYesNoDeleteDialog(currHarvest,position);
				
			}
		});
        return row;
	}
	static class HarvestHolder{
		TextView txtHarvestDate;
		TextView txtHarvestId;
		TextView txtHarvestAmount;
		TextView txtHarvestCategory;
		TextView txtHarvestBoxes;
		TextView txtHarvestNotes;
        ImageView delIcon;
	}
	public void onFinishEditDialog(Integer docId, Integer amount,
			Integer boxes, String notes, Double sugar, Double phValue,
			Double phenValue, Double acid, Date mDate, FruitQuality mQuality, Integer pass) {
		Harvest har = getCurrHarvest();
		har.setId(docId);
		har.setDate(mDate);
		har.setAmount(amount);
		har.setBoxes(boxes);
		har.setFruitQuality(mQuality);
		har.setPass(pass);
		if (!isNull(notes)){
			har.setNote(notes);
		}
		if (!isNull(sugar)){
			har.setSugar(sugar);
		}
		if (!isNull(phValue)){
			har.setPhValue(phValue);
		}
		if (!isNull(phenValue)){
			har.setPhValue(phenValue);
		}
		if (!isNull(acid)){
			har.setAcid(acid);
		}
		DatabaseManager.getInstance().updateHarvest(har);;
		notifyDataSetChanged(); 
	}
	private void setCurrHarvest(Harvest h){
		this.h = h;
	}
	private Harvest getCurrHarvest(){
		return h;
	}
	private Boolean isNull(Object arg){
		if (arg==null) return true;
		return false;
	}
	private void showYesNoDeleteDialog(final Harvest har, final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialogdeletetitel);
		builder.setMessage(R.string.dialogdeletemsg);
				 
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		
		   public void onClick(DialogInterface dialog, int which) {
		        // Deleting the entry
			   DatabaseManager.getInstance().deleteHarvest(har);;
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
}
