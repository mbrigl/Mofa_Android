package it.schmid.android.mofa.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.ArrayList;
import java.util.List;

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

class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "MofaDB.sqlite";
    private static final int DATABASE_VERSION = 17;

    // the DAO object we use to access the SimpleData table
    private Dao<Land, Integer> landDao = null;
    private Dao<Worker, Integer> workerDao = null;
    private Dao<Machine, Integer> machineDao = null;
    private Dao<Task, Integer> taskDao = null;
    private Dao<VQuarter, Integer> vquarterDao = null;
    private Dao<Work, Integer> workDao = null;
    private Dao<WorkVQuarter, Integer> workVquarterDao = null;
    private Dao<WorkWorker, Integer> workWorkerDao = null;
    private Dao<WorkMachine, Integer> workMachineDao = null;
    private Dao<Global, Integer> globalDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Land.class);
            TableUtils.createTable(connectionSource, Worker.class);
            TableUtils.createTable(connectionSource, Task.class);
            TableUtils.createTable(connectionSource, Machine.class);
            TableUtils.createTable(connectionSource, VQuarter.class);
            TableUtils.createTable(connectionSource, Work.class);
            TableUtils.createTable(connectionSource, WorkVQuarter.class);
            TableUtils.createTable(connectionSource, WorkWorker.class);
            TableUtils.createTable(connectionSource, WorkMachine.class);
            TableUtils.createTable(connectionSource, Global.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource source, int oldVersion, int newVersion) {
        try {
            List<String> allSql = new ArrayList<String>();
            switch (oldVersion) {
                case 17:
                    updateFromVersion17(db, source, oldVersion, newVersion);
                    break;
            }
            for (String sql : allSql) {
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    private void updateFromVersion17(SQLiteDatabase db, ConnectionSource source, int oldVersion, int newVersion) {
        try {
            Log.d("Update Db", "updating to ver 16");
            TransactionManager.callInTransaction(connectionSource, () -> {
                getWorkDao().executeRaw("ALTER TABLE `spraypesticide` ADD COLUMN periodCode VARCHAR;");
                return null;
            });
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    //Dao's for every table, returns the data
    public Dao<Land, Integer> getLandDao() {
        if (null == landDao) {
            try {
                landDao = getDao(Land.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return landDao;
    }

    public Dao<Machine, Integer> getMachineDao() {
        if (null == machineDao) {
            try {
                machineDao = getDao(Machine.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return machineDao;
    }

    public Dao<Worker, Integer> getWorkerDao() {
        if (null == workerDao) {
            try {
                workerDao = getDao(Worker.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workerDao;
    }

    public Dao<Task, Integer> getTaskDao() {
        if (null == taskDao) {
            try {
                taskDao = getDao(Task.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return taskDao;
    }

    public Dao<VQuarter, Integer> getVquarterDao() {
        if (null == vquarterDao) {
            try {
                vquarterDao = getDao(VQuarter.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return vquarterDao;
    }

    public Dao<Work, Integer> getWorkDao() {
        if (null == workDao) {
            try {
                workDao = getDao(Work.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workDao;
    }

    public Dao<WorkVQuarter, Integer> getWorkVQuarterDao() {
        if (null == workVquarterDao) {
            try {
                workVquarterDao = getDao(WorkVQuarter.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workVquarterDao;
    }

    public Dao<WorkWorker, Integer> getWorkWorkerDao() {
        if (null == workWorkerDao) {
            try {
                workWorkerDao = getDao(WorkWorker.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workWorkerDao;
    }

    public Dao<WorkMachine, Integer> getWorkMachineDao() {
        if (null == workMachineDao) {
            try {
                workMachineDao = getDao(WorkMachine.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workMachineDao;
    }

    public Dao<Global, Integer> getGlobalDao() {
        if (null == globalDao) {
            try {
                globalDao = getDao(Global.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return globalDao;
    }
}
