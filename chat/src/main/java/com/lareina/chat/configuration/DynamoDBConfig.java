

package com.lareina.chat.configuration;


import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * The type Dynamo db config.
 */

@Configuration
public class DynamoDBConfig {

    /**
     * Amazon dynamo db dynamo db client.
     *
     * @return the dynamo db client
     */
    @Bean
    public DynamoDbClient amazonDynamoDB() {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            // The region is meaningless for local DynamoDb but required for client builder validation
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create("dummy-key", "dummy-secret")))
            .build();
    }
}


