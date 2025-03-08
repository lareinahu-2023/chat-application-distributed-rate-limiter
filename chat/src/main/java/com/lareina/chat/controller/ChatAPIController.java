

package com.lareina.chat.controller;

import com.lareina.chat.dto.ChatThreadDTO;
import com.lareina.chat.dto.CreateThreadDTO;
import com.lareina.chat.dto.UserDTO;
import com.lareina.chat.exception.ResourceNotFoundException;
import com.lareina.chat.model.ChatMessage;
import com.abx.chat.model.ImmutableChatMessage;
import com.lareina.chat.model.User;
import com.lareina.chat.service.ChatThreadService;
import com.lareina.chat.service.MessageService;
import com.lareina.chat.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Chat api controller.
 * <p>
 *     This class is the controller for the chat API.
 *     It is used to handle the requests for the chat API.
 * </p>
 */
@RestController
@RequestMapping("/api")
@Validated
public class ChatAPIController {
    private static final Logger logger = LoggerFactory.getLogger(ChatAPIController.class);

    private final UserService userService;
    private final ChatThreadService chatThreadService;
    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    /**
     * Constructor for ChatAPIController.
     *
     * @param userService        Service for user-related operations.
     * @param chatThreadService  Service for chat thread operations.
     * @param template           Messaging template for sending messages.
     * @param messageService     Service for message-related operations.
     */
    public ChatAPIController(UserService userService, ChatThreadService chatThreadService,
        SimpMessagingTemplate template, MessageService messageService) {
        this.userService = userService;
        this.chatThreadService = chatThreadService;
        this.template = template;
        this.messageService = messageService;
    }

    /**
     * Register a new user.
     *
     * @param userDTO the user data transfer object
     * @return the created user
     */
    @PostMapping("/users/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
        logger.info("Registering new user with username: {}", userDTO.getUsername());
        try {
            if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
                logger.error("Failed to register user: username cannot be empty");
                return ResponseEntity.badRequest().build();
            }
            
            User user = userService.createUser(userDTO.toEntity());
            logger.info("User registered successfully with ID: {}", user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            logger.error("Failed to register user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all threads for a specific user.
     *
     * @param userId the user ID
     * @return list of chat thread DTOs
     */
    @GetMapping("/users/{userId}/threads")
    public ResponseEntity<List<ChatThreadDTO>> getThreadsForUser(@PathVariable String userId) {
        logger.info("Fetching threads for user: {}", userId);
        try {
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("Invalid user ID provided: {}", userId);
                return ResponseEntity.badRequest().build();
            }
            
            List<ChatThreadDTO> threads = chatThreadService.getThreadsForUser(userId);
            logger.info("Retrieved {} threads for user: {}", threads.size(), userId);
            return ResponseEntity.ok(threads);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching threads for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all user contacts.
     *
     * @return list of user DTOs
     */
    @GetMapping("/users/contacts")
    public ResponseEntity<List<UserDTO>> getContacts() {
        logger.info("Fetching all user contacts");
        try {
            List<UserDTO> contacts = userService.getAllUser().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
            
            logger.info("Retrieved {} contacts", contacts.size());
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            logger.error("Error fetching contacts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all chat threads.
     *
     * @return list of chat thread DTOs
     */
    @GetMapping("/threads")
    public ResponseEntity<List<ChatThreadDTO>> getChats() {
        logger.info("Fetching all chat threads");
        try {
            List<ChatThreadDTO> threads = chatThreadService.getAllThreads().stream()
                .map(ChatThreadDTO::fromEntity)
                .collect(Collectors.toList());
            
            logger.info("Retrieved {} chat threads", threads.size());
            return ResponseEntity.ok(threads);
        } catch (Exception e) {
            logger.error("Error fetching chat threads: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create a new chat thread.
     *
     * @param createThreadDTO the create thread DTO
     * @return the created chat thread DTO
     */
    @PostMapping("/threads")
    public ResponseEntity<ChatThreadDTO> createThread(@RequestBody CreateThreadDTO createThreadDTO) {
        logger.info("Creating new thread with name: {}, created by: {}", 
            createThreadDTO.getName(), createThreadDTO.getCreatedBy());
        try {
            if (createThreadDTO.getName() == null || createThreadDTO.getName().trim().isEmpty()) {
                logger.error("Failed to create thread: name cannot be empty");
                return ResponseEntity.badRequest().build();
            }
            
            if (createThreadDTO.getCreatedBy() == null || createThreadDTO.getCreatedBy().trim().isEmpty()) {
                logger.error("Failed to create thread: creator ID cannot be empty");
                return ResponseEntity.badRequest().build();
            }
            
            ChatThreadDTO thread = chatThreadService.createThread(
                createThreadDTO.getName(), createThreadDTO.getCreatedBy());
            logger.info("Thread created successfully with ID: {}", thread.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);
        } catch (Exception e) {
            logger.error("Failed to create thread: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Receive and process a new chat message.
     *
     * @param threadId the thread ID
     * @param message the chat message
     * @return the processed message
     */
    @MessageMapping("/threads/{threadId}/messages")
    public ResponseEntity<ChatMessage> receiveMessage(@DestinationVariable String threadId, @RequestBody ChatMessage message) {
        logger.info("Received message in thread: {}, from user: {}", threadId, message.getUserId());
        try {
            if (threadId == null || threadId.trim().isEmpty()) {
                logger.error("Invalid thread ID provided: {}", threadId);
                return ResponseEntity.badRequest().build();
            }
            
            if (message == null || message.getContent() == null || message.getContent().trim().isEmpty()) {
                logger.error("Empty message content received for thread: {}", threadId);
                return ResponseEntity.badRequest().build();
            }
            
            // Create a new message with the correct thread ID if needed
            ChatMessage messageToSave = message;
            if (!threadId.equals(message.getChatThreadId())) {
                logger.debug("Setting thread ID on message to: {}", threadId);
                messageToSave = ImmutableChatMessage.builder()
                    .from(message)
                    .chatThreadId(threadId)
                    .build();
            }
            
            // Save to database
            messageService.saveMessage(messageToSave);
            logger.info("Message saved successfully");
            
            // Broadcast to subscribers
            broadcastMessage("/topic/messages/" + threadId, messageToSave);
            
            return ResponseEntity.ok(messageToSave);
        } catch (Exception e) {
            logger.error("Error processing message for thread {}: {}", threadId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all messages for a specific thread.
     *
     * @param threadId the thread ID
     * @return list of chat messages
     */
    @GetMapping("/threads/{threadId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessagesForThread(@PathVariable String threadId) {
        logger.info("Fetching messages for thread: {}", threadId);
        try {
            if (threadId == null || threadId.trim().isEmpty()) {
                logger.error("Invalid thread ID provided: {}", threadId);
                return ResponseEntity.badRequest().build();
            }
            
            List<ChatMessage> messages = messageService.getMessagesForThread(threadId);
            logger.info("Retrieved {} messages for thread: {}", messages.size(), threadId);
            return ResponseEntity.ok(messages);
        } catch (ResourceNotFoundException e) {
            logger.error("Thread not found with ID: {}", threadId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching messages for thread {}: {}", threadId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Send a message to a specific thread via REST API.
     *
     * @param threadId the thread ID
     * @param message the chat message
     * @return the saved message
     */
    @PostMapping("/threads/{threadId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable String threadId, @RequestBody ChatMessage message) {
        logger.info("Sending message to thread: {}, from user: {}", threadId, message.getUserId());
        try {
            if (threadId == null || threadId.trim().isEmpty()) {
                logger.error("Invalid thread ID provided: {}", threadId);
                return ResponseEntity.badRequest().build();
            }
            
            if (message == null || message.getContent() == null || message.getContent().trim().isEmpty()) {
                logger.error("Empty message content received for thread: {}", threadId);
                return ResponseEntity.badRequest().build();
            }
            
            // Create a new message with the correct thread ID if needed
            ChatMessage messageToSave = message;
            if (!threadId.equals(message.getChatThreadId())) {
                logger.debug("Setting thread ID on message to: {}", threadId);
                messageToSave = ImmutableChatMessage.builder()
                    .from(message)
                    .chatThreadId(threadId)
                    .build();
            }
            
            // Save to database
            messageService.saveMessage(messageToSave);
            logger.info("Message saved successfully");
            
            // Broadcast to subscribers
            broadcastMessage("/topic/messages/" + threadId, messageToSave);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(messageToSave);
        } catch (Exception e) {
            logger.error("Error sending message to thread {}: {}", threadId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Broadcast a message to a specific destination.
     *
     * @param destination the destination topic
     * @param message the message to broadcast
     */
    private void broadcastMessage(String destination, ChatMessage message) {
        logger.info("Broadcasting message from user: {} to destination: {}", 
            message.getUserId(), destination);
        try {
            template.convertAndSend(destination, message);
            logger.debug("Message broadcast successful");
        } catch (Exception e) {
            logger.error("Failed to broadcast message to {}: {}", destination, e.getMessage(), e);
            // We don't rethrow here as this is a fire-and-forget operation
        }
    }
    
    /**
     * Exception handler for ResourceNotFoundException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    /**
     * Exception handler for IllegalArgumentException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Invalid argument: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    /**
     * Exception handler for general exceptions.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred. Please try again later.");
    }
}
