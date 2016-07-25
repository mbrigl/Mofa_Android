package it.schmid.android.mofa.model;


import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.interfaces.ProductInterface;
import it.schmid.android.mofa.db.DatabaseManager;

import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class Pesticide extends ImportBehavior implements ProductInterface{
	private static final String TAG = "PesticideClass";
    private static final int SHOWINFO = 1;
	@DatabaseField(id=true)
	@Expose
	private Integer id;
	@DatabaseField (index=true)
	private Integer regNumber;
	@DatabaseField (index=true)
	private String productName;
	@DatabaseField
	private Double defaultDose;
	@DatabaseField
	private String code;
    @DatabaseField
	private String constraints;
	private Boolean importError=false;
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getRegNumber() {
		return regNumber;
	}


	public void setRegNumber(Integer regNumber) {
		this.regNumber = regNumber;
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


    public int showInfo() {
        return SHOWINFO;
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

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    @Override
	public void importMasterData(JSONArray importData) {
		Pesticide pesticide;
		try {
    		Log.i(TAG,
    	          "Number of entries " + importData.length());
    		for (int i = 0; i < importData.length(); i++) {
    	        JSONObject jsonObject = importData.getJSONObject(i);
    	        Log.i(TAG, jsonObject.getString("product")  +","+jsonObject.getInt("id"));
    	        pesticide = DatabaseManager.getInstance().getPesticideWithId(jsonObject.getInt("id"));
    	        if (null!=pesticide) {
    	        	if (!jsonObject.isNull("regnr")){
    	        		pesticide.setRegNumber(jsonObject.getInt("regnr"));
    	        	}
    	        	
    	        	pesticide.setProductName(jsonObject.getString("product"));
    	        	if (!(jsonObject.getString("dose").equals (""))){
    	        		double doubleDose = 0;
    	        		java.text.NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
    	        		try{
    	        			doubleDose = nf.parse(jsonObject.getString("dose")).doubleValue(); 
    	        			pesticide.setDefaultDose(doubleDose); 
    	        		}catch (Exception ex){
    	        			ex.printStackTrace();
    	        		}
    	        		
    	        			        	
    	        	}
    	        	
    	            DatabaseManager.getInstance().updatePesticide(pesticide);
                } else {
                	Pesticide p = new Pesticide();
                    p.setId(jsonObject.getInt("id"));
                    if (!jsonObject.isNull("regnr")){
                    	p.setRegNumber(jsonObject.getInt("regnr"));
    	        	}
                    p.setProductName(jsonObject.getString("product"));
    	        	if (!(jsonObject.getString("dose").equals (""))){
    	        		double doubleDose = 0;
    	        		java.text.NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
    	        		try{
    	        			doubleDose = nf.parse(jsonObject.getString("dose")).doubleValue(); 
    	        			p.setDefaultDose(doubleDose); 
    	        		}catch (Exception ex){
    	        			ex.printStackTrace();
    	        		}       	
    	        	}
    	        	 	     
                    DatabaseManager.getInstance().addPesticide(p);
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
		List<Pesticide> importData;
		String backEndSoftware;
		MofaApplication app = MofaApplication.getInstance();
	    backEndSoftware = app.getBackendSoftware();
	    switch (Integer.parseInt(backEndSoftware)) {
        case 1: //ASA
       	 Log.d ("TAG", "BackendSoftware: ASAAGRAR");
       	 	importData = pesticideXmlParserASA(xmlString,notification); 
            break;
        default: //default
       	 Log.d ("TAG", "BackendSoftware:Default");
       	 importData = pesticideXmlParser(xmlString,notification);
            break;
        
        }
		
		Log.d(TAG, "[importMasterData] + size of importData" + importData.size());
	    for(Pesticide p:importData){
	    	Pesticide pesticide = DatabaseManager.getInstance().getPesticideWithId(p.getId());
	    	if (pesticide!=null) {
                Log.d(TAG, "[importMasterData] updating " + p.getProductName());
	        	pesticide.setProductName(p.getProductName());
	        	pesticide.setDefaultDose(p.getDefaultDose());
	        	pesticide.setRegNumber(p.getRegNumber());
				pesticide.setConstraints(p.getConstraints());
	            DatabaseManager.getInstance().updatePesticide(pesticide);
            } else
            {
                Log.d(TAG, "[importMasterData] Adding " + p.getProductName());
            	Pesticide newPesticide = new Pesticide();
            	newPesticide.setId(p.getId());
            	newPesticide.setProductName(p.getProductName());
            	newPesticide.setDefaultDose(p.getDefaultDose());
                newPesticide.setConstraints(p.getConstraints());
            	newPesticide.setRegNumber(p.getRegNumber());
                DatabaseManager.getInstance().addPesticide(p);
            }
	    }
	    
		return importError;
		
	}
	private List<Pesticide> pesticideXmlParser(String inputData, NotificationService notification)	{
		List<Pesticide> mPesticideList=null ;
        JSONObject json=null;
		Integer xId = null;
		String xProduct = "";
		Integer xRegnr = null;
		Double xDose = null;
        Double maxAmount=null;
        Integer maxUsage=null;
        int wez=24;
        String restrictionText=null;
        Integer waitPeriod=null;
        int beeRestriction=0;
		Double maxDose = null;
		try {
	        //For String source
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        xpp.setInput(new StringReader(inputData)); 
	        int eventType = xpp.getEventType();
	        Pesticide currentPesticide = null;
	    
	        while (eventType != XmlPullParser.END_DOCUMENT ){
	            String name = null;
	            switch (eventType){
	                case XmlPullParser.START_DOCUMENT:
	                	mPesticideList = new ArrayList<Pesticide>();
	                    break;
	                case XmlPullParser.START_TAG:
	                    name = xpp.getName();
	                    if (name.equalsIgnoreCase("pesticide")){
	                    	currentPesticide = new Pesticide();
                            json = new JSONObject();
	                       // currentPesticide.setId(Integer.parseInt(xpp.getAttributeValue(0))); 
	                    } else if (currentPesticide!= null){
	                    	if (name.equalsIgnoreCase("ID")&& !(xpp.isEmptyElementTag())){
	                        	xId = Integer.parseInt(xpp.nextText());
	                        	currentPesticide.setId(xId);
	                                       	                        	
	                        }
	                        if (name.equalsIgnoreCase("regnr")&& !(xpp.isEmptyElementTag())){
	                        	xRegnr = Integer.parseInt(xpp.nextText());
	                        	currentPesticide.setRegNumber(xRegnr);
	                        }
	                        if (name.equalsIgnoreCase("product")){
	                        	xProduct = xpp.nextText();
	                           	currentPesticide.setProductName(xProduct);
	                        		                        	
	                        }
                            if (name.equalsIgnoreCase("waitPeriod")){
                                waitPeriod = Integer.parseInt(xpp.nextText());
                                try {
                                    json.put("waitingPeriod",waitPeriod);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (name.equalsIgnoreCase("wez")){
                                wez = Integer.parseInt(xpp.nextText());
                                try {
                                    json.put("wez",wez);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (name.equalsIgnoreCase("maxUsage")){
                               maxUsage = Integer.parseInt(xpp.nextText());
                                try {
                                    json.put("maxUsage",maxUsage);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (name.equalsIgnoreCase("maxAmount")){
                                NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
                                Number number = format.parse(xpp.nextText());
                                maxAmount = number.doubleValue();
                                try {
                                    json.put("maxAmount",maxAmount);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (name.equalsIgnoreCase("beeDanger")){
                                beeRestriction= Integer.parseInt(xpp.nextText());
                                try {
                                    json.put("beeRestriction",beeRestriction);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (name.equalsIgnoreCase("restriction")){
                                restrictionText= xpp.nextText();
                                try {
                                    json.put("restriction", restrictionText);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
							if (name.equalsIgnoreCase("maxDose")){
								NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
								Number number = format.parse(xpp.nextText());
								maxDose = number.doubleValue();
								try {
									json.put("maxDose",maxDose);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
	                        if (name.equalsIgnoreCase("dose")&& !(xpp.isEmptyElementTag())){
	                        	String value = xpp.nextText();
	                        	java.text.NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
	                        	try{
	                        		if (!(value.equalsIgnoreCase(""))){
	                        			xDose = nf.parse(value).doubleValue();
	                        			currentPesticide.setDefaultDose(xDose);
	                        		}
	                        	//	Log.d(TAG, "[pesticideXMLParser] Reading dose before convertation: "  + xDose);
	                        		
	                        	//	Log.d(TAG, "[pesticideXMLParser] Reading dose: "  + xDose);
	                        		
	        	        		}catch (Exception ex){
	        	        			importError = true;
	        	        			//ex.printStackTrace();
	        	        		}  
	                        	
	                           	
	                        		                        	
	                        }
	                    }
	                    
	                case XmlPullParser.END_TAG:
	                	name = xpp.getName();
	                    if (name.equalsIgnoreCase("pesticide") && currentPesticide != null){
                            currentPesticide.setConstraints(json.toString());
	                    	mPesticideList.add(currentPesticide);
	                    	//Log.d(TAG,"[XMLParserPesticide] adding pesticide: " + currentPesticide.getId() + " " + currentPesticide.getProductName());
	                    } 
	                   break;
	                }
	             eventType = xpp.next();
	             //Log.d(TAG, "[XMLParserPesticide] eventtype: " + eventType);
	            }
	          

	    } catch (XmlPullParserException e) {
	    	  importError = true;
	    	  CharSequence tickerText = "Pesticide";  
	    	  notification.completed(android.R.drawable.stat_sys_download_done, tickerText,"Parser Error");
	    	 // e.printStackTrace();
	    } catch (IOException e) {
	    	  importError = true;
	         // e.printStackTrace();
	    } catch (ParseException e) {
            e.printStackTrace();
        }
        return (mPesticideList);
	}
	private List<Pesticide> pesticideXmlParserASA(String inputData, NotificationService notification)	{
		List<Pesticide> mPesticideList=null ;
        JSONObject json;
        String text="";
		Integer xId = 1;
		String xProduct = "";
		Integer xRegnr = null;
		Double xDose = null;
		String xCode="";
		Boolean firstCode = true;
		Boolean firstName = true;
        Boolean isAgrios=false;
        Boolean isApple=false;
        Double maxAmount=null;
        Integer maxUsage=null;
        String restrictionText=null;
        Integer waitPeriod=null;
        int beeRestriction=0;
		try {
	        //For String source
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        xpp.setInput(new StringReader(inputData)); 
	        int eventType = xpp.getEventType();
	        Pesticide currentPesticide = null;
	    
	        while (eventType != XmlPullParser.END_DOCUMENT ){
	            String name = null;
	            switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mPesticideList = new ArrayList<Pesticide>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Spritzmittel")) {
                            currentPesticide = new Pesticide();
                            isAgrios=false;
                            isApple=false;
                            // currentPesticide.setId(Integer.parseInt(xpp.getAttributeValue(0)));
                        } else if (currentPesticide != null) {
                            if (name.equalsIgnoreCase("ID")) {
                                xId = Integer.parseInt(xpp.nextText());
                                currentPesticide.setId(xId);
                            }
                            if (name.equalsIgnoreCase("Code") && (firstCode)) {
                                xCode = xpp.nextText();
                                currentPesticide.setCode(xCode);
                                //currentPesticide.setId(xId);
                                //xId++;
                                firstCode = false;
                            }
                            //for the moment (2014) not activated, due the fact that in ASA some regnr are alphanumeric
//	                        if (name.equalsIgnoreCase("Zulassungsnummer")&& !(xpp.isEmptyElementTag())){
//	                        	xRegnr = Integer.parseInt(xpp.nextText());
//	                        	currentPesticide.setRegNumber(xRegnr);
//	                        }
                            if (name.equalsIgnoreCase("Name") && firstName) {
                                xProduct = xpp.nextText(); //in ASA the machine name is stored as attribute
                                currentPesticide.setProductName(xProduct);
                                firstName = false;


                            }
                            if (name.equalsIgnoreCase("DosierungProHl") && !(xpp.isEmptyElementTag())) {
                                String value = xpp.nextText();
                                java.text.NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH); //English number settings
                                try {
                                    if (!(value.equalsIgnoreCase(""))) {
                                        xDose = nf.parse(value).doubleValue();
                                        currentPesticide.setDefaultDose(xDose);
                                    }
                                    //	Log.d(TAG, "[pesticideXMLParser] Reading dose before convertation: "  + xDose);

                                    //	Log.d(TAG, "[pesticideXMLParser] Reading dose: "  + xDose);

                                } catch (Exception ex) {
                                    importError = true;
                                    //ex.printStackTrace();
                                }


                            }

                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Spritzmittel") && currentPesticide != null) {
                            json = new JSONObject();
                            if (maxAmount != null) {
                                json.put("maxAmount", maxAmount);
                            }
                            if (maxUsage != null) {
                                json.put("maxUsage", maxUsage);
                            }
                            if (restrictionText != null){
                                json.put("restriction", restrictionText);
                            }
                            if (waitPeriod!=null){
                                json.put("waitingPeriod",waitPeriod);
                            }
                            json.put("beeRestriction", beeRestriction);
                            currentPesticide.setConstraints(json.toString());
                            Log.d(TAG, "Creating JSON for pesticide " + currentPesticide.getProductName() +": " + json.toString());
                            mPesticideList.add(currentPesticide);

                            //resetting the variables
                            firstCode = true;
                            firstName = true;
                            isAgrios = false;
                            isApple = false;
                            maxAmount=null;
                            maxUsage=null;
                            restrictionText=null;
                            waitPeriod=null;
                        } else if (name.equalsIgnoreCase("Einschraenkung") ) {
                            isAgrios = false; //resetting the variable, because there is also the wait time to check
                            isApple = false;
                        } else if (name.equalsIgnoreCase("Karenzzeit") ){
                            isAgrios = false; //resetting the variable, because there are also other waiting periods like bio..
                            isApple = false;
                        } else if (name.equalsIgnoreCase("Name") && text.equalsIgnoreCase("Agrios")) {
                            isAgrios = true;
                        } else if (name.equalsIgnoreCase("Kultur") && text.equalsIgnoreCase("Apfel")) {
                            isApple = true;
                        } else if (name.equalsIgnoreCase("MaximaleDosierungProAnwendung") && isAgrios && isApple) {
                            maxAmount = Double.parseDouble(text);
                        } else if (name.equalsIgnoreCase("MaximaleAnzahlAnwendungen") && isAgrios && isApple) {
                            maxUsage = Integer.parseInt(text);
                        } else if (name.equalsIgnoreCase("Notiz") && isAgrios && isApple){
                            restrictionText = text;
                        } else if (name.equalsIgnoreCase("Tage")&&isAgrios && isApple){
                            waitPeriod = Integer.parseInt(text);
                        } else if (name.equalsIgnoreCase("Bienenschutz")){
                            beeRestriction = Integer.parseInt(text);
                        }
                        break;
                }
	             eventType = xpp.next();
	             //Log.d(TAG, "[XMLParserPesticide] eventtype: " + eventType);
	            }
	          

	    } catch (XmlPullParserException e) {
	    	  importError = true;
	    	  CharSequence tickerText = "Pesticide";  
	    	  notification.completed(android.R.drawable.stat_sys_download_done, tickerText,"Parser Error");
	    	 // e.printStackTrace();
	    } catch (IOException e) {
	    	  importError = true;
	         // e.printStackTrace();
	    } catch (JSONException e) {
            e.printStackTrace();
        }
        return (mPesticideList);
	}

}
