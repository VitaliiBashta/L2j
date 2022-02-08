package com.l2jserver.commons.database;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ConnectionFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);
	
	private static final int MAX_LIFETIME = 10;
	
	private final HikariDataSource dataSource;
	
	private ConnectionFactory(Builder builder) {
		dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(builder.url);
		dataSource.setUsername(builder.user);
		dataSource.setPassword(builder.password);
		dataSource.setMaximumPoolSize(builder.maxPoolSize);
		dataSource.setIdleTimeout(SECONDS.toMillis(builder.maxIdleTime));
		dataSource.setMaxLifetime(MINUTES.toMillis(MAX_LIFETIME));
	}
	
	public static ConnectionFactory getInstance() {
		return Builder.INSTANCE;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public Connection getConnection() {
		Connection con = null;
		while (con == null) {
			try {
				con = getDataSource().getConnection();
			} catch (SQLException ex) {
				LOG.warn("Unable to get a connection!", ex);
			}
		}
		return con;
	}
	
	public void close() {
		try {
			dataSource.close();
		} catch (Exception e) {
			LOG.warn("There has been a problem closing the data source!", e);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		protected static volatile ConnectionFactory INSTANCE;
		
		private String url;
		private String user;
		private String password;
		private int maxPoolSize;
		private int maxIdleTime;
		
		private Builder() {
		}
		
		public Builder withUrl(String url) {
			this.url = url;
			return this;
		}
		
		public Builder withUser(String user) {
			this.user = user;
			return this;
		}
		
		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}
		
		public Builder withMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
			return this;
		}
		
		public Builder withMaxIdleTime(int maxIdleTime) {
			this.maxIdleTime = maxIdleTime;
			return this;
		}
		
		public void build() {
			if (INSTANCE == null) {
				synchronized (this) {
					if (INSTANCE == null) {
						INSTANCE = new ConnectionFactory(this);
					} else {
						LOG.warn("Trying to build another Connection Factory!");
					}
				}
			}
		}
	}
}
