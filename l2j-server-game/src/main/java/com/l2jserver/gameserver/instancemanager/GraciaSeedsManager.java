package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.tasks.UpdateSoDStateTask;
import com.l2jserver.gameserver.model.quest.Quest;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.logging.Logger;

import static com.l2jserver.gameserver.config.Configuration.graciaSeeds;

@Service
public final class GraciaSeedsManager {
	
	private static final Logger _log = Logger.getLogger(GraciaSeedsManager.class.getName());
	
	public static String ENERGY_SEEDS = "EnergySeeds";
	
	private static final byte SOITYPE = 2;
	
	private static final byte SOATYPE = 3;
	
	// Seed of Destruction
	private static final byte SODTYPE = 1;
	private int _SoDTiatKilled = 0;
	private int _SoDState = 1;
	private final Calendar _SoDLastStateChangeDate;

	private final QuestManager questManager;
	private final GlobalVariablesManager globalVariablesManager;
	protected GraciaSeedsManager(QuestManager questManager, GlobalVariablesManager globalVariablesManager) {
		this.questManager = questManager;
		this.globalVariablesManager = globalVariablesManager;
		_SoDLastStateChangeDate = Calendar.getInstance();
		loadData();
		handleSodStages();
	}
	
	public void loadData() {
		// Seed of Destruction variables
		if (globalVariablesManager.hasVariable("SoDState")) {
			_SoDState = globalVariablesManager.getInt("SoDState");
			_SoDTiatKilled = globalVariablesManager.getInt("SoDTiatKilled", _SoDTiatKilled);
			_SoDLastStateChangeDate.setTimeInMillis(globalVariablesManager.getLong("SoDLSCDate"));
		} else {
			// save Initial values
			saveData(SODTYPE);
		}
	}
	
	public void saveData(byte seedType) {
		switch (seedType) {
			case SODTYPE:
				// Seed of Destruction
				globalVariablesManager.set("SoDState", _SoDState);
				globalVariablesManager.set("SoDTiatKilled", _SoDTiatKilled);
				globalVariablesManager.set("SoDLSCDate", _SoDLastStateChangeDate.getTimeInMillis());
				break;
			case SOITYPE:
				// Seed of Infinity
				break;
			case SOATYPE:
				// Seed of Annihilation
				break;
			default:
				_log.warning(getClass().getSimpleName() + ": Unknown SeedType in SaveData: " + seedType);
				break;
		}
	}
	
	private void handleSodStages() {
		switch (_SoDState) {
			case 1:
				// do nothing, players should kill Tiat a few times
				break;
			case 2:
				// Conquest Complete state, if too much time is passed than change to defense state
				long timePast = System.currentTimeMillis() - _SoDLastStateChangeDate.getTimeInMillis();
				if (timePast >= graciaSeeds().getStage2Length()) {
					// change to Attack state because Defend state is not implemented
					setSoDState(1, true);
				} else {
					ThreadPoolManager.getInstance().scheduleEffect(new UpdateSoDStateTask(), graciaSeeds().getStage2Length() - timePast);
				}
				break;
			case 3:
				// not implemented
				setSoDState(1, true);
				break;
			default:
				_log.warning(getClass().getSimpleName() + ": Unknown Seed of Destruction state(" + _SoDState + ")! ");
		}
	}
	
	public void updateSodState() {
		final Quest quest = questManager.getQuest(ENERGY_SEEDS);
		if (quest == null) {
			_log.warning(getClass().getSimpleName() + ": missing EnergySeeds Quest!");
		} else {
			quest.notifyEvent("StopSoDAi", null, null);
		}
	}
	
	public void increaseSoDTiatKilled() {
		if (_SoDState == 1) {
			_SoDTiatKilled++;
			if (_SoDTiatKilled >= graciaSeeds().getTiatKillCountForNextState()) {
				setSoDState(2, false);
			}
			saveData(SODTYPE);
			Quest esQuest = questManager.getQuest(ENERGY_SEEDS);
			if (esQuest == null) {
				_log.warning(getClass().getSimpleName() + ": missing EnergySeeds Quest!");
			} else {
				esQuest.notifyEvent("StartSoDAi", null, null);
			}
		}
	}
	
	public int getSoDTiatKilled() {
		return _SoDTiatKilled;
	}
	
	public void setSoDState(int value, boolean doSave) {
		_log.info(getClass().getSimpleName() + ": New Seed of Destruction state -> " + value + ".");
		_SoDLastStateChangeDate.setTimeInMillis(System.currentTimeMillis());
		_SoDState = value;
		// reset number of Tiat kills
		if (_SoDState == 1) {
			_SoDTiatKilled = 0;
		}
		
		handleSodStages();
		
		if (doSave) {
			saveData(SODTYPE);
		}
	}
	
	public long getSoDTimeForNextStateChange() {
		// this should not happen!
		return switch (_SoDState) {
			case 1 -> -1;
			case 2 -> ((_SoDLastStateChangeDate.getTimeInMillis() + graciaSeeds().getStage2Length()) - System.currentTimeMillis());
			case 3 -> -1; // not implemented yet
			default -> -1;
		};
	}
	
	public Calendar getSoDLastStateChangeDate() {
		return _SoDLastStateChangeDate;
	}
	
	public int getSoDState() {
		return _SoDState;
	}
	

	public static GraciaSeedsManager getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
		protected static final GraciaSeedsManager _instance = new GraciaSeedsManager(null, null);
	}
}