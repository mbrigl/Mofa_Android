package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Xml;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.j256.ormlite.misc.TransactionManager;

import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkMachine;
import it.schmid.android.mofa.model.WorkVQuarter;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;


public class SendingProcess implements Runnable {
    private Context context;
    private NotificationService mNotificationService; // notification services
    private Boolean asa_New_Ver;
    private String sendingData; //
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

        sendingData = asa_New_Ver ? createXMLASAVer16() : createXMLASA();

        writeFileToDropBox(sendingData);

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

    private String createXMLASA() {
        //List<Work> workUploadList = DatabaseManager.getInstance().getAllWorks();
        List<Work> workUploadList = DatabaseManager.getInstance().getAllValidNotSendedWorks();
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Arbeitseintraege");
            //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
            for (Work wk : workUploadList) {
                serializer.startTag("", "Arbeitseintrag");
                serializer.startTag("", "Datum");
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd");
                serializer.text(sdf.format(wk.getDate()));
                serializer.endTag("", "Datum");
                serializer.startTag("", "Arbeit");
                serializer.startTag("", "Code");
                serializer.text(wk.getTask().getCode());
                serializer.endTag("", "Code");
                serializer.endTag("", "Arbeit");

                serializer.startTag("", "Notiz");
                String note = wk.getNote();
                serializer.text(note);
                serializer.endTag("", "Notiz");
                List<WorkWorker> workers = DatabaseManager.getInstance().getWorkWorkerByWorkId(wk.getId());

                for (WorkWorker w : workers) {
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Code");
                    Worker worker = DatabaseManager.getInstance().getWorkerWithId(w.getWorker().getId());
                    serializer.text(worker.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Arbeitskraft");
                    serializer.startTag("", "Stunden");
                    serializer.text(w.getHours().toString());
                    serializer.endTag("", "Stunden");

                    serializer.endTag("", "Arbeitskraft");
                }


                List<WorkMachine> machines = DatabaseManager.getInstance().getWorkMachineByWorkId(wk.getId());

                for (WorkMachine m : machines) {
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Code");
                    Machine machine = DatabaseManager.getInstance().getMachineWithId(m.getMachine().getId());
                    serializer.text(machine.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Maschine");
                    serializer.startTag("", "Stunden");
                    serializer.text(m.getHours().toString());
                    serializer.endTag("", "Stunden");
                    serializer.endTag("", "Maschine");
                }

                List<WorkVQuarter> vquarters = DatabaseManager.getInstance().getVQuarterByWorkId(wk.getId());

                for (WorkVQuarter vq : vquarters) {
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Code");
                    VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
                    serializer.text(vquarter.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Sortenquartier");
                    serializer.endTag("", "Sortenquartier");
                }

                serializer.endTag("", "Arbeitseintrag");
            }
            serializer.endTag("", "Arbeitseintraege");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createXMLASAVer16() {
        //List<Work> workUploadList = DatabaseManager.getInstance().getAllWorks();
        List<Work> workUploadList = DatabaseManager.getInstance().getAllValidNotSendedWorks();
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Arbeitseintraege");
            //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
            for (Work wk : workUploadList) {
                serializer.startTag("", "Arbeitseintrag");
                serializer.startTag("", "Datum");
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd");
                serializer.text(sdf.format(wk.getDate()));
                serializer.endTag("", "Datum");
                serializer.startTag("", "Arbeit");
                serializer.startTag("", "Code");
                serializer.text(wk.getTask().getCode());
                serializer.endTag("", "Code");
                serializer.endTag("", "Arbeit");

                serializer.startTag("", "Notiz");
                String note = wk.getNote();
                serializer.text(note);
                serializer.endTag("", "Notiz");
                List<WorkWorker> workers = DatabaseManager.getInstance().getWorkWorkerByWorkId(wk.getId());

                for (WorkWorker w : workers) {
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Code");
                    Worker worker = DatabaseManager.getInstance().getWorkerWithId(w.getWorker().getId());
                    serializer.text(worker.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Arbeitskraft");
                    serializer.startTag("", "Stunden");
                    serializer.text(w.getHours().toString());
                    serializer.endTag("", "Stunden");

                    serializer.endTag("", "Arbeitskraft");
                }


                List<WorkMachine> machines = DatabaseManager.getInstance().getWorkMachineByWorkId(wk.getId());

                for (WorkMachine m : machines) {
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Code");
                    Machine machine = DatabaseManager.getInstance().getMachineWithId(m.getMachine().getId());
                    serializer.text(machine.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Maschine");
                    serializer.startTag("", "Stunden");
                    serializer.text(m.getHours().toString());
                    serializer.endTag("", "Stunden");
                    serializer.endTag("", "Maschine");
                }

                List<WorkVQuarter> vquarters = DatabaseManager.getInstance().getVQuarterByWorkId(wk.getId());

                for (WorkVQuarter vq : vquarters) {
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Code");
                    VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
                    serializer.text(vquarter.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Sortenquartier");
                    serializer.endTag("", "Sortenquartier");
                }
                serializer.endTag("", "Arbeitseintrag");
            }
            serializer.endTag("", "Arbeitseintraege");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
