package com.intelliflow.modules.user.dto;

import com.intelliflow.modules.user.domain.RoleEnum;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for returning User Profile details across REST APIs.
 * 
 * Never includes sensitive security data like passwordHash.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private RoleEnum role;
    private UserStatus status;
    private UUID departmentId;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Static Factory Mapping Method converting UserEntity to UserProfileResponseDto.
     */
    public static UserProfileResponseDto fromEntity(UserEntity user) {
        if (user == null) return null;
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .departmentId(user.getDepartmentId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
