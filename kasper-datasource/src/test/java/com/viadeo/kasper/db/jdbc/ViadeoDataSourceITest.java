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
        String dsFile = "classpath:datasources.json", dispatcherFile = "classpath:dispatcher.json";
        
        DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile, dispatcherFile);
        ViadeoDataSource dataSource = new ViadeoDataSource(builder);
        assertNotNull(dataSource);
        dataSource.getConnection();
    }

    @Test
    public void testFind() throws SQLException {
        String dsFile = "classpath:datasources.json", dispatcherFile = "classpath:dispatcher.json";
        DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile, dispatcherFile);
        ViadeoDataSource dataSource = new ViadeoDataSource(builder);
        assertNotNull(dataSource);
        Connection connection =  dataSource.getConnection();
        connection.getMetaData();
        String sql = "select * from Member";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        assertNotNull(resultSet);
        ResultSetMetaData metaData = resultSet.getMetaData();
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
        String dsFile = "classpath:datasources.json", dispatcherFile = "classpath:dispatcher.json";
        DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile, dispatcherFile);
        ViadeoDataSource dataSource = new ViadeoDataSource(builder);
        assertNotNull(dataSource);
        Connection connection =  dataSource.getConnection();
        connection.getMetaData();
        connection.setClientInfo("client.info", "Viadeo");
        String sql = "select * from Action";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        assertNotNull(resultSet);
        ResultSetMetaData metaData = resultSet.getMetaData();
        assertNotNull(metaData);
        Properties prop = connection.getClientInfo();
        String value = (String)prop.get("client.info");
        assertNotSame("Viadeo", value);
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }


}
