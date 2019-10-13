package com.amedigital.wallet.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.ConversionSchemas;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder.standard;

@Configuration
public class DynamoDBConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDBConfig.class);

    private static final String region = "us-east-1";

    @Value("${aws.dynamodb.url}")
    private String url = "http://localhost:8000";

    @Value("${aws.dynamodb.prefix}")
    private String prefix = "dev";

    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB client) {
        return new DynamoDB(client);
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider credentialsProvider) {
        LOG.info("Conectando no DymanoDB [{}]...", url);

        AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(url, region);

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
