package com.l2jserver.gameserver.model.variables;

import com.l2jserver.commons.database.ConnectionFactory;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AccountVariables extends AbstractVariables {
	
	private static final Logger _log = Logger.getLogger(AccountVariables.class.getName());
	
	// SQL Queries.
	private static final String SELECT_QUERY = "SELECT * FROM account_gsdata WHERE account_name = ?";
	
	private static final String DELETE_QUERY = "DELETE FROM account_gsdata WHERE account_name = ?";
	
	private static final String INSERT_QUERY = "INSERT INTO account_gsdata (account_name, var, value) VALUES (?, ?, ?)";
	
	private final String _accountName;
	
	public AccountVariables(String accountName) {
		_accountName = accountName;
		restoreMe();
	}
	
	public boolean restoreMe() {
		// Restore previous variables.
		try (var con = ConnectionFactory.getInstance().getConnection();
			var st = con.prepareStatement(SELECT_QUERY)) {
			st.setString(1, _accountName);
			try (var rs = st.executeQuery()) {
				while (rs.next()) {
					set(rs.getString("var"), rs.getString("value"));
				}
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't restore variables for: " + _accountName, e);
			return false;
		} finally {
			compareAndSetChanges(true, false);
		}
		return true;
	}
	
	public boolean storeMe() {
		// No changes, nothing to store.
		if (!hasChanges()) {
			return false;
		}
		
		try (var con = ConnectionFactory.getInstance().getConnection()) {
			// Clear previous entries.
			try (var st = con.prepareStatement(DELETE_QUERY)) {
				st.setString(1, _accountName);
				st.execute();
			}
			
			// Insert all variables.
			try (var st = con.prepareStatement(INSERT_QUERY)) {
				st.setString(1, _accountName);
				for (Entry<String, Object> entry : getSet().entrySet()) {
					st.setString(2, entry.getKey());
					st.setString(3, String.valueOf(entry.getValue()));
					st.addBatch();
				}
				st.executeBatch();
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't update variables for: " + _accountName, e);
			return false;
		} finally {
			compareAndSetChanges(true, false);
		}
		return true;
	}
}
