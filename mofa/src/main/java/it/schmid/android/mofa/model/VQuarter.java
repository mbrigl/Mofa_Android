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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;

@DatabaseTable
public class VQuarter extends ImportBehavior {
    private static final String TAG = "VQuarterClass";
    @DatabaseField(id = true)
    @Expose
    private Integer id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Land land;
    @DatabaseField
    private String variety;
    @DatabaseField
    private String clone;
    @DatabaseField
    private Integer plantYear;
    @DatabaseField
    private Double wateramount;
    @DatabaseField
    private Double size;
    @DatabaseField
    private String code;
    @DatabaseField
    private String data;
    @DatabaseField
    private Double gps_x1;
    @DatabaseField
    private Double gps_x2;
    @DatabaseField
    private Double gps_y1;
    @DatabaseField
    private Double gps_y2;
    private Boolean importError = false;

    public VQuarter() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Land getLand() {
        return land;
    }

    public void setLand(Land land) {
        this.land = land;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getClone() {
        return clone;
    }

    public void setClone(String clone) {
        this.clone = clone;
    }

    public Integer getPlantYear() {
        return plantYear;
    }

    public void setPlantYear(Integer plantYear) {
        this.plantYear = plantYear;
    }

    public Double getWateramount() {
        return wateramount;
    }

    public void setWateramount(Double wateramount) {
        this.wateramount = wateramount;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getGps_x1() {
        return gps_x1;
    }

    public void setGps_x1(Double gps_x1) {
        this.gps_x1 = gps_x1;
    }

    public Double getGps_x2() {
        return gps_x2;
    }

    public void setGps_x2(Double gps_x2) {
        this.gps_x2 = gps_x2;
    }

    public Double getGps_y1() {
        return gps_y1;
    }

    public void setGps_y1(Double gps_y1) {
        this.gps_y1 = gps_y1;
    }

    public Double getGps_y2() {
        return gps_y2;
    }

    public void setGps_y2(Double gps_y2) {
        this.gps_y2 = gps_y2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void importMasterData(JSONArray importData) {
        VQuarter vquarter;
        Land land;
        try {
            Log.i(TAG,
                    "Number of entries " + importData.length());
            for (int i = 0; i < importData.length(); i++) {
                JSONObject jsonObject = importData.getJSONObject(i);
                Log.i(TAG, jsonObject.getString("variety") + "," + jsonObject.getInt("id") + "," + jsonObject.getInt("year") +
                        "," + jsonObject.getString("clone") + jsonObject.getDouble("wateramount"));
                vquarter = DatabaseManager.getInstance().getVQuarterWithId(jsonObject.getInt("id"));
                //getting the land of the current vQuarter
                JSONObject jLand = jsonObject.getJSONObject("land");
                land = DatabaseManager.getInstance().getLandWithId(jLand.getInt("id"));
                if (null != vquarter) {
                    vquarter.setVariety(jsonObject.getString("variety"));
                    vquarter.setClone(jsonObject.getString("clone"));
                    vquarter.setPlantYear(jsonObject.getInt("year"));
                    vquarter.setWateramount(jsonObject.getDouble("wateramount"));
                    vquarter.setLand(land);
                    DatabaseManager.getInstance().updateVQuarter(vquarter);
                } else {
                    VQuarter v = new VQuarter();
                    v.setId(jsonObject.getInt("id"));
                    v.setVariety(jsonObject.getString("variety"));
                    v.setClone(jsonObject.getString("clone"));
                    v.setPlantYear(jsonObject.getInt("year"));
                    v.setWateramount(jsonObject.getDouble("wateramount"));
                    v.setLand(land);
                    DatabaseManager.getInstance().addVquarter(v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        String backEndSoftware;
        VQuarter vquarter;
        List<VQuarter> importData;
        MofaApplication app = MofaApplication.getInstance();
        backEndSoftware = app.getBackendSoftware();
        //default
        if (Integer.parseInt(backEndSoftware) == 1) { //ASA
            Log.d("TAG", "BackendSoftware: ASAAGRAR");
            importData = vquarterXmlParserASA(xmlString, notification);
        } else {
            Log.d("TAG", "BackendSoftware:Default");
            importData = vquarterXmlParser(xmlString, notification);
        }


        for (VQuarter vq : importData) {
            vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getId());
            if (vquarter != null) {
                vquarter.setVariety(vq.getVariety());
                vquarter.setClone(vq.getClone());
                vquarter.setPlantYear(vq.getPlantYear());
                vquarter.setWateramount(vq.getWateramount());
                vquarter.setLand(vq.getLand());
                vquarter.setCode(vq.getCode());
                vquarter.setSize(vq.getSize());
                DatabaseManager.getInstance().updateVQuarter(vquarter);
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
                DatabaseManager.getInstance().addVquarter(v);
            }
        }
        return importError;

    }

    //default case
    private List<VQuarter> vquarterXmlParser(String inputData, NotificationService notification) {
        List<VQuarter> mVquarterList = null;
        Integer xId = null;
        Integer xLandId = null;
        String xVariety = null;
        String xClone = null;
        Double xSize = null;
        Integer xYear = null;
        Double xWater = null;
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
                        if (name.equalsIgnoreCase("vquarter")) {
                            currentVquarter = new VQuarter();

                        } else if (currentVquarter != null) {
                            if (name.equalsIgnoreCase("id") && !(xpp.isEmptyElementTag())) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentVquarter.setId(xId);

                            }
                            if (name.equalsIgnoreCase("land")) {

                                xpp.nextTag();

                                if (xpp.getName().equalsIgnoreCase("id")) {
                                    xLandId = Integer.parseInt(xpp.nextText());
                                    //	Log.d(TAG, "[xmlvquarterParser] Land id " + xLandId + " of vquarter number: " + xId);
                                    Land land = DatabaseManager.getInstance().getLandWithId(xLandId);
                                    currentVquarter.setLand(land);
                                }


                            }
                            if (name.equalsIgnoreCase("clone")) {
                                xClone = xpp.nextText();
                                Log.d(TAG, "[XMLParserVQuarter] clone: " + xClone);
                                currentVquarter.setClone(xClone);

                            }
                            if (name.equalsIgnoreCase("variety")) {
                                xVariety = xpp.nextText();
                                Log.d(TAG, "[XMLParserVQuarter] variety: " + xVariety);
                                currentVquarter.setVariety(xVariety);

                            }
                            if (name.equalsIgnoreCase("year")) {
                                xYear = Integer.parseInt(xpp.nextText());
                                Log.d(TAG, "[XMLParserVQuarter] year: " + xYear);
                                currentVquarter.setPlantYear(xYear);

                            }
                            if (name.equalsIgnoreCase("size")) {
                                xSize = Double.parseDouble(xpp.nextText());
                                Log.d(TAG, "[XMLParserVQuarter] size: " + xSize);
                                currentVquarter.setSize(xSize);

                            }
                            if (name.equalsIgnoreCase("wateramount")) {
                                java.text.NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                                String value = xpp.nextText();
                                try {
                                    if (!(value.equalsIgnoreCase(""))) {
                                        xWater = nf.parse(value).doubleValue();
                                        currentVquarter.setWateramount((xWater));
                                    }
                                } catch (Exception ex) {
                                    importError = true;
                                    //ex.printStackTrace();
                                }


                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("vquarter") && currentVquarter != null) {
                            Log.d(TAG, "[XMLParserLand] adding vquarter: " + currentVquarter.getId() + " " + currentVquarter.getVariety() + " - " + currentVquarter.getWateramount());
                            if (xWater == null) {// we have to set it to 0.00, because in xml there is not wateramount
                                currentVquarter.setWateramount(0.00);
                            }
                            mVquarterList.add(currentVquarter);
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

    private List<VQuarter> vquarterXmlParserBigApple(String inputData, NotificationService notification) {
        List<VQuarter> mVquarterList = null;
        Integer xId = null;
        Integer xLandId = null;
        String xVariety = null;
        String xClone = null;
        Integer xYear = null;
        Double xWater = null;
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
                        if (name.equalsIgnoreCase("vquarter")) {
                            currentVquarter = new VQuarter();

                        } else if (currentVquarter != null) {
                            if (name.equalsIgnoreCase("id") && !(xpp.isEmptyElementTag())) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentVquarter.setId(xId);

                            }
                            if (name.equalsIgnoreCase("land")) {
                                xLandId = Integer.parseInt(xpp.nextText());
                                //	Log.d(TAG, "[xmlvquarterParser] Land id " + xLandId + " of vquarter number: " + xId);
                                Land land = DatabaseManager.getInstance().getLandWithId(xLandId);
                                currentVquarter.setLand(land);

                            }
                            if (name.equalsIgnoreCase("clone")) {
                                xClone = xpp.nextText();
                                Log.d(TAG, "[XMLParserVQuarter] clone: " + xClone);
                                currentVquarter.setClone(xClone);

                            }
                            if (name.equalsIgnoreCase("variety")) {
                                xVariety = xpp.nextText();
                                Log.d(TAG, "[XMLParserVQuarter] variety: " + xVariety);
                                currentVquarter.setVariety(xVariety);

                            }
                            if (name.equalsIgnoreCase("year")) {
                                xYear = Integer.parseInt(xpp.nextText());
                                Log.d(TAG, "[XMLParserVQuarter] year: " + xYear);
                                currentVquarter.setPlantYear(xYear);

                            }
                            if (name.equalsIgnoreCase("wateramount")) {
                                java.text.NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                                String value = xpp.nextText();
                                try {
                                    if (!(value.equalsIgnoreCase(""))) {
                                        xWater = nf.parse(value).doubleValue();
                                        currentVquarter.setWateramount((xWater));
                                    }
                                } catch (Exception ex) {
                                    importError = true;
                                    //ex.printStackTrace();
                                }

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("vquarter") && currentVquarter != null) {
                            if (xWater == null) {// we have to set it to 0.00
                                currentVquarter.setWateramount(0.00);
                            }
                            Log.d(TAG, "[XMLParserLand] adding vquarter: " + currentVquarter.getId() + " " + currentVquarter.getVariety());
                            mVquarterList.add(currentVquarter);
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "VQuarter";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //     e.printStackTrace();
        }
        return (mVquarterList);
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
                                        land = DatabaseManager.getInstance().getLandWithId(xLandId);
                                        currentVquarter.setLand(land);
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                }


                            }

                            if (name.equalsIgnoreCase("Name")) {

                                xVariety = modifyString(xpp.nextText());
                                Log.d(TAG, "[XMLParserVQuarter] variety: " + xVariety);
                                currentVquarter.setVariety(xVariety);

                            }
                            if (name.equalsIgnoreCase("GueltigSeitEJ")) {
                                xYear = Integer.parseInt(xpp.nextText());
                                Log.d(TAG, "[XMLParserVQuarter] year: " + xYear);
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
                                Log.d(TAG, "[XMLParserVQuarter] size: " + xSize);
                                currentVquarter.setSize(xSize);

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Sortenquartier") && currentVquarter != null) {
                            Log.d(TAG, "[XMLParserLand] adding vquarter: " + currentVquarter.getId() + " " + currentVquarter.getVariety() + " - " + currentVquarter.getCode());
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

    private void loadElement(XmlPullParser xpp) throws XmlPullParserException, IOException {

        int eventType = xpp.getEventType();
        if (eventType == XmlPullParser.START_TAG && 0 == "Anlage".compareTo(xpp.getName())) {
            eventType = xpp.next();
            while (eventType != XmlPullParser.END_TAG || 0 != "Anlage".compareTo(xpp.getName())) {
                if (eventType == XmlPullParser.START_TAG && 0 == "Code".compareTo(xpp.getName())) {
                    loadItem(xpp);
                }

                eventType = xpp.next();
            }
        }
    }

    private void loadItem(XmlPullParser xpp) throws XmlPullParserException, IOException {

        int eventType = xpp.getEventType();
        if (eventType == XmlPullParser.START_TAG && 0 == "Code".compareTo(xpp.getName())) {

            eventType = xpp.next();
            while (eventType != XmlPullParser.END_TAG || 0 != "Code".compareTo(xpp.getName())) {

                // Get attributes.
                String attr = xpp.getAttributeValue(null, "Text");
                String text = null;

                // Get item text if present.
                eventType = xpp.next();
                while (eventType != XmlPullParser.END_TAG || 0 != "Code".compareTo(xpp.getName())) {
                    if (eventType == XmlPullParser.TEXT) {
                        text = xpp.getText();
                    }

                    eventType = xpp.next();
                }

                eventType = xpp.next();
            }
        }
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
