// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.datasource;

import com.viadeo.kasper.shard.settings.DataSourceSetting;

import javax.sql.DataSource;

public interface DataSourceProvider {
	
	DataSource provide(DataSourceSetting dbSetting);

}
