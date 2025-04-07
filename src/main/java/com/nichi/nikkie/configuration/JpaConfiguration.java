package com.nichi.nikkie.configuration;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class JpaConfiguration {

    private final XMLMapperConfiguration xmlMapperConfiguration;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(xmlMapperConfiguration.getDbUrl());
        dataSource.setUsername(xmlMapperConfiguration.getDbUsername());
        dataSource.setPassword(xmlMapperConfiguration.getDbPassword());
        dataSource.setDriverClassName(xmlMapperConfiguration.getDbDriver());
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.nichi.nikkie.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", xmlMapperConfiguration.getHibernateDialect());
        properties.setProperty("hibernate.hbm2ddl.auto", xmlMapperConfiguration.getDdlAuto());
        properties.setProperty("hibernate.show_sql", "true");

        em.setJpaProperties(properties);
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
