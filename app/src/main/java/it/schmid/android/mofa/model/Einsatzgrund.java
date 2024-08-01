package it.schmid.android.mofa.model;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;

/**
 * Created by schmida on 24.04.17.
 */

public class Einsatzgrund extends ImportBehavior {
    String code;

    String name;

    List<Einsatzgrund> einsatzgrundList = new ArrayList<Einsatzgrund>();
    private Boolean importError = false;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        MofaApplication app = MofaApplication.getInstance();
        //default
        Log.d("TAG", "BackendSoftware: ASAAGRAR");
        einsatzgrundList = reasonXmlParserASA(xmlString, notification);

        return importError;
    }

    private List<Einsatzgrund> reasonXmlParser(String inputData, NotificationService notification) {
        return null;
    }

    private List<Einsatzgrund> reasonXmlParserASA(String inputData, NotificationService notification) {
        List<Einsatzgrund> reasonList = null;

        String xCode = "";
        String xMachineName = "";

        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            Einsatzgrund currentReason = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        reasonList = new ArrayList<Einsatzgrund>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Einsatzgrund")) {
                            currentReason = new Einsatzgrund();
                            // currentMachine.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentReason != null) {

                            if (name.equalsIgnoreCase("Code")) {
                                xCode = xpp.nextText();
                                currentReason.setCode(xCode);
                            }

                            if (name.equalsIgnoreCase("Name")) {
                                xMachineName = xpp.nextText();
                                currentReason.setName(xMachineName);

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Einsatzgrund") && currentReason != null) {
                            Log.d("Reasons", "[XMLParserReason] adding reason: " + currentReason.getName());
                            reasonList.add(currentReason);

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
        return (reasonList);
    }
}
