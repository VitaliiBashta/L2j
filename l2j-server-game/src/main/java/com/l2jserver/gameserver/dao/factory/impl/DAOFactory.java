package com.l2jserver.gameserver.dao.factory.impl;

import static com.l2jserver.gameserver.config.Configuration.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.dao.factory.IDAOFactory;

public class DAOFactory {
	private DAOFactory() {
		// Hide constructor.
	}
	
	public static IDAOFactory getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private static final Logger LOG = LoggerFactory.getLogger(DAOFactory.class);
		
		protected static IDAOFactory INSTANCE = null;
		
		static {
			switch (database().getEngine()) {
				case "MariaDB", "MySQL" -> INSTANCE = MySQLDAOFactory.INSTANCE;
			}
			LOG.info("Using {} DAO Factory.", INSTANCE.getClass().getSimpleName().replace("DAOFactory", ""));
		}
	}
}
