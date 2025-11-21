package com.romy.platform.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
@RequiredArgsConstructor
@MapperScan(basePackages = {"com.romy.platform.sub"}, sqlSessionFactoryRef = "subSqlSessionFactory", annotationClass = Mapper.class)
public class SubDataSourceConfig {

    private final ApplicationContext applicationContext;

    private final HikariConfig hikariConfig;

    @Bean
    @ConfigurationProperties("romy.datasource.sub")
    public DataSourceProperties subDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource subDataSource(@Qualifier("subDataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        dataSource.setConnectionTimeout(hikariConfig.getConnectionTimeout());
        dataSource.setValidationTimeout(hikariConfig.getValidationTimeout());
        dataSource.setIdleTimeout(hikariConfig.getIdleTimeout());
        dataSource.setMinimumIdle(hikariConfig.getMinimumIdle());
        dataSource.setMaxLifetime(hikariConfig.getMaxLifetime());
        dataSource.setMaximumPoolSize(hikariConfig.getMaximumPoolSize());
        dataSource.setAutoCommit(hikariConfig.isAutoCommit());

        return dataSource;
    }

    @Bean
    public SqlSessionFactory subSqlSessionFactory(@Qualifier("subDataSource") DataSource subDataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(subDataSource);
        factoryBean.setMapperLocations(applicationContext.getResources("classpath:mappers/sub/**/*.xml"));
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
        return factoryBean.getObject();
    }

    @Bean(name = "subTransactionManager")
    public DataSourceTransactionManager subTransactionManager(@Qualifier("subDataSource") DataSource subDataSource) {
        return new DataSourceTransactionManager(subDataSource);
    }
}
