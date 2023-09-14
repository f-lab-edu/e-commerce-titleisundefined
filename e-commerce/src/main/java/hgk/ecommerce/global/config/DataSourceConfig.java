package hgk.ecommerce.global.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Profile("release")
public class DataSourceConfig {
    private static final String MASTER_DATASOURCE = "masterDataSource";
    private static final String SLAVE_DATASOURCE = "slaveDataSource";

    @Bean(MASTER_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.master.hikari")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(SLAVE_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public DataSource routingDataSource(@Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
                                        @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        HashMap<Object, Object> sourceHashMap = new HashMap<>();
        sourceHashMap.put("master", masterDataSource);
        sourceHashMap.put("slave", slaveDataSource);

        Map<Object, Object> dataSourceMap = Collections.unmodifiableMap(new LinkedHashMap<>(sourceHashMap));

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }

    static class RoutingDataSource extends AbstractRoutingDataSource {

        @Override
        protected Object determineCurrentLookupKey() {
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

            return isReadOnly ? "slave" : "master";
        }
    }
}
