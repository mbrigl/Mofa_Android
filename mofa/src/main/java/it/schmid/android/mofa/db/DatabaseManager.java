package it.schmid.android.mofa.db;

import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.FruitQuality;
import it.schmid.android.mofa.model.Harvest;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.Purchase;
import it.schmid.android.mofa.model.PurchaseFertilizer;
import it.schmid.android.mofa.model.PurchasePesticide;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkFertilizer;
import it.schmid.android.mofa.model.WorkMachine;
import it.schmid.android.mofa.model.WorkVQuarter;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

public class DatabaseManager {
	private PreparedQuery<VQuarter> vqForWorkQuery = null;
	private PreparedQuery<Worker> workerForWorkQuery = null;
	private PreparedQuery<Machine> machineForWorkQuery = null;
	private PreparedQuery<SoilFertilizer> fertilizerForWorkQuery = null;
	private PreparedQuery<Pesticide> pesticideForSprayQuery = null;
	private PreparedQuery<Fertilizer> fertilizerForSprayQuery = null;
	private PreparedQuery<Pesticide>pesticideForPurchaseQuery = null;
	private PreparedQuery<Fertilizer>fertilizerForPurchaseQuery=null;
	static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null==instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseHelper helper;
    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }
    /*
     * helper db queries
     */
    public Integer getDbVersion(){
    	return getHelper().getReadableDatabase().getVersion();
    }
    public Boolean checkIfEmpty(){
    	Boolean isEmpty = false;
    	try {
			isEmpty = ((getHelper().getVquarterDao().countOf()==0)||(getHelper().getTaskDao().countOf()==0)||(getHelper().getWorkerDao().countOf()==0));
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    	return isEmpty;
    }
    /**counting queries for spraying
     * 
     */
    public Boolean sprayIsEmpty(Integer workid){
    	Boolean isEmpty = false;
    	Integer sprayId=0;
    	if (getSprayingByWorkId(workid).size()!=0){
    		sprayId = getSprayingByWorkId(workid).get(0).getId();
    		 }else{ //there is no spray, we can return immediately
    			 return true;
    		 }
    	
    	try {
			isEmpty= getHelper().getSprayPesticideDao().queryForEq(SprayPesticide.SPRAY_ID_FIELD_NAME, sprayId).isEmpty() &&
			 getHelper().getSprayFertilizerDao().queryForEq(SprayFertilizer.SPRAY_ID_FIELD_NAME, sprayId).isEmpty();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    	 
    	return isEmpty;
    }
    public Boolean soilFertIsEmpty(Integer workid){
    	Boolean isEmpty = false;
    	
    		if (getWorkFertilizerByWorkId(workid).size()==0){
    			isEmpty = true;
    		}
			
    	return isEmpty;
    }
   /***************************************************
    * 
    * LAND - DB Operations
    */
    //Stored - Queries
    public List<Land> getAllLands() {
        List<Land> landList = null;
        try {
            landList = getHelper().getLandDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return landList;
    }
    public List<Land> getAllLandsOrdered() {
        List<Land> landList = null;
        try {
            landList = getHelper().getLandDao().queryBuilder().orderByRaw("name").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return landList;
    }
    // adding,updating Land class
    public void addLand(Land l) {
        try {
            getHelper().getLandDao().create(l);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLand(Land land) {
        try {
            getHelper().getLandDao().update(land);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the land exists by id
    public Land getLandWithId(int landId) {
        Land land = null;
        try {
            land = getHelper().getLandDao().queryForId(landId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return land;
    }
    public List<Land> getLandWithCode(String code) throws SQLException{
    	List<Land> result = getHelper().getLandDao().queryForEq("code", code);
    	return result;
    }
    public void flushLand(){
    	try {
            getHelper().getLandDao().delete(getAllLands());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /******************************************
     * 
     * Machine - DB Operations
     */ 
    //Stored - Queries
    public List<Machine> getAllMachines() {
        List<Machine> machineList = null;
        try {
            machineList = getHelper().getMachineDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machineList;
    }
    // adding,updating Land class
    public void addMachine(Machine m) {
        try {
            getHelper().getMachineDao().create(m);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMachine(Machine machine) {
        try {
            getHelper().getMachineDao().update(machine);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the machine exists by id
    public Machine getMachineWithId(int machineId) {
        Machine machine = null;
        try {
            machine = getHelper().getMachineDao().queryForId(machineId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machine;
    }
    public void flushMachine(){
    	try {
            getHelper().getMachineDao().delete(getAllMachines());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*************************************
     * 
     * Task - DB Operations
     */ 
  //Stored - Queries
    public List<Task> getAllTasks() {
        List<Task> taskList = null;
        try {
            taskList = getHelper().getTaskDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskList;
    }
    public List<Task> getAllTasksOrdered() {
        List<Task> taskList = null;
        try {
            taskList = getHelper().getTaskDao().queryBuilder().orderByRaw("task").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskList;
    }
    // adding,updating Land class
    public void addTask(Task t) {
        try {
            getHelper().getTaskDao().create(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task) {
        try {
            getHelper().getTaskDao().update(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the task exists by id
    public Task getTaskWithId(int taskId) {
        Task task= null;
        try {
            task = getHelper().getTaskDao().queryForId(taskId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }
    public void flushTask(){
    	try {
            getHelper().getTaskDao().delete(getAllTasks());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*************************************
     * 
     * VQuarter - DB Operations
     */ 
  //Stored - Queries
    public List<VQuarter> getAllVQuarters() {
        List<VQuarter> vquarterList = null;
        try {
            vquarterList = getHelper().getVquarterDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vquarterList;
    }
    // adding,updating VQuarter class
    public void addVquarter(VQuarter v) {
        try {
            getHelper().getVquarterDao().create(v);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVQuarter(VQuarter vquarter) {
        try {
            getHelper().getVquarterDao().update(vquarter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the vquarter exists by id
    public VQuarter getVQuarterWithId(int vquarterId) {
        VQuarter vquarter= null;
        try {
            vquarter = getHelper().getVquarterDao().queryForId(vquarterId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vquarter;
    }
    
    public void flushVQuarter(){
    	try {
            getHelper().getVquarterDao().delete(getAllVQuarters());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*************************************
     * 
     * Worker - DB Operations
     */
  //Stored - Queries
    public List<Worker> getAllWorkers() {
        List<Worker> workerList = null;
        try {
            workerList = getHelper().getWorkerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workerList;
    }
    // adding,updating Worker class
    public void addWorker(Worker w) {
        try {
            getHelper().getWorkerDao().create(w);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWorker(Worker worker) {
        try {
            getHelper().getWorkerDao().update(worker);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the worker exists by id
    public Worker getWorkerWithId(int workerId) {
        Worker worker= null;
        try {
            worker = getHelper().getWorkerDao().queryForId(workerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return worker;
    }
    public void flushWorker(){
    	try {
            getHelper().getWorkerDao().delete(getAllWorkers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*************************************
     * 
     * Pesticide - DB Operations
     */
  //Stored - Queries
    public List<Pesticide> getAllPesticides() {
        List<Pesticide> pesticideList = null;
        try {
            pesticideList = getHelper().getPesticideDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pesticideList;
    }
    public List<Pesticide> getAllPesticidesOrderByName(){
    	List<Pesticide> pesticideList=null;
    	try{
    		pesticideList=getHelper().getPesticideDao().queryBuilder().orderByRaw("productName").query();
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return pesticideList;
    }
    // adding,updating Pesticide class
    public void addPesticide(Pesticide p) {
        try {
            getHelper().getPesticideDao().create(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePesticide(Pesticide p) {
        try {
            getHelper().getPesticideDao().update(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the pesticide exists by id
    public Pesticide getPesticideWithId(int p) {
        Pesticide pesticide= null;
        try {
            pesticide = getHelper().getPesticideDao().queryForId(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pesticide;
    }
    public void flushPesticide(){
    	try {
            getHelper().getPesticideDao().delete(getAllPesticides());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void flushPesticideNew(){
    	try {
            TableUtils.clearTable(helper.getConnectionSource(), Pesticide.class);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * SoilFertilizer Operations
     */
   
  //Stored - Queries
    public List<SoilFertilizer> getAllSoilFertilizers() {
        List<SoilFertilizer> soilFertilizerList = null;
        try {
            soilFertilizerList = getHelper().getSoilFertilizerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soilFertilizerList;
    }
    public List<SoilFertilizer> getAllSoilFertilizersOrderByName(){
    	List<SoilFertilizer> soilFertilizerList=null;
    	try{
    		soilFertilizerList=getHelper().getSoilFertilizerDao().queryBuilder().orderByRaw("productName").query();
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return soilFertilizerList;
    }
    // adding,updating Fertilizer class
    public void addSoilFertilizer(SoilFertilizer f) {
        try {
            getHelper().getSoilFertilizerDao().create(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSoilFertilizer(SoilFertilizer f) {
        try {
            getHelper().getSoilFertilizerDao().update(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the fertilizer exists by id
    public SoilFertilizer getSoilFertilizerWithId(int p) {
        SoilFertilizer soilfertilizer= null;
        try {
           soilfertilizer = getHelper().getSoilFertilizerDao().queryForId(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soilfertilizer;
    }
    public void flushSoilFertilizer(){
    	try {
            getHelper().getSoilFertilizerDao().delete(getAllSoilFertilizers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Fertilizer Operations
     */
   
  //Stored - Queries
    public List<Fertilizer> getAllFertilizers() {
        List<Fertilizer> fertilizerList = null;
        try {
            fertilizerList = getHelper().getFertilizerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fertilizerList;
    }
    public List<Fertilizer> getAllFertilizersOrderByName(){
    	List<Fertilizer> fertilizerList=null;
    	try{
    		fertilizerList=getHelper().getFertilizerDao().queryBuilder().orderByRaw("productName").query();
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return fertilizerList;
    }
    // adding,updating Fertilizer class
    public void addFertilizer(Fertilizer f) {
        try {
            getHelper().getFertilizerDao().create(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFertilizer(Fertilizer f) {
        try {
            getHelper().getFertilizerDao().update(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the fertilizer exists by id
    public Fertilizer getFertilizerWithId(int p) {
        Fertilizer fertilizer= null;
        try {
           fertilizer = getHelper().getFertilizerDao().queryForId(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fertilizer;
    }
    public void flushFertilizer(){
    	try {
            getHelper().getFertilizerDao().delete(getAllFertilizers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void flushFertilizerNew(){
    	try {
            TableUtils.clearTable(helper.getConnectionSource(), Fertilizer.class);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*************************************
     * 
     * Works - DB Operations
     */
    public List<Work> getAllWorks() {
        List<Work> workList = null;
        try {
            workList = getHelper().getWorkDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }
    public List<Work> getAllValidWorks(){
    	List<Work> workList=null;
    	try{
    		workList=getHelper().getWorkDao().queryForEq("valid", true);
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return workList;
    }
    public List<Work> getAllNotSendedWorks(){
    	List<Work> workList=null;
    	try{
    		QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
    		qb.where().eq("sended", false);
    		qb.orderBy("date", false);
    		PreparedQuery<Work> preparedQuery = qb.prepare();
    		workList = getHelper().getWorkDao().query(preparedQuery);
    		
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return workList;
    }
    public List<Work> getAllWorksOrderByDate(){
    	List<Work> workList=null;
    	try{
    		workList=getHelper().getWorkDao().queryBuilder().orderBy("date", false).query();
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return workList;
    }
    public List<Work> getWorksForTaskId(int id){
    	List<Work> workList=null;
    	try{
    		workList=getHelper().getWorkDao().queryForEq("task_id", id);
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return workList;
    }
    public List<Work> getWorksForTaskIdOrdered(int id)throws SQLException{
    	
    	QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
    	   	
    	if (id<=3){
        	qb.where().eq("task_id", id);
        }else{
        	qb.where().ge("task_id", id);
        }
        qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
		return getHelper().getWorkDao().query(preparedQuery);
        
        
    }
    public List<Work> getWorksForTaskIdOrderedASA(String type)throws SQLException{ //filtering of asa codes
    	QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
    	final Where<Work, Integer> w = qb.where();
    	List<Task> asaTasks = getTaskForASAFiltering(type);
    	int clauseC = 0;
    	for (Task t:asaTasks){ //generating a dynamic or
    		w.eq("task_id", t.getId());
    		clauseC++;
    	}
    	if (clauseC > 1) {
    	    w.or(clauseC);
    	}
    	qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
		return getHelper().getWorkDao().query(preparedQuery);	
    }

    public List<Work> getWorksForTaskIdOrderedASARest()throws SQLException{ //all others from the code not equal
    	QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
    	final Where<Work, Integer> w = qb.where();
    	List<Task> asaTasks = getTaskForASAFilteringRest();
    	int clauseC = 0;
    	for (Task t:asaTasks){ //generating a dynamic or not equal
    		w.eq("task_id", t.getId());
    		clauseC++;
    	}
    	if (clauseC > 1) {
    	    w.or(clauseC);
    	}
    	qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
		return getHelper().getWorkDao().query(preparedQuery);	
    }
    public List<Task>getTaskForASAFiltering(String type)throws SQLException{
    	QueryBuilder<Task, Integer> qb = getHelper().getTaskDao().queryBuilder();
    	final Where<Task, Integer> w = qb.where();
    	w.eq("type", type);
    	PreparedQuery<Task> preparedQuery = qb.prepare();
		return getHelper().getTaskDao().query(preparedQuery);	
    }
    public List<Task>getTaskForASAFilteringRest()throws SQLException{
        QueryBuilder<Task, Integer> qb = getHelper().getTaskDao().queryBuilder();
        final Where<Task, Integer> w = qb.where();
        w.or(
            w.isNull("type"),
            w.eq("type","E")
        );
        PreparedQuery<Task> preparedQuery = qb.prepare();
        return getHelper().getTaskDao().query(preparedQuery);
    }
    public Work getWorkWithId(int workId) {
        Work work= null;
        try {
            work = getHelper().getWorkDao().queryForId(workId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return work;
    }
 // adding,updating work class
    public void addWork(Work w) {
        try {
            getHelper().getWorkDao().create(w);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWork(Work work) {
        try {
            getHelper().getWorkDao().update(work);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteWork(Work work){
    	try {
            getHelper().getWorkDao().delete(work);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteCascWork(Work work){
    	try {
            //del Varquarters of current work
    		DeleteBuilder dbwq = getHelper().getWorkVQuarterDao().deleteBuilder();
            dbwq.where().eq("work_id", work.getId());
            getHelper().getWorkVQuarterDao().delete(dbwq.prepare());
            //del worker of current work
            DeleteBuilder dbw = getHelper().getWorkWorkerDao().deleteBuilder();
            dbw.where().eq("work_id", work.getId());
            getHelper().getWorkWorkerDao().delete(dbw.prepare());
            //del machine of current work
            DeleteBuilder dbm = getHelper().getWorkMachineDao().deleteBuilder();
            dbm.where().eq("work_id", work.getId());
            getHelper().getWorkMachineDao().delete(dbm.prepare());
            DeleteBuilder dbf = getHelper().getWorkFertilizerDao().deleteBuilder();
            dbf.where().eq("work_id", work.getId());
            getHelper().getWorkFertilizerDao().delete(dbf.prepare());
            List<Spraying> sprayList= getSprayingByWorkId(work.getId());
            if (sprayList.size()>0){
            	Spraying sp = sprayList.get(0);
            	deleteWorkSprayItems(sp);
            	deleteWorkFertItems(sp);
            }
            DeleteBuilder dbp = getHelper().getSprayingDao().deleteBuilder();
            dbp.where().eq("work_id", work.getId());
            getHelper().getSprayingDao().delete(dbp.prepare());
            DeleteBuilder dbh = getHelper().getHarvestDao().deleteBuilder();
            dbp.where().eq("work_id",work.getId());
    		//delete the work
            getHelper().getWorkDao().delete(work);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteWorkSprayItems(Spraying spray){
    	try{
    		List<SprayPesticide> lp = getSprayPesticideBySprayId(spray.getId());
    		getHelper().getSprayPesticideDao().delete(lp);
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    }
    public void deleteWorkFertItems(Spraying spray){
    	try{
    		List<SprayFertilizer> lf = getSprayFertilizerBySprayId(spray.getId());
    		getHelper().getSprayFertilizerDao().delete(lf);
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    }
   /**
    *  WorkVQuarter-Query and Operations
    *
    * @return gives us the VQuarters of a work
    * @throws SQLException
    */
    public List<WorkVQuarter> getAllWorkVQuarter() {
        List<WorkVQuarter> workVQuarterList = null;
        try {
            workVQuarterList = getHelper().getWorkVQuarterDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workVQuarterList;
    }
    public List<VQuarter> lookupVQuarterForWork(Work work) throws SQLException{
       		if (vqForWorkQuery == null){
        		vqForWorkQuery = makeVqForWorkQuery();
        	}
        	vqForWorkQuery.setArgumentHolderValue(0, work);
    	    	
    	return getHelper().getVquarterDao().query(vqForWorkQuery);
    }
    private PreparedQuery<VQuarter> makeVqForWorkQuery() throws SQLException{
    	QueryBuilder<WorkVQuarter,Integer>workVqQb = getHelper().getWorkVQuarterDao().queryBuilder();
    	workVqQb.selectColumns(WorkVQuarter.VQUARTER_ID_FIELD_NAME);
    	SelectArg workSelectArg = new SelectArg();
    	workVqQb.where().eq(WorkVQuarter.WORK_ID_FIELD_NAME, workSelectArg);
    	QueryBuilder<VQuarter, Integer> vqQb = getHelper().getVquarterDao().queryBuilder();
    	vqQb.where().in("id", workVqQb);
    	return vqQb.prepare();
    }
 // getting the workvquarter object of a work
    public List<WorkVQuarter> getVQuarterByWorkId(int workId) {
        List<WorkVQuarter> workvquarter= null;
        try {
           workvquarter = getHelper().getWorkVQuarterDao().queryForEq(WorkVQuarter.WORK_ID_FIELD_NAME, workId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return workvquarter;
    }
    
 // getting the workvquarter object of a work ordered by vquarterid
    public List<WorkVQuarter> getVQuarterByWorkIdOrderedByVq(int workId) throws SQLException {
        
        QueryBuilder<WorkVQuarter, Integer> qb = getHelper().getWorkVQuarterDao().queryBuilder();
        
			qb.where().eq(WorkVQuarter.WORK_ID_FIELD_NAME, workId);
			qb.orderBy(WorkVQuarter.VQUARTER_ID_FIELD_NAME, true);
			PreparedQuery<WorkVQuarter> preparedQuery = qb.prepare();
			return getHelper().getWorkVQuarterDao().query(preparedQuery);
		
        
        
		
    }
    
    public void deleteWorkVquarter(WorkVQuarter workvquarter){
    	try {
            getHelper().getWorkVQuarterDao().delete(workvquarter);
            } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void addWorkVQuarter(WorkVQuarter w) {
        try {
            getHelper().getWorkVQuarterDao().create(w);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<WorkVQuarter>getWorkVQuarterByWorkIdAndByVQuarterId(int workid, int vquarterid) throws SQLException{
    	QueryBuilder<WorkVQuarter, Integer> queryBuilder =
    			 getHelper().getWorkVQuarterDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<WorkVQuarter, Integer> where = queryBuilder.where();
    			// the workid field must be equal to workid
    			where.eq(WorkVQuarter.WORK_ID_FIELD_NAME, workid);
    			// and
    			where.and();
    			// the vquarterid field must be equal to workerid
    			where.eq(WorkVQuarter.VQUARTER_ID_FIELD_NAME, vquarterid);
    			PreparedQuery<WorkVQuarter> preparedQuery = queryBuilder.prepare();
    			return getHelper().getWorkVQuarterDao().query(preparedQuery);
    }
    /**
     *  SprayPesticide-Query and Operations
     * 
     */
    public List<SprayPesticide> getAllSprayPesticide() {
        List<SprayPesticide> sprayPesticideList = null;
        try {
        	sprayPesticideList = getHelper().getSprayPesticideDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sprayPesticideList;
    }
    public List<Pesticide> lookupPesticideforSpraying(Spraying spray) throws SQLException{
   		if (pesticideForSprayQuery == null){
   			pesticideForSprayQuery = makePesticideForSprayQuery();
    	}
   		pesticideForSprayQuery.setArgumentHolderValue(0, spray);
	    	
	return getHelper().getPesticideDao().query(pesticideForSprayQuery);
    }
    private PreparedQuery<Pesticide> makePesticideForSprayQuery() throws SQLException{
    	QueryBuilder<SprayPesticide,Integer>sprayPesticideQb = getHelper().getSprayPesticideDao().queryBuilder();
    	sprayPesticideQb.selectColumns(SprayPesticide.PESTICIDE_ID_FIELD_NAME);
    	SelectArg spraySelectArg = new SelectArg();
    	sprayPesticideQb.where().eq(SprayPesticide.SPRAY_ID_FIELD_NAME, spraySelectArg);
    	QueryBuilder<Pesticide, Integer> pesticideQb = getHelper().getPesticideDao().queryBuilder();
    	pesticideQb.where().in("id", sprayPesticideQb);
    	return pesticideQb.prepare();
    }
 // getting the spraypesticide objects of a spraying
    public List<SprayPesticide> getSprayPesticideBySprayId(int sprayId) {
        List<SprayPesticide> spraypesticide= null;
        try {
        	spraypesticide = getHelper().getSprayPesticideDao().queryForEq(SprayPesticide.SPRAY_ID_FIELD_NAME, sprayId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return spraypesticide;
    }
    public void deleteSprayPesticide(SprayPesticide spraypesticide){
    	try {
            getHelper().getSprayPesticideDao().delete(spraypesticide);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addSprayPesticide(SprayPesticide p) {
        try {
            getHelper().getSprayPesticideDao().create(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateSprayPesticide(SprayPesticide p) {
        try {
            getHelper().getSprayPesticideDao().update(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<SprayPesticide>getSprayPesticideBySprayIdAndByPesticideId(int sprayid, int pestid) throws SQLException{
    	QueryBuilder<SprayPesticide, Integer> queryBuilder =
    			 getHelper().getSprayPesticideDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<SprayPesticide, Integer> where = queryBuilder.where();
    			// the sprayid field must be equal to sprayid
    			where.eq(SprayPesticide.SPRAY_ID_FIELD_NAME, sprayid);
    			// and
    			where.and();
    			// the pesticideid field must be equal to pesticideid
    			where.eq(SprayPesticide.PESTICIDE_ID_FIELD_NAME, pestid);
    			PreparedQuery<SprayPesticide> preparedQuery = queryBuilder.prepare();
    			return getHelper().getSprayPesticideDao().query(preparedQuery);
    }
    /**
     *  SprayFertilizers-Query and Operations
     * 
     */
    public List<SprayFertilizer> getAllSprayFertilizers() {
        List<SprayFertilizer> sprayFertilizerList = null;
        try {
        	sprayFertilizerList = getHelper().getSprayFertilizerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sprayFertilizerList;
    }
    public List<Fertilizer> lookupFertilizerforSpraying(Spraying spray) throws SQLException{
   		if (fertilizerForSprayQuery == null){
   			fertilizerForSprayQuery = makeFertilizerForSprayQuery();
    	}
   		fertilizerForSprayQuery.setArgumentHolderValue(0, spray);
	    	
	return getHelper().getFertilizerDao().query(fertilizerForSprayQuery);
    }
    private PreparedQuery<Fertilizer> makeFertilizerForSprayQuery() throws SQLException{
    	QueryBuilder<SprayFertilizer,Integer>sprayFertilizerQb = getHelper().getSprayFertilizerDao().queryBuilder();
    	sprayFertilizerQb.selectColumns(SprayFertilizer.FERTILIZER_ID_FIELD_NAME);
    	SelectArg spraySelectArg = new SelectArg();
    	sprayFertilizerQb.where().eq(SprayFertilizer.SPRAY_ID_FIELD_NAME, spraySelectArg);
    	QueryBuilder<Fertilizer, Integer> fertilizerQb = getHelper().getFertilizerDao().queryBuilder();
    	fertilizerQb.where().in("id", sprayFertilizerQb);
    	return fertilizerQb.prepare();
    }
 // getting the sprayfertilizer objects of a spraying
    public List<SprayFertilizer> getSprayFertilizerBySprayId(int sprayId) {
        List<SprayFertilizer> sprayfertilizer= null;
        try {
        	sprayfertilizer = getHelper().getSprayFertilizerDao().queryForEq(SprayFertilizer.SPRAY_ID_FIELD_NAME, sprayId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return sprayfertilizer;
    }
    public void deleteSprayFertilizer(SprayFertilizer sprayfertilizer){
    	try {
            getHelper().getSprayFertilizerDao().delete(sprayfertilizer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addSprayFertilizer(SprayFertilizer f) {
        try {
            getHelper().getSprayFertilizerDao().create(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateSprayFertilizer(SprayFertilizer f) {
        try {
            getHelper().getSprayFertilizerDao().update(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<SprayFertilizer>getSprayFertilizerBySprayIdAndByFertilizerId(int sprayid, int fertid) throws SQLException{
    	QueryBuilder<SprayFertilizer, Integer> queryBuilder =
    			 getHelper().getSprayFertilizerDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<SprayFertilizer, Integer> where = queryBuilder.where();
    			// the sprayid field must be equal to sprayid
    			where.eq(SprayFertilizer.SPRAY_ID_FIELD_NAME, sprayid);
    			// and
    			where.and();
    			// the pesticideid field must be equal to pesticideid
    			where.eq(SprayFertilizer.FERTILIZER_ID_FIELD_NAME, fertid);
    			PreparedQuery<SprayFertilizer> preparedQuery = queryBuilder.prepare();
    			return getHelper().getSprayFertilizerDao().query(preparedQuery);
    }
    /**
     *  WorkWorker-Query and Operations
     *
     * @return gives us the Worker of a work
     * @throws SQLException
     */
    public List<WorkWorker> getAllWorkWorker() {
        List<WorkWorker> workWorkerList = null;
        try {
            workWorkerList = getHelper().getWorkWorkerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workWorkerList;
    }
    
    public List<Worker> lookupWorkerForWork(Work work) throws SQLException{
   		if (workerForWorkQuery == null){
    		workerForWorkQuery = makeWorkerForWorkQuery();
    	}
    	workerForWorkQuery.setArgumentHolderValue(0, work);
	    	
	return getHelper().getWorkerDao().query(workerForWorkQuery);
    }
    private PreparedQuery<Worker> makeWorkerForWorkQuery() throws SQLException{
    	QueryBuilder<WorkWorker,Integer>workWorkerQb = getHelper().getWorkWorkerDao().queryBuilder();
    	workWorkerQb.selectColumns(WorkWorker.WORKER_ID_FIELD_NAME);
    	SelectArg workSelectArg = new SelectArg();
    	workWorkerQb.where().eq(WorkWorker.WORK_ID_FIELD_NAME, workSelectArg);
    	QueryBuilder<Worker, Integer> workerQb = getHelper().getWorkerDao().queryBuilder();
    	workerQb.where().in("id", workWorkerQb);
    	return workerQb.prepare();
    }
 // getting the workworker object of a work
    public List<WorkWorker> getWorkWorkerByWorkId(int workId) {
        List<WorkWorker> workworker= null;
        try {
           workworker = getHelper().getWorkWorkerDao().queryForEq(WorkWorker.WORK_ID_FIELD_NAME, workId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return workworker;
    }
    public void deleteWorkWorker(WorkWorker workworker){
    	try {
            getHelper().getWorkWorkerDao().delete(workworker);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addWorkWorker(WorkWorker w) {
        try {
            getHelper().getWorkWorkerDao().create(w);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateWorkWorker(WorkWorker w) {
        try {
            getHelper().getWorkWorkerDao().update(w);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<WorkWorker>getWorkWorkerByWorkIdAndByWorkerId(int workid, int workerid) throws SQLException{
    	QueryBuilder<WorkWorker, Integer> queryBuilder =
    			 getHelper().getWorkWorkerDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<WorkWorker, Integer> where = queryBuilder.where();
    			// the workid field must be equal to workid
    			where.eq(WorkWorker.WORK_ID_FIELD_NAME, workid);
    			// and
    			where.and();
    			// the workerid field must be equal to workerid
    			where.eq(WorkWorker.WORKER_ID_FIELD_NAME, workerid);
    			PreparedQuery<WorkWorker> preparedQuery = queryBuilder.prepare();
    			return getHelper().getWorkWorkerDao().query(preparedQuery);
    }
    /**
     *  WorkMachine-Query and Operations
     *
     * @return gives us the Machine of a work
     * @throws SQLException
     */
    public List<WorkMachine> getAllWorkMachine() {
        List<WorkMachine> workMachineList = null;
        try {
            workMachineList = getHelper().getWorkMachineDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workMachineList;
    }
    
    public List<Machine> lookupMachineForWork(Work work) throws SQLException{
   		if (machineForWorkQuery == null){
    		machineForWorkQuery = makeMachineForWorkQuery();
    	}
    	machineForWorkQuery.setArgumentHolderValue(0, work);
	    	
	return getHelper().getMachineDao().query(machineForWorkQuery);
    }
    private PreparedQuery<Machine> makeMachineForWorkQuery() throws SQLException{
    	QueryBuilder<WorkMachine,Integer>workMachineQb = getHelper().getWorkMachineDao().queryBuilder();
    	workMachineQb.selectColumns(WorkMachine.MACHINE_ID_FIELD_NAME);
    	SelectArg workSelectArg = new SelectArg();
    	workMachineQb.where().eq(WorkMachine.WORK_ID_FIELD_NAME, workSelectArg);
    	QueryBuilder<Machine, Integer> machineQb = getHelper().getMachineDao().queryBuilder();
    	machineQb.where().in("id", workMachineQb);
    	return machineQb.prepare();
    }
 // getting the workmachine object of a work
    public List<WorkMachine> getWorkMachineByWorkId(int workId) {
        List<WorkMachine> workmachine= null;
        try {
           workmachine = getHelper().getWorkMachineDao().queryForEq(WorkMachine.WORK_ID_FIELD_NAME, workId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return workmachine;
    }
    public void deleteWorkMachine(WorkMachine workmachine){
    	try {
            getHelper().getWorkMachineDao().delete(workmachine);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addWorkMachine(WorkMachine w) {
        try {
            getHelper().getWorkMachineDao().create(w);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateWorkMachine(WorkMachine w) {
        try {
            getHelper().getWorkMachineDao().update(w);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<WorkMachine>getWorkMachineByWorkIdAndByMachineId(int workid, int machineid) throws SQLException{
    	QueryBuilder<WorkMachine, Integer> queryBuilder =
    			 getHelper().getWorkMachineDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<WorkMachine, Integer> where = queryBuilder.where();
    			// the workid field must be equal to workid
    			where.eq(WorkMachine.WORK_ID_FIELD_NAME, workid);
    			// and
    			where.and();
    			// the machineid field must be equal to machineid
    			where.eq(WorkMachine.MACHINE_ID_FIELD_NAME, machineid);
    			PreparedQuery<WorkMachine> preparedQuery = queryBuilder.prepare();
    			return getHelper().getWorkMachineDao().query(preparedQuery);
    }
    /**
     *  Spraying-Queries and Operations
       */
 // getting the spraying object of a work
    public List<Spraying> getSprayingByWorkId(int workId) {
        List<Spraying> spray= null;
        try {
           spray = getHelper().getSprayingDao().queryForEq(Spraying.WORK_ID_FIELD_NAME, workId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return spray;
    }
    public boolean isNewSpraying(int workId){
    	List<Spraying> spray= getSprayingByWorkId(workId);
    	if (spray ==null){
    		return true;
    	}else{
    		return false;
    	}
    }
 // adding,updating spraying class
    public void addSpray(Spraying s) {
        try {
            getHelper().getSprayingDao().create(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSpray(Spraying spray) {
        try {
            getHelper().getSprayingDao().update(spray);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteSpray(Spraying spray){
    	try {
            getHelper().getSprayingDao().delete(spray);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // checking if the worker exists by id
    public Spraying getSprayingWithId(int sprayId) {
        Spraying spray= null;
        try {
            spray = getHelper().getSprayingDao().queryForId(sprayId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spray;
    }
    /**
     *  WorkSoilFertilizer-Query and Operations
     *
     * @return gives us theFertilizer of a work
     * @throws SQLException
     */
    public List<WorkFertilizer> getAllWorkFertilizers() {
        List<WorkFertilizer> workFertilizerList = null;
        try {
            workFertilizerList = getHelper().getWorkFertilizerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workFertilizerList;
    }
    
    public List<SoilFertilizer> lookupFertilizerForWork(Work work) throws SQLException{
   		if (fertilizerForWorkQuery == null){
    		fertilizerForWorkQuery = makeFertilizerForWorkQuery();
    	}
    	fertilizerForWorkQuery.setArgumentHolderValue(0, work);
	    	
	return getHelper().getSoilFertilizerDao().query(fertilizerForWorkQuery);
    }
    private PreparedQuery<SoilFertilizer> makeFertilizerForWorkQuery() throws SQLException{
    	QueryBuilder<WorkFertilizer,Integer>workFertilizerQb = getHelper().getWorkFertilizerDao().queryBuilder();
    	workFertilizerQb.selectColumns(WorkFertilizer.SOILFERTILIZER_ID_FIELD_NAME);
    	SelectArg workSelectArg = new SelectArg();
    	workFertilizerQb.where().eq(WorkFertilizer.WORK_ID_FIELD_NAME, workSelectArg);
    	QueryBuilder<SoilFertilizer, Integer> fertilizerQb = getHelper().getSoilFertilizerDao().queryBuilder();
    	fertilizerQb.where().in("id", workFertilizerQb);
    	return fertilizerQb.prepare();
    }
 // getting the workmachine object of a work
    public List<WorkFertilizer> getWorkFertilizerByWorkId(int workId) {
        List<WorkFertilizer> workfertilizer= null;
        try {
           workfertilizer = getHelper().getWorkFertilizerDao().queryForEq(WorkFertilizer.WORK_ID_FIELD_NAME, workId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return workfertilizer;
    }
    public void deleteWorkFertilizer(WorkFertilizer workfertilizer){
    	try {
            getHelper().getWorkFertilizerDao().delete(workfertilizer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addWorkFertilizer(WorkFertilizer f) {
        try {
            getHelper().getWorkFertilizerDao().create(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateWorkFertilizer(WorkFertilizer f) {
        try {
            getHelper().getWorkFertilizerDao().update(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<WorkFertilizer>getWorkFertilizerByWorkIdAndBySoiFertilizerId(int workid, int soilFertilizerid) throws SQLException{
    	QueryBuilder<WorkFertilizer, Integer> queryBuilder =
    			 getHelper().getWorkFertilizerDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<WorkFertilizer, Integer> where = queryBuilder.where();
    			// the workid field must be equal to workid
    			where.eq(WorkFertilizer.WORK_ID_FIELD_NAME, workid);
    			// and
    			where.and();
    			// the machineid field must be equal to machineid
    			where.eq(WorkFertilizer.SOILFERTILIZER_ID_FIELD_NAME, soilFertilizerid);
    			PreparedQuery<WorkFertilizer> preparedQuery = queryBuilder.prepare();
    			return getHelper().getWorkFertilizerDao().query(preparedQuery);
    }
    /*************************************
     * 
     * Purchase - DB Operations
     */
    public List<Purchase> getAllPurchases() {
        List<Purchase> purchaseList = null;
        try {
            purchaseList = getHelper().getPurchaseDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchaseList;
    }
    public List<Purchase> getAllPurchasesOrderByDate(){
    	List<Purchase> purchaseList=null;
    	try{
    		purchaseList=getHelper().getPurchaseDao().queryBuilder().orderBy("date", false).query();
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return purchaseList;
    }
    public Purchase getPurchaseWithId(int purchaseId) {
        Purchase purchase= null;
        try {
            purchase = getHelper().getPurchaseDao().queryForId(purchaseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchase;
    }
 // adding,updating purchase class
    public void addPurchase(Purchase p) {
        try {
            getHelper().getPurchaseDao().create(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePurchase(Purchase p) {
        try {
            getHelper().getPurchaseDao().update(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deletePurchaseAndProducts(Purchase p){
    	try {
    		 //del pesticide of purchase
    		DeleteBuilder<PurchasePesticide, Integer> dbpest = getHelper().getPurchasePesticideDao().deleteBuilder();
            dbpest.where().eq("purchase_id", p.getId());
            getHelper().getPurchasePesticideDao().delete(dbpest.prepare());
            //del fertilizer of purchase
            DeleteBuilder<PurchaseFertilizer, Integer> dbfert = getHelper().getPurchaseFertilizerDao().deleteBuilder();
            dbfert.where().eq("purchase_id", p.getId());
            getHelper().getPurchaseFertilizerDao().delete(dbfert.prepare());
            // finally delete the purchase record
            getHelper().getPurchaseDao().delete(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     *  PurchasePesticide-Query and Operations
     * 
     */
    public List<PurchasePesticide> getAllPurchasePesticide() {
        List<PurchasePesticide> purchasePesticideList = null;
        try {
        	purchasePesticideList = getHelper().getPurchasePesticideDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchasePesticideList;
    }
    public List<Pesticide> lookupPesticideForPurchase(Purchase purchase) throws SQLException{
   		if (pesticideForPurchaseQuery == null){
   			pesticideForPurchaseQuery = makePesticideForPurchaseQuery();
    	}
   		pesticideForPurchaseQuery.setArgumentHolderValue(0, purchase);
	    	
	return getHelper().getPesticideDao().query(pesticideForPurchaseQuery);
    }
    private PreparedQuery<Pesticide> makePesticideForPurchaseQuery() throws SQLException{
    	QueryBuilder<PurchasePesticide,Integer>purchasePesticideQb = getHelper().getPurchasePesticideDao().queryBuilder();
    	purchasePesticideQb.selectColumns(PurchasePesticide.PESTICIDE_ID_FIELD_NAME);
    	SelectArg purchaseSelectArg = new SelectArg();
    	purchasePesticideQb.where().eq(PurchasePesticide.PURCHASE_ID_FIELD_NAME, purchaseSelectArg);
    	QueryBuilder<Pesticide, Integer> pesticideQb = getHelper().getPesticideDao().queryBuilder();
    	pesticideQb.where().in("id", purchasePesticideQb);
    	return pesticideQb.prepare();
    }
 // getting the purchasepesticide objects of a purchase
    public List<PurchasePesticide> getPurchasePesticideByPurchaseId(int purchaseId) {
        List<PurchasePesticide> purchasepesticide= null;
        try {
        	purchasepesticide = getHelper().getPurchasePesticideDao().queryForEq(PurchasePesticide.PURCHASE_ID_FIELD_NAME, purchaseId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchasepesticide;
    }
    public void deletePurchasePesticide(PurchasePesticide purchasepesticide){
    	try {
            getHelper().getPurchasePesticideDao().delete(purchasepesticide);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addPurchasePesticide(PurchasePesticide p) {
        try {
            getHelper().getPurchasePesticideDao().create(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updatePurchasePesticide(PurchasePesticide p) {
        try {
            getHelper().getPurchasePesticideDao().update(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<PurchasePesticide>getPurchasePesticideByPurchaseIdAndByPesticideId(int purchaseid, int pestid) throws SQLException{
    	QueryBuilder<PurchasePesticide, Integer> queryBuilder =
    			 getHelper().getPurchasePesticideDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<PurchasePesticide, Integer> where = queryBuilder.where();
    			// the sprayid field must be equal to sprayid
    			where.eq(PurchasePesticide.PURCHASE_ID_FIELD_NAME, purchaseid);
    			// and
    			where.and();
    			// the pesticideid field must be equal to pesticideid
    			where.eq(PurchasePesticide.PESTICIDE_ID_FIELD_NAME, pestid);
    			PreparedQuery<PurchasePesticide> preparedQuery = queryBuilder.prepare();
    			return getHelper().getPurchasePesticideDao().query(preparedQuery);
    }
    /**
     *  PurchaseFertilizer-Query and Operations
     * 
     */
    public List<PurchaseFertilizer> getAllPurchaseFertilizer() {
        List<PurchaseFertilizer> PurchaseFertilizerList = null;
        try {
        	PurchaseFertilizerList = getHelper().getPurchaseFertilizerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return PurchaseFertilizerList;
    }
    public List<Fertilizer> lookupFertilizerForPurchase(Purchase purchase) throws SQLException{
   		if (fertilizerForPurchaseQuery == null){
   			fertilizerForPurchaseQuery = makeFertilizerForPurchaseQuery();
    	}
   		fertilizerForPurchaseQuery.setArgumentHolderValue(0, purchase);
	    	
	return getHelper().getFertilizerDao().query(fertilizerForPurchaseQuery);
    }
    private PreparedQuery<Fertilizer> makeFertilizerForPurchaseQuery() throws SQLException{
    	QueryBuilder<PurchaseFertilizer,Integer>PurchaseFertilizerQb = getHelper().getPurchaseFertilizerDao().queryBuilder();
    	PurchaseFertilizerQb.selectColumns(PurchaseFertilizer.FERTILIZER_ID_FIELD_NAME);
    	SelectArg purchaseSelectArg = new SelectArg();
    	PurchaseFertilizerQb.where().eq(PurchaseFertilizer.PURCHASE_ID_FIELD_NAME, purchaseSelectArg);
    	QueryBuilder<Fertilizer, Integer> fertilizerQb = getHelper().getFertilizerDao().queryBuilder();
    	fertilizerQb.where().in("id", PurchaseFertilizerQb);
    	return fertilizerQb.prepare();
    }
 // getting the PurchaseFertilizer objects of a purchase
    public List<PurchaseFertilizer> getPurchaseFertilizerByPurchaseId(int purchaseId) {
        List<PurchaseFertilizer> purchaseFertilizer= null;
        try {
        	purchaseFertilizer = getHelper().getPurchaseFertilizerDao().queryForEq(PurchaseFertilizer.PURCHASE_ID_FIELD_NAME, purchaseId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchaseFertilizer;
    }
    public void deletePurchaseFertilizer(PurchaseFertilizer purchaseFertilizer){
    	try {
            getHelper().getPurchaseFertilizerDao().delete(purchaseFertilizer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addPurchaseFertilizer(PurchaseFertilizer f) {
        try {
            getHelper().getPurchaseFertilizerDao().create(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updatePurchaseFertilizer(PurchaseFertilizer f) {
        try {
            getHelper().getPurchaseFertilizerDao().update(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<PurchaseFertilizer>getPurchaseFertilizerByPurchaseIdAndByFertilizerId(int purchaseid, int fertid) throws SQLException{
    	QueryBuilder<PurchaseFertilizer, Integer> queryBuilder =
    			 getHelper().getPurchaseFertilizerDao().queryBuilder();
    			// get the WHERE object to build our query
    			Where<PurchaseFertilizer, Integer> where = queryBuilder.where();
    			// the sprayid field must be equal to sprayid
    			where.eq(PurchaseFertilizer.PURCHASE_ID_FIELD_NAME, purchaseid);
    			// and
    			where.and();
    			// the pesticideid field must be equal to pesticideid
    			where.eq(PurchaseFertilizer.FERTILIZER_ID_FIELD_NAME, fertid);
    			PreparedQuery<PurchaseFertilizer> preparedQuery = queryBuilder.prepare();
    			return getHelper().getPurchaseFertilizerDao().query(preparedQuery);
    }
    /*************************************
     * 
     * FrutiQuality - DB Operations
     */
  //Stored - Queries
    public List<FruitQuality> getAllQualities() {
        List<FruitQuality> qualityList = null;
        try {
            qualityList = getHelper().getFruitQualityDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return qualityList;
    }
    // adding,updating Worker class
    public void addQuality(FruitQuality f) {
        try {
            getHelper().getFruitQualityDao().create(f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQuality(FruitQuality quality) {
        try {
            getHelper().getFruitQualityDao().update(quality);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the worker exists by id
    public FruitQuality getQualityWithId(int qualityId) {
       FruitQuality quality= null;
        try {
           quality = getHelper().getFruitQualityDao().queryForId(qualityId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quality;
    }
    public void flushQuality(){
    	try {
            getHelper().getFruitQualityDao().delete(getAllQualities());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*************************************
     * 
     * Harvest - DB Operations
     */
  //Stored - Queries
    public List<Harvest> getAllHarvestEntries() {
        List<Harvest> harvestList = null;
        try {
            harvestList = getHelper().getHarvestDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return harvestList;
    }
    // adding,updating Worker class
    public void addHarvest(Harvest h) {
        try {
            getHelper().getHarvestDao().create(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateHarvest(Harvest harvest) {
        try {
            getHelper().getHarvestDao().update(harvest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // checking if the harvest exists by id
    public Harvest getHarvestWithId(int id) {
       Harvest harvest= null;
        try {
           harvest = getHelper().getHarvestDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return harvest;
    }
    public void flushHarvest(){
    	try {
            getHelper().getHarvestDao().delete(getAllHarvestEntries());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteHarvest(Harvest harvest){
    	try {
            getHelper().getHarvestDao().delete(harvest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // getting the list of harvest objects of a work
    public List<Harvest> getHarvestListbyWorkId(int workId) {
        List<Harvest> harvestList= null;
        try {
        	harvestList = getHelper().getHarvestDao().queryForEq("work_id", workId);
        		   } catch (SQLException e) {
            e.printStackTrace();
        }
        return harvestList;
    }
}
