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


import com.lareina.chat.model.User;

import java.util.UUID;

/**
 * The type User dto.
 * <p>
 *     This class represents the User DTO.
 *     It is used to transfer the User entity between the client and the server.
 * </p>
 */
public class UserDTO {
    private String id;
    private String username;
    private String password;

    // Constructors
    public UserDTO() {
    }

    public UserDTO(String id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optionally, you can include methods to convert between the entity and the DTO
    public static UserDTO fromEntity(User user) {
        return new UserDTO(user.getId().toString(), user.getUsername());
    }

    public User toEntity() {
        User user = new User();
        if(this.id != null) {
            user.setId(UUID.fromString(this.id));
        }
        if(this.password != null) {
            user.setPassword(this.password);
        }

        user.setUsername(this.username);
        return user;
    }
}


