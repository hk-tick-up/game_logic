package com.tickup.gamelogic.config;

import com.tickup.gamelogic.stocksettings.repository.*;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
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
        basePackages = "com.tickup.gamelogic.ml",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = CompanyInfoRepository.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = GameEventsRepository.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = StockDataRepository.class
                ),
        },
        entityManagerFactoryRef = "mlEntityManagerFactory",
        transactionManagerRef = "mlTransactionManager"
)
@EntityScan(basePackages = "com.tickup.gamelogic.ml")
public class MLDataSourceConfig {

    @Bean(name = "mlDataSource")
    @ConfigurationProperties(prefix = "spring.ml-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mlDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.tickup.gamelogic.ml")
                .persistenceUnit("ml")
                .properties(Map.of(
                        "hibernate.dialect", "org.hibernate.dialect.MySQLDialect",  // 데이터베이스 플랫폼
                        "hibernate.hbm2ddl.auto", "none",                        // ddl-auto 설정
                        "hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",  // 네이밍 전략
                        "hibernate.show_sql", "true",                              // SQL 로그 표시 여부
                        "hibernate.format_sql", "true"                             // SQL 포맷팅 여부
                ))
                .build();
    }

    @Bean(name = "mlTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("mlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

