package it.schmid.android.mofa.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
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
public class FruitQuality extends ImportBehavior {
    private static final String TAG = "FruitQualityClass";
    private Boolean importError = false;
    @DatabaseField(id = true)
    @Expose
    private Integer id;
    @DatabaseField
    private String quality;
    @DatabaseField
    private String code;
    @ForeignCollectionField
    private ForeignCollection<Harvest> harvests;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Harvest> getHarvests() {
        ArrayList<Harvest> harvestList = new ArrayList<Harvest>();
        for (Harvest harvest : harvests) {
            harvestList.add(harvest);
        }
        return harvestList;
    }

    public void setItem(ForeignCollection<Harvest> harvests) {
        this.harvests = harvests;
    }

    @Override
    public void importMasterData(JSONArray importData) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean equals(Object obj) {
        //null instanceof Object will always return false
        if (!(obj instanceof FruitQuality))
            return false;
        if (obj == this)
            return true;
        return this.quality.equalsIgnoreCase(((FruitQuality) obj).quality);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = (int) (id / 12) + 5;
        return result;
    }

    @Override
    public Boolean importMasterData(String xmlString,
                                    NotificationService notification) {
        String backEndSoftware;
        List<FruitQuality> importData;
        MofaApplication app = MofaApplication.getInstance();
        backEndSoftware = app.getBackendSoftware();
        //default
        if (Integer.parseInt(backEndSoftware) == 1) { //ASA
            Log.d("TAG", "BackendSoftware: ASAAGRAR");
            importData = qualityXmlParserASA(xmlString, notification);
        } else {
            Log.d("TAG", "BackendSoftware:Default");
            importData = qualityXmlParser(xmlString, notification);
        }


        for (FruitQuality f : importData) {
            FruitQuality quality = DatabaseManager.getInstance().getQualityWithId(f.getId());
            if (quality != null) {

                quality.setQuality(f.getQuality());
                DatabaseManager.getInstance().updateQuality(quality);
            } else {

                FruitQuality newQuality = new FruitQuality();
                newQuality.setId(f.getId());
                newQuality.setQuality(f.getQuality());
                DatabaseManager.getInstance().addQuality(f);
            }
        }
        return importError;

    }

    private List<FruitQuality> qualityXmlParser(String inputData, NotificationService notification) {
        List<FruitQuality> mQualityList = null;
        String xQualityName = null;
        Integer xId = null;
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));

            int eventType = xpp.getEventType();
            FruitQuality currentQuality = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mQualityList = new ArrayList<FruitQuality>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("quality")) {
                            currentQuality = new FruitQuality();

                        } else if (currentQuality != null) {
                            if (name.equalsIgnoreCase("id") && !(xpp.isEmptyElementTag())) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentQuality.setId(xId);

                            }
                            if (name.equalsIgnoreCase("desc")) {
                                xQualityName = xpp.nextText();
                                currentQuality.setQuality(xQualityName);
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("quality") && currentQuality != null) {
                            Log.d(TAG, "[XMLQuality] adding quality: " + currentQuality.getId() + " " + currentQuality.getQuality());
                            mQualityList.add(currentQuality);
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Quality";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //   e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mQualityList);
    }

    private List<FruitQuality> qualityXmlParserASA(String inputData,
                                                   NotificationService notification) {
        List<FruitQuality> mQualityList = null;
        Integer xId = 1;
        String xCode = "";
        String xQualityName = null;
        Boolean firstCode = false; //due the fact that ASA uses the Code Tag in different nodes
        Boolean firstCat = true;
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));

            int eventType = xpp.getEventType();
            FruitQuality currentQuality = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mQualityList = new ArrayList<FruitQuality>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Kategorie")) {
                            currentQuality = new FruitQuality();

                        } else if (currentQuality != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentQuality.setId(xId);
                            }
                            if ((name.equalsIgnoreCase("Code")) && (!firstCode)) {
                                xCode = xpp.nextText();
                                currentQuality.setCode(xCode); //in ASA Code is the primary key
                                //  currentQuality.setId(xId);    //setting the primary key

                                //  xId++;
                                firstCode = true;
                            }
                            if (name.equalsIgnoreCase("Name") && (firstCat)) {
                                //xQualityName = xpp.getAttributeValue(null, "Text");;
                                xQualityName = xpp.nextText();
                                currentQuality.setQuality(xQualityName);
                                firstCat = false;

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Kategorie") && currentQuality != null) {
                            Log.d(TAG, "[XMLParserQuality] adding quality: " + currentQuality.getId() + " " + currentQuality.getQuality());
                            mQualityList.add(currentQuality);
                            firstCode = false;
                            firstCat = true; //there are more than one categories desc in the multilanguage environment, we need only the first one
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Fruitquality";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //   e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mQualityList);
    }

}
