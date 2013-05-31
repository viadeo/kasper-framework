package com.viadeo.kasper.db.datasource;

import javax.sql.DataSource;

public interface DataSourceBuilder {
	
	public DataSource build(DataSourceSetting dbSetting);

}
