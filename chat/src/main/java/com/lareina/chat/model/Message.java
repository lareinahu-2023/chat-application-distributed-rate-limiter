

package com.lareina.chat.model;


import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Message model
 * <p>
 *     This class is used to represent a message in a chat thread.
 *     It is annotated with DynamoDbBean to indicate that it is a DynamoDB model.
 *     It is annotated with DynamoDbPartitionKey and DynamoDbSortKey to indicate that the threadId and timestamp
 *     are the partition key and sort key respectively.
 * </p>
 */

@DynamoDbBean
public class Message {

    private Long threadId;
    private Long timestamp;
    private String content;
    private Long userId;
    private String messageType;

    @DynamoDbPartitionKey
    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
