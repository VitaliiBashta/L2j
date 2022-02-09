package com.l2jserver.gameserver.instancemanager;

import java.util.Map.Entry;

import com.l2jserver.gameserver.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.variables.AbstractVariables;
import org.springframework.stereotype.Service;

@Service
public final class GlobalVariablesManager extends AbstractVariables {
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalVariablesManager.class);
	
	private static final String SELECT_QUERY = "SELECT * FROM global_variables";
	
	private static final String DELETE_QUERY = "DELETE FROM global_variables";
	
	private static final String INSERT_QUERY = "INSERT INTO global_variables (var, value) VALUES (?, ?)";
	private final Context context;

	protected GlobalVariablesManager(Context context) {
		this.context = context;
		restoreMe();
	}
	
	@Override
	public boolean restoreMe() {
		// Restore previous variables.
		try (var con = context.connectionFactory.getConnection();
			var st = con.createStatement();
			var rs = st.executeQuery(SELECT_QUERY)) {
			while (rs.next()) {
				set(rs.getString("var"), rs.getString("value"));
			}
		} catch (Exception ex) {
			LOG.warn("Couldn't restore global variables!", ex);
			return false;
		} finally {
			compareAndSetChanges(true, false);
		}
		LOG.info("Loaded {} variables.", getSet().size());
		return true;
	}
	
	@Override
	public boolean storeMe() {
		// No changes, nothing to store.
		if (!hasChanges()) {
			return false;
		}
		
		try (var con = ConnectionFactory.getInstance().getConnection();
			var del = con.createStatement();
			var st = con.prepareStatement(INSERT_QUERY)) {
			// Clear previous entries.
			del.execute(DELETE_QUERY);
			
			// Insert all variables.
			for (Entry<String, Object> entry : getSet().entrySet()) {
				st.setString(1, entry.getKey());
				st.setString(2, String.valueOf(entry.getValue()));
				st.addBatch();
			}
			st.executeBatch();
		} catch (Exception ex) {
			LOG.warn("Couldn't save global variables to database!", ex);
			return false;
		} finally {
			compareAndSetChanges(true, false);
		}
		LOG.info("Stored {} variables.", getSet().size());
		return true;
	}
	
	public static GlobalVariablesManager getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
		protected static final GlobalVariablesManager _instance = new GlobalVariablesManager(null);
	}
}