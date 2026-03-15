package it.schmid.android.mofa.dropbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.model.ImportBehavior;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Worker;


public class WebServiceCall {
    private static final String UTF = "UTF-8";

    ProgressDialog dialog;
    String data = "";
    JSONArray jObj = null;
    private static boolean error = false;
    private final Context mContext;
    private final Boolean mOffline;
    private final Boolean mDropbox;
    private final String format;
    private final DbxClientV2 mDbxClient;
    private NotificationService mNotificationService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public WebServiceCall(Context context, Boolean offline, String format, Boolean dropBox, String backEndSoftware, DbxClientV2 dbxClient) {
        this.mContext = context;
        this.mOffline = offline;
        this.mDropbox = dropBox;
        this.format = format;
        this.mDbxClient = dbxClient;
        mNotificationService = new NotificationService(context, true);
        switch (Integer.parseInt(backEndSoftware)) {
            case 1:
                Log.d("TAG", "BackendSoftware: ASAAGRAR");
                break;
            case 2:
                Log.d("TAG", "BackendSoftware:Default");
                break;
            case 3:
                Log.d("TAG", "BackendSoftware:Default");
                break;
        }
    }

    public void execute(ArrayList<Integer> items, String url) {
        onPreExecute();
        executor.execute(() -> {
            String result = doInBackground(items, url);
            mainHandler.post(() -> onPostExecute(result));
        });
    }

    private void publishProgress(int progress) {
        mainHandler.post(() -> onProgressUpdate(progress));
    }

    private void onPreExecute() {
        mNotificationService = new NotificationService(mContext, true);
        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = mContext.getString(R.string.download_title);
        String notifMess = mContext.getString(R.string.download_mess);
        mNotificationService.createNotification(icon, tickerText, notifMess);
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Import");
        dialog.setMax(100);
        dialog.show();
    }

    private void onProgressUpdate(int value) {
        dialog.incrementProgressBy(value);
    }

    private String doInBackground(ArrayList<Integer> items, String url) {
        String extension;
        int progress = (100 / (items.size() * 2));
        if (format.equalsIgnoreCase("1")) {
            extension = ".json";
        } else {
            extension = ".xml";
        }

        for (Integer i : items) {
            switch (i) {
                case 1:
                    Land land = new Land();
                    data = getData(url + "/land/list" + extension);
                    publishProgress(progress);
                    importData(data, land);
                    break;
                case 2:
                    VQuarter vquarter = new VQuarter();
                    data = getData(url + "/vquarter/list" + extension);
                    publishProgress(progress);
                    importData(data, vquarter);
                    break;
                case 3:
                    Machine machine = new Machine();
                    data = getData(url + "/machine/list" + extension);
                    publishProgress(progress);
                    importData(data, machine);
                    break;
                case 4:
                    Worker worker = new Worker();
                    data = getData(url + "/worker/list" + extension);
                    publishProgress(progress);
                    importData(data, worker);
                    break;
                case 5:
                    Task task = new Task();
                    data = getData(url + "/task/list" + extension);
                    publishProgress(progress);
                    importData(data, task);
                    break;
                default:
                    break;
            }
            publishProgress(progress);
        }
        return data;
    }

    private void onPostExecute(String result) {
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

    private String getData(String filePath) {
        if (mDropbox == false) {
            if (mOffline == true) {
                data = offlineImport(filePath);
            } else {
                data = HttpConnect(filePath);
            }
        } else {
            data = getDropboxData(filePath);
            deleteDropboxFile(filePath);
        }
        return data;
    }

    private void importData(String data, ImportBehavior selectedTable) {
        if (format.equalsIgnoreCase("1")) {
            try {
                jObj = new JSONArray(data);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e);
                error = true;
            }
            selectedTable.importMasterData(jObj);
        } else {
            error = selectedTable.importMasterData(data, mNotificationService);
        }
        if (error) {
            onPostExecute("Error in parsing file");
        }
    }

    private String HttpConnect(String restUrl) {
        MofaApplication app = MofaApplication.getInstance();
        okhttp3.OkHttpClient client = app.getHttpClient();
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(restUrl)
                    .post(okhttp3.RequestBody.create("", null))
                    .build();
            try (okhttp3.Response httpResponse = client.newCall(request).execute()) {
                if (httpResponse.body() != null) {
                    data = httpResponse.body().string();
                }
            }
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        }
        return data;
    }

    private String offlineImport(String filePath) {
        String jString = "";
        try {
            File dir = mContext.getExternalFilesDir(null);
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
        return jString;
    }

    private String getDropboxData(String filePath) {
        try {
            FileMetadata mData = (FileMetadata) mDbxClient.files().getMetadata(filePath);
            InputStream in = mDbxClient.files().download(mData.getPathLower()).getInputStream();
            return getStringFromInputStream(in);
        } catch (DbxException e) {
            error = true;
        }
        return null;
    }

    private void deleteDropboxFile(String filePath) {
        try {
            mDbxClient.files().deleteV2(filePath);
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
