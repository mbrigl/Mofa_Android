package it.schmid.android.mofa.db;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    private static DatabaseManager instance;

    public static void init(Context ctx) {
        if (instance == null) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private final DatabaseHelper helper;
    private PreparedQuery<VQuarter> vqForWorkQuery = null;

    private DatabaseManager(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
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

    /***************************************************{
     *
     }
     *
     * LAND - DB Operations
     */
    //Stored - Queries
    public List<Land> getAllLands() {
        try {
            return getHelper().getLandDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Land> getAllLandsOrderedByCode() {
        try {
            return getHelper().getLandDao().queryBuilder().orderByRaw("code").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
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
        try {
            return getHelper().getLandDao().queryForId(landId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public List<Work> getAllWorksOrderByDate() {
        List<Work> workList = null;
        try {
            workList = getHelper().getWorkDao().queryBuilder().orderBy("date", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workList;
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
}