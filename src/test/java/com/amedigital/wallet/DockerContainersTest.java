package com.amedigital.wallet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.endoint.request.method.BankTransferMethodRequest;
import com.amedigital.wallet.endoint.request.order.CashOutOrderRequest;
import com.amedigital.wallet.security.WalletToken;
import com.amedigital.wallet.service.fastcash.request.CashOutRequest;
import com.b2wdigital.bpay.oauth2.token.ConsumerType;
import com.b2wdigital.bpay.oauth2.token.Token;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.UUID;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;

@AutoConfigureWebTestClient(timeout = "36000")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = WalletApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@Disabled
public class DockerContainersTest {

	
	@Autowired
	private WebTestClient webClient;
	
//	public static GenericContainer sampleFooWebserver = new GenericContainer("alpine:3.2")
//		.withExposedPorts(Integer.valueOf(getAvailablePort()))
//		.withCommand("/bin/sh", "-c", "while true; do echo " + "\"HTTP/1.1 200 OK\n\nHello World!\" | nc -l -p 80; done");

//	public static MySQLContainer MYSQL_CONTAINER = new MySQLContainer<>("mysql:5.7.12")
//			.withDatabaseName("db_wallet_v2")
//			.withUsername("root")
//			.withPassword("")
			//.withInitScript("integration_tests.sql")
//			.withExposedPorts(Integer.valueOf(getAvailablePort()))
			;

	@BeforeAll
	public static void setUp() {
//		MYSQL_CONTAINER.start();
//		assertTrue(MYSQL_CONTAINER.isRunning());
	}

	@AfterAll
	public static void finish() {
//		MYSQL_CONTAINER.stop();
//		assertFalse(MYSQL_CONTAINER.isRunning());
	}

	/***
	 * Get any available port
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getAvailablePort() {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			String availablePort = String.valueOf(serverSocket.getLocalPort());
			System.out.println("Returning available port: " +  availablePort);
			return availablePort;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Test
	void testInfo() throws Exception {
		
		webClient.get()
	       .uri(WALLET_CONTEXT_PATH + "/info")
	       .header(AUTHORIZATION, "Bearer invalid")
	       .exchange()
	       .expectStatus().is4xxClientError();
	}
	
	@Test
	void testHealthCheck() throws Exception {
		
		webClient.get()
		.uri(WALLET_CONTEXT_PATH + "/health")
		.exchange()
		.expectStatus().isOk();
	}
	
	 @Test
	 void test1() {
       
       BankTransferMethodRequest bankTransferMethodRequest = new BankTransferMethodRequest();

       bankTransferMethodRequest.setClientName("fooname");
       bankTransferMethodRequest.setClientCPF("fooDoc");
       bankTransferMethodRequest.setClientEmail("fooEmail");
       bankTransferMethodRequest.setClientPhone("123");
       bankTransferMethodRequest.setBank(123);
       bankTransferMethodRequest.setAgency("0022");
       bankTransferMethodRequest.setAccountNumber("123");
       bankTransferMethodRequest.setTaxApplied(0L);

       CashOutOrderRequest cashOutOrderRequest = new CashOutOrderRequest();

       cashOutOrderRequest.setPaymentMethods(Collections.singletonList(bankTransferMethodRequest));
       cashOutOrderRequest.setCreatedByWalletId("123");
       cashOutOrderRequest.setDebitWalletId("123");
       cashOutOrderRequest.setTitle("Transferencia bancaria");
       cashOutOrderRequest.setDescription("transferencia");
       cashOutOrderRequest.setTotalAmountInCents(123L);
       

       webClient.post()
               .uri(WALLET_CONTEXT_PATH + "/orders")
               .header(AUTHORIZATION, "Bearer invalid")
               .accept(APPLICATION_JSON)
               .body(Mono.just(cashOutOrderRequest), CashOutOrderRequest.class)
               .exchange()
               .expectStatus().is4xxClientError()
               ;
   }
	

//	public void testeMySqlContainer() throws Exception {
//		
//		HikariDataSource ds = (HikariDataSource) dataSource();
//	    Statement statement = ds.getConnection().createStatement();
//	    statement.execute("SELECT 1");
//	    ResultSet resultSet = statement.getResultSet();
//	    
//	    assertEquals("There is a result", resultSet.next(), true);
//	    int resultSetInt = resultSet.getInt(1);
//	    assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
//		
//		System.out.println("Passei");
//	}
	
	
//	public DataSource dataSource() {
//		System.out.println(MessageFormat.format("Conectando no banco de dados [{0}]...", MYSQL_CONTAINER.getJdbcUrl()));
//
//		  // Create the Flyway instance and point it to the database
//        Flyway flyway = Flyway.configure().dataSource(MYSQL_CONTAINER.getJdbcUrl(), MYSQL_CONTAINER.getUsername(), MYSQL_CONTAINER.getPassword()).load();
//
//        // Start the migration
//        flyway.migrate();
//		
//		HikariConfig config = new HikariConfig();
//		config.setDriverClassName(MYSQL_CONTAINER.getDriverClassName());
//		config.setJdbcUrl(MYSQL_CONTAINER.getJdbcUrl());
//		config.setUsername(MYSQL_CONTAINER.getUsername());
//		config.setPassword(MYSQL_CONTAINER.getPassword());
//		//config.setMaximumPoolSize(this.maximumPoolSize);
//
//		return new HikariDataSource(config);
//	}

}
