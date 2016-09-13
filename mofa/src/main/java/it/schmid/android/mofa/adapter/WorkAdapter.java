package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.PreviewAnimation;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.Util;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.db.WorkLoader;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Global;
import it.schmid.android.mofa.model.Harvest;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkFertilizer;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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
                SpannableStringBuilder txtHarvest = new SpannableStringBuilder();
				SpannableStringBuilder txtWorkers = new SpannableStringBuilder();
				SpannableStringBuilder txtSpray= new SpannableStringBuilder();
                SpannableStringBuilder txtFert = new SpannableStringBuilder();
                SpannableStringBuilder txtWater = new SpannableStringBuilder();
			//	Work work = data.get(position);
				View toolbar =row.findViewById(R.id.toolbar);
				txtVquarters = getVquarters(work);
				txtWorkers = getWorkers(work);
				txtSpray = getSprayInfos(work);
                txtHarvest = getHarvestEntry(work);
                txtFert = getFertInfos(work);
                txtWater = getWaterInfos(work);
				if (txtVquarters != ""){
					TextView txt = (TextView) row.findViewById(R.id.txtprevvquarters);
					txt.setText(txtVquarters);
				}
				if (txtWorkers.length() != 0){
					TextView txtworker = (TextView) row.findViewById(R.id.txtprevworkers);
					txtworker.setVisibility(View.VISIBLE);
					txtworker.setText(txtWorkers);
				}
				if (txtSpray.length()!=0){
					TextView txtSprayPrev = (TextView) row.findViewById(R.id.txtprevspray);
					txtSprayPrev.setVisibility(View.VISIBLE);
					txtSprayPrev.setText(txtSpray);
				}
                if (txtHarvest.length()!=0){
                    TextView txtSprayPrev = (TextView) row.findViewById(R.id.txtprevspray);
                    txtSprayPrev.setVisibility(View.VISIBLE);
                    txtSprayPrev.setText(txtHarvest);
                }
                if (txtFert.length()!=0){
                    TextView txtSprayPrev = (TextView) row.findViewById(R.id.txtprevspray);
                    txtSprayPrev.setVisibility(View.VISIBLE);
                    txtSprayPrev.setText(txtFert);
                }
                if (txtWater.length() != 0){
                    TextView txtSprayPrev = (TextView) row.findViewById(R.id.txtprevwater);
                    txtSprayPrev.setVisibility(View.VISIBLE);
                    txtSprayPrev.setText(txtWater);
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
	private SpannableStringBuilder getWorkers(Work work){
		SpannableStringBuilder workerBuilder = new SpannableStringBuilder();
        boolean first = true;
            List<WorkWorker> selectedWorkers = DatabaseManager.getInstance().getWorkWorkerByWorkId(work.getId());
			for (WorkWorker w: selectedWorkers){
                String txtWorkers;
                if (first){
                    first = false;
                } else{
                    workerBuilder.append("\n");
                }

                Worker worker = DatabaseManager.getInstance().getWorkerWithId(w.getWorker().getId());
                txtWorkers = worker.getFirstName() + " " +worker.getLastname() + ": " + w.getHours() + " h";
				workerBuilder.append(txtWorkers);
                workerBuilder.setSpan(new BulletSpan(10), workerBuilder.length() - txtWorkers.length(), workerBuilder.length(),17 );


				
				}

		return workerBuilder;
	}
    private SpannableStringBuilder getHarvestEntry(Work work){
       SpannableStringBuilder harvestBuilder = new SpannableStringBuilder();
       SpannableStringBuilder harvestBuilder1 = new SpannableStringBuilder();
       if (DatabaseManager.getInstance().getHarvestListbyWorkId(work.getId()).size()!=0){
           boolean first = true;
           List<Harvest> harvestList = DatabaseManager.getInstance().getHarvestListbyWorkId(work.getId());
           for (Harvest h : harvestList){
              if (first){
                  first = false;
              }else{
                  harvestBuilder.append("\n");
              }
              String str1 = context.getResources().getString(R.string.har_number) + ": ";
              harvestBuilder.append(str1);
              String str2 = h.getId().toString() + " ";
              harvestBuilder.append(str2);
              harvestBuilder.setSpan(new StyleSpan(1), harvestBuilder.length() - str2.length(), harvestBuilder.length(), 17);
              str1 = context.getResources().getString(R.string.har_amount) + ": ";
              harvestBuilder.append(str1);
              str2 = h.getAmount().toString() + " kg ";
              harvestBuilder.append(str2);
              harvestBuilder.setSpan(new StyleSpan(1), harvestBuilder.length() - str2.length(), harvestBuilder.length(), 17);
              str1 = context.getResources().getString(R.string.har_category) + ": ";
              harvestBuilder.append("\n");
              harvestBuilder.append(str1);
              str2 = h.getFruitQuality().getQuality().toString();
              harvestBuilder.append(str2);
              harvestBuilder.setSpan(new StyleSpan(1), harvestBuilder.length() - str2.length(), harvestBuilder.length(), 17);

           }
       }
       return harvestBuilder;
    }
    private SpannableStringBuilder getFertInfos(Work work){
        SpannableStringBuilder fertBuilder = new SpannableStringBuilder();
        if (DatabaseManager.getInstance().getWorkFertilizerByWorkId(work.getId()).size()!=0){
            boolean first=true;
            List<WorkFertilizer> wF = DatabaseManager.getInstance().getWorkFertilizerByWorkId(work.getId());
            for (WorkFertilizer f: wF){
                if (first){
                    first=false;
                }else{
                    fertBuilder.append("\n");
                }
                SoilFertilizer soilFertilizer = DatabaseManager.getInstance().getSoilFertilizerWithId(f.getSoilFertilizer().getId());
                String fertStr = soilFertilizer.getProductName();
                fertStr+= " (" + f.getAmount() + " kg)";
                fertBuilder.append(fertStr);
                fertBuilder.setSpan(new BulletSpan(10), fertBuilder.length() - fertStr.length(), fertBuilder.length(), 17);

            }
        }
        return fertBuilder;
    }
	private SpannableStringBuilder getSprayInfos(Work work){

        SpannableStringBuilder sprayBuilder = new SpannableStringBuilder();


		//getting the spray pesticides, if a spray work
    	if(DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).size()!=0){ //its a spraying work
    		Spraying sprayInfo = DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).get(0);
            //start formatting output using SpannableStringBuilder
            String str1 = context.getResources().getString(R.string.concentrationcaption) + ": ";
            sprayBuilder.append(str1);
            String str2 = sprayInfo.getConcentration() + " x ";
            sprayBuilder.append(str2);
            sprayBuilder.setSpan(new StyleSpan(1),sprayBuilder.length() - str2.length(), sprayBuilder.length(), 17);
            str1 = context.getResources().getString(R.string.sumwatercaption) + ": ";
            sprayBuilder.append(str1);
            str2 = sprayInfo.getWateramount().toString() ;
            sprayBuilder.append(str2);
            sprayBuilder.setSpan(new StyleSpan(1),sprayBuilder.length() - str2.length(), sprayBuilder.length(), 17);
            int msprayId;

			msprayId = DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).get(0).getId();
			List<SprayPesticide> selectedPesticides = DatabaseManager.getInstance().getSprayPesticideBySprayId(msprayId);
			for (SprayPesticide sp : selectedPesticides){
				 Pesticide pesticide = DatabaseManager.getInstance().getPesticideWithId(sp.getPesticide().getId());
                 String pestStr = pesticide.getProductName();
                 pestStr +=  "(" + sp.getDose() + " )";
                 sprayBuilder.append("\n");
                 sprayBuilder.append(pestStr);
                 sprayBuilder.setSpan(new BulletSpan(10), sprayBuilder.length() - pestStr.length(), sprayBuilder.length(), 17);


				 
			}
			List<SprayFertilizer> selectedFertilizers = DatabaseManager.getInstance().getSprayFertilizerBySprayId(msprayId);
			for (SprayFertilizer sf : selectedFertilizers){
				Fertilizer fertilizer = DatabaseManager.getInstance().getFertilizerWithId(sf.getFertilizer().getId());
                String fertStr = fertilizer.getProductName();
                fertStr += " (" + sf.getDose() + " )";
                sprayBuilder.append("\n");
                sprayBuilder.append(fertStr);
                sprayBuilder.setSpan(new BulletSpan(10), sprayBuilder.length() - fertStr.length(), sprayBuilder.length(),17 );
				
			}
    	}
    	return sprayBuilder;
	}
    private SpannableStringBuilder getWaterInfos(Work work){
        final String GLOBALTYP = "Irrigation";
        double irrDuration= 0.00;
        int irrigationType;
        String irrDesc="";
        SpannableStringBuilder waterBuilder = new SpannableStringBuilder();
        if (DatabaseManager.getInstance().getGlobalbyWorkIdAndIrrigation(work.getId(),GLOBALTYP).size() != 0) {
            Global irrData = DatabaseManager.getInstance().getGlobalbyWorkId(work.getId()).get(0);
            if (irrData.getData() != null) {
                try {
                    JSONObject jsonObj = new JSONObject(irrData.getData());

                    if (jsonObj.has("irrduration")){
                        irrDuration = Util.getJSONDouble(jsonObj,"irrduration");
                    }
                    if (jsonObj.has("irrtype")){
                        irrigationType = Util.getJSONInt(jsonObj,"irrtype");
                        switch (irrigationType) {
                            case ActivityConstants.DRYIRRIGATION:
                                irrDesc = (context.getResources().getString(R.string.irrDry));
                                break;
                            case ActivityConstants.FROSTIRRIGATION:
                                irrDesc = (context.getResources().getString(R.string.irrFrost));
                                break;
                            case ActivityConstants.DRIPIRRIGATION:
                                irrDesc = (context.getResources().getString(R.string.irrDrip));
                                break;
                            default:
                                irrDesc = "";
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                waterBuilder.append(irrDesc);
                waterBuilder.append("\n");
                waterBuilder.append(String.valueOf(irrDuration)+ "h");
            }
        }

        return waterBuilder;
    }
}
