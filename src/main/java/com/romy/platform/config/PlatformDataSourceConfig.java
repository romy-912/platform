package com.romy.platform.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
@RequiredArgsConstructor
@MapperScan(basePackages = {"com.romy.platform.main"}, sqlSessionFactoryRef = "platformSqlSessionFactory", annotationClass = Mapper.class)
public class PlatformDataSourceConfig {

    private final ApplicationContext applicationContext;

    @Bean
    @ConfigurationProperties("romy.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @Primary
    @ConfigurationProperties("romy.datasource.platform")
    public DataSourceProperties platformDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource platformDataSource(@Qualifier("platformDataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        HikariConfig config = this.hikariConfig();

        dataSource.setConnectionTimeout(config.getConnectionTimeout());
        dataSource.setValidationTimeout(config.getValidationTimeout());
        dataSource.setIdleTimeout(config.getIdleTimeout());
        dataSource.setMinimumIdle(config.getMinimumIdle());
        dataSource.setMaxLifetime(config.getMaxLifetime());
        dataSource.setMaximumPoolSize(config.getMaximumPoolSize());
        dataSource.setAutoCommit(config.isAutoCommit());

        return dataSource;
    }

    @Bean
    @Primary
    public SqlSessionFactory platformSqlSessionFactory(@Qualifier("platformDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(applicationContext.getResources("classpath:mappers/platform/**/*.xml"));
        factory.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));

        return factory.getObject();

    }

    @Bean
    @Primary
    public SqlSessionTemplate platformSqlSessionTemplate(@Qualifier("platformSqlSessionFactory") SqlSessionFactory factory) {
        return new SqlSessionTemplate(factory);
    }

    @Primary
    @Bean(name = "platformTransactionManager")
    public DataSourceTransactionManager platformTransactionManager(@Qualifier("platformDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
