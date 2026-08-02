package com.intelliflow.modules.user.dto;

import com.intelliflow.modules.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user administrative status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequestDto {

    @NotNull(message = "Target status is required")
    private UserStatus status;
}
