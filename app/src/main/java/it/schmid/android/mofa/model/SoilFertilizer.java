package it.schmid.android.mofa.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;

public class SoilFertilizer extends ImportBehavior {
    private static final String TAG = "SoilFertilizerClass";
    @DatabaseField(id = true)
    @Expose
    private Integer id;
    @DatabaseField(index = true)
    private String productName;
    @DatabaseField
    private String code;
    private Boolean importError = false;
    private String asaSoilFertType;
    private MofaApplication app;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void importMasterData(JSONArray importData) {
        SoilFertilizer sFertilizer;
        try {
            Log.i(TAG,
                    "Number of entries " + importData.length());
            for (int i = 0; i < importData.length(); i++) {
                JSONObject jsonObject = importData.getJSONObject(i);
                Log.i(TAG, jsonObject.getString("product") + "," + jsonObject.getInt("id"));
                sFertilizer = DatabaseManager.getInstance().getSoilFertilizerWithId(jsonObject.getInt("id"));
                if (null != sFertilizer) {
                    sFertilizer.setProductName(jsonObject.getString("product"));
                    DatabaseManager.getInstance().updateSoilFertilizer(sFertilizer);
                } else {
                    SoilFertilizer f = new SoilFertilizer();
                    f.setId(jsonObject.getInt("id"));
                    f.setProductName(jsonObject.getString("product"));
                    DatabaseManager.getInstance().addSoilFertilizer(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return productName;
    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        List<SoilFertilizer> importData;
        app = MofaApplication.getInstance();
            Log.d("TAG", "BackendSoftware: ASAAGRAR");
            importData = soilFertilizerXmlParserASA(xmlString, notification);
        for (SoilFertilizer f : importData) {
            SoilFertilizer soilFertilizer = DatabaseManager.getInstance().getSoilFertilizerWithId(f.getId());
            if (soilFertilizer != null) {

                soilFertilizer.setProductName(f.getProductName());
                DatabaseManager.getInstance().updateSoilFertilizer(soilFertilizer);
            } else {
                SoilFertilizer newSoilFertilizer = new SoilFertilizer();
                newSoilFertilizer.setId(f.getId());
                newSoilFertilizer.setProductName(f.getProductName());
                DatabaseManager.getInstance().addSoilFertilizer(f);
            }
        }
        return importError;
    }

    private List<SoilFertilizer> soilFertilizerXmlParser(String inputData, NotificationService notification) {
        List<SoilFertilizer> mSoilFertilizerList = null;
        Integer xId = null;
        String xProduct = "";
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            SoilFertilizer currentSoilFertilizer = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mSoilFertilizerList = new ArrayList<SoilFertilizer>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("soilfertilizer")) {
                            currentSoilFertilizer = new SoilFertilizer();
                            //  currentFertilizer.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentSoilFertilizer != null) {
                            if (name.equalsIgnoreCase("id") && !(xpp.isEmptyElementTag())) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentSoilFertilizer.setId(xId);

                            }
                            if (name.equalsIgnoreCase("product")) {
                                xProduct = xpp.nextText();
                                currentSoilFertilizer.setProductName(xProduct);

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("soilfertilizer") && currentSoilFertilizer != null) {
                            Log.d(TAG, "[XMLParserSoilFertilizer] adding soilfertilizer: " + currentSoilFertilizer.getId() + " " + currentSoilFertilizer.getProductName());
                            mSoilFertilizerList.add(currentSoilFertilizer);
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "SoilFertilizer";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mSoilFertilizerList);
    }

    private List<SoilFertilizer> soilFertilizerXmlParserASA(String inputData, NotificationService notification) {
        List<SoilFertilizer> mFertilizerList = null;
        Integer xId = 1;
        String xFertType = "";
        String xProduct = "";
        String xCode = "";
        Boolean firstCode = true;//due the fact that ASA uses the Code Tag in different nodes

        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            SoilFertilizer currentFertilizer = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mFertilizerList = new ArrayList<SoilFertilizer>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Duengemittel")) {
                            currentFertilizer = new SoilFertilizer();

                        } else if (currentFertilizer != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentFertilizer.setId(xId);
                            }
                            if (name.equalsIgnoreCase("Code") && (firstCode)) {
                                xCode = xpp.nextText();
                                currentFertilizer.setCode(xCode);
                                //currentFertilizer.setId(xId);
                                //xId++;
                                firstCode = false;


                            }
                            if (name.equalsIgnoreCase("Name")) {
                                xProduct = xpp.nextText(); //in ASA the machine name is stored as attribute
                                currentFertilizer.setProductName(xProduct);

                            }


                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Duengemittel") && currentFertilizer != null) {
                            Log.d(TAG, "[XMLParserFertilizer] adding  soil fertilizer: " + currentFertilizer.getId() + " " + currentFertilizer.getProductName());
                            mFertilizerList.add(currentFertilizer);
                            //     soilFert=false;
                            // firstCode=true;
                        }
                        if (name.equalsIgnoreCase("Duengemittel")) { //new Duengemittel
                            currentFertilizer = null;
                            firstCode = true;
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "SoilFertilizer";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mFertilizerList);
    }

    private static <T> boolean contains(final T[] array, final T v) {
        for (final T e : array)
            if (Objects.equals(v, e))
                return true;

        return false;
    }
}
