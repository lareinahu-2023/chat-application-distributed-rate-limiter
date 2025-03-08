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

package com.lareina.chat.dto;

import com.lareina.chat.model.ChatThread;

import java.util.UUID;

/**
 * The type Chat thread dto.
 * <p>
 *     This class represents the ChatThread DTO.
 *     It is used to transfer the ChatThread entity between the client and the server.
 * </p>
 */
public class ChatThreadDTO {
    private String id;
    private String name;
    // You can add more fields as needed, such as a list of participants, last message, etc.

    // Constructors
    public ChatThreadDTO() {
    }

    public ChatThreadDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Optionally, you can include methods to convert between the entity and the DTO
    public static ChatThreadDTO fromEntity(ChatThread chatThread) {
        return new ChatThreadDTO(chatThread.getId().toString(), chatThread.getName());
    }

    public ChatThread toEntity() {
        ChatThread chatThread = new ChatThread();
        chatThread.setId(UUID.fromString(this.id));
        chatThread.setName(this.name);
        return chatThread;
    }
}
