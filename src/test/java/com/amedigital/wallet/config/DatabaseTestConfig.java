package com.amedigital.wallet.config;

import java.sql.SQLException;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;

import com.amedigital.wallet.repository.mappers.BalanceMapper;
import com.amedigital.wallet.repository.mappers.CreditCardMapper;
import com.amedigital.wallet.repository.mappers.OrderItemMapper;
import com.amedigital.wallet.repository.mappers.OrderMapper;
import com.amedigital.wallet.repository.mappers.OwnerMapper;
import com.amedigital.wallet.repository.mappers.SimpleCreditCardMapper;
import com.amedigital.wallet.repository.mappers.StatementItemMapper;
import com.amedigital.wallet.repository.mappers.TransactionMapper;
import com.amedigital.wallet.repository.mappers.WalletMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
@Profile("test")
public class DatabaseTestConfig {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseTestConfig.class);
	
    @Value("${datasource.maximumPoolSize}")
    private Integer maximumPoolSize;
	
	@Autowired
	private MySQLContainer<?> mySqlTestsContainer;
	
	@Autowired
	private DataSource dataSource;
	

    @Bean(initMethod = "start", destroyMethod= "stop")
    public MySQLContainer<?> mySqlTestsContainer() {
    	LOG.info("Iniciando container docker com database de testes");
    	return DockerTestContainersConfig.getInstance().getMYSQL_CONTAINER();
    }
    
    @Bean(initMethod = "migrate")
    @DependsOn("dataSource")
    public Flyway flyway() throws SQLException {
    	
    	LOG.info("Registrando FlyWay e realizando migrate da base de testes: [{}]", dataSource.getConnection().getMetaData().getURL());
    	return Flyway.configure()
    			.locations("classpath:db/migration", "classpath:tests.flyway.data")
    			.dataSource(dataSource)
    			.load();
    }
	
    @Bean
    @DependsOn("mySqlTestsContainer")
    public DataSource dataSource() {
    	
		LOG.info("Registrando dataSource no banco de dados para testes na url: [{}]", mySqlTestsContainer.getJdbcUrl());
		
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(mySqlTestsContainer.getDriverClassName());
		config.setJdbcUrl(mySqlTestsContainer.getJdbcUrl());
		config.setUsername(mySqlTestsContainer.getUsername());
		config.setPassword(mySqlTestsContainer.getPassword());
        config.setMaximumPoolSize(this.maximumPoolSize);
        config.setConnectionTimeout(10000);
        
        return new HikariDataSource(config);
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        LOG.info("Registrando JDBI Mappers para testes...");

        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.registerRowMapper(new WalletMapper());
        jdbi.registerRowMapper(new OwnerMapper());
        jdbi.registerRowMapper(new OrderMapper());
        jdbi.registerRowMapper(new TransactionMapper());
        jdbi.registerRowMapper(new BalanceMapper());
        jdbi.registerRowMapper(new CreditCardMapper());
        jdbi.registerRowMapper(new StatementItemMapper());
        jdbi.registerRowMapper(new OrderItemMapper());
        jdbi.registerRowMapper(new SimpleCreditCardMapper());
        return jdbi;
    }

    @Bean
    public Scheduler jdbcScheduler() {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(this.maximumPoolSize));
    }

}
