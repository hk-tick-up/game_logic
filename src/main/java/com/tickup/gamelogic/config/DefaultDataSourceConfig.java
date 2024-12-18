package com.tickup.gamelogic.config;

import com.tickup.gamelogic.ml.repository.MLCorporationRepository;
import com.tickup.gamelogic.ml.repository.MLStockEventRepository;
import com.tickup.gamelogic.ml.repository.MLSummaryRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.tickup.gamelogic",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = MLCorporationRepository.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = MLStockEventRepository.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = MLSummaryRepository.class
                ),
        },
        entityManagerFactoryRef = "defaultEntityManagerFactory",
        transactionManagerRef = "defaultTransactionManager"
)
public class DefaultDataSourceConfig {

    @Primary
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "defaultEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("defaultDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.tickup.gamelogic")
                .persistenceUnit("default")
                .properties(Map.of(
                        "hibernate.dialect", "org.hibernate.dialect.MySQLDialect",  // 데이터베이스 플랫폼
                        "hibernate.hbm2ddl.auto", "update",                        // ddl-auto 설정
                        "hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy",  // 네이밍 전략
                        "hibernate.show_sql", "true",                              // SQL 로그 표시 여부
                        "hibernate.format_sql", "true"                             // SQL 포맷팅 여부
                ))
                .build();
    }

    @Primary
    @Bean(name = "defaultTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("defaultEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
