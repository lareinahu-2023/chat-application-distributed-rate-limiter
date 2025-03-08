/*
 * Copyright  (c) 2023.  ABX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lareina.chat.service;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

/**
 * The type Dynamo db setup service.
 * <p>
 *     This class is used to setup the DynamoDB table.
 * </p>
 */
@Service
public class DynamoDBSetupService {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBSetupService.class);

    private final DynamoDbClient dynamoDbClient;

    public DynamoDBSetupService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void createTable() {

        String tableName = "ChatMessages";

        try {
            CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("ThreadId")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("Timestamp")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("ThreadId")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("Timestamp") // Sort key
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build()
                )
                .tableName(tableName)
                .build();


            dynamoDbClient.createTable(request);
            logger.info("Table created successfully: {}", tableName);

        } catch (DynamoDbException e) {
            logger.error("Unable to create table: {}", tableName);
            logger.error("Error details: {}", e.getMessage());
        }
    }

    private boolean doesTableExist(DynamoDbClient dynamoDbClient, String tableName) {
        try {
            DescribeTableResponse describeTableResponse = dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            return tableName.equals(describeTableResponse.table().tableName());
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }


    @PostConstruct
    public void init() {
        setupTable();
    }

    public void setupTable() {
        if (!doesTableExist(dynamoDbClient, "ChatMessages")) {
            createTable();
        } else {
            logger.info("Table already exists.");
        }
    }
}
