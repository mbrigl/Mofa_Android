package it.schmid.android.mofa;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.dropbox.CheckFileTask;
import it.schmid.android.mofa.dropbox.DropboxClient;
import it.schmid.android.mofa.dropbox.SendingProcess;
import it.schmid.android.mofa.dropbox.WebServiceCall;

public class DatabaseSync {

    private final String ACCESS_TOKEN;
    private final Context context;

    public DatabaseSync(String token, Context context) {
        this.ACCESS_TOKEN = token;
        this.context = context;
    }




    /**
     * DropBox Operation for import
     */
    public void importFromDropbox() {
        final ProgressDialog waitingSpinner = new ProgressDialog(context);
        waitingSpinner.setTitle(context.getString(R.string.waitingspinnertitle));
        waitingSpinner.setMessage(context.getString(R.string.waitingspinnertext));
        waitingSpinner.show();
        String extension;
        String filename = "/list"; //the filename is always list
        final ArrayList<Integer> selElements = new ArrayList<Integer>(); //storing the elements to import/update
        final String[] elementDesc = {context.getString(R.string.landtable), context.getString(R.string.vquartertable), context.getString(R.string.machinetable),
                context.getString(R.string.workertable), context.getString(R.string.tasktable)};
        StringBuilder sb = new StringBuilder();
        boolean first = false; //only for checking if \n
        extension = ".xml";
        filename += extension;
        new CheckFileTask(DropboxClient.getClient(ACCESS_TOKEN), elementDesc, new CheckFileTask.Callback() {

            @Override
            public void onDataLoaded(ArrayList<Integer> result, StringBuilder sb) {
                if (result.size() > 0) {
                    waitingSpinner.dismiss();
                    showAlertDialog(sb, result);

                } else { // no updates
                    waitingSpinner.dismiss();
                    showNoUpdateDialog();
                }

            }

            @Override
            public void onError(Exception e) {

            }
        }).execute(filename);
    }



    @SuppressWarnings("deprecation")
    private void showAlertDialog(StringBuilder sb, final ArrayList<Integer> selElements) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.importTitle);
        // Adding a checkbox for reimport all data
        final CheckBox checkBox = new CheckBox(context);
        checkBox.setText("Reimport ALL DATA");
        // Adding a listener for the checkbox with a
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((isChecked) && (DatabaseManager.getInstance().getAllNotSendedWorks().size() > 0)) {
                    Toast.makeText(context, R.string.reimportmessage, Toast.LENGTH_LONG).show();
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(checkBox);
        alertDialog.setView(linearLayout);
        alertDialog.setMessage(sb);
        alertDialog.setPositiveButton(R.string.yesbutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (checkBox.isChecked()) {
                    if (DatabaseManager.getInstance().getAllNotSendedWorks().size() == 0) { // works table is empty
                        flushData(selElements);
                        updateData(selElements, Globals.IMPORT); //starting the import of dropbox data
                    } else { // works table not empty first export
                        //Toast.makeText(getApplicationContext(), R.string.reimportmessage,Toast.LENGTH_LONG).show();
                        if (DatabaseManager.getInstance().getAllWorks().size() != 0) {
                            SendingProcess sending = new SendingProcess(context); //first make the export
                            sending.sendData();
                            Toast.makeText(context, R.string.export_status_message, Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    // the standard case, only a update
                    updateData(selElements, Globals.IMPORT);
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(R.string.nobutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void showNoUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.noUpdatesInfo);
        alertDialog.setMessage(R.string.noupdate)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    /**
     * @param selItems contains an ArrayList of integers to decide which tables are to update
     * @param url      is the URL of the webservice entry point
     */
    private void updateData(ArrayList<Integer> selItems, String url) {
        @SuppressWarnings("unused")
        WebServiceCall importData = new WebServiceCall(context, DropboxClient.getClient(ACCESS_TOKEN));
        importData.execute(selItems, url);
    }

    //deleting the table entries
    private void flushData(ArrayList<Integer> selItems) {
        for (Integer i : selItems) {
            switch (i) {
                case 1:
                    DatabaseManager.getInstance().flushVQuarter(); //deleting vquarter too
                    DatabaseManager.getInstance().flushLand();
                    break;
                case 2:
                    DatabaseManager.getInstance().flushVQuarter();
                    break;
                case 3:
                    DatabaseManager.getInstance().flushMachine();
                    break;
                case 4:
                    DatabaseManager.getInstance().flushWorker();
                    break;
                case 5:
                    DatabaseManager.getInstance().flushTask();
                    break;
                default:
                    break;

            }
        }
    }
}
