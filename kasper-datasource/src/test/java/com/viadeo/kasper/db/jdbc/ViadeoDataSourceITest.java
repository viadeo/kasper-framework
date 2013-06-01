// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.jdbc;


import com.viadeo.kasper.db.datasource.DataSourceFactoryBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.*;

// integration test
@Ignore
public class ViadeoDataSourceITest {

    @Test
    public void testInit() throws SQLException {
        // Given
        final String dsFile = "classpath:datasources.json", dispatcherFile = "classpath:dispatcher.json";
        final DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile, dispatcherFile);

        // When
        final DataSource dataSource = new DataSource(builder);

        // Then
        assertNotNull(dataSource);
        dataSource.getConnection();
    }

    @Test
    public void testFind() throws SQLException {
        // Given
        final String dsFile = "classpath:datasources.json", dispatcherFile = "classpath:dispatcher.json";
        final DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile, dispatcherFile);

        // When
        DataSource dataSource = new DataSource(builder);

        // Then
        assertNotNull(dataSource);

        // Given
        final Connection connection =  dataSource.getConnection();
        connection.getMetaData();
        final String sql = "select * from Member";
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // When
        final ResultSet resultSet = preparedStatement.executeQuery();

        // Then
        assertNotNull(resultSet);

        // When
        final ResultSetMetaData metaData = resultSet.getMetaData();

        // Then
        assertNotNull(metaData);

        int nbColumn = metaData.getColumnCount();
        String message = "| %s : %s ";
        while(resultSet.next()) {
            System.out.println("----------------------------------------------------------");
            for (int i = 1; i <= nbColumn; i++) {
                System.out.print( String.format(message, metaData.getColumnLabel(i), resultSet.getObject(i)) );
            }
            System.out.println("----------------------------------------------------------");
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    @Test
    public void testTwoOperationsBeforePrepareStatement() throws SQLException {
        // Given
        final String dsFile = "classpath:datasources.json", dispatcherFile = "classpath:dispatcher.json";
        final DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile, dispatcherFile);

        // When
        final DataSource dataSource = new DataSource(builder);

        // Then
        assertNotNull(dataSource);

        // When
        final Connection connection =  dataSource.getConnection();
        connection.getMetaData();
        connection.setClientInfo("client.info", "Viadeo");

        final String sql = "select * from Action";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // When
        final ResultSet resultSet = preparedStatement.executeQuery();

        // Then
        assertNotNull(resultSet);

        // When
        final ResultSetMetaData metaData = resultSet.getMetaData();

        // Then
        assertNotNull(metaData);

        // When
        final Properties prop = connection.getClientInfo();
        final String value = (String) prop.get("client.info");

        // Then
        assertNotSame("Viadeo", value);

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

}
