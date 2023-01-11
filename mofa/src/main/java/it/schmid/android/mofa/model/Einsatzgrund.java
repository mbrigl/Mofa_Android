package it.schmid.android.mofa.model;

import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;

/**
 * Created by schmida on 24.04.17.
 */

public class Einsatzgrund extends ImportBehavior {
    @SerializedName("Code")
    @Expose
    String code;
    @SerializedName("Name")
    @Expose
    String name;

    List<Einsatzgrund> einsatzgrundList = new ArrayList<Einsatzgrund>();
    private Boolean importError=false;
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
    public void importMasterData(JSONArray importData) {

    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        String backEndSoftware;

        MofaApplication app = MofaApplication.getInstance();
        backEndSoftware = app.getBackendSoftware();
        switch (Integer.parseInt(backEndSoftware)) {
            case 1: //ASA
                Log.d ("TAG", "BackendSoftware: ASAAGRAR");
                einsatzgrundList = reasonXmlParserASA(xmlString,notification);
                break;
            default: //default
                Log.d ("TAG", "BackendSoftware:Default");
                einsatzgrundList = reasonXmlParser(xmlString,notification);
                break;

        }
        if (!einsatzgrundList.isEmpty()){
            List<Global> globalList = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.PESTREASONS);
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Type listOfReasons = new TypeToken<List<Einsatzgrund>>(){}.getType();
            String json = gson.toJson(einsatzgrundList,listOfReasons);

            if(globalList.isEmpty()){
                Global g = new Global();
                g.setTypeInfo(ActivityConstants.PESTREASONS);
                g.setData(json);
                DatabaseManager.getInstance().addGlobal(g);
                //new entry for global data
            }else{
                // existing entry
                Global g = globalList.get(0);
                g.setData(json);
                DatabaseManager.getInstance().updateGlobal(g);
            }


            //Log.d("ImportReason", json);

        }

        return importError;
    }

    private List<Einsatzgrund> reasonXmlParser(String inputData,NotificationService notification){
        return null;
    }
    private List<Einsatzgrund> reasonXmlParserASA(String inputData,NotificationService notification){
        List<Einsatzgrund> reasonList=null ;

        String xCode="";
        String xMachineName = "";

        try {
            //For String source
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(inputData));
            int eventType = xpp.getEventType();
            Einsatzgrund currentReason = null;

            while (eventType != XmlPullParser.END_DOCUMENT ){
                String name = null;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        reasonList = new ArrayList<Einsatzgrund>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Einsatzgrund")){
                            currentReason = new Einsatzgrund();
                            // currentMachine.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentReason!= null){

                            if (name.equalsIgnoreCase("Code")) {
                                xCode = xpp.nextText();
                                currentReason.setCode(xCode);
                            }

                            if (name.equalsIgnoreCase("Name")){
                                xMachineName = xpp.nextText();
                                currentReason.setName(xMachineName);

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Einsatzgrund") && currentReason != null){
                            Log.d("Reasons","[XMLParserReason] adding reason: " + currentReason.getName());
                            reasonList.add(currentReason);

                        }

                }
                eventType = xpp.next();
            }


        } catch (XmlPullParserException e) {
            importError = true;
            CharSequence tickerText = "Machine";
            notification.completed(android.R.drawable.stat_sys_download_done, tickerText,"Parser Error");
            //  e.printStackTrace();
        } catch (IOException e) {
            importError = true;
            // e.printStackTrace();
        }
        return (reasonList);
    }
    @Nullable
    public static HashMap<String,String> getEinsatzGrundHashMap(){
        List<Global> globalList = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.PESTREASONS);
        if (!globalList.isEmpty()){
            String jsonData = globalList.get(0).getData();
            Gson gson = new Gson();
            Einsatzgrund[] arrEinsatz = gson.fromJson(jsonData, Einsatzgrund[].class);
            HashMap<String,String> einsatzMap = new HashMap<String,String>();
            for (Einsatzgrund e: arrEinsatz){
                einsatzMap.put(e.getName(),e.getCode());
            }
            return einsatzMap;
        }else{
            return null;
        }

    }

}
