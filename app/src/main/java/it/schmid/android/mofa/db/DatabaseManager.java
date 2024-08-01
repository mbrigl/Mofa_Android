package it.schmid.android.mofa.db;

import android.content.Context;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.model.Global;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkMachine;
import it.schmid.android.mofa.model.WorkVQuarter;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

public class DatabaseManager {
    private PreparedQuery<VQuarter> vqForWorkQuery = null;
    private PreparedQuery<Worker> workerForWorkQuery = null;
    private PreparedQuery<Machine> machineForWorkQuery = null;
    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private final DatabaseHelper helper;

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    /*
     * helper db queries
     */
    public Integer getDbVersion() {
        return getHelper().getReadableDatabase().getVersion();
    }

    public ConnectionSource getConnection() {
        return getHelper().getConnectionSource();
    }

    public Boolean checkIfEmpty() {
        Boolean isEmpty = false;
        try {
            isEmpty = ((getHelper().getVquarterDao().countOf() == 0) || (getHelper().getTaskDao().countOf() == 0) || (getHelper().getWorkerDao().countOf() == 0));
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return isEmpty;
    }

    public void batchDeleteAllOldSprayEntries(final List<Work> workList) {
        try {
            getHelper().getWorkDao().callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    for (Work w : workList) {
                        deleteCascWork(w);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***************************************************{
     *
     }
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

    public List<Land> getAllLandsOrderedByCode() {
        List<Land> landList = null;
        try {
            landList = getHelper().getLandDao().queryBuilder().orderByRaw("code").query();
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

    public List<Land> getLandWithCode(String code) throws SQLException {
        List<Land> result = getHelper().getLandDao().queryForEq("code", code);
        return result;
    }

    public void flushLand() {
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

            machineList = getHelper().getMachineDao().queryBuilder().orderByRaw("code").query();
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

    public void flushMachine() {
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
        Task task = null;
        try {
            task = getHelper().getTaskDao().queryForId(taskId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    public void flushTask() {
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
        VQuarter vquarter = null;
        try {
            vquarter = getHelper().getVquarterDao().queryForId(vquarterId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vquarter;
    }

    public void flushVQuarter() {
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
            workerList = getHelper().getWorkerDao().queryBuilder().orderByRaw("code").query();
            // workerList = getHelper().getWorkerDao().queryForAll();
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
        Worker worker = null;
        try {
            worker = getHelper().getWorkerDao().queryForId(workerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return worker;
    }

    public void flushWorker() {
        try {
            getHelper().getWorkerDao().delete(getAllWorkers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getWorkerHours(Date fromDate, Date toDate, int workerid) {
        String sumHours = "0.00";
        QueryBuilder<Work, Integer> workqb = getHelper().getWorkDao().queryBuilder();
        try {
            workqb.where().between("date", fromDate, toDate);
            //  PreparedQuery<Work> prepWork = workqb.prepare();
            //  List<Work> test = getHelper().getWorkDao().query(prepWork);
            QueryBuilder<WorkWorker, Integer> qb = getHelper().getWorkWorkerDao().queryBuilder();
            qb.join(workqb);
            qb.selectRaw("SUM(hours) AS total");
            qb.where().eq("worker_id", workerid);
            GenericRawResults<String[]> sum = getHelper().getWorkWorkerDao().queryRaw(qb.prepareStatementString());
            String[] x = sum.getFirstResult();
            sumHours = x[0];

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sumHours;

//
//
//

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

    public List<Work> getAllValidWorks() {
        List<Work> workList = null;
        try {
            workList = getHelper().getWorkDao().queryForEq("valid", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }

    public List<Work> getAllValidNotSprayWorks() {
        List<Work> workList = null;

        try {
            workList = getWorksNotSpraying();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workList;
    }

    public List<Work> getAllOldValidNotSprayWorks() {
        List<Work> workList = null;

        try {
            workList = getOldWorksNotSpraying();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workList;
    }

    //after sending the data, set the remaining ones (= spray works) to sended true
    public void setWorksSendedToTrue() {
        UpdateBuilder<Work, Integer> updateBuilder = getHelper().getWorkDao().updateBuilder();
        try {
            updateBuilder.updateColumnValue("sended", true);
            updateBuilder.where().eq("valid", true);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Work> getAllNotSendedWorks() {
        List<Work> workList = null;
        try {
            QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
            qb.where().eq("sended", false);
            qb.orderBy("date", false);
            qb.orderBy("id", false);
            PreparedQuery<Work> preparedQuery = qb.prepare();
            workList = getHelper().getWorkDao().query(preparedQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }

    public List<Work> getAllSendedWorks() {
        List<Work> workList = null;
        try {
            QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
            qb.where().eq("sended", true);
            PreparedQuery<Work> preparedQuery = qb.prepare();
            workList = getHelper().getWorkDao().query(preparedQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }

    public List<Work> getAllValidNotSendedWorks() {
        List<Work> workList = null;
        try {
            QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
            qb.where().eq("sended", false).and().eq("valid", true);
            qb.orderBy("date", false);
            PreparedQuery<Work> preparedQuery = qb.prepare();
            workList = getHelper().getWorkDao().query(preparedQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }

    public List<Work> getAllValidNotSendedWorksExcel() {
        List<Work> workList = null;
        try {
            QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
            qb.where().eq("sended", false).and().eq("valid", true);
            qb.orderBy("date", true);
            PreparedQuery<Work> preparedQuery = qb.prepare();
            workList = getHelper().getWorkDao().query(preparedQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }

    public List<Work> getAllWorksOrderByDate() {
        List<Work> workList = null;
        try {
            workList = getHelper().getWorkDao().queryBuilder().orderBy("date", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }

    public List<Work> getWorksForTaskId(int id) {
        List<Work> workList = null;
        try {
            workList = getHelper().getWorkDao().queryForEq("task_id", id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
    }

    public List<Work> getWorksForTaskIdOrdered(int id) throws SQLException {

        QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();

        if (id <= 3) {
            qb.where().eq("task_id", id);
        } else {
            qb.where().ge("task_id", id);
        }
        qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
        return getHelper().getWorkDao().query(preparedQuery);


    }

    public List<Work> getWorksNotSpraying() throws SQLException { //filtering of asa codes
        QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
        final Where<Work, Integer> w = qb.where();
        List<Task> asaTasks = getTaskNotSpraying();
        int clauseC = 0;
        for (Task t : asaTasks) { //generating a dynamic or
            w.eq("task_id", t.getId()).and().eq("valid", true);
            clauseC++;
        }
        if (clauseC > 1) {
            w.or(clauseC);
        }
        qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
        return getHelper().getWorkDao().query(preparedQuery);
    }

    //query for deleting the old works, not spraying
    public List<Work> getOldWorksNotSpraying() throws SQLException { //filtering of asa codes
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, -60);
        Date currDateMinusFifty = c.getTime();
        QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
        final Where<Work, Integer> w = qb.where();
        List<Task> asaTasks = getTaskNotSpraying();
        int clauseC = 0;
        for (Task t : asaTasks) { //generating a dynamic or
            w.eq("task_id", t.getId()).and().eq("valid", true).and().le("date", currDateMinusFifty);
            clauseC++;
        }
        if (clauseC > 1) {
            w.or(clauseC);
        }
        qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
        return getHelper().getWorkDao().query(preparedQuery);
    }

    // all old spraying works that can be deleted using delete archive
    public List<Work> getOldSprayingWorks() { //filtering of asa codes
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, -50);
        Date currDateMinusFifty = c.getTime();
        QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
        final Where<Work, Integer> w = qb.where();
        try {
            List<Task> asaTasks = getTaskSpraying();
            int clauseC = 0;
            for (Task t : asaTasks) { //generating a dynamic or
                w.eq("task_id", t.getId()).and().eq("valid", true).and().eq("sended", true).and().le("date", currDateMinusFifty);
                clauseC++;
            }
            if (clauseC > 1) {
                w.or(clauseC);
            }
            qb.orderBy("date", false);
            PreparedQuery<Work> preparedQuery = qb.prepare();
            return getHelper().getWorkDao().query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; //only if error

    }

    //query for filtering data for S,D,H Spritzung, Düngung und Herbizid
    public List<Work> getWorksForTaskIdOrderedASA(String type) throws SQLException { //filtering of asa codes
        QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
        final Where<Work, Integer> w = qb.where();
        List<Task> asaTasks = getTaskForASAFiltering(type);
        int clauseC = 0;
        for (Task t : asaTasks) { //generating a dynamic or
            w.eq("task_id", t.getId()).and().eq("sended", false);
            clauseC++;
        }
        if (clauseC > 1) {
            w.or(clauseC);
        }
        qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
        return getHelper().getWorkDao().query(preparedQuery);
    }

    // query for filtering all other works
    public List<Work> getWorksForTaskIdOrderedASARest() throws SQLException { //all others from the code not equal
        QueryBuilder<Work, Integer> qb = getHelper().getWorkDao().queryBuilder();
        final Where<Work, Integer> w = qb.where();
        List<Task> asaTasks = getTaskForASAFilteringRest();
        int clauseC = 0;
        for (Task t : asaTasks) { //generating a dynamic or not equal
            w.eq("task_id", t.getId()).and().eq("sended", false);
            clauseC++;
        }
        if (clauseC > 1) {
            w.or(clauseC);
        }
        qb.orderBy("date", false);
        PreparedQuery<Work> preparedQuery = qb.prepare();
        return getHelper().getWorkDao().query(preparedQuery);
    }

    //subquery for task for Spritzung, Dpngung und Herbizid
    public List<Task> getTaskForASAFiltering(String type) throws SQLException {
        QueryBuilder<Task, Integer> qb = getHelper().getTaskDao().queryBuilder();
        final Where<Task, Integer> w = qb.where();
        w.eq("type", type);
        PreparedQuery<Task> preparedQuery = qb.prepare();
        return getHelper().getTaskDao().query(preparedQuery);
    }

    //subquery fo task for all other
    public List<Task> getTaskForASAFilteringRest() throws SQLException {
        QueryBuilder<Task, Integer> qb = getHelper().getTaskDao().queryBuilder();
        final Where<Task, Integer> w = qb.where();
        w.or(
                w.isNull("type"),
                w.eq("type", "E")
        );
        PreparedQuery<Task> preparedQuery = qb.prepare();
        return getHelper().getTaskDao().query(preparedQuery);
    }

    public List<Task> getTaskNotSpraying() throws SQLException {
        QueryBuilder<Task, Integer> qb = getHelper().getTaskDao().queryBuilder();
        final Where<Task, Integer> w = qb.where();
        w.or(
                w.isNull("type"),
                w.eq("type", "E"),
                w.eq("type", "D"),
                w.eq("type", "H"),
                w.eq("type", "O")
        );
        PreparedQuery<Task> preparedQuery = qb.prepare();
        return getHelper().getTaskDao().query(preparedQuery);
    }

    public List<Task> getTaskSpraying() throws SQLException {
        QueryBuilder<Task, Integer> qb = getHelper().getTaskDao().queryBuilder();
        final Where<Task, Integer> w = qb.where();
        w.eq("type", "S");
        PreparedQuery<Task> preparedQuery = qb.prepare();
        return getHelper().getTaskDao().query(preparedQuery);
    }

    public Work getWorkWithId(int workId) {
        Work work = null;
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

    public void deleteWork(Work work) {
        try {
            getHelper().getWorkDao().delete(work);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCascWork(Work work) {
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
            DeleteBuilder dbi = getHelper().getGlobalDao().deleteBuilder();
            dbi.where().eq("workId", work.getId());
            getHelper().getGlobalDao().delete(dbi.prepare());
            //delete the work
            getHelper().getWorkDao().delete(work);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * WorkVQuarter-Query and Operations
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

    public List<VQuarter> lookupVQuarterForWork(Work work) throws SQLException {
        if (vqForWorkQuery == null) {
            vqForWorkQuery = makeVqForWorkQuery();
        }
        vqForWorkQuery.setArgumentHolderValue(0, work);

        return getHelper().getVquarterDao().query(vqForWorkQuery);
    }

    private PreparedQuery<VQuarter> makeVqForWorkQuery() throws SQLException {
        QueryBuilder<WorkVQuarter, Integer> workVqQb = getHelper().getWorkVQuarterDao().queryBuilder();
        workVqQb.selectColumns(WorkVQuarter.VQUARTER_ID_FIELD_NAME);
        SelectArg workSelectArg = new SelectArg();
        workVqQb.where().eq(WorkVQuarter.WORK_ID_FIELD_NAME, workSelectArg);
        QueryBuilder<VQuarter, Integer> vqQb = getHelper().getVquarterDao().queryBuilder();
        vqQb.where().in("id", workVqQb);
        return vqQb.prepare();
    }

    // getting the workvquarter object of a work
    public List<WorkVQuarter> getVQuarterByWorkId(int workId) {
        List<WorkVQuarter> workvquarter = null;
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

    public void deleteWorkVquarter(WorkVQuarter workvquarter) {
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

    public List<WorkVQuarter> getWorkVQuarterByWorkIdAndByVQuarterId(int workid, int vquarterid) throws SQLException {
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
     * WorkWorker-Query and Operations
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

    public List<Worker> lookupWorkerForWork(Work work) throws SQLException {
        if (workerForWorkQuery == null) {
            workerForWorkQuery = makeWorkerForWorkQuery();
        }
        workerForWorkQuery.setArgumentHolderValue(0, work);

        return getHelper().getWorkerDao().query(workerForWorkQuery);
    }

    private PreparedQuery<Worker> makeWorkerForWorkQuery() throws SQLException {
        QueryBuilder<WorkWorker, Integer> workWorkerQb = getHelper().getWorkWorkerDao().queryBuilder();
        workWorkerQb.selectColumns(WorkWorker.WORKER_ID_FIELD_NAME);
        SelectArg workSelectArg = new SelectArg();
        workWorkerQb.where().eq(WorkWorker.WORK_ID_FIELD_NAME, workSelectArg);
        QueryBuilder<Worker, Integer> workerQb = getHelper().getWorkerDao().queryBuilder();
        workerQb.where().in("id", workWorkerQb);
        return workerQb.prepare();
    }

    // getting the workworker object of a work
    public List<WorkWorker> getWorkWorkerByWorkId(int workId) {
        List<WorkWorker> workworker = null;
        try {
            workworker = getHelper().getWorkWorkerDao().queryForEq(WorkWorker.WORK_ID_FIELD_NAME, workId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workworker;
    }

    public void deleteWorkWorker(WorkWorker workworker) {
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

    public List<WorkWorker> getWorkWorkerByWorkIdAndByWorkerId(int workid, int workerid) throws SQLException {
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
     * WorkMachine-Query and Operations
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

    public List<Machine> lookupMachineForWork(Work work) throws SQLException {
        if (machineForWorkQuery == null) {
            machineForWorkQuery = makeMachineForWorkQuery();
        }
        machineForWorkQuery.setArgumentHolderValue(0, work);

        return getHelper().getMachineDao().query(machineForWorkQuery);
    }

    private PreparedQuery<Machine> makeMachineForWorkQuery() throws SQLException {
        QueryBuilder<WorkMachine, Integer> workMachineQb = getHelper().getWorkMachineDao().queryBuilder();
        workMachineQb.selectColumns(WorkMachine.MACHINE_ID_FIELD_NAME);
        SelectArg workSelectArg = new SelectArg();
        workMachineQb.where().eq(WorkMachine.WORK_ID_FIELD_NAME, workSelectArg);
        QueryBuilder<Machine, Integer> machineQb = getHelper().getMachineDao().queryBuilder();
        machineQb.where().in("id", workMachineQb);
        return machineQb.prepare();
    }

    // getting the workmachine object of a work
    public List<WorkMachine> getWorkMachineByWorkId(int workId) {
        List<WorkMachine> workmachine = null;
        try {
            workmachine = getHelper().getWorkMachineDao().queryForEq(WorkMachine.WORK_ID_FIELD_NAME, workId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workmachine;
    }

    public void deleteWorkMachine(WorkMachine workmachine) {
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

    public List<WorkMachine> getWorkMachineByWorkIdAndByMachineId(int workid, int machineid) throws SQLException {
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

    public void flushVegData() {

        try {
            getHelper().getGlobalDao().callBatchTasks(new Callable<Void>() {

                public Void call() throws Exception {
                    getHelper().getGlobalDao().delete(getAllVegData());
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //getHelper().getFruitQualityDao().delete(getAllQualities());

    }

    private List<Global> getAllVegData() {
        List<Global> globalList = null;
        try {
            QueryBuilder<Global, Integer> qbGlobal = getHelper().getGlobalDao().queryBuilder();
            qbGlobal.where().eq("typeInfo", ActivityConstants.BLOSSOMSTART).or().eq("typeInfo", ActivityConstants.BLOSSOMEND);
            PreparedQuery<Global> preparedQuery = qbGlobal.prepare();
            globalList = getHelper().getGlobalDao().query(preparedQuery);
            return globalList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
