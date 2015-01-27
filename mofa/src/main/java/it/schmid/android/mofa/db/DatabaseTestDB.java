package it.schmid.android.mofa.db;

import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkVQuarter;
import it.schmid.android.mofa.model.WorkWorker;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.util.Log;

public class DatabaseTestDB {
	private static final String TAG = "DatabaseTestDB";
	static private DatabaseTestDB instance;
	static public void init(Context ctx) {
        if (null==instance) {
            instance = new DatabaseTestDB(ctx);
        }
    }

    static public DatabaseTestDB getInstance() {
        return instance;
    }

    private DatabaseHelper helper;
    private DatabaseTestDB(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }
    public void createTestRecords(){
    	
    		Date lDate = (new Date());
    		for (int i=1;i<350;i++){
    			Work w;
    			int task;
    			String note = "test";
    			lDate = addDays(lDate,1);
    			if (i%2==0){
    				task = 5;
    				w = createWork(lDate,task,"test2");
    				
    					
    			}else{
    				task = 21;
    				w = createWork(lDate,task,"test2");
    				createSprayEntry(w);
    				
    			}
    			createWorkVQuarter(w);
    			createWorkWorker(w);
    			Log.d(TAG, "Created record nr: " + i);
    		}
		
    }
    private Work createWork(Date lDate, Integer task, String note){
    	Work w = new Work();
		w.setDate(lDate);
		w.setTask(DatabaseManager.getInstance().getTaskWithId(task));
		w.setNote(note);
        w.setSended(true);
		w.setValid(true);
		
		DatabaseManager.getInstance().addWork(w);
		return w;
    }
    private void createWorkWorker (Work w){
    	for (int i=1; i<=2; i++){
    		WorkWorker ww= new WorkWorker();
    		ww.setWork(w);	
    		ww.setWorker(DatabaseManager.getInstance().getWorkerWithId(i));
    		ww.setHours(2.00);
    		DatabaseManager.getInstance().addWorkWorker(ww);
    	}
    	
    }
    private void createWorkVQuarter(Work w){
    	int[] vquarters = {1,2,6,7,8,9,10,11,13,14,15,16,18,20,22,23,24,31};
        int elPos = new Random().nextInt(vquarters.length-2);
        Log.d(TAG, "vquarterpos = " + elPos);
        int vQuarterId=6;
    	for (int i=1; i<=4; i++){
    		int vqId = vquarters[elPos];
    		WorkVQuarter wv= new WorkVQuarter();
    		wv.setWork(w);	
    		wv.setVquarter(DatabaseManager.getInstance().getVQuarterWithId(vqId));
    		DatabaseManager.getInstance().addWorkVQuarter(wv);
    		if (elPos >= vquarters.length-1){
                elPos = 0;
            }else{
                elPos++;
            }
    	}
    	
    }
    private void createSprayEntry(Work w){
    	int[] pestIds= {289,301,302,325,345,347,351,356,1179,895,974,977};
        int[] fertIds={106,109,258,282,380,383,726,728,730,755,790};
        int pestId;
    	int fertId;
    	Spraying sp = new Spraying();
    	sp.setWork(w);
    	sp.setConcentration(1);
    	sp.setWateramount(10.00);
    	DatabaseManager.getInstance().addSpray(sp);
        int elPestPos = new Random().nextInt(pestIds.length-2);
        Log.d(TAG, "pestpos = " + elPestPos);
    		for (int i=1; i<=2;i++){
                pestId = pestIds[elPestPos];
    			SprayPesticide sprayP = new SprayPesticide();
    			sprayP.setSpraying(sp);
    			sprayP.setPesticide(DatabaseManager.getInstance().getPesticideWithId(pestId));
    			sprayP.setDose(0.03);
    			sprayP.setDose_amount(20.00);
    			DatabaseManager.getInstance().addSprayPesticide(sprayP);
                if (elPestPos >= pestIds.length-1){
                    elPestPos = 0;
                }else{
                    elPestPos++;
                }
    		}
        int elFertPos = new Random().nextInt(fertIds.length-2);
        Log.d(TAG, "fertpos = " + elFertPos);
    		for (int i=1; i<=2;i++){
                fertId = fertIds[elFertPos];
    			SprayFertilizer sprayF = new SprayFertilizer();
    			sprayF.setSpraying(sp);
    			sprayF.setFertilizer(DatabaseManager.getInstance().getFertilizerWithId(fertId));
    			sprayF.setDose(0.03);
    			sprayF.setDose_amount(20.00);
    			DatabaseManager.getInstance().addSprayFertilizer(sprayF);
                if (elFertPos >= fertIds.length-1){
                    elFertPos = 0;
                }else{
                    elFertPos++;
                }
    		}
    }
   
    
        private static Date addDays(Date date, int days)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days); //minus number would decrement the days
            return cal.getTime();
        }
    
}
