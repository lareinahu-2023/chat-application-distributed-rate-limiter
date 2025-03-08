

package com.lareina.chat.repositories;

import com.lareina.chat.model.User;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface User repository.
 * <p>
 *     This interface is used to perform CRUD operations on the User entity.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    // create a new method findByUsername
    User findByUsername(String username);

}

