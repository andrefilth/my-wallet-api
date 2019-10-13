package com.amedigital.wallet.config;

import com.amedigital.wallet.repository.mappers.*;
import com.amedigital.wallet.repository.mappers.query.SimpleReleaseStatementMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.sql.DataSource;
import java.util.concurrent.Executors;

@Configuration
public class DatabaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${datasource.class}")
    private String driverClassName;

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${datasource.maximumPoolSize}")
    private Integer maximumPoolSize;

    @Bean
    public DataSource dataSource() {
        LOG.info("Conectando no banco de dados [{}] com o numero máximo de pool [{}]...", url, maximumPoolSize);

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(this.driverClassName);
        config.setJdbcUrl(this.url);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.setMaximumPoolSize(this.maximumPoolSize);

        return new HikariDataSource(config);
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        LOG.info("Registrando JDBI Mappers...");
        return Jdbi.create(dataSource)
                .registerRowMapper(new WalletMapper())
                .registerRowMapper(new OwnerMapper())
                .registerRowMapper(new OrderMapper())
                .registerRowMapper(new TransactionMapper())
                .registerRowMapper(new BalanceMapper())
                .registerRowMapper(new CreditCardMapper())
                .registerRowMapper(new StatementItemMapper())
                .registerRowMapper(new OrderItemMapper())
                .registerRowMapper(new SimpleCreditCardMapper())
                .registerRowMapper(new TransactionDataItemMapper())
                .registerRowMapper(new SimpleCreditCardMapper())
                .registerRowMapper(new SimpleReleaseStatementMapper());
    }

    @Bean
    public Scheduler jdbcScheduler() {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(this.maximumPoolSize));
    }

}
