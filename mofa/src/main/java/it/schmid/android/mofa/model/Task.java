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

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Task extends ImportBehavior {
	private static final String TAG = "TaskClass";
	@DatabaseField(id=true)
	@Expose
	private Integer id;
	@DatabaseField
	private String task;
    @DatabaseField
    private String data;
	@DatabaseField
	private String code;
    @DatabaseField
    private String type;
	@ForeignCollectionField
	private ForeignCollection<Work> works;
	private Boolean importError=false;
	
	
 public Task(){
	 
 }
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
public String getTask() {
	return task;
}
public void setTask(String task) {
	this.task = task;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getType() {
        return type;
    }
public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void importMasterData(JSONArray importData){
	Task task;
	try {
		Log.i(TAG,
	          "Number of entries " + importData.length());
		for (int i = 0; i < importData.length(); i++) {
	        JSONObject jsonObject = importData.getJSONObject(i);
	        Log.i(TAG, jsonObject.getString("task") +","+ jsonObject.getInt("id"));
	        task = DatabaseManager.getInstance().getTaskWithId(jsonObject.getInt("id"));
	        if (null!=task) {
	        	task.setTask(jsonObject.getString("task"));
	            DatabaseManager.getInstance().updateTask(task);
            } else {
            	Task t = new Task();
                t.setId(jsonObject.getInt("id"));
            	t.setTask(jsonObject.getString("task"));
                DatabaseManager.getInstance().addTask(t);
            }
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
}
public void setItems(ForeignCollection<Work> works) {
    this.works = works;
}

public List<Work> getWorks() {
    ArrayList<Work> workList = new ArrayList<Work>();
    for (Work work : works) {
        workList.add(work);
    }
    return workList;
}
@Override
public boolean equals(Object obj) {
    //null instanceof Object will always return false
    if (!(obj instanceof Task))
      return false;
    if (obj == this)
      return true;
    return this.task.equalsIgnoreCase(((Task) obj).task);
  }
@Override
  public int hashCode() {
    int result = 0;
    result = (int)(id/12) + 5;
    return result;
  }
@Override
public Boolean importMasterData(String xmlString, NotificationService notification) {
	String backEndSoftware;
	List<Task> importData ;
	MofaApplication app = MofaApplication.getInstance();
    backEndSoftware = app.getBackendSoftware();
    switch (Integer.parseInt(backEndSoftware)) {
    case 1: //ASA
   	 Log.d ("TAG", "BackendSoftware: ASAAGRAR");
   	 	importData = taskXmlParserASA(xmlString,notification); 
        break;
    default: //default
   	 Log.d ("TAG", "BackendSoftware:Default");
   	 importData = taskXmlParser(xmlString,notification);
        break;
    
    }
	
	
    for(Task t:importData){
    	Task task= DatabaseManager.getInstance().getTaskWithId(t.getId());
    	if (task!=null) {
        	task.setTask(t.getTask());
            task.setType(t.getType());
            DatabaseManager.getInstance().updateTask(task);
        } else
        {
        	
        	Task newTask = new Task();
            newTask.setId(t.getId());
        	newTask.setTask(t.getTask());
            newTask.setType(t.getType());
            DatabaseManager.getInstance().addTask(t);
        }
    }
    return importError;
}
private List<Task> taskXmlParser(String inputData, NotificationService notification)	{
	List<Task> mTaskList=null ;
	String xTaskName = null;
    String xType=null;
	Integer xId = null;
	try {
        //For String source
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(inputData)); 
        
        int eventType = xpp.getEventType();
        Task currentTask = null;
    
        while (eventType != XmlPullParser.END_DOCUMENT ){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	mTaskList = new ArrayList<Task>();
                    break;
                case XmlPullParser.START_TAG:
                    name = xpp.getName();
                    if (name.equalsIgnoreCase("task")){
                    	currentTask = new Task();
                     
                    } else if (currentTask != null){
                    	if (name.equalsIgnoreCase("id")&& !(xpp.isEmptyElementTag())){
                        	xId = Integer.parseInt(xpp.nextText());
                        	currentTask.setId(xId);
                                       	                        	
                        }
                        if (name.equalsIgnoreCase("desc")){
                        	xTaskName = xpp.nextText();
                        	currentTask.setTask(xTaskName);
                        }
                        if (name.equalsIgnoreCase("type")){
                            xType = xpp.nextText();
                            currentTask.setType(xType);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                	name = xpp.getName();
                    if (name.equalsIgnoreCase("task") && currentTask!= null){
                    	Log.d(TAG,"[XMLParserTask] adding task: " + currentTask.getId() + " " + currentTask.getTask());
                        mTaskList.add(currentTask);
                    } 
                   
                }
            eventType = xpp.next();
            }
          

    } catch (XmlPullParserException e) {
    	  importError = true;
    	  CharSequence tickerText = "Task";  
    	  notification.completed(android.R.drawable.stat_sys_download_done, tickerText,"Parser Error");
       //   e.printStackTrace();
    } catch (IOException e) {
    	  importError = true;
        //  e.printStackTrace();
    }
	return (mTaskList);
}
private List<Task> taskXmlParserASA(String inputData, NotificationService notification)	{
	List<Task> mTaskList=null ;
	Integer xId;
    String xArt = null;
	String xCode="";
	String xTaskName = null;
	Boolean firstCode = false; //due the fact that ASA uses the Code Tag in different nodes
	Boolean firstDesc = true;
	Log.d(TAG,"[XMLParserTaskASA] getting data: " +inputData);
	try {
        //For String source
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(inputData)); 
        
        int eventType = xpp.getEventType();
        Task currentTask = null;
    
        while (eventType != XmlPullParser.END_DOCUMENT ){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	mTaskList = new ArrayList<Task>();
                    break;
                case XmlPullParser.START_TAG:
                    name = xpp.getName();
                    if (name.equalsIgnoreCase("Arbeit")){
                    	currentTask = new Task();
                     
                    } else if (currentTask != null){
                    	if (name.equalsIgnoreCase("ID")){
                    		xId=Integer.parseInt(xpp.nextText());
                    		currentTask.setId(xId);
                    	}
                        if (name.equalsIgnoreCase("Art")){
                            xArt = xpp.nextText();
                            currentTask.setType(xArt);
                        }
                    	if ((name.equalsIgnoreCase("Code"))&&(firstCode==false)){
                    		xCode = xpp.nextText();
                    		currentTask.setCode(xCode); //in ASA Code is the primary key
                           // currentTask.setId(xId);    //setting the primary key
                          //  xId++;
                            firstCode=true;
                           }
                        if (name.equalsIgnoreCase("Name") && (firstDesc)){
                        	xTaskName = xpp.nextText();;
                        	currentTask.setTask(xTaskName);
                        	firstDesc=false;
                        	
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                	name = xpp.getName();
                    if (name.equalsIgnoreCase("Arbeit") && currentTask!= null){
                    	Log.d(TAG,"[XMLParserTaskASA] adding task: " + currentTask.getId() + " " + currentTask.getTask());
                        mTaskList.add(currentTask);
                        firstCode=false;
                        firstDesc=true; //there are more than one task description in the multilanguage environment, we need only the first one
                    } 
                   
                }
            eventType = xpp.next();
            }
          

    } catch (XmlPullParserException e) {
    	  importError = true;
    	  CharSequence tickerText = "Task";  
    	  notification.completed(android.R.drawable.stat_sys_download_done, tickerText,"Parser Error");
       //   e.printStackTrace();
    } catch (IOException e) {
    	  importError = true;
        //  e.printStackTrace();
    }
	return (mTaskList);
}


}
