package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.Globals;
import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.Purchase;
import it.schmid.android.mofa.model.PurchaseFertilizer;
import it.schmid.android.mofa.model.PurchasePesticide;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkFertilizer;
import it.schmid.android.mofa.model.WorkMachine;
import it.schmid.android.mofa.model.WorkVQuarter;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;


public class SendingProcess implements Runnable {
    private static final String TAG = "SendingProcess";
    private static final String ASANOTE = "(MoFa)";
    Context context;
    private NotificationService mNotificationService; // notification services
    private Boolean grossPrice; // checking if for ASA using gross prices
    private Boolean mofaNote;
    private Boolean asa_New_Ver;
    private String sendingData; //
    private String urlPath;
    private String notifMess = "";
    private boolean error = false; // error value for webservice connection
    private final String restResponse = ""; // not used yet, but response of json-webservice
    private final int callingActivity;
    private String asaWorkHerbicideCode;
    private String ACCESS_TOKEN; //Dropbox
    RemoveEntries mremoveEntries;

    //constructor
    public SendingProcess(Context context, Integer callingActivity) {
        this.context = context;
        this.callingActivity = callingActivity;
        mremoveEntries = (RemoveEntries) context;

    }

    // interface to delete the works from workoverview -- callback
    public interface RemoveEntries {
        void deleteAllEntries();
    }

    /**
     * background sending thread
     */
    public void run() {
        Looper.prepare(); //For Preparing Message Pool for the child Thread
        MofaApplication app = MofaApplication.getInstance();

        if (callingActivity == ActivityConstants.WORK_OVERVIEW) { //calling this asynch method from workoverview
            if (asa_New_Ver) {
                sendingData = createXMLASAVer16();
            } else {
                sendingData = createXMLASA();
            }

        }
        if (callingActivity == ActivityConstants.PURCHASING_ACTIVITY) { //calling this asynch method from purchasing activity
            if (asa_New_Ver) {
                Log.d("Sending Data", "ASA_New_Purchasexml");
                sendingData = createPurchaseXMLASAVer16();
            } else {
                sendingData = createPurchaseXMLASA();
            }

        }

        writeFileToDropBox(sendingData);

        int icon = android.R.drawable.stat_sys_upload_done;
        CharSequence tickerText = context.getString(R.string.upload_finished);
        if (error) {
            notifMess = context.getString(R.string.upload_finished_error);
            handler2.sendEmptyMessage(0); // handler for updating UI task
        } else {
            notifMess = context.getString(R.string.upload_finished_ok);
            handler.sendEmptyMessage(0); // handler for updating UI task

        }

        mNotificationService.completed(icon, tickerText, notifMess);
        Looper.loop(); //Loop in the message queue
    }

    //preparing to send Data
    public void sendData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        urlPath = preferences.getString("url", "");
        grossPrice = preferences.getBoolean("grossprice", false);
        mofaNote = preferences.getBoolean("asanote", false);
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
            mremoveEntries.deleteAllEntries();

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
     * helper function to handle the response of the webservice - not used for the moment
     *
     * @param is-inputstream
     * @return
     */
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * @param data
     * @param fileType "1" for Json; "2" for XML
     */
    private void writeFile(String data, String fileType) {
        if (isSdPresent()) {
            FileOutputStream fos;
            File sdCard = Environment.getExternalStorageDirectory();
            File file = null;
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            if (fileType.equalsIgnoreCase("1")) {
                if (callingActivity == ActivityConstants.WORK_OVERVIEW) {
                    file = new File(sdCard.getAbsolutePath() + Globals.EXPORT, "worklist" + dateFormat.format(date) + ".json");
                }
                if (callingActivity == ActivityConstants.PURCHASING_ACTIVITY) {
                    file = new File(sdCard.getAbsolutePath() + Globals.EXPORT, "purchaselist" + dateFormat.format(date) + ".json");
                }
            } else {
                if (callingActivity == ActivityConstants.WORK_OVERVIEW) {
                    file = new File(sdCard.getAbsolutePath() + Globals.EXPORT, "worklist" + dateFormat.format(date) + ".xml");
                }
                if (callingActivity == ActivityConstants.PURCHASING_ACTIVITY) {
                    file = new File(sdCard.getAbsolutePath() + Globals.EXPORT, "purchaselist" + dateFormat.format(date) + ".xml");
                }
            }

            byte[] bData = data.getBytes();
            try {
                fos = new FileOutputStream(file);
                fos.write(bData);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
            }
        } else {
            error = true;
        }

    }

    private boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    private String createXML() {
        //List<Work> workUploadList = DatabaseManager.getInstance().getAllWorks();
        List<Work> workUploadList = DatabaseManager.getInstance().getAllValidNotSendedWorksExcel();
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "works");
            serializer.attribute("", "number", String.valueOf(workUploadList.size()));
            for (Work wk : workUploadList) {
                serializer.startTag("", "work");
                serializer.startTag("", "date");
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd'T'hh:mm:sss'Z'");
                serializer.text(sdf.format(wk.getDate()));
                serializer.endTag("", "date");
                serializer.startTag("", "task");
                serializer.text(wk.getTask().getId().toString());
                serializer.endTag("", "task");
                serializer.startTag("", "type");
                if (wk.getTask().getType() == null) {
                    serializer.text("O");
                } else {
                    serializer.text(wk.getTask().getType());
                }
                serializer.endTag("", "type");
                serializer.startTag("", "note");
                serializer.text(wk.getNote());
                serializer.endTag("", "note");
                List<WorkVQuarter> vquarters = DatabaseManager.getInstance().getVQuarterByWorkIdOrderedByVq(wk.getId());
                for (WorkVQuarter vq : vquarters) {
                    serializer.startTag("", "vquarter");
                    serializer.startTag("", "vqid");
                    serializer.text(vq.getVquarter().getId().toString());
                    serializer.endTag("", "vqid");
                    serializer.endTag("", "vquarter");
                }
                List<WorkWorker> workers = DatabaseManager.getInstance().getWorkWorkerByWorkId(wk.getId());
                for (WorkWorker w : workers) {
                    serializer.startTag("", "worker");
                    serializer.startTag("", "workerid");
                    serializer.text(w.getWorker().getId().toString());
                    serializer.endTag("", "workerid");
                    serializer.startTag("", "workerhours");
                    serializer.text(w.getHours().toString());
                    serializer.endTag("", "workerhours");
                    serializer.endTag("", "worker");
                }
                List<WorkMachine> machines = DatabaseManager.getInstance().getWorkMachineByWorkId(wk.getId());
                for (WorkMachine m : machines) {
                    serializer.startTag("", "machine");
                    serializer.startTag("", "machineid");
                    serializer.text(m.getMachine().getId().toString());
                    serializer.endTag("", "machineid");
                    serializer.startTag("", "machinehours");
                    serializer.text(m.getHours().toString());
                    serializer.endTag("", "machinehours");
                    serializer.endTag("", "machine");
                }
                List<WorkFertilizer> soilfertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(wk.getId());
                for (WorkFertilizer wf : soilfertilizers) {
                    serializer.startTag("", "soilfertilizer");
                    serializer.startTag("", "soilfertilizerid");
                    serializer.text(wf.getSoilFertilizer().getId().toString());
                    serializer.endTag("", "soilfertilizerid");
                    serializer.startTag("", "soilfertamount");
                    serializer.text(wf.getAmount().toString());
                    serializer.endTag("", "soilfertamount");
                    serializer.endTag("", "soilfertilizer");
                }
                List<Spraying> spraying = DatabaseManager.getInstance().getSprayingByWorkId(wk.getId());
                for (Spraying s : spraying) {
                    serializer.startTag("", "spraying");
                    serializer.startTag("", "concentration");
                    serializer.text(s.getConcentration().toString());
                    serializer.endTag("", "concentration");
                    serializer.startTag("", "wateramount");
                    serializer.text(s.getWateramount().toString());
                    serializer.endTag("", "wateramount");
                    List<SprayPesticide> sprayPest = DatabaseManager.getInstance().getSprayPesticideBySprayId(s.getId());
                    for (SprayPesticide sp : sprayPest) {
                        serializer.startTag("", "Pesticide");
                        serializer.startTag("", "pestid");
                        serializer.text(sp.getPesticide().getId().toString());
                        serializer.endTag("", "pestid");
                        serializer.startTag("", "dose");
                        serializer.text(sp.getDose().toString());
                        serializer.endTag("", "dose");
                        serializer.startTag("", "amount");
                        serializer.text(sp.getDose_amount().toString());
                        serializer.endTag("", "amount");
                        serializer.endTag("", "Pesticide");
                    }
                    List<SprayFertilizer> sprayFert = DatabaseManager.getInstance().getSprayFertilizerBySprayId(s.getId());
                    for (SprayFertilizer sf : sprayFert) {
                        serializer.startTag("", "Fertilizer");
                        serializer.startTag("", "fertid");
                        serializer.text(sf.getFertilizer().getId().toString());
                        serializer.endTag("", "fertid");
                        serializer.startTag("", "dose");
                        serializer.text(sf.getDose().toString());
                        serializer.endTag("", "dose");
                        serializer.startTag("", "amount");
                        serializer.text(sf.getDose_amount().toString());
                        serializer.endTag("", "amount");
                        serializer.endTag("", "Fertilizer");
                    }
                    serializer.endTag("", "spraying");
                }
                serializer.endTag("", "work");
            }
            serializer.endTag("", "works");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
                String note;
                if (mofaNote) {
                    note = ASANOTE + " " + wk.getNote();
                } else {
                    note = wk.getNote();
                }
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


                List<WorkFertilizer> soilfertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(wk.getId());
                for (WorkFertilizer wf : soilfertilizers) {
                    serializer.startTag("", "Duengung");
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
                    serializer.startTag("", "Duengemittel");
                    serializer.startTag("", "Artikel");
                    serializer.startTag("", "Code");
                    SoilFertilizer sf = DatabaseManager.getInstance().getSoilFertilizerWithId(wf.getSoilFertilizer().getId());
                    serializer.text(sf.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Artikel");
                    serializer.startTag("", "Menge");
                    serializer.text(wf.getAmount().toString());
                    serializer.endTag("", "Menge");
                    serializer.endTag("", "Duengemittel");

                    serializer.endTag("", "Duengung");
                }
                List<Spraying> spraying = DatabaseManager.getInstance().getSprayingByWorkId(wk.getId());
                for (Spraying s : spraying) {
                    String test = wk.getTask().getType();

                    if (wk.getTask().getType().equalsIgnoreCase("H")) { //herbicide
                        serializer.startTag("", "Herbizideinsatz");
                    } else {
                        serializer.startTag("", "Spritzung");
                    }

                    serializer.startTag("", "Konzentration");
                    serializer.text(s.getConcentration().toString());
                    serializer.endTag("", "Konzentration");
                    serializer.startTag("", "Wassermenge");
                    serializer.text(s.getWateramount().toString());
                    serializer.endTag("", "Wassermenge");
                    serializer.startTag("", "Notiz");
                    serializer.endTag("", "Notiz");
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
                    List<SprayPesticide> sprayPest = DatabaseManager.getInstance().getSprayPesticideBySprayId(s.getId());
                    for (SprayPesticide sp : sprayPest) {
                        serializer.startTag("", "Spritzmittel");
                        serializer.startTag("", "Artikel");
                        serializer.startTag("", "Code");
                        Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(sp.getPesticide().getId());
                        serializer.text(pest.getCode());
                        serializer.endTag("", "Code");
                        serializer.endTag("", "Artikel");
                        serializer.startTag("", "Menge");
                        serializer.text(sp.getDose_amount().toString());
                        serializer.endTag("", "Menge");
                        serializer.startTag("", "MengeProHl1Mal");
                        serializer.text(sp.getDose().toString());
                        serializer.endTag("", "MengeProHl1Mal");
                        serializer.endTag("", "Spritzmittel");
                    }
                    List<SprayFertilizer> sprayFert = DatabaseManager.getInstance().getSprayFertilizerBySprayId(s.getId());
                    for (SprayFertilizer sf : sprayFert) {
                        serializer.startTag("", "Blattduenger");
                        serializer.startTag("", "Artikel");
                        serializer.startTag("", "Code");
                        Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(sf.getFertilizer().getId());
                        serializer.text(fert.getCode());
                        serializer.endTag("", "Code");
                        serializer.endTag("", "Artikel");
                        serializer.startTag("", "Menge");
                        serializer.text(sf.getDose_amount().toString());
                        serializer.endTag("", "Menge");
                        serializer.endTag("", "Blattduenger");
                    }
                    if (wk.getTask().getType().equalsIgnoreCase("H")) {
                        serializer.endTag("", "Herbizideinsatz");
                    } else {
                        serializer.endTag("", "Spritzung");
                    }
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
                String note;
                if (mofaNote) {
                    note = ASANOTE + " " + wk.getNote();
                } else {
                    note = wk.getNote();
                }
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


                List<WorkFertilizer> soilfertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(wk.getId());
                for (WorkFertilizer wf : soilfertilizers) {
                    serializer.startTag("", "Duengung");
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
                    serializer.startTag("", "Duengemittel");
                    serializer.startTag("", "Artikel");
                    serializer.startTag("", "Code");
                    SoilFertilizer sf = DatabaseManager.getInstance().getSoilFertilizerWithId(wf.getSoilFertilizer().getId());
                    serializer.text(sf.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Artikel");
                    serializer.startTag("", "Menge");
                    serializer.text(wf.getAmount().toString());
                    serializer.endTag("", "Menge");
                    serializer.endTag("", "Duengemittel");

                    serializer.endTag("", "Duengung");
                }
                List<Spraying> spraying = DatabaseManager.getInstance().getSprayingByWorkId(wk.getId());
                for (Spraying s : spraying) {
                    if (wk.getTask().getType().equalsIgnoreCase("H")) { //herbicide
                        serializer.startTag("", "Herbizideinsatz");
                    } else {
                        serializer.startTag("", "Spritzung");
                    }

                    serializer.startTag("", "Konzentration");
                    serializer.text(s.getConcentration().toString());
                    serializer.endTag("", "Konzentration");
                    serializer.startTag("", "Wassermenge");
                    serializer.text(s.getWateramount().toString());
                    serializer.endTag("", "Wassermenge");
                    serializer.startTag("", "Notiz");
                    serializer.endTag("", "Notiz");
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
                    List<SprayFertilizer> sprayFert = DatabaseManager.getInstance().getSprayFertilizerBySprayId(s.getId());
                    for (SprayFertilizer sf : sprayFert) {
                        serializer.startTag("", "Blattduenger");
                        serializer.startTag("", "Artikel");
                        serializer.startTag("", "Code");
                        Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(sf.getFertilizer().getId());
                        serializer.text(fert.getCode());
                        serializer.endTag("", "Code");
                        serializer.endTag("", "Artikel");
                        serializer.startTag("", "Menge");
                        serializer.text(sf.getDose_amount().toString());
                        serializer.endTag("", "Menge");
                        serializer.endTag("", "Blattduenger");
                    }
                    if (wk.getTask().getType().equalsIgnoreCase("H")) {
                        serializer.endTag("", "Herbizideinsatz");
                    } else {
                        serializer.endTag("", "Spritzung");
                    }
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

    private String createPurchaseXML() {
        List<Purchase> purchaseUploadList = DatabaseManager.getInstance().getAllPurchases();
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "purchases");
            serializer.attribute("", "number", String.valueOf(purchaseUploadList.size()));
            for (Purchase p : purchaseUploadList) {
                serializer.startTag("", "purchase");
                serializer.startTag("", "date");
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd'T'hh:mm:sss'Z'");
                serializer.text(sdf.format(p.getDate()));
                serializer.endTag("", "date");

                List<PurchasePesticide> purPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseId(p.getId());
                for (PurchasePesticide pp : purPest) {
                    serializer.startTag("", "pesticide");
                    serializer.startTag("", "pestid");
                    serializer.text(pp.getProduct().getId().toString());
                    serializer.endTag("", "pestid");
                    serializer.startTag("", "amount");
                    serializer.text(pp.getAmount().toString());
                    serializer.endTag("", "amount");
                    serializer.endTag("", "pesticide");
                }
                List<PurchaseFertilizer> purFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseId(p.getId());
                for (PurchaseFertilizer pf : purFert) {
                    serializer.startTag("", "fertilizer");
                    serializer.startTag("", "fertid");
                    serializer.text(pf.getProduct().getId().toString());
                    serializer.endTag("", "fertid");
                    serializer.startTag("", "amount");
                    serializer.text(pf.getAmount().toString());
                    serializer.endTag("", "amount");
                    serializer.endTag("", "fertilizer");
                }
                serializer.endTag("", "purchase");
            }
            serializer.endTag("", "purchases");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createPurchaseXMLASA() {
        List<Purchase> purchaseUploadList = DatabaseManager.getInstance().getAllPurchases();
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Lagereingaenge");
            //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
            for (Purchase p : purchaseUploadList) {
                serializer.startTag("", "Lagereingang");
                serializer.startTag("", "Datum");
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd");
                serializer.text(sdf.format(p.getDate()));
                serializer.endTag("", "Datum");
                serializer.startTag("", "Bruttobetraege");
                if (grossPrice) {
                    serializer.text("1");
                } else {
                    serializer.text("0");
                }

                serializer.endTag("", "Bruttobetraege");
                List<PurchasePesticide> purPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseId(p.getId());
                for (PurchasePesticide pp : purPest) {
                    double price = getPriceFromJson(pp.getData());
                    serializer.startTag("", "Lagereingangsposition");
                    serializer.startTag("", "Spritzmittel");
                    serializer.startTag("", "Code");
                    Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(pp.getProduct().getId());
                    serializer.text(pest.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Spritzmittel");
                    serializer.startTag("", "Menge");
                    serializer.text(pp.getAmount().toString());
                    serializer.endTag("", "Menge");
                    if (price != 0.00) {
                        serializer.startTag("", "Preis");
                        serializer.text(Double.toString(price));
                        serializer.endTag("", "Preis");
                    }
                    serializer.endTag("", "Lagereingangsposition");
                }
                List<PurchaseFertilizer> purFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseId(p.getId());
                for (PurchaseFertilizer pf : purFert) {
                    double price = getPriceFromJson(pf.getData());
                    serializer.startTag("", "Lagereingangsposition");
                    serializer.startTag("", "Duengemittel");
                    serializer.startTag("", "Code");
                    Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(pf.getProduct().getId());
                    serializer.text(fert.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Duengemittel");
                    serializer.startTag("", "Menge");
                    serializer.text(pf.getAmount().toString());
                    serializer.endTag("", "Menge");
                    if (price != 0.00) {
                        serializer.startTag("", "Preis");
                        serializer.text(Double.toString(price));
                        serializer.endTag("", "Preis");
                    }
                    serializer.endTag("", "Lagereingangsposition");
                }
                serializer.endTag("", "Lagereingang");
            }
            serializer.endTag("", "Lagereingaenge");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createPurchaseXMLASAVer16() {
        List<Purchase> purchaseUploadList = DatabaseManager.getInstance().getAllPurchases();

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Lagereingaenge");
            //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
            for (Purchase p : purchaseUploadList) {
                serializer.startTag("", "Lagereingang");
                serializer.startTag("", "Datum");
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd");
                serializer.text(sdf.format(p.getDate()));
                serializer.endTag("", "Datum");
                serializer.startTag("", "Bruttobetraege");
                if (grossPrice) {
                    serializer.text("1");
                } else {
                    serializer.text("0");
                }

                serializer.endTag("", "Bruttobetraege");
                List<PurchasePesticide> purPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseId(p.getId());
                for (PurchasePesticide pp : purPest) {
                    double price = getPriceFromJson(pp.getData());
                    serializer.startTag("", "Lagereingangsposition");
                    serializer.startTag("", "Pflanzenschutzmittel");
                    serializer.startTag("", "Code");
                    Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(pp.getProduct().getId());
                    serializer.text(pest.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Pflanzenschutzmittel");
                    serializer.startTag("", "Menge");
                    serializer.text(pp.getAmount().toString());
                    serializer.endTag("", "Menge");
                    if (price != 0.00) {
                        serializer.startTag("", "Preis");
                        serializer.text(Double.toString(price));
                        serializer.endTag("", "Preis");
                    }
                    serializer.endTag("", "Lagereingangsposition");
                }
                List<PurchaseFertilizer> purFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseId(p.getId());
                for (PurchaseFertilizer pf : purFert) {
                    double price = getPriceFromJson(pf.getData());
                    serializer.startTag("", "Lagereingangsposition");
                    serializer.startTag("", "Duengemittel");
                    serializer.startTag("", "Code");
                    Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(pf.getProduct().getId());
                    serializer.text(fert.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Duengemittel");
                    serializer.startTag("", "Menge");
                    serializer.text(pf.getAmount().toString());
                    serializer.endTag("", "Menge");
                    if (price != 0.00) {
                        serializer.startTag("", "Preis");
                        serializer.text(Double.toString(price));
                        serializer.endTag("", "Preis");
                    }
                    serializer.endTag("", "Lagereingangsposition");
                }
                serializer.endTag("", "Lagereingang");
            }
            serializer.endTag("", "Lagereingaenge");
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
        String filePath = null;
        ACCESS_TOKEN = DropboxClient.retrieveAccessToken(context);
        if (callingActivity == ActivityConstants.WORK_OVERVIEW) {
            filePath = Globals.EXPORT + "/worklist" + dateFormat.format(date) + ".xml";
        }
        if (callingActivity == ActivityConstants.PURCHASING_ACTIVITY) {
            filePath = Globals.EXPORT + "/purchaselist" + dateFormat.format(date) + ".xml";
        }
        if (callingActivity == ActivityConstants.VEGDATA_ACTIVITY) { // calling this asynch from vegdata
            filePath = Globals.EXPORT + "/vegdata" + dateFormat.format(date) + ".xml";
        }


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

    private String getEinsatzGrundForASA(String einsatzgrund) {
        String[] splittedText = einsatzgrund.split(",");
        return splittedText[0];
    }

    private double getPriceFromJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            double price = object.getDouble("price");
            return price;
        } catch (JSONException e) {
            return 0.00;
        }
    }
}
