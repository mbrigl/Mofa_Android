package it.schmid.android.mofa.model;


import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable
public class Land extends ImportBehavior{
	private static final String TAG = "LandClass";
	@DatabaseField(id=true)
	private Integer id;
	 @DatabaseField
	private String name;
	 @DatabaseField
	private String code;
	
	@ForeignCollectionField
	private ForeignCollection<VQuarter> vquarters;
	private Boolean importError=false;
	 public Land(){
		
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
	public void importMasterData(JSONArray importData){
		Land land;
		try {
    		Log.i(TAG,
    	          "Number of entries " + importData.length());
    		for (int i = 0; i < importData.length(); i++) {
    	        JSONObject jsonObject = importData.getJSONObject(i);
    	        Log.i(TAG, jsonObject.getString("name") +","+ jsonObject.getInt("id"));
    	        land = DatabaseManager.getInstance().getLandWithId(jsonObject.getInt("id"));
    	        if (land!=null) {
    	        	Log.i(TAG,land.getName());
    	        	land.setName(jsonObject.getString("name"));
    	            DatabaseManager.getInstance().updateLand(land);
                } else
                {
                	Log.i(TAG,"New Land");
                	Land l = new Land();
                    l.setId(jsonObject.getInt("id"));
                	l.setName(jsonObject.getString("name"));
                    DatabaseManager.getInstance().addLand(l);
                }
    	      }
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
	}
	
	public void setItems(ForeignCollection<VQuarter> vquarters) {
        this.vquarters = vquarters;
    }

    public List<VQuarter> getVQuarters() {
        ArrayList<VQuarter> vquarterList = new ArrayList<VQuarter>();
        for (VQuarter vquarter : vquarters) {
            vquarterList.add(vquarter);
        }
        return vquarterList;
    }
	@Override
	public Boolean importMasterData(String xmlString, NotificationService notification) {
		String backEndSoftware;
		List<Land> importData;
		MofaApplication app = MofaApplication.getInstance();
	    backEndSoftware = app.getBackendSoftware();
	    switch (Integer.parseInt(backEndSoftware)) {
        case 1: //ASA
       	 Log.d ("TAG", "BackendSoftware: ASAAGRAR");
       	 	importData = landXmlParserASA(xmlString, notification); 
            break;
        default: //default
       	 Log.d ("TAG", "BackendSoftware:Default");
       	 importData = landXmlParser(xmlString, notification);
            break;
        
        }
	    for(Land l:importData){
	    	Land land = DatabaseManager.getInstance().getLandWithId(l.getId());
	    	if (land!=null) {
	        	
	        	land.setName(l.getName());
	            DatabaseManager.getInstance().updateLand(land);
            } else
            {
            	
            	Land newLand = new Land();
                newLand.setId(l.getId());
            	newLand.setName(l.getName());
                DatabaseManager.getInstance().addLand(l);
            }
	    }
	    return importError;
	}
	private List<Land> landXmlParser(String inputData, NotificationService notification)	{
		List<Land> mLandList=null ;
		Integer xId= null;
		String xLandName = null;
		
		try {
	        //For String source
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        xpp.setInput(new StringReader(inputData)); 
	        int eventType = xpp.getEventType();
	        Land currentLand = null;
	    
	        while (eventType != XmlPullParser.END_DOCUMENT ){
	            String name = null;
	            switch (eventType){
	                case XmlPullParser.START_DOCUMENT:
	                	mLandList = new ArrayList<Land>();
	                    break;
	                case XmlPullParser.START_TAG:
	                    name = xpp.getName();
	                    if (name.equalsIgnoreCase("land")){
	                    	currentLand = new Land();
	                                               	
	                    } else if (currentLand != null){
	                    	if (name.equalsIgnoreCase("id")&& !(xpp.isEmptyElementTag())){
	                        	xId = Integer.parseInt(xpp.nextText());
	                        	currentLand.setId(xId);
	                                       	                        	
	                        }
	                        if (name.equalsIgnoreCase("Name")){
	                        	xLandName = xpp.nextText();
	                        	Log.d(TAG,"[XMLParserLand] land name: " + xLandName);
	                        	currentLand.setName(xLandName);
	                        	
	                        	
	                        }
	                    }
	                    break;
	                case XmlPullParser.END_TAG:
	                	name = xpp.getName();
	                    if (name.equalsIgnoreCase("land") && currentLand != null){
	                    	Log.d(TAG,"[XMLParserLand] adding land: " + currentLand.getId() + " " + currentLand.getName());
	                        mLandList.add(currentLand);
	                        
	                    } 
	                   
	                }
	            eventType = xpp.next();
	            }
	          

	    } catch (XmlPullParserException e) {
	    	  importError = true;
	    	  CharSequence tickerText = "Land";  
	    	  notification.completed(android.R.drawable.stat_sys_download_done, tickerText,"Parser Error");
	        //  e.printStackTrace();
	    } catch (IOException e) {
	    	  importError = true;
	        //  e.printStackTrace();
	    }
		return (mLandList);
	}
	private List<Land> landXmlParserASA(String inputData, NotificationService notification)	{
		List<Land> mLandList=null ;
		Integer xId= 1;
		String xCode="";
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
	    
	        while (eventType != XmlPullParser.END_DOCUMENT ){
	            String name = null;
	            switch (eventType){
	                case XmlPullParser.START_DOCUMENT:
	                	mLandList = new ArrayList<Land>();
	                    break;
	                case XmlPullParser.START_TAG:
	                    name = xpp.getName();
	                    if (name.equalsIgnoreCase("Anlage")){
	                    	currentLand = new Land();
	                                               	
	                    } else if (currentLand != null){
	                    	if (name.equalsIgnoreCase("ID")){
	                    		xId=Integer.parseInt(xpp.nextText());
	                    		currentLand.setId(xId);
	                    	}
	                    	 if (name.equalsIgnoreCase("code")){
	                    		xCode = xpp.nextText(); 
	                    		currentLand.setCode(xCode);
	                        	//currentLand.setId(xId);
	                           // xId++;      	                        	
	                        }
	                    	 if (name.equalsIgnoreCase("Name")&& (firstDesc)){
		                        	xLandName = xpp.nextText(); //in ASA the land name is stored as attribute
		                        	currentLand.setName(xLandName);
		                        	firstDesc=false;                       	
		                        }
	                    }
	                    break;
	                case XmlPullParser.END_TAG:
	                	name = xpp.getName();
	                    if (name.equalsIgnoreCase("Anlage") && currentLand != null){
	                    	Log.d(TAG,"[XMLParserLand] adding land: " + currentLand.getId() + " " + currentLand.getName());
	                        mLandList.add(currentLand);
	                        firstDesc=true;
	                    } 
	                   
	                }
	            eventType = xpp.next();
	            }
	          

	    } catch (XmlPullParserException e) {
	    	  importError = true;
	    	  CharSequence tickerText = "Land";  
	    	  notification.completed(android.R.drawable.stat_sys_download_done, tickerText,"Parser Error");
	        //  e.printStackTrace();
	    } catch (IOException e) {
	    	  importError = true;
	        //  e.printStackTrace();
	    }
		return (mLandList);
	}
}
