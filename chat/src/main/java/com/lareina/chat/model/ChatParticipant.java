

package com.lareina.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;


/**
 * The type Chat participant.
 * <p>
 *     This class represents the ChatParticipant entity.
 *     It is used to store the relationship between a user and a chat thread.
 * </p>
 */

@Entity
@Table(name = "chat_participant")
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_thread_id")
    private ChatThread chatThread;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatThread getChatThread() {
        return chatThread;
    }

    public void setChatThread(ChatThread chatThread) {
        this.chatThread = chatThread;
    }
}