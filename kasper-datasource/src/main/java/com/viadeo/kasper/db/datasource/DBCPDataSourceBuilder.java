// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.datasource;

import com.google.common.base.Strings;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;

public class DBCPDataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(final DataSourceSetting dbSetting) {
		
		final BasicDataSource ds = new BasicDataSource();
		ds.setUsername(dbSetting.getUserName());
		ds.setPassword(dbSetting.getPassword());
		ds.setDriverClassName(dbSetting.getDriverClassName());
		ds.setUrl(dbSetting.getUrl());

		if (null != dbSetting.getMaxIdle()) {
			ds.setMaxIdle(dbSetting.getMaxIdle());
		}
		if (null != dbSetting.getMaxActive()) {
			ds.setMaxActive(dbSetting.getMaxActive());
		}
		if (null != dbSetting.getMaxWait()) {
			ds.setMaxWait(dbSetting.getMaxWait());
		}
		if (null != dbSetting.getMinEvictableIdleTimeMillis()) {
			ds.setMinEvictableIdleTimeMillis(dbSetting.getMinEvictableIdleTimeMillis());
		}
		if (null != dbSetting.getTestWhileIdle()) {
			ds.setTestWhileIdle(dbSetting.getTestWhileIdle());
		}
		if (null != dbSetting.getTimeBetweenEvictionRunsMillis()) {
			ds.setTimeBetweenEvictionRunsMillis(dbSetting.getTimeBetweenEvictionRunsMillis());
		}

		if (!Strings.isNullOrEmpty(dbSetting.getValidationQuery())) {
			ds.setValidationQuery(dbSetting.getValidationQuery());
		}

		if (null != dbSetting.getTestOnBorrow()) {
			ds.setTestOnBorrow(dbSetting.getTestOnBorrow());
		}

		if (null != dbSetting.getTestOnReturn()) {
			ds.setTestOnReturn(dbSetting.getTestOnReturn());
		}

        /*
		JMXHelper.registerObject(ds, getFullUrl().replace(':', '-').replace('=', '-').replace('?', '-').replace('/', '-'),
				"com.viadeo.dao.datasource");
        */

		return ds;
	}

}
