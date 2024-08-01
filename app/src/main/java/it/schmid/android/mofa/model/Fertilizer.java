package it.schmid.android.mofa.model;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.ProductInterface;

public class Fertilizer extends ImportBehavior implements ProductInterface {
    private static final String TAG = "FertilizerClass";
    private static final int SHOWINFO = 0;
    @DatabaseField(id = true)

    private Integer id;
    @DatabaseField(index = true)
    private String productName;
    @DatabaseField
    private Double defaultDose;
    @DatabaseField
    private String code;
    @DatabaseField
    private String data;
    private Boolean importError = false;
    private String asaFertType;
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

    public Double getDefaultDose() {
        return defaultDose;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDefaultDose(Double defaultDose) {
        this.defaultDose = defaultDose;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return productName;
    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        List<Fertilizer> importData;
        app = MofaApplication.getInstance();

        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        importData = fertilizerXmlParserASA(xmlString, notification);

        for (Fertilizer f : importData) {
            Fertilizer fertilizer = DatabaseManager.getInstance().getFertilizerWithId(f.getId());
            if (fertilizer != null) {

                fertilizer.setProductName(f.getProductName());
                fertilizer.setDefaultDose(f.getDefaultDose());
                fertilizer.setCode(f.getCode());
                DatabaseManager.getInstance().updateFertilizer(fertilizer);
            } else {
                Fertilizer newFertilizer = new Fertilizer();
                newFertilizer.setId(f.getId());
                newFertilizer.setProductName(f.getProductName());
                newFertilizer.setDefaultDose(f.getDefaultDose());
                newFertilizer.setCode(f.getCode());
                DatabaseManager.getInstance().addFertilizer(f);
            }
        }
        return importError;
    }

    private List<Fertilizer> fertilizerXmlParser(String inputData, NotificationService notification) {
        List<Fertilizer> mFertilizerList = null;
        Integer xId = null;
        String xProduct = "";
        Double xDose = null;
        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            Fertilizer currentFertilizer = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mFertilizerList = new ArrayList<Fertilizer>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("fertilizer")) {
                            currentFertilizer = new Fertilizer();
                            //  currentFertilizer.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentFertilizer != null) {
                            if (name.equalsIgnoreCase("id") && !(xpp.isEmptyElementTag())) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentFertilizer.setId(xId);

                            }
                            if (name.equalsIgnoreCase("product")) {
                                xProduct = xpp.nextText();
                                currentFertilizer.setProductName(xProduct);

                            }
                            if (name.equalsIgnoreCase("dose") && !(xpp.isEmptyElementTag())) {
                                String value = xpp.nextText();
                                java.text.NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                                try {
                                    if (!(value.equalsIgnoreCase(""))) {
                                        xDose = nf.parse(value).doubleValue();
                                        currentFertilizer.setDefaultDose(xDose);
                                    }

                                } catch (Exception ex) {
                                    importError = true;
                                    ex.printStackTrace();
                                }


                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("fertilizer") && currentFertilizer != null) {
                            Log.d(TAG, "[XMLParserFertilizer] adding fertilizer: " + currentFertilizer.getId() + " " + currentFertilizer.getProductName());
                            mFertilizerList.add(currentFertilizer);
                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Fertilizer";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText, "Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            //  e.printStackTrace();
        }
        return (mFertilizerList);
    }

    private List<Fertilizer> fertilizerXmlParserASA(String inputData, NotificationService notification) {
        List<Fertilizer> mFertilizerList = null;
        Integer xId = 1;
        String xFertType = "";
        String xProduct = "";
        Double xDose = null;
        String xCode = "";
        Boolean firstCode = true;//due the fact that ASA uses the Code Tag in different nodes

        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            Fertilizer currentFertilizer = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mFertilizerList = new ArrayList<Fertilizer>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Duengemittel")) {
                            currentFertilizer = new Fertilizer();
                            //  currentFertilizer.setId(Integer.parseInt(xpp.getAttributeValue(0)));
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
                                xProduct = xpp.nextText(); //in ASA the fertilizer name is stored as attribute
                                currentFertilizer.setProductName(xProduct);

                            }

                            if (name.equalsIgnoreCase("DosierungProHl") && !(xpp.isEmptyElementTag())) {
                                String value = xpp.nextText();
                                java.text.NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH); //number settings in English
                                try {
                                    if (!(value.equalsIgnoreCase(""))) {
                                        xDose = nf.parse(value).doubleValue();
                                        currentFertilizer.setDefaultDose(xDose);
                                    }

                                } catch (Exception ex) {
                                    importError = true;
                                    ex.printStackTrace();
                                }


                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Duengemittel") && currentFertilizer != null) {
                            Log.d(TAG, "[XMLParserFertilizer] adding fertilizer: " + currentFertilizer.getId() + " " + currentFertilizer.getProductName());
                            mFertilizerList.add(currentFertilizer);
                            //leafFert=false;
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
            CharSequence tickerText = "Fertilizer";
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

    public int showInfo() {
        return SHOWINFO;
    }
}
