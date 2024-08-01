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
public class Machine extends ImportBehavior {
    private static final String TAG = "MachineClass";
    @DatabaseField(id = true)
    @Expose
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String code;

    private Boolean importError = false;

    public Machine() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void importMasterData(JSONArray importData) {
        Machine machine;
        try {
            Log.i(TAG,
                    "Number of entries " + importData.length());
            for (int i = 0; i < importData.length(); i++) {
                JSONObject jsonObject = importData.getJSONObject(i);
                Log.i(TAG, jsonObject.getString("name") + "," + jsonObject.getInt("id"));
                machine = DatabaseManager.getInstance().getMachineWithId(jsonObject.getInt("id"));
                if (null != machine) {
                    machine.setName(jsonObject.getString("name"));
                    DatabaseManager.getInstance().updateMachine(machine);
                } else {
                    Machine m = new Machine();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("name"));
                    DatabaseManager.getInstance().addMachine(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.getName();
    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        List<Machine> importData;
        MofaApplication app = MofaApplication.getInstance();
        //default
            Log.d("TAG", "BackendSoftware: ASAAGRAR");
            importData = machineXmlParserASA(xmlString, notification);

        for (Machine m : importData) {
            Machine machine = DatabaseManager.getInstance().getMachineWithId(m.getId());
            if (machine != null) {

                machine.setName(m.getName());
                DatabaseManager.getInstance().updateMachine(machine);
            } else {

                Machine newMachine = new Machine();
                newMachine.setId(m.getId());
                newMachine.setName(m.getName());
                DatabaseManager.getInstance().addMachine(m);
            }
        }
        return importError;


    }

    private List<Machine> machineXmlParser(String inputData, NotificationService notification) {
        List<Machine> mMachineList = null;
        Integer xId = null;
        String xMachineName = "";

        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            Machine currentMachine = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mMachineList = new ArrayList<Machine>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("machine")) {
                            currentMachine = new Machine();
                            // currentMachine.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentMachine != null) {
                            if (name.equalsIgnoreCase("id") && !(xpp.isEmptyElementTag())) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentMachine.setId(xId);

                            }
                            if (name.equalsIgnoreCase("name")) {
                                xMachineName = xpp.nextText();
                                currentMachine.setName(xMachineName);

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("machine") && currentMachine != null) {
                            Log.d(TAG, "[XMLParserMachine] adding machine: " + currentMachine.getId() + " " + currentMachine.getName());
                            mMachineList.add(currentMachine);
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Machine";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            // e.printStackTrace();
        }
        return (mMachineList);
    }

    private List<Machine> machineXmlParserASA(String inputData, NotificationService notification) {
        List<Machine> mMachineList = null;
        Integer xId = 1;
        String xCode = "";
        String xMachineName = "";
        Boolean firstCode = true;//due the fact that ASA uses the Code Tag in different nodes
        Boolean firstDesc = true;
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            Machine currentMachine = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mMachineList = new ArrayList<Machine>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Maschine")) {
                            currentMachine = new Machine();
                            // currentMachine.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentMachine != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentMachine.setId(xId);
                            }
                            if (name.equalsIgnoreCase("code") && (firstCode)) {
                                xCode = xpp.nextText();
                                currentMachine.setCode(xCode); //in ASA Code is the primary key
                                // currentMachine.setId(xId);    //setting the primary key
                                // xId++;
                                firstCode = false;
                            }
                            if (name.equalsIgnoreCase("Name") && (firstDesc)) {
                                xMachineName = xpp.nextText(); //in ASA the machine name is stored as attribute
                                currentMachine.setName(xMachineName);
                                firstDesc = false;
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Maschine") && currentMachine != null) {
                            Log.d(TAG, "[XMLParserMachine] adding machine: " + currentMachine.getId() + " " + currentMachine.getName());
                            mMachineList.add(currentMachine);
                            firstCode = true;
                            firstDesc = true;
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Machine";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            // e.printStackTrace();
        }
        return (mMachineList);
    }
}
