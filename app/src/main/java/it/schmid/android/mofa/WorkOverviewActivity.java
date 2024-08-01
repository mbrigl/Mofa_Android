package it.schmid.android.mofa;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.schmid.android.mofa.adapter.WorkAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.dropbox.SendingProcess;
import it.schmid.android.mofa.model.Work;


public class WorkOverviewActivity extends AppCompatActivity implements SendingProcess.UpdateEntries {
    private static final String TAG = "WorkOverviewActivity";
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    ListView listViewWork; // listview of work

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.work_list);
        // ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(R.layout.work_list,null);
        listViewWork = findViewById(R.id.listViewWork);
        FloatingActionButton myFab = findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(WorkOverviewActivity.this, WorkEditTabActivity.class);
                startActivityForResult(i, ACTIVITY_CREATE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData(); //filling the list
    }

    /**
     * Filling the listview with data
     */
    @Override
    public void updateData() {
        List<Work> workList = DatabaseManager.getInstance().getAllNotSendedWorks();
        Log.d(TAG, "Number of total works:" + DatabaseManager.getInstance().getAllWorks().size());

        WorkAdapter adapter = new WorkAdapter(this, R.layout.work_row, workList);
        listViewWork.setAdapter(adapter);
        listViewWork.setOnItemClickListener(new OnItemClickListener() { // listener for click event
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Work work = adapter.getItem(position); //current work

                Intent i = new Intent(WorkOverviewActivity.this, WorkEditTabActivity.class); // opening the corresponding activity
                i.putExtra("Work_ID", work.getId());

                // Activity returns an result if called with startActivityForResult
                startActivityForResult(i, ACTIVITY_EDIT);
            }
        });

    }

    // Called with the result of the other activity
    // requestCode was the origin request code send to the activity
    // resultCode is the return code, 0 is everything is ok
    // intend can be used to get data
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        updateData(); // refilling the controls
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) { //inflating the menu

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.work_menu, menu);

        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.work_menu_upload) {
            Log.d(TAG, "Upload the work entries");
            new DatabaseSync(null, this).exportToDropbox();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
