package it.schmid.android.mofa.dropbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.ImportBehavior;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Worker;


public class WebServiceCall extends AsyncTask<Object, Integer, String> {
    private static final String TAG = "WebServiceCall";
    private static final String UTF = "UTF-8";
    private static final String ISO8859 = "iso-8859-1";

    ProgressDialog dialog;
    InputStream is = null;
    String data = "";
    JSONArray jObj = null;
    private static boolean error = false;
    private final Context mContext;
    private String statusMsg = "";
    private ArrayList<Integer> selItems;
    private final String encoding; //setting the encoding for xml
    //Dropbox variable
    private final DbxClientV2 mDbxClient;
    //Notification variable
    private NotificationService mNotificationService;

    public WebServiceCall(Context context, DbxClientV2 dbxClient) {
        this.mContext = context;
        this.mDbxClient = dbxClient;
        //Get the notification manager
        mNotificationService = new NotificationService(context, true);
        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        encoding = UTF;
    }

    @Override
    protected void onPreExecute() {
        //Get the notification manager
        mNotificationService = new NotificationService(mContext, true);
        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = mContext.getString(R.string.download_title);
        String notifMess = mContext.getString(R.string.download_mess);
        mNotificationService.createNotification(icon, tickerText, notifMess);
        // Setup Progress Dialog
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Import");
        dialog.setMax(100);
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // Increment Progress Dialog with the Update
        // from the doInBackgroundMethod
        dialog.incrementProgressBy(values[0]);
    }

    @Override
    protected String doInBackground(Object... params) {
        String extension;
        String url;
        Integer progress;
        selItems = (ArrayList<Integer>) params[0];
        progress = (100 / ((selItems.size() * 2))); //progress dividing
        url = (String) params[1];
        //checking file format
        extension = ".xml";

        for (Integer i : selItems) {
            switch (i) {
                case 1:
                    Land land = new Land();
                    data = getData(url + "/land/list" + extension);
                    publishProgress(progress);
                    importData(data, land);
                    statusMsg += " land - error: " + error + "\n";
                    break;
                case 2:
                    VQuarter vquarter = new VQuarter();
                    data = getData(url + "/vquarter/list" + extension);
                    publishProgress(progress);
                    importData(data, vquarter);
                    statusMsg += " vquarter - error: " + error + "\n";
                    break;
                case 3:
                    Machine machine = new Machine();
                    data = getData(url + "/machine/list" + extension);
                    publishProgress(progress);
                    importData(data, machine);
                    statusMsg += " machine - error: " + error + "\n";
                    break;
                case 4:
                    Worker worker = new Worker();
                    data = getData(url + "/worker/list" + extension);
                    publishProgress(progress);
                    importData(data, worker);
                    statusMsg += " worker - error: " + error + "\n";
                    break;
                case 5:
                    Task task = new Task();
                    data = getData(url + "/task/list" + extension);
                    publishProgress(progress);
                    importData(data, task);
                    statusMsg += " task - error: " + error + "\n";
                    break;
                case 6:
                    Pesticide pesticide = new Pesticide();
                    data = getData(url + "/pesticide/list" + extension);
                    publishProgress(progress);
                    importData(data, pesticide);
                    statusMsg += " pesticide - error: " + error + "\n";
                    break;
                case 7:
                    Fertilizer fertilizer = new Fertilizer();
                    data = getData(url + "/fertilizer/list" + extension);
                    publishProgress(progress);
                    importData(data, fertilizer);
                    statusMsg += " fertilizer - error: " + error + "\n";
                    break;
                case 8:
                    SoilFertilizer sFertilizer = new SoilFertilizer();
                    data = getData(url + "/soilfertilizer/list" + extension);
                    publishProgress(progress);
                    importData(data, sFertilizer);
                    statusMsg += " soilfertilizer - error: " + error + "\n";
                    break;

                default:
                    break;
            }
            /** Invokes the callback method onProgressUpdate */
            publishProgress(progress);
        }


        // Log.d(TAG, "Content of request: " + data);
        return data;
    }


    /**
     * @param filePath = Path to the file
     * @return data = the content of the xml or json file
     */

    private String getData(String filePath) {
        data = getDropboxData(filePath);
//        deleteDropboxFile(filePath);
        return data;
    }

    private void importData(String data, ImportBehavior selectedTable) {
        //case XML
        error = selectedTable.importMasterData(data, mNotificationService); //concatinating the error status
        if (error) {
            onPostExecute("Error in parsing file");

        }
        //return data;
    }

    @Override
    protected void onPostExecute(String result) {
        String notifMess = "";

        int icon = android.R.drawable.stat_sys_download_done;
        CharSequence tickerText = mContext.getString(R.string.download_finished);
        if (error) {
            notifMess = mContext.getString(R.string.download_finished_error);
        } else {
            notifMess = mContext.getString(R.string.download_finished_ok);
        }


        mNotificationService.completed(icon, tickerText, notifMess);
        dialog.dismiss();
    }

    private String getDropboxData(String filePath) {
        try {
            FileMetadata mData = (FileMetadata) mDbxClient.files().getMetadata(filePath);
            InputStream in = mDbxClient.files().download(mData.getPathLower()).getInputStream();
            String data = getStringFromInputStream(in);

            return data;

        } catch (DbxException e) {
            error = true;
        }


        return null;
    }

    private void deleteDropboxFile(String filePath) {
        try {
            mDbxClient.files().delete(filePath);
        } catch (DbxException e) {
            error = true;
        }
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

}
