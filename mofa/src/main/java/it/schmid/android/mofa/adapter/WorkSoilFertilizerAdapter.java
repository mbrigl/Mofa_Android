package it.schmid.android.mofa.adapter;




import it.schmid.android.mofa.PromptDialogKeyboard;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.WorkFertilizer;


import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkSoilFertilizerAdapter extends ArrayAdapter<WorkFertilizer> {
	private static final String TAG = "WorkWorkerAdapter";
	Context context;
	int layoutResourceId;
	List<WorkFertilizer> data=null;
	public WorkSoilFertilizerAdapter(Context context, int layoutResourceId, List<WorkFertilizer> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
		DatabaseManager.init(context);
		
		
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
        SoilFertilizerHolder holder = null;
        if(row == null){
        	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new SoilFertilizerHolder();
            holder.txtSoilFertilizer = (TextView) row.findViewById(R.id.workSoilFertilizerLabel);
            holder.delIcon = (ImageView)row.findViewById(R.id.delete_soilFertilizer_icon);
            row.setTag(holder);
        }else{
        	holder = (SoilFertilizerHolder)row.getTag();
        }
        final WorkFertilizer workFertilizer = data.get(position) ;
        SoilFertilizer soilFertilizer = DatabaseManager.getInstance().getSoilFertilizerWithId(workFertilizer.getSoilFertilizer().getId());
        String strSoilFertilizer = soilFertilizer.getProductName() + " "  + workFertilizer.getAmount();
        holder.txtSoilFertilizer.setText(strSoilFertilizer);
        holder.txtSoilFertilizer.setClickable(true);
        holder.txtSoilFertilizer.setOnClickListener(new OnClickListener() { //handling the change of amount by clicking on the list
            
            public void onClick(View v) {
            	
            	PromptDialogKeyboard dlg = new PromptDialogKeyboard(context, R.string.title,
						R.string.enter_amount, workFertilizer.getAmount()) {
					@Override
					public boolean onOkClicked(Double input) {
						// do something
						
							workFertilizer.setAmount(input);
							DatabaseManager.getInstance().updateWorkFertilizer(workFertilizer);
							notifyDataSetChanged(); 
						//	updateState(workId, worker.getId(), input);
							
						
						return true; // true = close dialog

					}
				};
				dlg.show();

				
		 	}
        });	 
           
        holder.delIcon.setImageResource(R.drawable.ic_action_delete_small);
        holder.delIcon.setClickable(true);
        holder.delIcon.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
            	showYesNoDeleteDialog(workFertilizer,position);
            }
        });
        return row;
	}
	
	static class SoilFertilizerHolder{
		TextView txtSoilFertilizer;
        ImageView delIcon;
	}
	private void showYesNoDeleteDialog(final WorkFertilizer workFertilizer, final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialogdeletetitel);
		builder.setMessage(R.string.dialogdeletemsg);
				 
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		
		   public void onClick(DialogInterface dialog, int which) {
			  
			   // Deleting the entry
			   DatabaseManager.getInstance().deleteWorkFertilizer(workFertilizer);
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
