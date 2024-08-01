package it.schmid.android.mofa.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;

@DatabaseTable
public class Worker extends ImportBehavior {
    private static final String TAG = "WorkerClass";
    @DatabaseField(id = true)
    @Expose
    private Integer id;
    @DatabaseField
    private String lastname;
    @DatabaseField
    private String firstname;
    @DatabaseField
    private String code;

    private Boolean importError = false;

    public Worker() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void importMasterData(JSONArray importData) {
        Worker worker;
        try {
            Log.i(TAG,
                    "Number of entries " + importData.length());
            for (int i = 0; i < importData.length(); i++) {
                JSONObject jsonObject = importData.getJSONObject(i);
                Log.i(TAG, jsonObject.getString("firstname") + "," + jsonObject.getString("lastname") + "," + jsonObject.getInt("id"));
                worker = DatabaseManager.getInstance().getWorkerWithId(jsonObject.getInt("id"));
                if (null != worker) {
                    worker.setLastName(jsonObject.getString("lastname"));
                    worker.setFirstName(jsonObject.getString("firstname"));
                    DatabaseManager.getInstance().updateWorker(worker);
                } else {
                    Worker w = new Worker();
                    w.setId(jsonObject.getInt("id"));
                    w.setFirstName(jsonObject.getString("firstname"));
                    w.setLastName(jsonObject.getString("lastname"));
                    DatabaseManager.getInstance().addWorker(w);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.lastname + " " + this.firstname;
    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        List<Worker> importData;
        MofaApplication app = MofaApplication.getInstance();
        //default
            Log.d("TAG", "BackendSoftware: ASAAGRAR");
            importData = workerXmlParserASA(xmlString, notification);

        for (Worker w : importData) {
            Worker worker = DatabaseManager.getInstance().getWorkerWithId(w.getId());
            if (worker != null) {

                worker.setFirstName(w.getFirstName());
                worker.setLastName(w.getLastname());
                DatabaseManager.getInstance().updateWorker(worker);
            } else {
                Worker newWorker = new Worker();
                newWorker.setId(w.getId());
                newWorker.setFirstName(w.getFirstName());
                newWorker.setLastName(w.getLastname());
                DatabaseManager.getInstance().addWorker(w);
            }
        }

        return importError;

    }

    private List<Worker> workerXmlParser(String inputData, NotificationService notification) {
        List<Worker> mWorkerList = null;
        Integer xId = null;
        String xFirstName = "";
        String xLastName = "";
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));

            int eventType = xpp.getEventType();
            Worker currentWorker = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mWorkerList = new ArrayList<Worker>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("worker")) {
                            currentWorker = new Worker();
                            //  currentWorker.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentWorker != null) {
                            if (name.equalsIgnoreCase("id")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentWorker.setId(xId);

                            }
                            if (name.equalsIgnoreCase("firstname")) {
                                xFirstName = xpp.nextText();
                                currentWorker.setFirstName(xFirstName);

                            }
                            if (name.equalsIgnoreCase("lastname")) {
                                xLastName = xpp.nextText();
                                currentWorker.setLastName(xLastName);

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("worker") && currentWorker != null) {
                            Log.d(TAG, "[XMLParserWorker] adding worker: " + currentWorker.getId() + " " + currentWorker.getLastname());
                            mWorkerList.add(currentWorker);
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Worker";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            // e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mWorkerList);
    }

    /**
     * Only for ASAAgrar
     *
     * @param inputData XML-File
     * @return
     */
    private List<Worker> workerXmlParserASA(String inputData, NotificationService notification) {
        List<Worker> mWorkerList = null;
        Integer xId = 1;
        String xCode = "";
        String xFirstName = "";
        String xLastName = "";
        Boolean firstCode = true;
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));

            int eventType = xpp.getEventType();
            Worker currentWorker = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mWorkerList = new ArrayList<Worker>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Arbeitskraft")) {
                            currentWorker = new Worker();
                            //  currentWorker.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentWorker != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentWorker.setId(xId);
                            }
                            if (name.equalsIgnoreCase("Code") && (firstCode)) {
                                xCode = xpp.nextText();

                                currentWorker.setCode(xCode); //in ASA Code is the primary key
                                // currentWorker.setId(xId);    //setting the primary key
                                // xId++;
                                firstCode = false;
                            }
                            if (name.equalsIgnoreCase("Name2")) {
                                xFirstName = xpp.nextText();
                                currentWorker.setFirstName(xFirstName);

                            }
                            if (name.equalsIgnoreCase("Name1")) {
                                xLastName = xpp.nextText();
                                currentWorker.setLastName(xLastName);

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Arbeitskraft") && currentWorker != null) {

                            Log.d(TAG, "[XMLParserWorker] adding worker: " + currentWorker.getId() + " " + currentWorker.getLastname());
                            mWorkerList.add(currentWorker);
                            firstCode = true;
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Worker";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            // e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mWorkerList);
    }
}
