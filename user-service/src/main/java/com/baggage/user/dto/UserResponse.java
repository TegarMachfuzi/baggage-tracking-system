package com.baggage.user.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
