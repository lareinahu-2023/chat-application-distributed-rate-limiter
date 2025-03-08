

package com.lareina.chat.repositories;

import com.lareina.chat.model.ChatParticipant;
import com.lareina.chat.model.ChatThread;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The interface Chat participant repository.
 * <p>
 *     This interface is used to perform CRUD operations on the ChatParticipant entity.
 * </p>
 */
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    // Find all chat threads a user is a part of
    @Query("SELECT cp.chatThread FROM ChatParticipant cp WHERE cp.user.id = :userId")
    List<ChatThread> findChatThreadsByUserId(@Param("userId") UUID userId);

    List<ChatParticipant> findByUserId(UUID userId);

}

