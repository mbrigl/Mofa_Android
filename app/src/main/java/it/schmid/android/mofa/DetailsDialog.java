package it.schmid.android.mofa;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DetailsDialog extends Activity {
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String data = "";
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_details);
        TextView tv = findViewById(R.id.txtdetails);
        btnClose = findViewById(R.id.btn_closedialog);
        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();

            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            data = extras.getString("DATA");
        }
        tv.setText(data);
    }

}
