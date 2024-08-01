package it.schmid.android.mofa;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.schmid.android.mofa.adapter.ExpandableLandAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkVQuarter;

public class WorkSelectLandActivity extends AppCompatActivity {
    private static final String TAG = "WorkSelectLandActivity";
    private Work work = null;
    private ExpandableLandAdapter adapter;
    private ExpandableListView listView;
    private Button closeButton;
    private final Boolean sortLandByCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.land_list);

        listView = findViewById(R.id.landlistview);
        closeButton = findViewById(R.id.landclose_btn);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle && bundle.containsKey("Work_ID")) {
            int workId = bundle.getInt("Work_ID");
            Log.d(TAG, "Current workid: " + workId);
            work = DatabaseManager.getInstance().getWorkWithId(workId);
        }
        prepAdapter();
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void prepAdapter() {
        List<Land> landList;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean sorted = preferences.getBoolean("asa_landorder_code", false);

        List<WorkVQuarter> selQuarters = DatabaseManager.getInstance().getVQuarterByWorkId(work.getId()); //getting the vquarters of current work
        Log.d(TAG, "Number VQuarters for Current Work Var2 " + selQuarters.size());
        Map<Long, WorkVQuarter> selHashQuarters = new HashMap<Long, WorkVQuarter>(); //creating a HashMap with the selected vquarters
        for (WorkVQuarter quarter : selQuarters) {
            selHashQuarters.put(quarter.getVquarter().getId().longValue(), quarter);  //filling the HashMap with id of selected vquarter and workvquarter object
        }
        if (sorted) {
            landList = DatabaseManager.getInstance().getAllLandsOrderedByCode();
        } else {
            landList = DatabaseManager.getInstance().getAllLands();
        }
        //List<Land> landList = DatabaseManager.getInstance().getAllLands();
        adapter = new ExpandableLandAdapter(landList, this, selHashQuarters, work);
        listView.setAdapter(adapter);
    }
}
