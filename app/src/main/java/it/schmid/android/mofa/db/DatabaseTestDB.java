package it.schmid.android.mofa.db;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkVQuarter;
import it.schmid.android.mofa.model.WorkWorker;

public class DatabaseTestDB {
    private static final String TAG = "DatabaseTestDB";
    static private DatabaseTestDB instance;

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseTestDB(ctx);
        }
    }

    static public DatabaseTestDB getInstance() {
        return instance;
    }

    private final DatabaseHelper helper;

    private DatabaseTestDB(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    public void createTestRecords() {
        new Thread(new Runnable() {
            public void run() {
                Date lDate = (new Date());
                for (int i = 1; i < 700; i++) {
                    Work w;
                    int task;
                    String note = "test";
                    lDate = addDays(lDate, -i);
                    if (i % 2 == 0) {
                        task = 5;
                        w = createWork(lDate, task, "test2");


                    } else {
                        task = 21;
                        w = createWork(lDate, task, "test2");

                    }
                    createWorkVQuarter(w);
                    createWorkWorker(w);

                }

            }
        }).start();


    }

    private Work createWork(Date lDate, Integer task, String note) {
        Work w = new Work();
        w.setDate(lDate);
        w.setTask(DatabaseManager.getInstance().getTaskWithId(task));
        w.setNote(note);
        w.setSended(true);
        w.setValid(true);

        DatabaseManager.getInstance().addWork(w);
        return w;
    }

    private void createWorkWorker(Work w) {
        for (int i = 1; i <= 2; i++) {
            WorkWorker ww = new WorkWorker();
            ww.setWork(w);
            ww.setWorker(DatabaseManager.getInstance().getWorkerWithId(i));
            ww.setHours(2.00);
            DatabaseManager.getInstance().addWorkWorker(ww);
        }

    }

    private void createWorkVQuarter(Work w) {
        int[] vquarters = {1, 2, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 18, 20, 22, 23, 24, 31};
        int elPos = new Random().nextInt(vquarters.length - 2);
        Log.d(TAG, "vquarterpos = " + elPos);
        int vQuarterId = 6;
        for (int i = 1; i <= 4; i++) {
            int vqId = vquarters[elPos];
            WorkVQuarter wv = new WorkVQuarter();
            wv.setWork(w);
            wv.setVquarter(DatabaseManager.getInstance().getVQuarterWithId(vqId));
            DatabaseManager.getInstance().addWorkVQuarter(wv);
            if (elPos >= vquarters.length - 1) {
                elPos = 0;
            } else {
                elPos++;
            }
        }

    }


    private static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

}
