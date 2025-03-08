
package com.lareina.chat.repositories;

import com.lareina.chat.model.ChatThread;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface Chat thread repository.
 * <p>
 *     This interface is used to perform CRUD operations on the ChatThread entity.
 * </p>
 */
public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
}
