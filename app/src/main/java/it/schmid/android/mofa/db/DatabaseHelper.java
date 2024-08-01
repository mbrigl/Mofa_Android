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
import java.util.concurrent.Callable;

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

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
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
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        try {
            List<String> allSql = new ArrayList<String>();
            switch (oldVersion) {
                case 1:
                    updateFromVersion1(db, connectionSource, oldVersion, newVersion);
                    break;
                case 2:
                    updateFromVersion2(db, connectionSource, oldVersion, newVersion);
                    break;
                case 4:
                    updateFromVersion4(db, connectionSource, oldVersion, newVersion);
                    break;
                //allSql.add("alter table AdData add column `new_col` VARCHAR");
                //allSql.add("alter table AdData add column `new_col2` VARCHAR");
                case 5:
                    updateFromVersion5(db, connectionSource, oldVersion, newVersion);
                    break;
                case 6:
                    updateFromVersion6(db, connectionSource, oldVersion, newVersion);
                    break;
                case 7:
                    updateFromVersion7(db, connectionSource, oldVersion, newVersion);
                    break;
                case 8:
                    updateFromVersion8(db, connectionSource, oldVersion, newVersion);
                    break;
                case 9:
                    updateFromVersion9(db, connectionSource, oldVersion, newVersion);
                    break;
                case 10:
                    updateFromVersion10(db, connectionSource, oldVersion, newVersion);
                    break;
                case 11:
                    updateFromVersion11(db, connectionSource, oldVersion, newVersion);
                    break;
                case 12:
                    updateFromVersion12(db, connectionSource, oldVersion, newVersion);
                    break;
                case 13:
                    updateFromVersion13(db, connectionSource, oldVersion, newVersion);
                    break;
                case 14:
                    updateFromVersion14(db, connectionSource, oldVersion, newVersion);
                    break;
                case 15:
                    updateFromVersion15(db, connectionSource, oldVersion, newVersion);
                    break;
                case 16:
                    updateFromVersion16(db, connectionSource, oldVersion, newVersion);
                    break;
                case 17:
                    updateFromVersion17(db, connectionSource, oldVersion, newVersion);
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


    private void updateFromVersion1(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);

    }

    private void updateFromVersion2(SQLiteDatabase db,
                                    ConnectionSource connectionSource, int oldVersion, int newVersion) {
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);

    }

    private void updateFromVersion4(SQLiteDatabase db,
                                    ConnectionSource connectionSource, int oldVersion, int newVersion) {
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);

    }

    private void updateFromVersion5(SQLiteDatabase db,
                                    ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            getWorkDao().executeRaw("ALTER TABLE `work` ADD COLUMN note VARCHAR;");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion6(SQLiteDatabase db,
                                    ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            getLandDao().executeRaw("ALTER TABLE `land` ADD COLUMN code VARCHAR;");
            getVquarterDao().executeRaw("ALTER TABLE `vquarter` ADD COLUMN code VARCHAR;");
            getWorkerDao().executeRaw("ALTER TABLE `worker` ADD COLUMN code VARCHAR;");
            getMachineDao().executeRaw("ALTER TABLE `machine` ADD COLUMN code VARCHAR;");
            getTaskDao().executeRaw("ALTER TABLE `task` ADD COLUMN code VARCHAR;");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion7(SQLiteDatabase db,
                                    ConnectionSource connectionSource, int oldVersion, int newVersion) {
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion8(SQLiteDatabase db,
                                    ConnectionSource connectionSource, int oldVersion, int newVersion) {
        final String CAT_IMPORT_PATH = "MoFaBackend/import/category"; //this is the new Dropbox Folder
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion9(SQLiteDatabase db,
                                    ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            getWorkDao().executeRaw("ALTER TABLE `work` ADD COLUMN valid SMALLINT DEFAULT 0;");

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion10(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            getWorkDao().executeRaw("ALTER TABLE `work` ADD COLUMN sended SMALLINT DEFAULT 0;");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion11(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            getTaskDao().executeRaw("ALTER TABLE `task` ADD COLUMN type VARCHAR(1);");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion12(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            getVquarterDao().executeRaw("ALTER TABLE `vquarter` ADD COLUMN size REAL;");

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion13(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion14(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            getWorkDao().executeRaw("ALTER TABLE `Global` ADD COLUMN workId Integer;");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion15(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.d("Update Db", "updating to ver 15");
            TransactionManager.callInTransaction(connectionSource,
                    new Callable<Void>() {

                        public Void call() throws Exception {
                            getWorkDao().executeRaw("ALTER TABLE spraying RENAME TO tmp;");
                            getWorkDao().executeRaw("CREATE TABLE spraying (concentration DOUBLE PRECISION , id INTEGER PRIMARY KEY AUTOINCREMENT , wateramount DOUBLE PRECISION , work_id INTEGER );");
                            getWorkDao().executeRaw("INSERT INTO spraying(concentration, id, wateramount, work_id) SELECT concentration, id, wateramount, work_id FROM tmp;");
                            getWorkDao().executeRaw("DROP TABLE tmp;");
                            getWorkDao().executeRaw("ALTER TABLE `vquarter` ADD COLUMN data VARCHAR;");
                            getWorkDao().executeRaw("ALTER TABLE `pesticide` ADD COLUMN data VARCHAR;");
                            getWorkDao().executeRaw("ALTER TABLE `fertilizer` ADD COLUMN data VARCHAR;");
                            getWorkDao().executeRaw("ALTER TABLE `task` ADD COLUMN data VARCHAR;");
                            getWorkDao().executeRaw("ALTER TABLE `work` ADD COLUMN data VARCHAR;");
                            getWorkDao().executeRaw("ALTER TABLE `purchasefertilizer` ADD COLUMN data VARCHAR;");
                            getWorkDao().executeRaw("ALTER TABLE `purchasepesticide` ADD COLUMN data VARCHAR;");
                            return null;
                        }
                    });
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion16(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.d("Update Db", "updating to ver 16");
            TransactionManager.callInTransaction(connectionSource,
                    new Callable<Void>() {

                        public Void call() throws Exception {

                            getWorkDao().executeRaw("ALTER TABLE `pesticide` ADD COLUMN status VARCHAR;");
                            return null;
                        }
                    });
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion17(SQLiteDatabase db,
                                     ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.d("Update Db", "updating to ver 16");
            TransactionManager.callInTransaction(connectionSource,
                    new Callable<Void>() {

                        public Void call() throws Exception {


                            getWorkDao().executeRaw("ALTER TABLE `spraypesticide` ADD COLUMN periodCode VARCHAR;");
                            return null;
                        }
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
