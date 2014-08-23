package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.PreviewAnimation;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.db.WorkLoader;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.Worker;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkAdapter extends ArrayAdapter<Work> {
	private static final String TAG = "ArrayAdapter";
	Context context;
	int layoutResourceId;
	List<Work> data=null;
	
	public WorkAdapter(Context context,int layoutResourceId,List<Work> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
		DatabaseManager.init(context);
	}
	public WorkAdapter(Context context, int layoutResourceId){ //constructor for the loader
		super(context, layoutResourceId);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		DatabaseManager.init(context);
	}
	

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final String DATE_FORMAT = "dd.MM.yyyy";
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		final View row;
		WorkHolder holder;
        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
           // setViewHolder(row);
            holder = new WorkHolder();
            holder.imgIcon = (ImageView)convertView.findViewById(R.id.icon);
            holder.txtDate = (TextView)convertView.findViewById(R.id.dateLabel);
            holder.txtWork = (TextView)convertView.findViewById(R.id.workLabel);
            
            holder.delIcon = (ImageView)convertView.findViewById(R.id.delete_icon);
            convertView.setTag(holder);
            
        }
        else
        {
           // row=convertView;
        	holder = (WorkHolder)convertView.getTag();
        }
    	row=convertView;
        final Work work = data.get(position);
        String myDate = dateFormat.format(work.getDate());
      //  Log.d(TAG,myDate);
      //  Log.d(TAG,work.getDate().toString());
        holder.txtDate.setText(myDate);
        
        Task task = work.getTask(); // getting the task
        if (task!=null){
        	holder.txtWork.setText(task.getTask());
        }
        if (work.getValid()==true){
        	holder.imgIcon.setImageResource(R.drawable.ic_ok_icon);
        }else{
        	holder.imgIcon.setImageResource(R.drawable.ic_alerts_and_states_warning);
        }
        
        holder.imgIcon.setClickable(true);
        
        holder.imgIcon.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String txtVquarters = "";
				String txtWorkers = "";
				String txtSpray="";
			//	Work work = data.get(position);
				View toolbar =row.findViewById(R.id.toolbar);
				txtVquarters = getVquarters(work);
				txtWorkers = getWorkers(work);
				txtSpray = getSprayInfos(work);
				if (txtVquarters != ""){
					TextView txt = (TextView) row.findViewById(R.id.txtprevvquarters);
					txt.setText(txtVquarters);
				}
				if (txtWorkers != ""){
					TextView txtworker = (TextView) row.findViewById(R.id.txtprevworkers);
					txtworker.setVisibility(View.VISIBLE);
					txtworker.setText(txtWorkers);
				}
				if (txtSpray !=""){
					TextView txtSprayPrev = (TextView) row.findViewById(R.id.txtprevspray);
					txtSprayPrev.setVisibility(View.VISIBLE);
					txtSprayPrev.setText(txtSpray);
				}
				PreviewAnimation expandAni = new PreviewAnimation(toolbar, 500);
				toolbar.startAnimation(expandAni);
				
				
			}
		});
        holder.delIcon.setImageResource(R.drawable.ic_trash_empty);
        holder.delIcon.setClickable(true);
        holder.delIcon.setOnClickListener(new OnClickListener() {
                    
                    public void onClick(View v) {
                        showYesNoDeleteDialog(work,position);
                    }
                });
        return convertView;
	}
	static class WorkHolder{
		ImageView imgIcon;
        TextView txtDate;
        TextView txtWork;
        ImageView delIcon;
	}
	
	
	private void showYesNoDeleteDialog(final Work work, final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialogdeletetitel);
		builder.setMessage(R.string.dialogdeletemsg);
				 
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		
		   public void onClick(DialogInterface dialog, int which) {
		        // Deleting the entry
			   DatabaseManager.getInstance().deleteCascWork(work);
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
	private String getVquarters(Work work){
		String txtVquarters = "";
		Boolean first = true;
		try { // get vquarters for current work
			List<VQuarter> selectedQuarters = DatabaseManager.getInstance().lookupVQuarterForWork(work);
			for (VQuarter vq: selectedQuarters){
				if (first){
					txtVquarters = vq.getLand().getName() + " " + vq.getVariety() ;
					first = false;
				}else{
					txtVquarters += ", " + vq.getLand().getName() + " " + vq.getVariety() ;
				}
				
				}
			} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return txtVquarters;
	}
	private String getWorkers(Work work){
		String txtWorkers = "";
		Boolean first = true;
		try { // get workers for current work
			List<Worker> selectedWorkers = DatabaseManager.getInstance().lookupWorkerForWork(work);
			for (Worker w: selectedWorkers){
				if (first){
					txtWorkers = w.getFirstName() + " " + w.getLastname() ;
					first = false;
				}else{
					txtWorkers += ", " + w.getFirstName() + " " + w.getLastname() ;
				}
				
				}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return txtWorkers;
	}
	private String getSprayInfos(Work work){
		String txtSpray = "";
		Boolean first = true;
		//getting the spray pesticides, if a spray work
    	if(DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).size()!=0){ //its a spraying work
    		int msprayId;
			msprayId = DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).get(0).getId();
			List<SprayPesticide> selectedPesticides = DatabaseManager.getInstance().getSprayPesticideBySprayId(msprayId);
			for (SprayPesticide sp : selectedPesticides){
				 Pesticide pesticide = DatabaseManager.getInstance().getPesticideWithId(sp.getPesticide().getId());
				 if (first){
						txtSpray = pesticide.getProductName() ;
						first = false;
					}else{
						txtSpray += ", " + pesticide.getProductName() ;
					}
				 
			}
			List<SprayFertilizer> selectedFertilizers = DatabaseManager.getInstance().getSprayFertilizerBySprayId(msprayId);
			for (SprayFertilizer sf : selectedFertilizers){
				Fertilizer fertilizer = DatabaseManager.getInstance().getFertilizerWithId(sf.getFertilizer().getId());
				if (first){
					txtSpray = fertilizer.getProductName() ;
					first = false;
				}else{
					txtSpray += ", " + fertilizer.getProductName() ;
				}
				
			}
    	}
    	return txtSpray;
	}	
}
