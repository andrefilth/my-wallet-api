package com.amedigital.wallet.config;

import static com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder.standard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.ConversionSchemas;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

@Configuration
@Profile("test")
public class DynamoDBTestConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDBTestConfig.class);

    private static final String region = "us-east-1";
    private String prefix = "test";
    
    @Autowired
    private DynamoDBContainer dynamoDbTestsContainer;

    @Bean
    public DynamoDBContainer dynamoDbTestsContainer() {
    	LOG.info("Iniciando container docker com dynamoDb de testes");
    	DockerTestContainersConfig.getInstance().getDYNAMO_DB_CONTAINER().getGenericContainer().start();
    	return DockerTestContainersConfig.getInstance().getDYNAMO_DB_CONTAINER();
    }
    
    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB client) {
        return new DynamoDB(client);
    }

    @Bean
    @DependsOn("dynamoDbTestsContainer")
    public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider credentialsProvider) {
        LOG.info("Conectando no DymanoDB [{}]...", dynamoDbTestsContainer.getGenericContainer().getContainerIpAddress());

        AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(dynamoDbTestsContainer.getGenericContainer().getContainerIpAddress(), region);

        return standard().withEndpointConfiguration(endpointConfig)
                .withCredentials(credentialsProvider)
                .build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB client) {
        String prefix = String.format("%s_", this.prefix);

        DynamoDBMapperConfig.TableNameOverride tableNameOverride = DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(prefix);

        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
                .withConversionSchema(ConversionSchemas.V2)
                .withTableNameOverride(tableNameOverride)
                .build();

        return new DynamoDBMapper(client, mapperConfig);
    }

}
