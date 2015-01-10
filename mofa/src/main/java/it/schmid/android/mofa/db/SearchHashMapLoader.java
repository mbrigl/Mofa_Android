package it.schmid.android.mofa.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;

public class SearchHashMapLoader extends AsyncTaskLoader<HashMap<Integer,List<String>>> {
	private static final String TAG = "SearchHashMapLoader";
	Receiver receiver;
	public final static String RELOAD = "WorkLoader.RELOAD";
	private HashMap<Integer,List<String>> mData = new HashMap<Integer, List<String>>();
    private ArrayList<Integer> vquarters;
    private Context context;
	public SearchHashMapLoader(Context context, ArrayList<Integer> vquarters) {
		super(context);
		this.vquarters=vquarters;
        this.context= context;
	}

	@Override
	public HashMap<Integer,List<String>> loadInBackground() {
        final String DATE_FORMAT = "dd.MM.yyyy";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        String sprayDate;
        Integer sprayId;
		Log.d(TAG,"Loading Data in a Background Process");
        for (Integer vqId : vquarters){
            List<String> sprayString = new ArrayList<String>();
            List<Work> sprayWorks = DatabaseManager.getInstance().getSprayWorksForVQ(vqId);
            if (sprayWorks.size()==0){
                sprayString.add (context.getString(R.string.search_no_result));
            }
            for (Work w : sprayWorks){
                if (DatabaseManager.getInstance().getSprayingByWorkId(w.getId()).size()!=0){
                    sprayId = DatabaseManager.getInstance().getSprayingByWorkId(w.getId()).get(0).getId();
                    List<SprayPesticide> selectedPesticides = DatabaseManager.getInstance().getSprayPesticideBySprayId(sprayId);
                    for(SprayPesticide sP: selectedPesticides){
                        Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(sP.getPesticide().getId());
                        sprayString.add(dateFormat.format(w.getDate()) + " " + pest.getProductName() + " " + sP.getDose());
                    }
                    List<SprayFertilizer> selectedFertilizers = DatabaseManager.getInstance().getSprayFertilizerBySprayId(sprayId);
                    for(SprayFertilizer sF: selectedFertilizers){
                        Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(sF.getFertilizer().getId());
                        sprayString.add(dateFormat.format(w.getDate()) + " " + fert.getProductName() + " " + sF.getDose());
                    }
                }
            }
            mData.put(vqId,sprayString);
        }
		return mData;
		
	}

	@Override
	public void deliverResult(HashMap<Integer,List<String>> data) {
		 if (isReset()){
			 return;
		 }
		 mData = data;
		 if (isStarted()){
			 super.deliverResult(data);
		 }
		// Log.d(TAG,"Delivered results");
	}

	@Override
	protected void onReset() {
		getContext().unregisterReceiver(receiver);
		onStopLoading();
		if (mData != null){
			mData = null;
		}
		Log.d(TAG,"onReset, setting data to null");
	}

	@Override
	protected void onStartLoading() {
		receiver = new Receiver(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(RELOAD);
		getContext().registerReceiver(receiver, filter);
		if (mData!=null){
			deliverResult(mData);
		}
		
		if (takeContentChanged()||mData==null){
			forceLoad();
		}
	//	forceLoad();
		super.onStartLoading();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	public class Receiver extends BroadcastReceiver{
		SearchHashMapLoader loader;
		
		public Receiver(SearchHashMapLoader loader){
			this.loader = loader;
		}


		@Override
		public void onReceive(Context context, Intent intent) {
			loader.onContentChanged();
			
			}
	}	
	
	
}

