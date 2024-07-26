package it.schmid.android.mofa;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import it.schmid.android.mofa.model.Einsatzgrund;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.FruitQuality;
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
    private final Boolean mOffline;
    private final Boolean mDropbox;
    private final String format;
    private String statusMsg = "";
    private ArrayList<Integer> selItems;
    private String encoding; //setting the encoding for xml
    //Dropbox variable
    private final DbxClientV2 mDbxClient;
    //Notification variable
    private NotificationService mNotificationService;

    public WebServiceCall(Context context, Boolean offline, String format, Boolean dropBox, String backEndSoftware, DbxClientV2 dbxClient) {
        this.mContext = context;
        this.mOffline = offline;
        this.mDropbox = dropBox;
        this.format = format;
        this.mDbxClient = dbxClient;
        //Get the notification manager
        mNotificationService = new NotificationService(context, true);
        switch (Integer.parseInt(backEndSoftware)) {
            case 1:
                Log.d("TAG", "BackendSoftware: ASAAGRAR");
                encoding = UTF;
                break;
            case 2:
                Log.d("TAG", "BackendSoftware:Default");
                encoding = UTF;
                break;
            case 3:
                Log.d("TAG", "BackendSoftware:Default");
                encoding = UTF;
                break;


        }
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
        if (format.equalsIgnoreCase("1")) { //json
            extension = ".json";
        } else {
            extension = ".xml";
        }

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
                case 9:
                    FruitQuality fruitQuality = new FruitQuality();
                    data = getData(url + "/category/list" + extension);
                    publishProgress(progress);
                    importData(data, fruitQuality);
                    statusMsg += " fruitquality - error: " + error + "\n";
                    break;
                case 11:
                    Einsatzgrund einsatzgrund = new Einsatzgrund();
                    data = getData(url + "/reason/list" + extension);
                    publishProgress(progress);
                    importData(data, einsatzgrund);
                    statusMsg += " reason - error: " + error + "\n";
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

        if (!mDropbox) {
            if (mOffline) { //we make only a offline import
                data = offlineImport(filePath);
            } else { //import over Internet
                data = HttpConnect(filePath);
            }
        } else {
            data = getDropboxData(filePath);
            deleteDropboxFile(filePath);
        }
        return data;
    }

    private void importData(String data, ImportBehavior selectedTable) {
        if (format.equalsIgnoreCase("1")) { //case JSON
            try {
                jObj = new JSONArray(data);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e);
                error = true; //concatinating the error status
            }
            selectedTable.importMasterData(jObj);
        } else {  //case XML
            error = selectedTable.importMasterData(data, mNotificationService); //concatinating the error status
        }
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

    // modified connection, using application object as singleton connection
    private String HttpConnect(String restUrl) {
        try {
            MofaApplication app = MofaApplication.getInstance();
            HttpClient client = app.getHttpClient();
            HttpPost httpPost = new HttpPost(restUrl);
            HttpResponse httpResponse = client.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, StandardCharsets.ISO_8859_1), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            data = sb.toString();
        } catch (Exception e) {
            // Log.e("Buffer Error", "Error converting result " + e.toString());
            error = true;
        }
        return data;
    }


    private String offlineImport(String filePath) {
        String jString = "";
        try {

            File dir = Environment.getExternalStorageDirectory();
            File importFile = new File(dir, filePath);
            if (importFile.exists()) {
                FileInputStream stream = new FileInputStream(importFile);

                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    jString = Charset.defaultCharset().decode(bb).toString();
                } finally {
                    stream.close();
                }
            } else {
                error = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO
        return jString;
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
