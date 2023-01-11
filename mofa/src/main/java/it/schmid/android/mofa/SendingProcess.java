package it.schmid.android.mofa;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.dropbox.DropboxClient;
import it.schmid.android.mofa.model.Einsatzgrund;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Global;
import it.schmid.android.mofa.model.Harvest;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.Purchase;
import it.schmid.android.mofa.model.PurchaseFertilizer;
import it.schmid.android.mofa.model.PurchasePesticide;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkFertilizer;
import it.schmid.android.mofa.model.WorkMachine;
import it.schmid.android.mofa.model.WorkVQuarter;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Xml;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class SendingProcess implements Runnable{
	private static final String TAG = "SendingProcess";
	private static final String ASANOTE = "(MoFa)";
	Context context;
	private NotificationService mNotificationService; // notification services
	private Boolean dropBox;  // checking if dropbox setting is enabled
	private Boolean offline; // checking if offline or through REST
	private Boolean grossPrice; // checking if for ASA using gross prices
	private Boolean mofaNote;
	private Boolean asa_New_Ver;
	private String format; // file format
	private String sendingData; //
	private String urlPath;
	private String notifMess = "";
	private String backEndSoftware;
	private boolean error = false; // error value for webservice connection
	private String restResponse=""; // not used yet, but response of json-webservice
	private int callingActivity;
	private String asaWorkHerbicideCode;
	private String ACCESS_TOKEN; //Dropbox
	RemoveEntries mremoveEntries;
	//constructor
	public SendingProcess (Context context, Integer callingActivity){
		this.context = context;
		this.callingActivity= callingActivity;
		mremoveEntries = (RemoveEntries) context;
		
	}
	// interface to delete the works from workoverview -- callback
		public interface RemoveEntries {
			public void deleteAllEntries ();
		}
/**
 * background sending thread
 */
public void run(){
	Looper.prepare(); //For Preparing Message Pool for the child Thread
	MofaApplication app = MofaApplication.getInstance();
	backEndSoftware = app.getBackendSoftware();

	if (format.equalsIgnoreCase("1")){ //JSON
		try {
			sendingData = createJSON();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}else{ //XML
		if (Integer.parseInt(backEndSoftware)==1){ //special case ASA 

			if (callingActivity==ActivityConstants.WORK_OVERVIEW){ //calling this asynch method from workoverview
				if (asa_New_Ver){
					sendingData = createXMLASAVer16();
				}else{
					sendingData = createXMLASA();
				}

			}
			if (callingActivity==ActivityConstants.PURCHASING_ACTIVITY){ //calling this asynch method from purchasing activity
				if(asa_New_Ver){
					Log.d("Sending Data","ASA_New_Purchasexml");
					sendingData = createPurchaseXMLASAVer16();
				}else{
					sendingData = createPurchaseXMLASA();
				}

			}
			if (callingActivity==ActivityConstants.VEGDATA_ACTIVITY){ // calling this asynch from vegdata
				sendingData = createVegDataXMLASA();
			}
		}else{ //default case LibreOffice
			if (callingActivity==ActivityConstants.WORK_OVERVIEW){ //default case
				sendingData = createXML(); //default case
			}
			if (callingActivity==ActivityConstants.PURCHASING_ACTIVITY){
				sendingData= createPurchaseXML();
			}
			if (callingActivity==ActivityConstants.VEGDATA_ACTIVITY){ // calling this asynch from vegdata
				sendingData = createVegDataXML();
			}
		}
		
	}
	if (dropBox==true){ //DropBox case
		writeFileToDropBox(sendingData, format);
	}else{ //not Dropbox
		if (offline == true){ //offline-export to SD-Card
			writeFile(sendingData, format);
		}else{ //creating REST connection
			
			HttpClient client = app.getHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
	        HttpResponse response;
	        try{
                HttpPost post = new HttpPost(urlPath);
                StringEntity se = new StringEntity(sendingData);  
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);
              //  Checking response 
                if(response!=null){
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    Log.d (TAG, convertStreamToString(in));
                    restResponse= restResponse + convertStreamToString(in) + "\n";
                }
            }
            catch(Exception e){
                e.printStackTrace();
                error = true;
            
    	
            }
		}
	}
	
	int icon = android.R.drawable.stat_sys_upload_done;
    CharSequence tickerText = context.getString(R.string.upload_finished);  
	if (error){
		notifMess = context.getString(R.string.upload_finished_error);
	}else{
		notifMess = context.getString(R.string.upload_finished_ok);
		handler.sendEmptyMessage(0); // handler for updating UI task
		
	}
	
	mNotificationService.completed(icon, tickerText, notifMess);
    Looper.loop(); //Loop in the message queue
}
	//preparing to send Data	
		public void sendData(){
			  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			  offline = preferences.getBoolean("updateOffline", false);
			  dropBox = preferences.getBoolean("dropbox", false);
			  format = preferences.getString("listFormat", "-1");
			  urlPath = preferences.getString("url", "");
			  grossPrice = preferences.getBoolean("grossprice", false);
			  mofaNote = preferences.getBoolean("asanote", false);
			  asa_New_Ver = preferences.getBoolean("asa_new_ver",false);
			  mNotificationService= new NotificationService(context,false);
				int icon = android.R.drawable.stat_sys_upload;
			    CharSequence tickerText = context.getString(R.string.upload_title);  
				notifMess = context.getString(R.string.upload_mess);
			    mNotificationService.createNotification(icon, tickerText,notifMess);
			    
				Thread t = new Thread(this);
		        t.start();
		  }
		/**
		 * handler used for deleting and gui refreshing, accessing gui only through a handler
		 */
		private Handler handler = new Handler() {
			 @Override
	         public void handleMessage(Message msg) {
				 mremoveEntries.deleteAllEntries();

	         }
		};
		/**
		 * helper function to handle the response of the webservice - not used for the moment
		 * @param is-inputstream
		 * @return
		 */
		public static String convertStreamToString(java.io.InputStream is) {
		    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		    return s.hasNext() ? s.next() : "";
		}
		private String createJSON() throws JSONException{
		
			JSONObject workData = null;
			JSONArray mainArray = new JSONArray();
			
	        List<Work> workUploadList = DatabaseManager.getInstance().getAllWorks();
			try {
				for (Work work: workUploadList){
					workData = new JSONObject();
					workData.put("date", work.getDate());
					workData.put("task", work.getTask().getId());		
					List<WorkVQuarter> vquarters = DatabaseManager.getInstance().getVQuarterByWorkId(work.getId());
						for (WorkVQuarter vq : vquarters){
							JSONObject vqData = new JSONObject();
							vqData.put("vquarter", vq.getVquarter().getId());
							workData.accumulate("vq", vqData);
						}
					List<WorkWorker> workers = DatabaseManager.getInstance().getWorkWorkerByWorkId(work.getId());
						for (WorkWorker w : workers){
							JSONObject wData = new JSONObject();
							wData.put("workerid", w.getWorker().getId());
							wData.put("workerhours", w.getHours());
							workData.accumulate("workers", wData);
						}
					List<WorkMachine> machines = DatabaseManager.getInstance().getWorkMachineByWorkId(work.getId());
						for (WorkMachine m : machines){
							JSONObject mData = new JSONObject();
							mData.put("machineid", m.getMachine().getId());
							mData.put("machinehours", m.getHours());
							workData.accumulate("machines", mData);
						}
					List<WorkFertilizer> soilfertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(work.getId());
						for (WorkFertilizer wf : soilfertilizers){
							JSONObject mData = new JSONObject();
							mData.put("soilfertid", wf.getSoilFertilizer().getId());
							mData.put("machinehours", wf.getAmount());
							workData.accumulate("soilfertilizers", mData);
						}
					List<Spraying> spraying = DatabaseManager.getInstance().getSprayingByWorkId(work.getId());
						for (Spraying s : spraying){
							JSONObject sprayData = new JSONObject();
							sprayData.put("concentration", s.getConcentration());
							sprayData.put("wateramount", s.getWateramount());
							workData.accumulate("spraying", sprayData);
							List<SprayPesticide> sprayPest = DatabaseManager.getInstance().getSprayPesticideBySprayId(s.getId());
								for (SprayPesticide sp : sprayPest){
									JSONObject sprayPestObject = new JSONObject();
									sprayPestObject.put("pestid",sp.getPesticide().getId());
									sprayPestObject.put("dose",sp.getDose());
									sprayPestObject.put("amount",sp.getDose_amount());
									workData.accumulate("sprayPesticide", sprayPestObject);
								}
							List<SprayFertilizer>sprayFert = DatabaseManager.getInstance().getSprayFertilizerBySprayId(s.getId());
								for (SprayFertilizer sf : sprayFert){
									JSONObject sprayFertObject = new JSONObject();
									sprayFertObject.put("fertid", sf.getFertilizer().getId());
									sprayFertObject.put("dose", sf.getDose());
									sprayFertObject.put("amount", sf.getDose_amount());
									workData.accumulate("sprayFertilizer", sprayFertObject);
								}
						}
					
					mainArray.put(workData);	
				}
					
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		try {
			Log.d("TAG", "[creatingJSON] - " + mainArray.toString(4)); //4 stays for intending the lines, make the return array human readable
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(mainArray.toString(4));
		}
		/**
		 * 
		 * @param data
		 * @param fileType "1" for Json; "2" for XML
		 */
		private void writeFile(String data, String fileType){
			if (isSdPresent()){
				FileOutputStream fos;
				File sdCard = Environment.getExternalStorageDirectory();
				File file=null;
				Date date = new Date() ;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
				if (fileType.equalsIgnoreCase("1")){
					if (callingActivity==ActivityConstants.WORK_OVERVIEW){
						file = new File(sdCard.getAbsolutePath() + "/MoFaBackend/export", "worklist" + dateFormat.format(date) + ".json");
					}
					if (callingActivity==ActivityConstants.PURCHASING_ACTIVITY){
						file = new File(sdCard.getAbsolutePath() + "/MoFaBackend/export", "purchaselist" + dateFormat.format(date) + ".json");						
					}
				}else{
					if (callingActivity==ActivityConstants.WORK_OVERVIEW){
						file = new File(sdCard.getAbsolutePath() + "/MoFaBackend/export", "worklist" + dateFormat.format(date) + ".xml");
					}
					if (callingActivity==ActivityConstants.PURCHASING_ACTIVITY){
						file = new File(sdCard.getAbsolutePath() + "/MoFaBackend/export", "purchaselist" + dateFormat.format(date) + ".xml");
					}
				}
				
				byte[] bData = data.getBytes();
				try {
					fos = new FileOutputStream(file);
				    fos.write(bData);
				    fos.flush();
				    fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					error=true;
				}
			}else{
				error=true;
			}
				
		}
		private boolean isSdPresent(){
			return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		}
		private String createXML(){
			//List<Work> workUploadList = DatabaseManager.getInstance().getAllWorks();
			List<Work> workUploadList = DatabaseManager.getInstance().getAllValidNotSendedWorksExcel();
			 XmlSerializer serializer = Xml.newSerializer();
			 StringWriter writer = new StringWriter();
			 try {
			        serializer.setOutput(writer);
			        serializer.startDocument("UTF-8", true);
			        serializer.startTag("", "works");
			        serializer.attribute("", "number", String.valueOf(workUploadList.size()));
			        for (Work wk: workUploadList){
			            serializer.startTag("", "work");
			            serializer.startTag("", "date");
			            SimpleDateFormat sdf = new SimpleDateFormat();
			            sdf.applyPattern("yyyy-MM-dd'T'hh:mm:sss'Z'");
			            serializer.text(sdf.format(wk.getDate()));
			            serializer.endTag("", "date");
			            serializer.startTag("", "task");
			            serializer.text(wk.getTask().getId().toString());
			            serializer.endTag("", "task");
                        serializer.startTag("", "type");
                        if (wk.getTask().getType()==null){
                            serializer.text("O");
                        }else{
                            serializer.text(wk.getTask().getType().toString());
                        }
                        ;
                        serializer.endTag("", "type");
			            serializer.startTag("", "note");
			            serializer.text(wk.getNote());
			            serializer.endTag("", "note");
			            List<WorkVQuarter> vquarters = DatabaseManager.getInstance().getVQuarterByWorkIdOrderedByVq(wk.getId());
						for (WorkVQuarter vq : vquarters){
							serializer.startTag("", "vquarter");
							serializer.startTag("", "vqid");
				            serializer.text(vq.getVquarter().getId().toString());
				            serializer.endTag("", "vqid");
							serializer.endTag("", "vquarter");
						}
						List<WorkWorker> workers = DatabaseManager.getInstance().getWorkWorkerByWorkId(wk.getId());
						for (WorkWorker w : workers){
							serializer.startTag("", "worker");
							serializer.startTag("", "workerid");
				            serializer.text(w.getWorker().getId().toString());
				            serializer.endTag("", "workerid");
				            serializer.startTag("", "workerhours");
				            serializer.text(w.getHours().toString());
				            serializer.endTag("", "workerhours");
							serializer.endTag("", "worker");
						}
						List<WorkMachine> machines = DatabaseManager.getInstance().getWorkMachineByWorkId(wk.getId());
						for (WorkMachine m : machines){
							serializer.startTag("", "machine");
							serializer.startTag("", "machineid");
				            serializer.text(m.getMachine().getId().toString());
				            serializer.endTag("", "machineid");
				            serializer.startTag("", "machinehours");
				            serializer.text(m.getHours().toString());
				            serializer.endTag("", "machinehours");
							serializer.endTag("", "machine");
						}
						List<WorkFertilizer> soilfertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(wk.getId());
						for (WorkFertilizer wf : soilfertilizers){
							serializer.startTag("", "soilfertilizer");
							serializer.startTag("", "soilfertilizerid");
							serializer.text(wf.getSoilFertilizer().getId().toString());
							serializer.endTag("","soilfertilizerid");
							serializer.startTag("", "soilfertamount");
							serializer.text(wf.getAmount().toString());
							serializer.endTag("","soilfertamount");
							serializer.endTag("","soilfertilizer");
						}
						List<Spraying> spraying = DatabaseManager.getInstance().getSprayingByWorkId(wk.getId());
						for (Spraying s : spraying){
							serializer.startTag("", "spraying");
							serializer.startTag("", "concentration");
				            serializer.text(s.getConcentration().toString());
				            serializer.endTag("", "concentration");
				            serializer.startTag("", "wateramount");
				            serializer.text(s.getWateramount().toString());
				            serializer.endTag("", "wateramount");
                            serializer.startTag("", "weather");
                            String weatherStr = getWeatherString(s.getWeather());
                            serializer.text(weatherStr);
                            serializer.endTag("", "weather");
				            List<SprayPesticide> sprayPest = DatabaseManager.getInstance().getSprayPesticideBySprayId(s.getId());
							for (SprayPesticide sp : sprayPest){
								serializer.startTag("", "Pesticide");
								serializer.startTag("", "pestid");
					            serializer.text(sp.getPesticide().getId().toString());
					            serializer.endTag("", "pestid");
					            serializer.startTag("", "dose");
					            serializer.text(sp.getDose().toString());
					            serializer.endTag("", "dose");
					            serializer.startTag("", "amount");
					            serializer.text(sp.getDose_amount().toString());
					            serializer.endTag("", "amount");
								serializer.endTag("", "Pesticide");
							}
							List<SprayFertilizer>sprayFert = DatabaseManager.getInstance().getSprayFertilizerBySprayId(s.getId());
							for (SprayFertilizer sf : sprayFert){
								serializer.startTag("", "Fertilizer");
								serializer.startTag("", "fertid");
					            serializer.text(sf.getFertilizer().getId().toString());
					            serializer.endTag("", "fertid");
					            serializer.startTag("", "dose");
					            serializer.text(sf.getDose().toString());
					            serializer.endTag("", "dose");
					            serializer.startTag("", "amount");
					            serializer.text(sf.getDose_amount().toString());
					            serializer.endTag("", "amount");
								serializer.endTag("", "Fertilizer");
							}
							serializer.endTag("", "spraying");
						}
                        List<Harvest> harvest = DatabaseManager.getInstance().getHarvestListbyWorkId(wk.getId());
                        for (Harvest h:harvest){
                            serializer.startTag("", "harvest");
                                serializer.startTag("", "Datum");
                                    serializer.text(sdf.format(h.getDate()));
                                serializer.endTag("", "Datum");
                                serializer.startTag("", "LieferscheinNummer");
                                    serializer.text(h.getId().toString());
                                serializer.endTag("", "LieferscheinNummer");
                                serializer.startTag("","Menge");
                                    serializer.text(h.getAmount().toString());
                                serializer.endTag("","Menge");
                                serializer.startTag("","Durchgang");
                                    serializer.text(h.getPass().toString());
                                serializer.endTag("","Durchgang");
                                serializer.startTag("","Kategorie");
                                    serializer.text(h.getFruitQuality().getQuality());
                                serializer.endTag("","Kategorie");
                                serializer.startTag("","Kisten");
                                    serializer.text(h.getBoxes().toString());
                                serializer.endTag("","Kisten");
                                if(h.getSugar()!=null){
                                    serializer.startTag("","Zucker");
                                        serializer.text(h.getSugar().toString());
                                    serializer.endTag("","Zucker");
                                }
                                if(h.getAcid()!=null){
                                    serializer.startTag("","Saeure");
                                        serializer.text(h.getAcid().toString());
                                    serializer.endTag("","Saeure");
                                }
                                if(h.getPhenol()!=null){
                                    serializer.startTag("","Phenole");
                                        serializer.text(h.getPhenol().toString());
                                    serializer.endTag("","Phenole");
                                }
                                if(h.getPhValue()!=null){
                                    serializer.startTag("","PHWert");
                                        serializer.text(h.getPhValue().toString());
                                    serializer.endTag("","PHWert");
                                }
                                if(h.getNote()!=null){
                                    serializer.startTag("","Notiz");
                                        serializer.text(h.getNote());
                                    serializer.endTag("","Notiz");
                                }
                            serializer.endTag("", "harvest");

                        }
						List<Global> irrigation = DatabaseManager.getInstance().getGlobalbyWorkId(wk.getId());
						for (Global irr: irrigation) {
							serializer.startTag("","irrigation");
							try {
								JSONObject jsonObj = new JSONObject(irr.getData());
								if (jsonObj.has("irrduration")){
									serializer.startTag("", "irrduration");
									Double irrDuration = (Util.getJSONDouble(jsonObj,"irrduration"));
									serializer.text(irrDuration.toString());
									serializer.endTag("", "irrduration");
								}
								if (jsonObj.has("irramount")) {
									serializer.startTag("", "irramount");
									Double irrAmount = Util.getJSONDouble(jsonObj,"irramount");
									serializer.text(irrAmount.toString());
									serializer.endTag("", "irramount");
								}
								if (jsonObj.has("irrtotale")) {
									serializer.startTag("", "irrtotale");
									Double irrAmountRelative = Util.getJSONDouble(jsonObj,"irrtotale");
									serializer.text(irrAmountRelative.toString());
									serializer.endTag("", "irrtotale");

								}

								if (jsonObj.has("irrtype")){
									serializer.startTag("", "irrtype");
									Integer irrigationType = Util.getJSONInt(jsonObj,"irrtype");
									serializer.text(irrigationType.toString());
									serializer.endTag("", "irrtype");

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}



							serializer.endTag("","irrigation");

						}
			        serializer.endTag("", "work");
			        }
			        serializer.endTag("", "works");
			        serializer.endDocument();
			        return writer.toString();
			    } catch (Exception e) {
			        throw new RuntimeException(e);
			    } 
		}
	private String createXMLASA(){
		//List<Work> workUploadList = DatabaseManager.getInstance().getAllWorks();
		List<Work> workUploadList = DatabaseManager.getInstance().getAllValidNotSendedWorks();
		 XmlSerializer serializer = Xml.newSerializer();
		 StringWriter writer = new StringWriter();
		 try {
		        serializer.setOutput(writer);
		        serializer.startDocument("UTF-8", true);
		        serializer.startTag("", "Arbeitseintraege");
		        //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
		        for (Work wk: workUploadList){
		            serializer.startTag("", "Arbeitseintrag");
		            serializer.startTag("", "Datum");
		            SimpleDateFormat sdf = new SimpleDateFormat();
		            sdf.applyPattern("yyyy-MM-dd");
		            serializer.text(sdf.format(wk.getDate()));
		            serializer.endTag("", "Datum");
		            serializer.startTag("", "Arbeit");
		            	serializer.startTag("", "Code");
		            		serializer.text(wk.getTask().getCode());
		            	serializer.endTag("", "Code");
		            serializer.endTag("", "Arbeit");
		            
		            serializer.startTag("", "Notiz");
						String note;
						if (mofaNote) {
							note = ASANOTE + " " + wk.getNote();
						}else {
							note = wk.getNote();
						}
					serializer.text(note);
		            serializer.endTag("", "Notiz");
		            List<WorkWorker> workers = DatabaseManager.getInstance().getWorkWorkerByWorkId(wk.getId());
		              	
						for (WorkWorker w : workers){
							serializer.startTag("", "Arbeitskraft");
								serializer.startTag("", "Arbeitskraft");
									serializer.startTag("", "Code");
										Worker worker = DatabaseManager.getInstance().getWorkerWithId(w.getWorker().getId());
										serializer.text(worker.getCode());
									serializer.endTag("", "Code");
								serializer.endTag("", "Arbeitskraft");
									serializer.startTag("", "Stunden");
										serializer.text(w.getHours().toString());
									serializer.endTag("", "Stunden");
								
							serializer.endTag("", "Arbeitskraft");
						}
						
		            
		            
					List<WorkMachine> machines = DatabaseManager.getInstance().getWorkMachineByWorkId(wk.getId());
					
					for (WorkMachine m : machines){
						serializer.startTag("", "Maschine");
							serializer.startTag("", "Maschine");
								serializer.startTag("", "Code");
									Machine machine = DatabaseManager.getInstance().getMachineWithId(m.getMachine().getId());
									serializer.text(machine.getCode());
								serializer.endTag("", "Code");
							serializer.endTag("", "Maschine");	
							serializer.startTag("", "Stunden");
								serializer.text(m.getHours().toString());
							serializer.endTag("", "Stunden");
						serializer.endTag("", "Maschine");
					}
					
		            List<WorkVQuarter> vquarters = DatabaseManager.getInstance().getVQuarterByWorkId(wk.getId());
		            
					for (WorkVQuarter vq : vquarters){
						serializer.startTag("", "Sortenquartier");
							serializer.startTag("", "Sortenquartier");
								serializer.startTag("", "Code");
									VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
									serializer.text(vquarter.getCode());
								serializer.endTag("", "Code");
							serializer.endTag("", "Sortenquartier");
						serializer.endTag("", "Sortenquartier");
					}
					
					
					List<WorkFertilizer> soilfertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(wk.getId());
					for (WorkFertilizer wf : soilfertilizers){
						serializer.startTag("", "Duengung");
						for (WorkVQuarter vq : vquarters){
							serializer.startTag("", "Sortenquartier");
								serializer.startTag("", "Sortenquartier");
									serializer.startTag("", "Code");
										VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
										serializer.text(vquarter.getCode());
									serializer.endTag("", "Code");
								serializer.endTag("", "Sortenquartier");
							serializer.endTag("", "Sortenquartier");
						}
						serializer.startTag("", "Duengemittel");
							serializer.startTag("", "Artikel");
								serializer.startTag("", "Code");
									SoilFertilizer sf = DatabaseManager.getInstance().getSoilFertilizerWithId(wf.getSoilFertilizer().getId());
									serializer.text(sf.getCode());
								serializer.endTag("", "Code");
							serializer.endTag("", "Artikel");
							serializer.startTag("", "Menge");
		                		serializer.text(wf.getAmount().toString());
		                	serializer.endTag("", "Menge");
						serializer.endTag("","Duengemittel");
						
						serializer.endTag("","Duengung");
					}
					List<Spraying> spraying = DatabaseManager.getInstance().getSprayingByWorkId(wk.getId());
					for (Spraying s : spraying){
						String test = wk.getTask().getType();
						
						if (wk.getTask().getType().equalsIgnoreCase("H")){ //herbicide
							serializer.startTag("", "Herbizideinsatz");
						}else{
							serializer.startTag("", "Spritzung");
						}
						
						serializer.startTag("", "Konzentration");
			            serializer.text(s.getConcentration().toString());
			            serializer.endTag("", "Konzentration");
			            serializer.startTag("", "Wassermenge");
			            serializer.text(s.getWateramount().toString());
			            serializer.endTag("", "Wassermenge");
                        serializer.startTag("", "Notiz");
                        String weatherStr = getWeatherString(s.getWeather());
                        serializer.text(weatherStr);
                        serializer.endTag("", "Notiz");
			            for (WorkVQuarter vq : vquarters){
							serializer.startTag("", "Sortenquartier");
								serializer.startTag("", "Sortenquartier");
									serializer.startTag("", "Code");
										VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
										serializer.text(vquarter.getCode());
									serializer.endTag("", "Code");
								serializer.endTag("", "Sortenquartier");
							serializer.endTag("", "Sortenquartier");
						}
			            List<SprayPesticide> sprayPest = DatabaseManager.getInstance().getSprayPesticideBySprayId(s.getId());
						for (SprayPesticide sp : sprayPest){
							serializer.startTag("", "Spritzmittel");
								serializer.startTag("", "Artikel");
									serializer.startTag("", "Code");
										Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(sp.getPesticide().getId());
										serializer.text(pest.getCode());
				                    serializer.endTag("", "Code");
				                serializer.endTag("", "Artikel");
				                serializer.startTag("", "Menge");
				                	serializer.text(sp.getDose_amount().toString());
				                serializer.endTag("", "Menge");
				                serializer.startTag("", "MengeProHl1Mal");
			                		serializer.text(sp.getDose().toString());
			                	serializer.endTag("", "MengeProHl1Mal");
				            serializer.endTag("", "Spritzmittel");
						}
						List<SprayFertilizer>sprayFert = DatabaseManager.getInstance().getSprayFertilizerBySprayId(s.getId());
						for (SprayFertilizer sf : sprayFert){
							serializer.startTag("", "Blattduenger");
								serializer.startTag("", "Artikel");
									serializer.startTag("", "Code");
										Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(sf.getFertilizer().getId());
										serializer.text(fert.getCode());
									serializer.endTag("", "Code");
								serializer.endTag("", "Artikel");
							serializer.startTag("","Menge");
				            	serializer.text(sf.getDose_amount().toString());
				            serializer.endTag("","Menge");
							serializer.endTag("", "Blattduenger");
						}
						if (wk.getTask().getType().equalsIgnoreCase("H")){
							serializer.endTag("", "Herbizideinsatz");
						}else{
							serializer.endTag("", "Spritzung");
						}
					}
					List<Harvest> harvest = DatabaseManager.getInstance().getHarvestListbyWorkId(wk.getId());
					for (Harvest h:harvest) {
						serializer.startTag("", "Ernteeintrag");
						serializer.startTag("", "Datum");
						serializer.text(sdf.format(h.getDate()));
						serializer.endTag("", "Datum");
						serializer.startTag("", "LieferscheinNummer");
						serializer.text(h.getId().toString());
						serializer.endTag("", "LieferscheinNummer");
						serializer.startTag("", "Menge");
						serializer.text(h.getAmount().toString());
						serializer.endTag("", "Menge");
						serializer.startTag("", "Durchgang");
						serializer.text(h.getPass().toString());
						serializer.endTag("", "Durchgang");
						serializer.startTag("", "Kategorie");
						serializer.startTag("", "Code");
						serializer.text(h.getFruitQuality().getCode());
						serializer.endTag("", "Code");
						serializer.endTag("", "Kategorie");
						serializer.startTag("", "Kisten");
						serializer.text(h.getBoxes().toString());
						serializer.endTag("", "Kisten");
						if (h.getSugar() != null) {
							serializer.startTag("", "Zucker");
							serializer.text(h.getSugar().toString());
							serializer.endTag("", "Zucker");
						}
						if (h.getAcid() != null) {
							serializer.startTag("", "Saeure");
							serializer.text(h.getAcid().toString());
							serializer.endTag("", "Saeure");
						}
						if (h.getPhenol() != null) {
							serializer.startTag("", "Phenole");
							serializer.text(h.getPhenol().toString());
							serializer.endTag("", "Phenole");
						}
						if (h.getPhValue() != null) {
							serializer.startTag("", "PHWert");
							serializer.text(h.getPhValue().toString());
							serializer.endTag("", "PHWert");
						}
						if (h.getNote() != null) {
							serializer.startTag("", "Notiz");
							serializer.text(h.getNote());
							serializer.endTag("", "Notiz");
						}
						for (WorkVQuarter vq : vquarters) {
							serializer.startTag("", "Sortenquartier");
							serializer.startTag("", "Sortenquartier");
							serializer.startTag("", "Code");
							VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
							serializer.text(vquarter.getCode());
							serializer.endTag("", "Code");
							serializer.endTag("", "Sortenquartier");
							serializer.endTag("", "Sortenquartier");
						}
						serializer.endTag("", "Ernteeintrag");
					}
						List<Global> irrigation = DatabaseManager.getInstance().getGlobalbyWorkId(wk.getId());
						for (Global irr: irrigation) {
							serializer.startTag("","Bewaesserung");
							try {
								JSONObject jsonObj = new JSONObject(irr.getData());
																if (jsonObj.has("irrduration")){
									serializer.startTag("", "Dauer");
										Double irrDuration = (Util.getJSONDouble(jsonObj,"irrduration"));
										serializer.text(irrDuration.toString());
									serializer.endTag("", "Dauer");
								}
								if (jsonObj.has("irramount")) {
									serializer.startTag("", "MengeProStunde");
										 Double irrAmount = Util.getJSONDouble(jsonObj,"irramount");
										serializer.text(irrAmount.toString());
									serializer.endTag("", "MengeProStunde");
								}
								if (jsonObj.has("irrtotale")) {
									serializer.startTag("", "MengeRelativ");
									Double irrAmountRelative = Util.getJSONDouble(jsonObj,"irrtotale");
									serializer.text(irrAmountRelative.toString());
									serializer.endTag("", "MengeRelativ");

								}

								if (jsonObj.has("irrtype")){
									serializer.startTag("", "Art");
										Integer irrigationType = Util.getJSONInt(jsonObj,"irrtype");
										serializer.text(irrigationType.toString());
									serializer.endTag("", "Art");

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							for (WorkVQuarter vq : vquarters){
								serializer.startTag("", "Sortenquartier");
								serializer.startTag("", "Sortenquartier");
								serializer.startTag("", "Code");
								VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
								serializer.text(vquarter.getCode());
								serializer.endTag("", "Code");
								serializer.endTag("", "Sortenquartier");
								serializer.endTag("", "Sortenquartier");
							}


							serializer.endTag("","Bewaesserung");

						}
		        serializer.endTag("", "Arbeitseintrag");
		        }
		        serializer.endTag("", "Arbeitseintraege");
		        serializer.endDocument();
		        return writer.toString();
		    } catch (Exception e) {
		        throw new RuntimeException(e);
		    } 
	}
	private String createXMLASAVer16(){
		//List<Work> workUploadList = DatabaseManager.getInstance().getAllWorks();
		List<Work> workUploadList = DatabaseManager.getInstance().getAllValidNotSendedWorks();
		HashMap<String,String> reasonMap = Einsatzgrund.getEinsatzGrundHashMap();
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "Arbeitseintraege");
			//serializer.attribute("", "number", String.valueOf(workUploadList.size()));
			for (Work wk: workUploadList){
				serializer.startTag("", "Arbeitseintrag");
				serializer.startTag("", "Datum");
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("yyyy-MM-dd");
				serializer.text(sdf.format(wk.getDate()));
				serializer.endTag("", "Datum");
				serializer.startTag("", "Arbeit");
				serializer.startTag("", "Code");
				serializer.text(wk.getTask().getCode());
				serializer.endTag("", "Code");
				serializer.endTag("", "Arbeit");

				serializer.startTag("", "Notiz");
				String note;
				if (mofaNote) {
					note = ASANOTE + " " + wk.getNote();
				}else {
					note = wk.getNote();
				}
				serializer.text(note);
				serializer.endTag("", "Notiz");
				List<WorkWorker> workers = DatabaseManager.getInstance().getWorkWorkerByWorkId(wk.getId());

				for (WorkWorker w : workers){
					serializer.startTag("", "Arbeitskraft");
					serializer.startTag("", "Arbeitskraft");
					serializer.startTag("", "Code");
					Worker worker = DatabaseManager.getInstance().getWorkerWithId(w.getWorker().getId());
					serializer.text(worker.getCode());
					serializer.endTag("", "Code");
					serializer.endTag("", "Arbeitskraft");
					serializer.startTag("", "Stunden");
					serializer.text(w.getHours().toString());
					serializer.endTag("", "Stunden");

					serializer.endTag("", "Arbeitskraft");
				}



				List<WorkMachine> machines = DatabaseManager.getInstance().getWorkMachineByWorkId(wk.getId());

				for (WorkMachine m : machines){
					serializer.startTag("", "Maschine");
					serializer.startTag("", "Maschine");
					serializer.startTag("", "Code");
					Machine machine = DatabaseManager.getInstance().getMachineWithId(m.getMachine().getId());
					serializer.text(machine.getCode());
					serializer.endTag("", "Code");
					serializer.endTag("", "Maschine");
					serializer.startTag("", "Stunden");
					serializer.text(m.getHours().toString());
					serializer.endTag("", "Stunden");
					serializer.endTag("", "Maschine");
				}

				List<WorkVQuarter> vquarters = DatabaseManager.getInstance().getVQuarterByWorkId(wk.getId());

				for (WorkVQuarter vq : vquarters){
					serializer.startTag("", "Sortenquartier");
					serializer.startTag("", "Sortenquartier");
					serializer.startTag("", "Code");
					VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
					serializer.text(vquarter.getCode());
					serializer.endTag("", "Code");
					serializer.endTag("", "Sortenquartier");
					serializer.endTag("", "Sortenquartier");
				}


				List<WorkFertilizer> soilfertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(wk.getId());
				for (WorkFertilizer wf : soilfertilizers){
					serializer.startTag("", "Duengung");
					for (WorkVQuarter vq : vquarters){
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Code");
						VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
						serializer.text(vquarter.getCode());
						serializer.endTag("", "Code");
						serializer.endTag("", "Sortenquartier");
						serializer.endTag("", "Sortenquartier");
					}
					serializer.startTag("", "Duengemittel");
					serializer.startTag("", "Artikel");
					serializer.startTag("", "Code");
					SoilFertilizer sf = DatabaseManager.getInstance().getSoilFertilizerWithId(wf.getSoilFertilizer().getId());
					serializer.text(sf.getCode());
					serializer.endTag("", "Code");
					serializer.endTag("", "Artikel");
					serializer.startTag("", "Menge");
					serializer.text(wf.getAmount().toString());
					serializer.endTag("", "Menge");
					serializer.endTag("","Duengemittel");

					serializer.endTag("","Duengung");
				}
				List<Spraying> spraying = DatabaseManager.getInstance().getSprayingByWorkId(wk.getId());
				for (Spraying s : spraying){
					serializer.startTag("","Witterungsverhaeltnis");
					serializer.startTag("","Code");
						String weatherCode = getWeatherCodeForASA(s.getWeather());
						serializer.text(weatherCode);
					serializer.endTag("","Code");
					serializer.endTag("","Witterungsverhaeltnis");

					if (wk.getTask().getType().equalsIgnoreCase("H")){ //herbicide
						serializer.startTag("", "Herbizideinsatz");
					}else{
						serializer.startTag("", "Spritzung");
					}

					serializer.startTag("", "Konzentration");
					serializer.text(s.getConcentration().toString());
					serializer.endTag("", "Konzentration");
					serializer.startTag("", "Wassermenge");
					serializer.text(s.getWateramount().toString());
					serializer.endTag("", "Wassermenge");
					serializer.startTag("", "Notiz");
					String weatherStr = getWeatherString(s.getWeather());
					serializer.text(weatherStr);
					serializer.endTag("", "Notiz");
					for (WorkVQuarter vq : vquarters){
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Code");
						VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
						serializer.text(vquarter.getCode());
						serializer.endTag("", "Code");
						serializer.endTag("", "Sortenquartier");
						serializer.endTag("", "Sortenquartier");
					}
					List<SprayPesticide> sprayPest = DatabaseManager.getInstance().getSprayPesticideBySprayId(s.getId());
					for (SprayPesticide sp : sprayPest){
						serializer.startTag("", "Pflanzenschutzmittel");
						serializer.startTag("", "Artikel");
						serializer.startTag("", "Code");
						Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(sp.getPesticide().getId());
						serializer.text(pest.getCode());
						serializer.endTag("", "Code");
						serializer.endTag("", "Artikel");
						serializer.startTag("", "Menge");
						serializer.text(sp.getDose_amount().toString());
						serializer.endTag("", "Menge");
						serializer.startTag("", "MengeProHl1Mal");
						serializer.text(sp.getDose().toString());
						serializer.endTag("", "MengeProHl1Mal");
						serializer.startTag("","Einsatzperiode");
							serializer.startTag("","Code");
								serializer.text(sp.getPeriodCode());
							serializer.endTag("","Code");
						serializer.endTag("","Einsatzperiode");
						serializer.startTag("","EinsatzgruendeAlsString");
						serializer.text(getEinsatzGrundForASA(sp.getReason()));
						serializer.endTag("","EinsatzgruendeAlsString");
						serializer.startTag("","Einsatzgrund");
							serializer.startTag("","Einsatzgrund");
								serializer.startTag("","Code");
									serializer.text(reasonMap.get(getEinsatzGrundForASA(sp.getReason())));
								serializer.endTag("","Code");
							serializer.endTag("","Einsatzgrund");
						serializer.endTag("","Einsatzgrund");
						serializer.endTag("", "Pflanzenschutzmittel");
					}
					List<SprayFertilizer>sprayFert = DatabaseManager.getInstance().getSprayFertilizerBySprayId(s.getId());
					for (SprayFertilizer sf : sprayFert){
						serializer.startTag("", "Blattduenger");
						serializer.startTag("", "Artikel");
						serializer.startTag("", "Code");
						Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(sf.getFertilizer().getId());
						serializer.text(fert.getCode());
						serializer.endTag("", "Code");
						serializer.endTag("", "Artikel");
						serializer.startTag("","Menge");
						serializer.text(sf.getDose_amount().toString());
						serializer.endTag("","Menge");
						serializer.endTag("", "Blattduenger");
					}
					if (wk.getTask().getType().equalsIgnoreCase("H")){
						serializer.endTag("", "Herbizideinsatz");
					}else{
						serializer.endTag("", "Spritzung");
					}
				}
				List<Harvest> harvest = DatabaseManager.getInstance().getHarvestListbyWorkId(wk.getId());
				for (Harvest h:harvest) {
					serializer.startTag("", "Ernteeintrag");
					serializer.startTag("", "Datum");
					serializer.text(sdf.format(h.getDate()));
					serializer.endTag("", "Datum");
					serializer.startTag("", "LieferscheinNummer");
					serializer.text(h.getId().toString());
					serializer.endTag("", "LieferscheinNummer");
					serializer.startTag("", "Menge");
					serializer.text(h.getAmount().toString());
					serializer.endTag("", "Menge");
					serializer.startTag("", "Durchgang");
					serializer.text(h.getPass().toString());
					serializer.endTag("", "Durchgang");
					serializer.startTag("", "Kategorie");
					serializer.startTag("", "Code");
					serializer.text(h.getFruitQuality().getCode());
					serializer.endTag("", "Code");
					serializer.endTag("", "Kategorie");
					serializer.startTag("", "Kisten");
					serializer.text(h.getBoxes().toString());
					serializer.endTag("", "Kisten");
					if (h.getSugar() != null) {
						serializer.startTag("", "Zucker");
						serializer.text(h.getSugar().toString());
						serializer.endTag("", "Zucker");
					}
					if (h.getAcid() != null) {
						serializer.startTag("", "Saeure");
						serializer.text(h.getAcid().toString());
						serializer.endTag("", "Saeure");
					}
					if (h.getPhenol() != null) {
						serializer.startTag("", "Phenole");
						serializer.text(h.getPhenol().toString());
						serializer.endTag("", "Phenole");
					}
					if (h.getPhValue() != null) {
						serializer.startTag("", "PHWert");
						serializer.text(h.getPhValue().toString());
						serializer.endTag("", "PHWert");
					}
					if (h.getNote() != null) {
						serializer.startTag("", "Notiz");
						serializer.text(h.getNote());
						serializer.endTag("", "Notiz");
					}
					for (WorkVQuarter vq : vquarters) {
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Code");
						VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
						serializer.text(vquarter.getCode());
						serializer.endTag("", "Code");
						serializer.endTag("", "Sortenquartier");
						serializer.endTag("", "Sortenquartier");
					}
					serializer.endTag("", "Ernteeintrag");
				}
				List<Global> irrigation = DatabaseManager.getInstance().getGlobalbyWorkId(wk.getId());
				for (Global irr: irrigation) {
					serializer.startTag("","Bewaesserung");
					try {
						JSONObject jsonObj = new JSONObject(irr.getData());
						if (jsonObj.has("irrduration")){
							serializer.startTag("", "Dauer");
							Double irrDuration = (Util.getJSONDouble(jsonObj,"irrduration"));
							serializer.text(irrDuration.toString());
							serializer.endTag("", "Dauer");
						}
						if (jsonObj.has("irramount")) {
							serializer.startTag("", "MengeProStunde");
							Double irrAmount = Util.getJSONDouble(jsonObj,"irramount");
							serializer.text(irrAmount.toString());
							serializer.endTag("", "MengeProStunde");
						}
						if (jsonObj.has("irrtotale")) {
							serializer.startTag("", "MengeRelativ");
							Double irrAmountRelative = Util.getJSONDouble(jsonObj,"irrtotale");
							serializer.text(irrAmountRelative.toString());
							serializer.endTag("", "MengeRelativ");

						}

						if (jsonObj.has("irrtype")){
							serializer.startTag("", "Art");
							Integer irrigationType = Util.getJSONInt(jsonObj,"irrtype");
							serializer.text(irrigationType.toString());
							serializer.endTag("", "Art");

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					for (WorkVQuarter vq : vquarters){
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Sortenquartier");
						serializer.startTag("", "Code");
						VQuarter vquarter = DatabaseManager.getInstance().getVQuarterWithId(vq.getVquarter().getId());
						serializer.text(vquarter.getCode());
						serializer.endTag("", "Code");
						serializer.endTag("", "Sortenquartier");
						serializer.endTag("", "Sortenquartier");
					}


					serializer.endTag("","Bewaesserung");

				}
				serializer.endTag("", "Arbeitseintrag");
			}
			serializer.endTag("", "Arbeitseintraege");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private String createPurchaseXML(){
		List<Purchase> purchaseUploadList = DatabaseManager.getInstance().getAllPurchases();
		 XmlSerializer serializer = Xml.newSerializer();
		 StringWriter writer = new StringWriter();
		 try {
		        serializer.setOutput(writer);
		        serializer.startDocument("UTF-8", true);
		        serializer.startTag("", "purchases");
		        serializer.attribute("", "number", String.valueOf(purchaseUploadList.size()));
		        for (Purchase p: purchaseUploadList){
		            serializer.startTag("", "purchase");
		            serializer.startTag("", "date");
		            SimpleDateFormat sdf = new SimpleDateFormat();
		            sdf.applyPattern("yyyy-MM-dd'T'hh:mm:sss'Z'");
		            serializer.text(sdf.format(p.getDate()));
		            serializer.endTag("", "date");
		            
		            List<PurchasePesticide> purPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseId(p.getId());
					for (PurchasePesticide pp : purPest){
						serializer.startTag("", "pesticide");
						serializer.startTag("", "pestid");
			            serializer.text(pp.getProduct().getId().toString());
			            serializer.endTag("", "pestid");
			            serializer.startTag("", "amount");
			            serializer.text(pp.getAmount().toString());
			            serializer.endTag("", "amount");
						serializer.endTag("", "pesticide");
					}
					List<PurchaseFertilizer> purFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseId(p.getId());
					for (PurchaseFertilizer pf : purFert){
						serializer.startTag("", "fertilizer");
						serializer.startTag("", "fertid");
			            serializer.text(pf.getProduct().getId().toString());
			            serializer.endTag("", "fertid");
			            serializer.startTag("", "amount");
			            serializer.text(pf.getAmount().toString());
			            serializer.endTag("", "amount");
			            serializer.endTag("", "fertilizer");
					}
				serializer.endTag("", "purchase");
		        }
		        serializer.endTag("", "purchases");
		        serializer.endDocument();
		        return writer.toString();
		    } catch (Exception e) {
		        throw new RuntimeException(e);
		    } 
	}
	private String createPurchaseXMLASA(){
		List<Purchase> purchaseUploadList = DatabaseManager.getInstance().getAllPurchases();
		 XmlSerializer serializer = Xml.newSerializer();
		 StringWriter writer = new StringWriter();
		 try {
		        serializer.setOutput(writer);
		        serializer.startDocument("UTF-8", true);
		        serializer.startTag("", "Lagereingaenge");
		        //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
		        for (Purchase p: purchaseUploadList){
		            serializer.startTag("", "Lagereingang");
		            serializer.startTag("", "Datum");
		            SimpleDateFormat sdf = new SimpleDateFormat();
		            sdf.applyPattern("yyyy-MM-dd");
		            serializer.text(sdf.format(p.getDate()));
		            serializer.endTag("", "Datum");
		            serializer.startTag("", "Bruttobetraege");
		            	if (grossPrice){
		            		serializer.text("1");
		            	}else{
		            		serializer.text("0");
		            	}
		            
		            serializer.endTag("", "Bruttobetraege");
		            List<PurchasePesticide> purPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseId(p.getId());
					for (PurchasePesticide pp : purPest){
						double price = getPriceFromJson(pp.getData());
						serializer.startTag("", "Lagereingangsposition");
							serializer.startTag("", "Spritzmittel");
								serializer.startTag("", "Code");
									Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(pp.getProduct().getId());
									serializer.text(pest.getCode());
								serializer.endTag("", "Code");
							serializer.endTag("", "Spritzmittel");
							serializer.startTag("", "Menge");
								serializer.text(pp.getAmount().toString());
							serializer.endTag("", "Menge");
							if (price != 0.00){
								serializer.startTag("", "Preis");
									serializer.text(Double.toString(price));
								serializer.endTag("", "Preis");
							}
						serializer.endTag("", "Lagereingangsposition");
					}
					List<PurchaseFertilizer> purFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseId(p.getId());
					for (PurchaseFertilizer pf : purFert){
						double price = getPriceFromJson(pf.getData());
						serializer.startTag("", "Lagereingangsposition");
						serializer.startTag("", "Duengemittel");
							serializer.startTag("", "Code");
								Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(pf.getProduct().getId());
								serializer.text(fert.getCode());
							serializer.endTag("", "Code");
						serializer.endTag("", "Duengemittel");
						serializer.startTag("", "Menge");
							serializer.text(pf.getAmount().toString());
						serializer.endTag("", "Menge");
						if (price != 0.00){
							serializer.startTag("", "Preis");
							serializer.text(Double.toString(price));
							serializer.endTag("", "Preis");
						}
					serializer.endTag("", "Lagereingangsposition");
					}
		        serializer.endTag("", "Lagereingang");
		        }
		        serializer.endTag("", "Lagereingaenge");
		        serializer.endDocument();
		        return writer.toString();
		    } catch (Exception e) {
		        throw new RuntimeException(e);
		    } 
	}
	private String createPurchaseXMLASAVer16(){
		List<Purchase> purchaseUploadList = DatabaseManager.getInstance().getAllPurchases();

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "Lagereingaenge");
			//serializer.attribute("", "number", String.valueOf(workUploadList.size()));
			for (Purchase p: purchaseUploadList){
				serializer.startTag("", "Lagereingang");
				serializer.startTag("", "Datum");
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("yyyy-MM-dd");
				serializer.text(sdf.format(p.getDate()));
				serializer.endTag("", "Datum");
				serializer.startTag("", "Bruttobetraege");
				if (grossPrice){
					serializer.text("1");
				}else{
					serializer.text("0");
				}

				serializer.endTag("", "Bruttobetraege");
				List<PurchasePesticide> purPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseId(p.getId());
				for (PurchasePesticide pp : purPest){
					double price = getPriceFromJson(pp.getData());
					serializer.startTag("", "Lagereingangsposition");
					serializer.startTag("", "Pflanzenschutzmittel");
					serializer.startTag("", "Code");
					Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(pp.getProduct().getId());
					serializer.text(pest.getCode());
					serializer.endTag("", "Code");
					serializer.endTag("", "Pflanzenschutzmittel");
					serializer.startTag("", "Menge");
					serializer.text(pp.getAmount().toString());
					serializer.endTag("", "Menge");
					if (price != 0.00){
						serializer.startTag("", "Preis");
						serializer.text(Double.toString(price));
						serializer.endTag("", "Preis");
					}
					serializer.endTag("", "Lagereingangsposition");
				}
				List<PurchaseFertilizer> purFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseId(p.getId());
				for (PurchaseFertilizer pf : purFert){
					double price = getPriceFromJson(pf.getData());
					serializer.startTag("", "Lagereingangsposition");
					serializer.startTag("", "Duengemittel");
					serializer.startTag("", "Code");
					Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(pf.getProduct().getId());
					serializer.text(fert.getCode());
					serializer.endTag("", "Code");
					serializer.endTag("", "Duengemittel");
					serializer.startTag("", "Menge");
					serializer.text(pf.getAmount().toString());
					serializer.endTag("", "Menge");
					if (price != 0.00){
						serializer.startTag("", "Preis");
						serializer.text(Double.toString(price));
						serializer.endTag("", "Preis");
					}
					serializer.endTag("", "Lagereingangsposition");
				}
				serializer.endTag("", "Lagereingang");
			}
			serializer.endTag("", "Lagereingaenge");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private String createVegDataXML(){
		HashMap<String,String> blossStart = new HashMap<String,String>();
		HashMap<String,String> blossEnd = new HashMap<String,String>();
		HashMap<String,String> harvStart = new HashMap<String,String>();
		HashMap<String,String> estCrop = new HashMap<String,String>();
		if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).isEmpty()){
			String json = "";
			Global blossomStart = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).get(0);
			json = blossomStart.getData();
			blossStart = getMapFromJson(json);
		};
		if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).isEmpty()){
			String json = "";
			Global blossomEnd = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).get(0);
			json = blossomEnd.getData();
			blossEnd = getMapFromJson(json);
		};
		if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.HARVESTSTART).isEmpty()){
			String json = "";
			Global harvestStart = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.HARVESTSTART).get(0);
			json = harvestStart.getData();
			harvStart = getMapFromJson(json);
		};
		if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.ESTIMCROP).isEmpty()){
			String json = "";
			Global estimCrop = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.ESTIMCROP).get(0);
			json = estimCrop.getData();
			estCrop = getMapFromJson(json);
		};
		List<VQuarter> vquarters = DatabaseManager.getInstance().getAllVQuarters();
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		//Calendar calender = Calendar.getInstance();
		//int curYear = calender.get(Calendar.YEAR);
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "vquarters");
			for (VQuarter vq : vquarters) {
				String blossDateStart = blossStart.get(vq.getId().toString());
				String blossDateEnd = blossEnd.get(vq.getId().toString());
				String harvestDate = harvStart.get(vq.getId().toString());
				String estimCropAmount = estCrop.get(vq.getId().toString());
				if (blossDateStart != null || blossDateEnd != null || harvestDate != null || estimCropAmount != null){
					serializer.startTag("","vquarter");
						serializer.startTag("", "vqid");
							serializer.text(vq.getId().toString());
						serializer.endTag("", "vqid");
						if (blossDateStart != null) {
							serializer.startTag("", "blossomStart");
							serializer.text(blossDateStart);
							serializer.endTag("", "blossomStart");
						}
						if (blossDateEnd != null) {
							serializer.startTag("", "blossomEnd");
							serializer.text(blossDateEnd);
							serializer.endTag("", "blossomEnd");
						}
						if (harvestDate != null) {
							serializer.startTag("", "harvestStart");
							serializer.text(harvestDate);
							serializer.endTag("", "harvestStart");
						}
						if (estimCropAmount!= null) {
							serializer.startTag("", "cropAmount");
							serializer.text(estimCropAmount);
							serializer.endTag("", "cropAmount");
						}
					serializer.endTag("","vquarter");
				}
			}
			serializer.endTag("","vquarters");
			serializer.endDocument();
			return writer.toString();
		}catch (Exception e){
			throw new RuntimeException(e);
		}

	}
	private String createVegDataXMLASA(){
		HashMap<String,String> blossStart = new HashMap<String,String>();
		HashMap<String,String> blossEnd = new HashMap<String,String>();
		HashMap<String,String> harvStart = new HashMap<String,String>();
		HashMap<String,String> estCrop = new HashMap<String,String>();
		if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).isEmpty()){
			String json = "";
			Global blossomStart = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).get(0);
			json = blossomStart.getData();
			blossStart = getMapFromJson(json);
		};
		if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).isEmpty()){
			String json = "";
			Global blossomEnd = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).get(0);
			json = blossomEnd.getData();
			blossEnd = getMapFromJson(json);
		};
		List<VQuarter> vquarters = DatabaseManager.getInstance().getAllVQuarters();
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		Calendar calender = Calendar.getInstance();
		int curYear = calender.get(Calendar.YEAR);
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "Sortenquartiere");
			for (VQuarter vq : vquarters) {
				String blossDateStart = blossStart.get(vq.getId().toString());
				String blossDateEnd = blossEnd.get(vq.getId().toString());
				String harvestDate = harvStart.get(vq.getId().toString());
				String estimCropAmount = estCrop.get(vq.getId().toString());
				if (blossDateStart != null || blossDateEnd != null || harvestDate != null || estimCropAmount != null){
					serializer.startTag("","Sortenquartier");
					serializer.startTag("", "Code");
						serializer.text(vq.getCode());
					serializer.endTag("", "Code");
					serializer.startTag("","Jahresdaten");
					serializer.startTag("","Erntejahr");
						serializer.text(Integer.toString(curYear));
					serializer.endTag("","Erntejahr");
					if (blossDateStart != null) {
						serializer.startTag("", "Bluehbeginn");
						serializer.text(getDateStringForASA(blossDateStart,Integer.toString(curYear)));
						serializer.endTag("", "Bluehbeginn");
					}
					if (blossDateEnd != null) {
						serializer.startTag("", "Bluehende");
						serializer.text(getDateStringForASA(blossDateEnd, Integer.toString(curYear)));
						serializer.endTag("", "Bluehende");
					}
					if (harvestDate != null) {
						serializer.startTag("", "Erntebeginn");
						serializer.text(getDateStringForASA(harvestDate, Integer.toString(curYear)));
						serializer.endTag("", "Erntebeginn");
					}
					if (estimCropAmount != null) {
						serializer.startTag("", "ErnteschaetzungProHa");
						serializer.text(getCropAmountInKilo(estimCropAmount));
						serializer.endTag("", "ErnteschaetzungProHa");
					}
					serializer.endTag("","Jahresdaten");
					serializer.endTag("","Sortenquartier");
				}
			}
			serializer.endTag("","Sortenquartiere");
			serializer.endDocument();
			return writer.toString();
		}catch (Exception e){
			throw new RuntimeException(e);
		}

		
	}
	/**
	 * Dropbox sending process
	 * 
	 */
	private void writeFileToDropBox(String data, String fileType){

		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
		String filePath=null;
		ACCESS_TOKEN = DropboxClient.retrieveAccessToken(context);
		if (fileType.equalsIgnoreCase("1")){
			if (callingActivity==ActivityConstants.WORK_OVERVIEW){
				filePath =  "/MoFaBackend/export/worklist" + dateFormat.format(date) + ".json";
			}
			if (callingActivity==ActivityConstants.PURCHASING_ACTIVITY){
				filePath =  "/MoFaBackend/export/purchaselist" + dateFormat.format(date) + ".json";
			}
			if (callingActivity==ActivityConstants.VEGDATA_ACTIVITY){ // calling this asynch from vegdata
				filePath = "/MoFaBackend/export/vegdata" + dateFormat.format(date) + ".json";
			}
		}else{
			if (callingActivity==ActivityConstants.WORK_OVERVIEW){
				filePath =  "/MoFaBackend/export/worklist" + dateFormat.format(date) + ".xml";
			}
			if (callingActivity==ActivityConstants.PURCHASING_ACTIVITY){
				filePath =  "/MoFaBackend/export/purchaselist" + dateFormat.format(date) + ".xml";
			}
			if (callingActivity==ActivityConstants.VEGDATA_ACTIVITY){ // calling this asynch from vegdata
				filePath = "/MoFaBackend/export/vegdata" + dateFormat.format(date) + ".xml";
			}
		}


	    try {
			InputStream inputStream = new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8")));
			if (ACCESS_TOKEN != null) {
				DbxClientV2 dbxClient = DropboxClient.getClient(ACCESS_TOKEN);
				dbxClient.files().uploadBuilder(filePath)
						.withMode(WriteMode.OVERWRITE)
						.uploadAndFinish(inputStream);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			error=true;
		} catch (DbxException e){
			error = true;
		}

	}
	private String getWeatherString(int weatherId){
		switch(weatherId){
			case ActivityConstants.SUNNY:
				return context.getString(R.string.weathersunny);

			case ActivityConstants.PARTLYCLOUDED:
				return context.getString(R.string.weatherpartlycloudy);

			case ActivityConstants.CLOUDED:
				return context.getString(R.string.weathercloudy);

			case ActivityConstants.LIGHTRAIN:
				return context.getString(R.string.weatherlightrain);

			case ActivityConstants.RAIN:
				return context.getString(R.string.weatherrain);
			case ActivityConstants.MOON:
				return context.getString(R.string.weathernight);
			default:
				return context.getString(R.string.weathersunny);



		}
	}
	//this for new ASA - Version, important is that the weathercodes are number 01,02..05
	private String getWeatherCodeForASA(int weatherId){
		switch(weatherId){
			case ActivityConstants.SUNNY:
				return "01";

			case ActivityConstants.PARTLYCLOUDED:
				return "02";
			case ActivityConstants.CLOUDED:
				return "03";

			case ActivityConstants.LIGHTRAIN:
				return "04";

			case ActivityConstants.RAIN:
				return "05";
			case ActivityConstants.MOON:
				return "01";
			default:
				return "01";



		}
	}
	private HashMap<String,String> getMapFromJson(String json){
		HashMap<String,String> tmpMap;
		Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		Gson gson = new Gson();
		tmpMap =  gson.fromJson(json, type);
		if (tmpMap == null) {
			tmpMap = new HashMap<String,String>();
		}
		return tmpMap;
	}
	private String getDateStringForASA(String dateOrg, String curYear){
		String [] splitted = dateOrg.split("\\.");
		return curYear +"-" + splitted[1] + "-" + splitted[0];
	}
	private String getEinsatzGrundForASA(String einsatzgrund){
		String[] splittedText = einsatzgrund.split(",");
		return splittedText[0];
	}

	private String getCropAmountInKilo(String amount){
		Integer amountInT = Integer.parseInt(amount);
		Integer amountInK = amountInT * 1000;
		return amountInK.toString();
	}
	private double getPriceFromJson(String json){
		try {
			JSONObject object = new JSONObject(json);
			double price = object.getDouble("price");
			return price;
		} catch (JSONException e) {
			return 0.00;
		}
	}
}
