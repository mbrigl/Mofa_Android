package it.schmid.android.mofa.db;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.FruitQuality;
import it.schmid.android.mofa.model.Global;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends  OrmLiteSqliteOpenHelper{
	private static final String DATABASE_NAME = "MofaDB.sqlite";
	private static final int DATABASE_VERSION =15;
	
	// the DAO object we use to access the SimpleData table
    private Dao<Land, Integer> landDao = null;
    private Dao<Worker, Integer> workerDao = null;
    private Dao<Machine, Integer> machineDao = null;
    private Dao<Task, Integer> taskDao = null;
    private Dao<VQuarter, Integer> vquarterDao = null;
    private Dao<Pesticide,Integer> pesticideDao = null;
    private Dao<Fertilizer,Integer> fertilizerDao = null;
    private Dao<Work, Integer> workDao = null;
    private Dao<WorkVQuarter, Integer> workVquarterDao = null;
    private Dao<WorkWorker, Integer> workWorkerDao = null;
    private Dao<WorkMachine, Integer> workMachineDao = null;
    private Dao<WorkFertilizer, Integer> workFertilizerDao = null;
    private Dao<Spraying,Integer> sprayingDao=null;
    private Dao<SprayPesticide,Integer> sprayPesticideDao=null;
    private Dao<SprayFertilizer,Integer> sprayFertilizerDao=null;
    private Dao<SoilFertilizer, Integer> soilFertilizerDao=null;
    private Dao<Purchase, Integer> purchaseDao=null;
    private Dao<PurchasePesticide,Integer>purchasePesticideDao=null;
    private Dao<PurchaseFertilizer,Integer>purchaseFertilizerDao=null;
    private Dao<FruitQuality,Integer>fruitQualityDao=null;
    private Dao<Harvest,Integer>harvestDao=null;
	private Dao<Global,Integer>globalDao=null;
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
            TableUtils.createTable(connectionSource, Pesticide.class);
            TableUtils.createTable(connectionSource, Fertilizer.class);
            TableUtils.createTable(connectionSource, SoilFertilizer.class);
            TableUtils.createTable(connectionSource, Work.class);
            TableUtils.createTable(connectionSource, Spraying.class);
            TableUtils.createTable(connectionSource, SprayPesticide.class);
            TableUtils.createTable(connectionSource, SprayFertilizer.class);
            TableUtils.createTable(connectionSource, WorkVQuarter.class);
            TableUtils.createTable(connectionSource, WorkWorker.class);
            TableUtils.createTable(connectionSource, WorkMachine.class);
            TableUtils.createTable(connectionSource, WorkFertilizer.class);
            TableUtils.createTable(connectionSource, Purchase.class);
            TableUtils.createTable(connectionSource, PurchasePesticide.class);
            TableUtils.createTable(connectionSource, PurchaseFertilizer.class);
            TableUtils.createTable(connectionSource, FruitQuality.class);
            TableUtils.createTable(connectionSource, Harvest.class);
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
	            switch(oldVersion) 
	            {
	              case 1: 
	            	  updateFromVersion1(db, connectionSource, oldVersion, newVersion);
	                  break;
	              case 2:
	            	  updateFromVersion2(db, connectionSource, oldVersion, newVersion);
	                  break;
	              case 3: 
	            	  updateFromVersion3(db, connectionSource, oldVersion, newVersion);
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
	            	  updateFromVersion7(db,connectionSource,oldVersion,newVersion);
	            	  break;
	              case 8:
	            	  updateFromVersion8(db,connectionSource,oldVersion,newVersion);
	            	  break;
	              case 9:
	            	  updateFromVersion9(db,connectionSource,oldVersion,newVersion);
	            	  break; 
	              case 10:
	            	  updateFromVersion10(db,connectionSource,oldVersion,newVersion);
	            	  break;
                  case 11:
                      updateFromVersion11(db,connectionSource,oldVersion,newVersion);
                      break;
					case 12:
						updateFromVersion12(db,connectionSource,oldVersion,newVersion);
						break;
					case 13:
						updateFromVersion13(db,connectionSource,oldVersion,newVersion);
						break;
					case 14:
						updateFromVersion14(db,connectionSource,oldVersion,newVersion);
						break;
					case 15:
						updateFromVersion15(db,connectionSource,oldVersion,newVersion);
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
	
	
	private void updateFromVersion1(SQLiteDatabase db,	ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Fertilizer.class);
			TableUtils.createTable(connectionSource, SprayFertilizer.class);
			
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
		
	}
	private void updateFromVersion2(SQLiteDatabase db,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
		
	}
	private void updateFromVersion3(SQLiteDatabase db,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, SoilFertilizer.class);
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
	}
	private void updateFromVersion4(SQLiteDatabase db,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, WorkFertilizer.class);
			Log.d("DatabaseHelperClass", "Upgrade Vers. 4, creating WorkFertilizer Table");
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
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
			getPesticideDao().executeRaw("ALTER TABLE `pesticide` ADD COLUMN code VARCHAR;");
			getFertilizerDao().executeRaw("ALTER TABLE `fertilizer` ADD COLUMN code VARCHAR;");
			getSoilFertilizerDao().executeRaw("ALTER TABLE `soilfertilizer` ADD COLUMN code VARCHAR;");
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
	}
	private void updateFromVersion7(SQLiteDatabase db,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Purchase.class);
			TableUtils.createTableIfNotExists(connectionSource, PurchasePesticide.class);
			TableUtils.createTableIfNotExists(connectionSource, PurchaseFertilizer.class);
			getPesticideDao().executeRaw("ALTER TABLE `pesticide` ADD COLUMN barCode VARCHAR;");
			getFertilizerDao().executeRaw("ALTER TABLE `fertilizer` ADD COLUMN barCode VARCHAR;");
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		onUpgrade(db, connectionSource, oldVersion + 1, newVersion);
	}
	private void updateFromVersion8(SQLiteDatabase db,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
			final String CAT_IMPORT_PATH = "MoFaBackend/import/category"; //this is the new Dropbox Folder
		try {
			//we have to add a new folder for backward compatibility on upgrade

			TableUtils.createTableIfNotExists(connectionSource, FruitQuality.class);
			TableUtils.createTableIfNotExists(connectionSource, Harvest.class);
			
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}

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
			getPesticideDao().executeRaw("ALTER TABLE `pesticide` ADD COLUMN constraints VARCHAR;");
			getHarvestDao().executeRaw("ALTER TABLE `harvest` ADD COLUMN pass INTEGER;");
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
		try {
			getWorkDao().executeRaw("ALTER TABLE `Spraying` ADD COLUMN weather Integer;");
			TableUtils.createTableIfNotExists(connectionSource, Global.class);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
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
									 ConnectionSource connectionSource, int oldVersion, int newVersion){
		try {
			Log.d("Update Db", "updating to ver 15");
			TransactionManager.callInTransaction(connectionSource,
					new Callable<Void>() {

						public Void call() throws Exception {
							getWorkDao().executeRaw("ALTER TABLE spraying RENAME TO tmp;");
							getWorkDao().executeRaw("CREATE TABLE spraying (concentration DOUBLE PRECISION , id INTEGER PRIMARY KEY AUTOINCREMENT , wateramount DOUBLE PRECISION , weather INTEGER , work_id INTEGER );");
							getWorkDao().executeRaw("INSERT INTO spraying(concentration, id, wateramount, weather, work_id) SELECT concentration, id, wateramount, weather, work_id FROM tmp;");
							getWorkDao().executeRaw("DROP TABLE tmp;");
							getWorkDao().executeRaw("ALTER TABLE `vquarter` ADD COLUMN data VARCHAR;");
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
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return landDao;
    }
	public Dao<Machine, Integer> getMachineDao() {
        if (null == machineDao) {
            try {
                machineDao = getDao(Machine.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return machineDao;
            }
	public Dao<Worker, Integer> getWorkerDao() {
        if (null == workerDao) {
            try {
                workerDao = getDao(Worker.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workerDao;
    }
	public Dao<Task, Integer> getTaskDao() {
        if (null == taskDao) {
            try {
                taskDao = getDao(Task.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return taskDao;
    }
	public Dao<Pesticide, Integer> getPesticideDao(){
		if (null== pesticideDao) {
			try{
				pesticideDao = getDao(Pesticide.class);
			}catch (java.sql.SQLException e){
				e.printStackTrace();
			}
		}
		return pesticideDao;
	}
	public Dao<Fertilizer, Integer> getFertilizerDao(){
		if (null== fertilizerDao) {
			try{
				fertilizerDao = getDao(Fertilizer.class);
			}catch (java.sql.SQLException e){
				e.printStackTrace();
			}
		}
		return fertilizerDao;
	}
	public Dao<SoilFertilizer, Integer> getSoilFertilizerDao(){
		if (null== soilFertilizerDao){
			try{
				soilFertilizerDao = getDao(SoilFertilizer.class);
				}catch (java.sql.SQLException e){
					e.printStackTrace();
				}
		}
		return soilFertilizerDao;
	}
	public Dao<SprayPesticide, Integer> getSprayPesticideDao(){
		if (null== sprayPesticideDao) {
			try{
				sprayPesticideDao = getDao(SprayPesticide.class);
			}catch (java.sql.SQLException e){
				e.printStackTrace();
			}
		}
		return sprayPesticideDao;
	}
	public Dao<SprayFertilizer, Integer> getSprayFertilizerDao(){
		if (null== sprayFertilizerDao) {
			try{
				sprayFertilizerDao = getDao(SprayFertilizer.class);
			}catch (java.sql.SQLException e){
				e.printStackTrace();
			}
		}
		return sprayFertilizerDao;
	}
	
	public Dao<Spraying, Integer> getSprayingDao(){
		if (null== sprayingDao) {
			try{
				sprayingDao = getDao(Spraying.class);
			}catch (java.sql.SQLException e){
				e.printStackTrace();
			}
		}
		return sprayingDao;
	}
	public Dao<VQuarter, Integer> getVquarterDao() {
        if (null == vquarterDao) {
            try {
                vquarterDao = getDao(VQuarter.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return vquarterDao;
    }
	public Dao<Work, Integer> getWorkDao() {
        if (null == workDao) {
            try {
                workDao = getDao(Work.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workDao;
    }
	public Dao<WorkVQuarter, Integer> getWorkVQuarterDao() {
        if (null == workVquarterDao) {
            try {
            	workVquarterDao = getDao(WorkVQuarter.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workVquarterDao;
    }
	public Dao<WorkWorker, Integer> getWorkWorkerDao() {
        if (null == workWorkerDao) {
            try {
            	workWorkerDao = getDao(WorkWorker.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workWorkerDao;
    }
	public Dao<WorkMachine, Integer> getWorkMachineDao() {
        if (null == workMachineDao) {
            try {
            	workMachineDao = getDao(WorkMachine.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workMachineDao;
    }
	public Dao<WorkFertilizer, Integer> getWorkFertilizerDao() {
        if (null == workFertilizerDao) {
            try {
            	workFertilizerDao = getDao(WorkFertilizer.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return workFertilizerDao;
    }
	public Dao<Purchase, Integer> getPurchaseDao() {
        if (null == purchaseDao) {
            try {
            	purchaseDao = getDao(Purchase.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return purchaseDao;
    }
	public Dao<PurchasePesticide, Integer> getPurchasePesticideDao() {
        if (null == purchasePesticideDao) {
            try {
            	purchasePesticideDao = getDao(PurchasePesticide.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return purchasePesticideDao;
    }
	public Dao<PurchaseFertilizer, Integer> getPurchaseFertilizerDao() {
        if (null == purchaseFertilizerDao) {
            try {
            	purchaseFertilizerDao = getDao(PurchaseFertilizer.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return purchaseFertilizerDao;
    }
	public Dao<FruitQuality, Integer> getFruitQualityDao() {
        if (null == fruitQualityDao) {
            try {
            	fruitQualityDao = getDao(FruitQuality.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return fruitQualityDao;
    }
	public Dao<Harvest, Integer> getHarvestDao() {
        if (null == harvestDao) {
            try {
            	harvestDao = getDao(Harvest.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return harvestDao;
    }
	public Dao<Global, Integer> getGlobalDao() {
		if (null == globalDao) {
			try {
				globalDao = getDao(Global.class);
			}catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return globalDao;
	}
}
