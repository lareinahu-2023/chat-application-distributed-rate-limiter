

package com.lareina.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * The type Chat thread.
 * <p>
 *     This class represents the ChatThread entity.
 *     It is used to store the relationship between a user and a chat thread.
 * </p>
 */
@Entity
@Table(name = "chat_thread")
public class ChatThread {
    // You can add more fields as needed, such as a list of participants, last message, etc.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
