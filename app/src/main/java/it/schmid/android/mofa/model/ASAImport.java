package it.schmid.android.mofa.model;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;

public class ASAImport implements Entity.Visitor<Boolean, String> {

    private final DatabaseManager storage;
    private final NotificationService notification;

    private Boolean importError;

    public ASAImport(DatabaseManager storage, NotificationService notification) {
        this.storage = storage;
        this.notification = notification;
        this.importError = false;
    }

    @Override
    public final Boolean visit(Global entity, String data) {
        return null;
    }

    @Override
    public final Boolean visit(Land entity, String data) {
        List<Land> importData;
        MofaApplication app = MofaApplication.getInstance();
        //default
        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        importData = landXmlParserASA(data, notification);
        for (Land l : importData) {
            Land land = storage.getLandWithId(l.getId());
            if (land != null) {

                land.setName(l.getName());
                storage.updateLand(land);
            } else {

                Land newLand = new Land();
                newLand.setId(l.getId());
                newLand.setName(l.getName());
                storage.addLand(l);
            }
        }
        return importError;
    }

    @Override
    public final Boolean visit(Machine entity, String data) {

        List<Machine> importData;
        MofaApplication app = MofaApplication.getInstance();
        //default
        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        importData = machineXmlParserASA(data, notification);

        for (Machine m : importData) {
            Machine machine = storage.getMachineWithId(m.getId());
            if (machine != null) {

                machine.setName(m.getName());
                storage.updateMachine(machine);
            } else {

                Machine newMachine = new Machine();
                newMachine.setId(m.getId());
                newMachine.setName(m.getName());
                storage.addMachine(m);
            }
        }
        return importError;
    }

    @Override
    public final Boolean visit(Task entity, String data) {
        List<Task> importData;
        MofaApplication app = MofaApplication.getInstance();
        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        importData = taskXmlParserASA(data, notification);


        for (Task t : importData) {
            Task task = storage.getTaskWithId(t.getId());
            if (task != null) {
                task.setTask(t.getTask());
                task.setType(t.getType());
                storage.updateTask(task);
            } else {

                Task newTask = new Task();
                newTask.setId(t.getId());
                newTask.setTask(t.getTask());
                newTask.setType(t.getType());
                storage.addTask(t);
            }
        }
        return importError;
    }

    @Override
    public final Boolean visit(VQuarter entity, String data) {

        VQuarter vquarter;
        List<VQuarter> importData;
        MofaApplication app = MofaApplication.getInstance();
        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        importData = vquarterXmlParserASA(data, notification);


        for (VQuarter vq : importData) {
            vquarter = storage.getVQuarterWithId(vq.getId());
            if (vquarter != null) {
                vquarter.setVariety(vq.getVariety());
                vquarter.setClone(vq.getClone());
                vquarter.setPlantYear(vq.getPlantYear());
                vquarter.setWateramount(vq.getWateramount());
                vquarter.setLand(vq.getLand());
                vquarter.setCode(vq.getCode());
                vquarter.setSize(vq.getSize());
                storage.updateVQuarter(vquarter);
            } else {
                VQuarter v = new VQuarter();
                v.setId(vq.getId());
                v.setVariety(vq.getVariety());
                v.setClone(vq.getClone());
                v.setPlantYear(vq.getPlantYear());
                v.setWateramount(vq.getWateramount());
                v.setLand(vq.getLand());
                v.setCode(vq.getCode());
                v.setSize(vq.getSize());
                storage.addVquarter(v);
            }
        }
        return importError;
    }

    @Override
    public final Boolean visit(Worker entity, String data) {

        List<Worker> importData;
        MofaApplication app = MofaApplication.getInstance();
        //default
        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        importData = workerXmlParserASA(data, notification);

        for (Worker w : importData) {
            Worker worker = storage.getWorkerWithId(w.getId());
            if (worker != null) {

                worker.setFirstName(w.getFirstName());
                worker.setLastName(w.getLastname());
                storage.updateWorker(worker);
            } else {
                Worker newWorker = new Worker();
                newWorker.setId(w.getId());
                newWorker.setFirstName(w.getFirstName());
                newWorker.setLastName(w.getLastname());
                storage.addWorker(w);
            }
        }

        return importError;
    }

    @Override
    public final Boolean visit(Work entity, String data) {
        return null;
    }

    @Override
    public final Boolean visit(WorkMachine entity, String data) {
        return null;
    }

    @Override
    public final Boolean visit(WorkVQuarter entity, String data) {
        return null;
    }

    @Override
    public final Boolean visit(WorkWorker entity, String data) {
        return null;
    }


    private List<Land> landXmlParserASA(String inputData, NotificationService notification) {
        List<Land> mLandList = null;
        Integer xId = 1;
        String xCode = "";
        String xLandName = "";
        Boolean firstDesc = true;
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            Land currentLand = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mLandList = new ArrayList<Land>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Anlage")) {
                            currentLand = new Land();

                        } else if (currentLand != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentLand.setId(xId);
                            }
                            if (name.equalsIgnoreCase("code")) {
                                xCode = xpp.nextText();
                                currentLand.setCode(xCode);
                                //currentLand.setId(xId);
                                // xId++;
                            }
                            if (name.equalsIgnoreCase("Name") && (firstDesc)) {
                                xLandName = xpp.nextText(); //in ASA the land name is stored as attribute
                                currentLand.setName(xLandName);
                                firstDesc = false;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Anlage") && currentLand != null) {
                            Log.d(Land.TAG, "[XMLParserLand] adding land: " + currentLand.getId() + " " + currentLand.getName());
                            mLandList.add(currentLand);
                            firstDesc = true;
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Land";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mLandList);
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
                            Log.d(Machine.TAG, "[XMLParserMachine] adding machine: " + currentMachine.getId() + " " + currentMachine.getName());
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

    private List<Task> taskXmlParserASA(String inputData, NotificationService notification) {
        List<Task> mTaskList = null;
        Integer xId;
        String xArt = null;
        String xCode = "";
        String xTaskName = null;
        Boolean firstCode = false; //due the fact that ASA uses the Code Tag in different nodes
        Boolean firstDesc = true;
        Log.d(Task.TAG, "[XMLParserTaskASA] getting data: " + inputData);
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));

            int eventType = xpp.getEventType();
            Task currentTask = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mTaskList = new ArrayList<Task>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Arbeit")) {
                            currentTask = new Task();

                        } else if (currentTask != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentTask.setId(xId);
                            }
                            if (name.equalsIgnoreCase("Art")) {
                                xArt = xpp.nextText();
                                currentTask.setType(xArt);
                            }
                            if ((name.equalsIgnoreCase("Code")) && (!firstCode)) {
                                xCode = xpp.nextText();
                                currentTask.setCode(xCode); //in ASA Code is the primary key
                                // currentTask.setId(xId);    //setting the primary key
                                //  xId++;
                                firstCode = true;
                            }
                            if (name.equalsIgnoreCase("Name") && (firstDesc)) {
                                xTaskName = xpp.nextText();
                                currentTask.setTask(xTaskName);
                                firstDesc = false;

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Arbeit") && currentTask != null) {
                            Log.d(Task.TAG, "[XMLParserTaskASA] adding task: " + currentTask.getId() + " " + currentTask.getTask());
                            mTaskList.add(currentTask);
                            firstCode = false;
                            firstDesc = true; //there are more than one task description in the multilanguage environment, we need only the first one
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Task";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //   e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mTaskList);
    }

    //ASA case
    private List<VQuarter> vquarterXmlParserASA(String inputData, NotificationService notification) {
        List<VQuarter> mVquarterList = null;
        Integer xId = 1;
        String xLandCode = "";
        Integer xLandId = null;
        String xVariety = null;
        String xClone = null;
        Integer xYear = null;
        Double xSize = null;
        Double xWater = null;
        String xCode = "";
        Boolean firstCode = true;
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            VQuarter currentVquarter = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mVquarterList = new ArrayList<VQuarter>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Sortenquartier")) {
                            currentVquarter = new VQuarter();

                        } else if (currentVquarter != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentVquarter.setId(xId);
                            }
                            if (name.equalsIgnoreCase("code") && (firstCode)) {
                                xCode = xpp.nextText();
                                currentVquarter.setCode(xCode);
                                //currentVquarter.setId(xId);
                                //xId++;
                                firstCode = false;
                            }
                            if (name.equalsIgnoreCase("Anlage")) {

                                xpp.nextTag();

                                if (xpp.getName().equalsIgnoreCase("ID")) {
                                    xLandId = Integer.parseInt(xpp.nextText());
                                    Land land;
                                    try {
                                        land = storage.getLandWithId(xLandId);
                                        currentVquarter.setLand(land);
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                }


                            }

                            if (name.equalsIgnoreCase("Name")) {

                                xVariety = modifyString(xpp.nextText());
                                Log.d(VQuarter.TAG, "[XMLParserVQuarter] variety: " + xVariety);
                                currentVquarter.setVariety(xVariety);

                            }
                            if (name.equalsIgnoreCase("GueltigSeitEJ")) {
                                xYear = Integer.parseInt(xpp.nextText());
                                Log.d(VQuarter.TAG, "[XMLParserVQuarter] year: " + xYear);
                                currentVquarter.setPlantYear(xYear);

                            }
                            if (name.equalsIgnoreCase("Spritzvorgabe")) {
                                java.text.NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
                                String value = xpp.nextText();
                                try {
                                    if (!(value.equalsIgnoreCase(""))) {
                                        xWater = nf.parse(value).doubleValue();
                                        xWater = Double.parseDouble(value);
                                        currentVquarter.setWateramount((xWater));
                                    }
                                } catch (Exception ex) {
                                    importError = true;
                                    //ex.printStackTrace();
                                }


                            }
                            if (name.equalsIgnoreCase("Nettoflaeche")) {
                                xSize = Double.parseDouble(xpp.nextText());
                                Log.d(VQuarter.TAG, "[XMLParserVQuarter] size: " + xSize);
                                currentVquarter.setSize(xSize);

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Sortenquartier") && currentVquarter != null) {
                            Log.d(VQuarter.TAG, "[XMLParserLand] adding vquarter: " + currentVquarter.getId() + " " + currentVquarter.getVariety() + " - " + currentVquarter.getCode());
                            mVquarterList.add(currentVquarter);
                            firstCode = true;
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "VQuarter";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            e.printStackTrace();
        }
        return (mVquarterList);
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

                            Log.d(Worker.TAG, "[XMLParserWorker] adding worker: " + currentWorker.getId() + " " + currentWorker.getLastname());
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

    private String modifyString(String varietyName) {
        final int MAX = 13; //Cutting point of the String
        String[] parts = varietyName.split(" ", 2);
        String variety = parts[1];
        if (variety.length() > MAX) {
            variety = variety.substring(0, MAX) + ".";
        }
        return variety;
    }
}
