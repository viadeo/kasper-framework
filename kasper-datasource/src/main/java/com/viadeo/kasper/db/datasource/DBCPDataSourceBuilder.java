package com.viadeo.kasper.db.datasource;

import com.google.common.base.Strings;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;

public class DBCPDataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(DataSourceSetting dbSetting) {
		
		final BasicDataSource ds = new BasicDataSource();
		ds.setUsername(dbSetting.getUserName());
		ds.setPassword(dbSetting.getPassword());
		ds.setDriverClassName(dbSetting.getDriverClassName());
		ds.setUrl(dbSetting.getUrl());

		if (dbSetting.getMaxIdle() != null) {
			ds.setMaxIdle(dbSetting.getMaxIdle());
		}
		if (dbSetting.getMaxActive() != null) {
			ds.setMaxActive(dbSetting.getMaxActive());
		}
		if (dbSetting.getMaxWait() != null) {
			ds.setMaxWait(dbSetting.getMaxWait());
		}
		if (dbSetting.getMinEvictableIdleTimeMillis() != null) {
			ds.setMinEvictableIdleTimeMillis(dbSetting.getMinEvictableIdleTimeMillis());
		}
		if (dbSetting.getTestWhileIdle() != null) {
			ds.setTestWhileIdle(dbSetting.getTestWhileIdle());
		}
		if (dbSetting.getTimeBetweenEvictionRunsMillis() != null) {
			ds.setTimeBetweenEvictionRunsMillis(dbSetting.getTimeBetweenEvictionRunsMillis());
		}

		if (!Strings.isNullOrEmpty(dbSetting.getValidationQuery())) {
			ds.setValidationQuery(dbSetting.getValidationQuery());
		}

		if (dbSetting.getTestOnBorrow() != null) {
			ds.setTestOnBorrow(dbSetting.getTestOnBorrow());
		}

		if (dbSetting.getTestOnReturn() != null) {
			ds.setTestOnReturn(dbSetting.getTestOnReturn());
		}

        /*
		JMXHelper.registerObject(ds, getFullUrl().replace(':', '-').replace('=', '-').replace('?', '-').replace('/', '-'),
				"com.viadeo.dao.datasource");

        */
		return ds;
	}

}
