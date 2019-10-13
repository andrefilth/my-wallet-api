package com.amedigital.wallet.config;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

public final class DockerTestContainersConfig {

	private static final Logger LOG = LoggerFactory.getLogger(DockerTestContainersConfig.class);
	
	private static DockerTestContainersConfig DOCKER_TEST_CONFIG_INSTANCE;
	
	private static MySQLContainer<?> MYSQL_CONTAINER;
	
	private static DynamoDBContainer DYNAMO_DB_CONTAINER;
	
	private DockerTestContainersConfig() {}
	
	static {
		
		MYSQL_CONTAINER = new MySQLContainer<>("mysql:5.7.12")
				.withDatabaseName("db_wallet_v2")
				.withUsername("root")
				.withPassword("")
				;
		
		DYNAMO_DB_CONTAINER = new DynamoDBContainer(new GenericContainer<>("amazon/dynamodb-local:1.11.119"));
	}
	
	
	public static DockerTestContainersConfig getInstance() {
		if (DOCKER_TEST_CONFIG_INSTANCE == null) {
			synchronized (DockerTestContainersConfig.class) {
				if (DOCKER_TEST_CONFIG_INSTANCE == null) {
					DOCKER_TEST_CONFIG_INSTANCE = new DockerTestContainersConfig();
				}
			}
		}
		return DOCKER_TEST_CONFIG_INSTANCE;
	}

	/***
	 * Get any available port
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Integer getAvailablePort() {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			Integer availablePort = serverSocket.getLocalPort();
			LOG.info("Returning available port: [{}]",  availablePort);
			return availablePort;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public MySQLContainer<?> getMYSQL_CONTAINER() {
		return MYSQL_CONTAINER;
	}

	public DynamoDBContainer getDYNAMO_DB_CONTAINER() {
		return DYNAMO_DB_CONTAINER;
	}
}
