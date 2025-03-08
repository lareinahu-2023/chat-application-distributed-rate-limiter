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

import com.lareina.chat.model.ChatMessage;
import com.abx.chat.model.ImmutableChatMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

/**
 * The type Message service.
 * <p>
 *     This class is used to perform CRUD operations on the ChatMessage entity.
 * </p>
 */
@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final DynamoDbClient dynamoDbClient;

    private static final String TABLE_NAME = "ChatMessages";

    public MessageService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Save a chat message to dynamoDB.
     *
     * @param message the message
     */
    public void saveMessage(ChatMessage message) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();

        // [
        // ThreadID 123
        // UserId: 1
        // Content: "Hello"
        // Timestamp: "2021-09-01T12:00:00"


        // ThreadID 123
        // UserId: 2
        // Content: "Hi"
        // Timestamp: "2021-09-01T12:00:01"
        // ]

        itemValues.put("UserId", AttributeValue.builder().s(String.valueOf(message.getUserId())).build());
        itemValues.put("ThreadId", AttributeValue.builder().s(message.getChatThreadId()).build());
        itemValues.put("Content", AttributeValue.builder().s(message.getContent()).build());
        itemValues.put("Timestamp", AttributeValue.builder().s(message.getTimestamp()).build());

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(itemValues)
            .build();

        try {
            dynamoDbClient.putItem(request);
            logger.info("Message saved successfully!");
        } catch (ResourceNotFoundException e) {
            logger.error("Error: The table {} was not found.", TABLE_NAME);
        } catch (DynamoDbException e) {
            logger.error("Error saving message: {}", e.getMessage());
        }
    }


    /**
     * Get all messages for a given thread.
     *
     * @param threadId the thread id
     * @return the messages for thread
     */
    public List<ChatMessage> getMessagesForThread(String threadId) {
        HashMap<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":tid", AttributeValue.builder().s(threadId).build());

        QueryRequest queryReq = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .keyConditionExpression("ThreadId = :tid")
            .expressionAttributeValues(valueMap)
            .build();

        List<ChatMessage> messages = new ArrayList<>();
        for (Map<String, AttributeValue> item : dynamoDbClient.query(queryReq).items()) {
            messages.add(ImmutableChatMessage.builder()
                .userId(item.get("UserId").s())
                .chatThreadId(item.get("ThreadId").s())
                .content(item.get("Content").s())
                .timestamp(item.get("Timestamp").s())
                .build());
        }
        return messages;
    }


}
