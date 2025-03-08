
package com.lareina.chat.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * The type Chat message.
 * <p>
 *     This class represents the ChatMessage entity.
 *     It is used to store the relationship between a user and a chat thread.
 * </p>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableChatMessage.class)
@JsonDeserialize(as = ImmutableChatMessage.class)
public interface ChatMessage {

    String getUserId();
    String getChatThreadId();
    String getContent();
    String getTimestamp();
}
