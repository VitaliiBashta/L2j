package com.l2jserver.gameserver.model;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.data.xml.impl.UIData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIKeysSettings {
	
	private static final Logger _log = Logger.getLogger(UIKeysSettings.class.getName());
	
	private final int _playerObjId;
	private Map<Integer, List<ActionKey>> _storedKeys;
	private Map<Integer, List<Integer>> _storedCategories;
	private boolean _saved = true;
	
	public UIKeysSettings(int playerObjId) {
		_playerObjId = playerObjId;
		loadFromDB();
	}
	
	public void storeAll(Map<Integer, List<Integer>> catMap, Map<Integer, List<ActionKey>> keyMap) {
		_saved = false;
		_storedCategories = catMap;
		_storedKeys = keyMap;
	}
	
	public void storeCategories(Map<Integer, List<Integer>> catMap) {
		_saved = false;
		_storedCategories = catMap;
	}
	
	public Map<Integer, List<Integer>> getCategories() {
		return _storedCategories;
	}
	
	public void storeKeys(Map<Integer, List<ActionKey>> keyMap) {
		_saved = false;
		_storedKeys = keyMap;
	}
	
	public Map<Integer, List<ActionKey>> getKeys() {
		return _storedKeys;
	}
	
	public void loadFromDB() {
		getCatsFromDB();
		getKeysFromDB();
	}
	
	/**
	 * Save Categories and Mapped Keys into GameServer DataBase
	 */
	public void saveInDB() {
		StringBuilder query;
		if (_saved) {
			return;
		}
		
		// TODO(Zoey76): Refactor this to use batch.
		query = new StringBuilder("REPLACE INTO character_ui_categories (`charId`, `catId`, `order`, `cmdId`) VALUES ");
		for (int category : _storedCategories.keySet()) {
			int order = 0;
			for (int key : _storedCategories.get(category)) {
				query.append("(").append(_playerObjId).append(", ").append(category).append(", ").append(order++).append(", ").append(key).append("),");
			}
		}
		query = new StringBuilder(query.substring(0, query.length() - 1) + "; ");
		try (var con = ConnectionFactory.getInstance().getConnection();
			var statement = con.prepareStatement(query.toString())) {
			statement.execute();
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception: saveInDB(): " + e.getMessage(), e);
		}
		
		query = new StringBuilder("REPLACE INTO character_ui_actions (`charId`, `cat`, `order`, `cmd`, `key`, `tgKey1`, `tgKey2`, `show`) VALUES");
		for (List<ActionKey> keyLst : _storedKeys.values()) {
			int order = 0;
			for (ActionKey key : keyLst) {
				query.append(key.getSqlSaveString(_playerObjId, order++)).append(",");
			}
		}
		query = new StringBuilder(query.substring(0, query.length() - 1) + ";");
		
		try (var con = ConnectionFactory.getInstance().getConnection();
			var statement = con.prepareStatement(query.toString())) {
			statement.execute();
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception: saveInDB(): " + e.getMessage(), e);
		}
		_saved = true;
	}
	
	public void getCatsFromDB() {
		if (_storedCategories != null) {
			return;
		}
		
		_storedCategories = new HashMap<>();
		
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement("SELECT * FROM character_ui_categories WHERE `charId` = ? ORDER BY `catId`, `order`")) {
			ps.setInt(1, _playerObjId);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					UIData.addCategory(_storedCategories, rs.getInt("catId"), rs.getInt("cmdId"));
				}
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception: getCatsFromDB(): " + e.getMessage(), e);
		}
		
		if (_storedCategories.isEmpty()) {
			_storedCategories = UIData.getInstance().getCategories();
		}
	}
	
	public void getKeysFromDB() {
		if (_storedKeys != null) {
			return;
		}
		
		_storedKeys = new HashMap<>();
		
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement("SELECT * FROM character_ui_actions WHERE `charId` = ? ORDER BY `cat`, `order`")) {
			ps.setInt(1, _playerObjId);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					int cat = rs.getInt("cat");
					int cmd = rs.getInt("cmd");
					int key = rs.getInt("key");
					int tgKey1 = rs.getInt("tgKey1");
					int tgKey2 = rs.getInt("tgKey2");
					int show = rs.getInt("show");
					UIData.addKey(_storedKeys, cat, new ActionKey(cat, cmd, key, tgKey1, tgKey2, show));
				}
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception: getKeysFromDB(): " + e.getMessage(), e);
		}
		
		if (_storedKeys.isEmpty()) {
			_storedKeys = UIData.getInstance().getKeys();
		}
	}
	
	public boolean isSaved() {
		return _saved;
	}
}
