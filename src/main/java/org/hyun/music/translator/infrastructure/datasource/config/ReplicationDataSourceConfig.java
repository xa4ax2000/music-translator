package org.hyun.music.translator.infrastructure.datasource.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hyun.music.translator.infrastructure.datasource.ReplicationRoutingDataSource;
import org.hyun.music.translator.infrastructure.datasource.config.properties.DatabaseConfigProperties;
import org.hyun.music.translator.infrastructure.datasource.config.properties.DatabaseReadOnlyConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReplicationDataSourceConfig {
    @Autowired
    private DatabaseConfigProperties databaseConfigProperties;

    @Autowired
    private DatabaseReadOnlyConfigProperties databaseReadOnlyConfigProperties;

    /**
     * Main Datasource
     * <p>Application must use this dataSource.</p>
     */
    @Primary
    @Bean
    // @DependsOn required so we can ensure these beans are initialized before initializing this bean!
    @DependsOn({"writeDataSource", "readDataSource", "routingDataSource"})
    public DataSource dataSource() { return new LazyConnectionDataSourceProxy(routingDataSource());}

    @Bean
    public DataSource routingDataSource(){
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("write", writeDataSource());
        dataSourceMap.put("read", readDataSource());
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource());

        return routingDataSource;
    }

    @Bean(destroyMethod = "close")
    public DataSource writeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(databaseConfigProperties.getDataSourceClassName());
        config.addDataSourceProperty("url", databaseConfigProperties.getUrl());
        config.addDataSourceProperty("user", databaseConfigProperties.getUsername());
        config.addDataSourceProperty("password", databaseConfigProperties.getPassword());

        return new HikariDataSource(config);
    }

    @Bean(destroyMethod = "close")
    public DataSource readDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(databaseReadOnlyConfigProperties.getDataSourceClassName());
        config.addDataSourceProperty("url", databaseReadOnlyConfigProperties.getUrl());
        config.addDataSourceProperty("user", databaseReadOnlyConfigProperties.getUsername());
        config.addDataSourceProperty("password", databaseReadOnlyConfigProperties.getPassword());

        return new HikariDataSource(config);
    }
}
