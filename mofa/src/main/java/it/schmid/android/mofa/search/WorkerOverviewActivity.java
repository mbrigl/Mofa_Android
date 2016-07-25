package it.schmid.android.mofa.search;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import it.schmid.android.mofa.PreviewAnimation;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Worker;

public class WorkerOverviewActivity extends Activity {
EditText fromDate;
EditText toDate;
Calendar myCalendar1 = Calendar.getInstance();
Calendar myCalendar2 = Calendar.getInstance();
View toolbar;
int callSource = 1;
    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_overview);
        //inflating the views
        fromDate = (EditText) findViewById(R.id.editTextFromDate);
        toDate = (EditText) findViewById(R.id.editTextToDate);
        ListView workerLV = (ListView) findViewById(R.id.workerhourslist);

        //settings the default dates
        Date dateTo = new Date();
        toDate.setText(sdf.format(dateTo));
        Calendar cal = GregorianCalendar.getInstance();
        cal.add( Calendar.DAY_OF_YEAR, -6); //6 days befor current date
        Date sixDaysAgo = cal.getTime();
        fromDate.setText(sdf.format(sixDaysAgo));
        /**
         * click-Listeners for from and toDate
         */
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSource=2; //to check from which editText the datepicker will be called
                new DatePickerDialog(WorkerOverviewActivity.this, date, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSource=1; //to check from which editText the datepicker will be called
                new DatePickerDialog(WorkerOverviewActivity.this, date, myCalendar1
                        .get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        /*
        * setting the adapter for the list
         */
        List<Worker> workerList = DatabaseManager.getInstance().getAllWorkers();
        ArrayAdapter<Worker> workerAdapter = new WorkerAdapter(this,R.layout.worker_row_hours,workerList);
        workerLV.setAdapter(workerAdapter);
       /*
        *listener for listview click
         */
        workerLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Worker worker = (Worker) adapterView.getAdapter().getItem(i); //get selected worker
                try {
                    String hoursOutput;
                    Date date1 = sdf.parse(fromDate.getText().toString());
                    Date date2 = sdf.parse(toDate.getText().toString());
                    String sum = DatabaseManager.getInstance().getWorkerHours(date1,date2,worker.getId());
                    if (sum==null){ //no hours for current worker
                            hoursOutput= getResources().getString(R.string.noHoursMsg);
                    }else{
                        hoursOutput= "TOTALE: " + sum + " h";
                    }
                    toolbar = view.findViewById(R.id.toolbar);
                    TextView sumText = (TextView) view.findViewById(R.id.hourslabel);

                    sumText.setText(hoursOutput);
                    PreviewAnimation expandAni = new PreviewAnimation(toolbar, 500);
                    toolbar.startAnimation(expandAni);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if (callSource==1){ //calling from first edittext from date
                myCalendar1.set(Calendar.YEAR, year);
                myCalendar1.set(Calendar.MONTH, monthOfYear);
                myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel1();
            }else{  //calling from second edittext to date
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
            }
        }

    };
    private void updateLabel1(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -60);
        Date dateBefore60Days = cal.getTime();
        if (myCalendar1.getTime().before(dateBefore60Days)){
            Toast.makeText(this,R.string.datemsgtomuch,Toast.LENGTH_LONG).show();
            fromDate.setText(sdf.format(cal.getTime()));
        }else{
            fromDate.setText(sdf.format(myCalendar1.getTime()));
        }

    }
    private void updateLabel2(){
        toDate.setText(sdf.format(myCalendar2.getTime()));
    }
    public static class WorkerAdapter extends ArrayAdapter<Worker> {
        List<Worker>workerList;
        Context context;
        int layoutResourceId;
        public WorkerAdapter(Context context,int layoutResourceId, List<Worker> workerList){
            super(context,layoutResourceId, workerList);
            this.context= context;
            this.layoutResourceId=layoutResourceId;
            this.workerList = workerList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final Worker w;
            w = workerList.get(position);
            WorkerHolder holder = null;
            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new WorkerHolder();
                holder.txtWorker = (TextView) row.findViewById(R.id.workerlabel);
                row.setTag(holder);

            }else{
                holder = (WorkerHolder)row.getTag();
            }


           holder.txtWorker.setText(w.getFirstName() + " " + w.getLastname());
            return row;
        }

        @Override
        public int getCount() {
            return workerList.size();
        }

        @Override
        public Worker getItem(int position) {
            return (Worker)workerList.get(position);
        }

        private static class WorkerHolder{
            TextView txtWorker;
        }
    }
}
