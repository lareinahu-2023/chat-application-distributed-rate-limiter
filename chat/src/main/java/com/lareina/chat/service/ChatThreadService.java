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


import com.lareina.chat.dto.ChatThreadDTO;
import com.lareina.chat.model.ChatParticipant;
import com.lareina.chat.model.ChatThread;
import com.lareina.chat.repositories.ChatParticipantRepository;
import com.lareina.chat.repositories.ChatThreadRepository;
import com.lareina.chat.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * The type Chat thread service.
 * <p>
 *     This class is used to perform CRUD operations on the ChatThread entity.
 * </p>
 */
@Service
public class ChatThreadService {
    private final ChatThreadRepository chatThreadRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    public ChatThreadService(ChatThreadRepository chatThreadRepository,
        ChatParticipantRepository chatParticipantRepository, UserRepository userRepository) {
        this.chatThreadRepository = chatThreadRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.userRepository = userRepository;
    }


    public List<ChatThreadDTO> getThreadsForUser(String userId) {
        List<ChatParticipant> participants = chatParticipantRepository.findByUserId(UUID.fromString(userId));
        return participants.stream()
            .map(participant -> ChatThreadDTO.fromEntity(participant.getChatThread()))
            .collect(Collectors.toList());
    }

    public ChatThreadDTO joinThread(String userId1, String userId2) {
        // Check if a thread already exists between these two users
        Optional<ChatThread> existingThread = findThreadBetweenUsers(userId1, userId2);
        if (existingThread.isPresent()) {
            return ChatThreadDTO.fromEntity(existingThread.get());
        }

        // If not, create a new thread and add both users as participants
        ChatThread newThread = createNewThread(userId1, userId2);
        return ChatThreadDTO.fromEntity(newThread);
    }

    public Optional<ChatThread> findThreadBetweenUsers(String userId1, String userId2) {
        List<ChatThread> user1Threads = chatParticipantRepository
            .findChatThreadsByUserId(UUID.fromString(userId1));
        List<ChatThread> user2Threads = chatParticipantRepository
            .findChatThreadsByUserId(UUID.fromString(userId2));

        // Find the common thread between the two users
        return user1Threads.stream()
            .filter(user2Threads::contains)
            .findFirst();
    }

    public ChatThread createNewThread(String userId1, String userId2) {
        // Create a new chat thread
        ChatThread chatThread = new ChatThread();
        chatThread.setName("Chat between " + userId1 + " and " + userId2); // You can customize the name as needed
        chatThread = chatThreadRepository.save(chatThread);

        // Add both users as participants to the thread
        addParticipantToThread(userId1, chatThread);
        addParticipantToThread(userId2, chatThread);

        return chatThread;
    }
    
    /**
     * Create a new thread with a specific name and creator.
     *
     * @param name the name of the thread
     * @param createdBy the ID of the user who created the thread
     * @return the created chat thread DTO
     */
    public ChatThreadDTO createThread(String name, String createdBy) {
        // Create a new chat thread
        ChatThread chatThread = new ChatThread();
        chatThread.setName(name);
        chatThread = chatThreadRepository.save(chatThread);

        // Add the creator as a participant
        addParticipantToThread(createdBy, chatThread);

        return ChatThreadDTO.fromEntity(chatThread);
    }

    private void addParticipantToThread(String userId, ChatThread chatThread) {
        ChatParticipant participant = new ChatParticipant();
        participant.setUser(userRepository.findById(UUID.fromString(userId)).orElse(null));
        participant.setChatThread(chatThread);
        chatParticipantRepository.save(participant);
    }


    public List<ChatThread> getAllThreads() {
        return chatThreadRepository.findAll();
    }
}
