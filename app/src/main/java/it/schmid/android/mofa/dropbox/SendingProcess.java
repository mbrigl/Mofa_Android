package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.j256.ormlite.misc.TransactionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import it.schmid.android.mofa.Globals;
import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.ASAExport;
import it.schmid.android.mofa.model.Work;


public class SendingProcess implements Runnable {
    private Context context;
    private NotificationService mNotificationService; // notification services
    private Boolean asa_New_Ver;
    private String notifMess = "";
    private boolean error = false; // error value for webservice connection
    private String ACCESS_TOKEN; //Dropbox
    UpdateEntries updateEntries;

    //constructor
    public SendingProcess(Context context) {
        this.context = context;
        updateEntries = (UpdateEntries) context;

    }

    // interface to delete the works from workoverview -- callback
    public interface UpdateEntries {
        void updateData();
    }

    /**
     * background sending thread
     */
    public void run() {
        Looper.prepare(); //For Preparing Message Pool for the child Thread
        MofaApplication app = MofaApplication.getInstance();

        ASAExport exporter = new ASAExport(asa_New_Ver);

        writeFileToDropBox(exporter.export(DatabaseManager.getInstance()));

        int icon = android.R.drawable.stat_sys_upload_done;
        CharSequence tickerText = context.getString(R.string.upload_finished);
        if (error) {
            notifMess = context.getString(R.string.upload_finished_error);
            handler2.sendEmptyMessage(0); // handler for updating UI task
        } else {
            deleteAllEntries();
            notifMess = context.getString(R.string.upload_finished_ok);
            handler.sendEmptyMessage(0); // handler for updating UI task
        }

        mNotificationService.completed(icon, tickerText, notifMess);
        Looper.loop(); //Loop in the message queue
    }

    //preparing to send Data
    public void sendData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        asa_New_Ver = preferences.getBoolean("asa_new_ver", false);
        mNotificationService = new NotificationService(context, false);
        int icon = android.R.drawable.stat_sys_upload;
        CharSequence tickerText = context.getString(R.string.upload_title);
        notifMess = context.getString(R.string.upload_mess);
        mNotificationService.createNotification(icon, tickerText, notifMess);

        Thread t = new Thread(this);
        t.start();
    }

    /**
     * handler used for deleting and gui refreshing, accessing gui only through a handler
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateEntries.updateData();
        }
    };
    /**
     * handler used for deleting and gui refreshing, accessing gui only through a handler
     */
    private final Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    };

    /**
     * Dropbox sending process
     */
    private void writeFileToDropBox(String data) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        ACCESS_TOKEN = DropboxClient.retrieveAccessToken(context);
        String filePath = Globals.EXPORT + "/worklist" + dateFormat.format(date) + ".xml";

        try {
            InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
            if (ACCESS_TOKEN != null) {
                DbxClientV2 dbxClient = DropboxClient.getClient(ACCESS_TOKEN);
                dbxClient.files().uploadBuilder(filePath)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            error = true;
        } catch (DbxException e) {
            error = true;
        }
    }

    private void deleteAllEntries() {
        try {
            TransactionManager.callInTransaction(DatabaseManager.getInstance().getConnection(),
                    new Callable<Void>() {
                        public Void call() throws Exception {
                            List<Work> removeWorkList = DatabaseManager.getInstance().getAllOldValidNotSprayWorks();
                            for (Work w : removeWorkList) {
                                DatabaseManager.getInstance().deleteCascWork(w);

                            }
                            DatabaseManager.getInstance().setWorksSendedToTrue();
                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
