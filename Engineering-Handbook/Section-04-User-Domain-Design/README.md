# Section 04: User Domain Design & REST Controller Layer

---

## 1. Prerequisites

Before reading this section, you should understand:
- REST API Conventions (`GET`, `POST`, `PATCH`, `DELETE`).
- Cryptographic Password Verification (`PasswordEncoder.matches`).
- Multi-Device Session Liquidation via Redis.

---

## 2. Learning Objectives

After completing this section, you will master:
- **Dedicated Password Mutation Endpoint**: Why password changes must use a dedicated `POST /api/v1/users/me/password` endpoint rather than metadata `PATCH /me`.
- **Credential Verification & Anti-Reuse**: Enforcing current password verification and blocking password reuse.
- **Session Liquidation**: Automatically revoking all active Redis refresh tokens across devices when a user changes their password.

---

## 3. Implementation Checklist

Verify your understanding of implemented endpoints:
- [x] **`GET /api/v1/users/me`**: Profile lookup using `Authentication` parameter resolution.
- [x] **`PATCH /api/v1/users/me`**: Profile name update (`firstName`, `lastName`).
- [x] **`POST /api/v1/users/me/password`**: Password change workflow with current password verification and Redis token revocation.

---

## 4. `POST /api/v1/users/me/password` Endpoint Specification

| Attribute | Value |
| :--- | :--- |
| **HTTP Method & Path** | `POST /api/v1/users/me/password` |
| **Authentication** | Bearer JWT Required |
| **Request Payload** | `@Valid @RequestBody ChangePasswordRequestDto` (`currentPassword`, `newPassword`) |
| **Success Response** | HTTP `200 OK` (`ApiResponse<Void>`) with message: *"Password changed successfully. Please log in again."* |
| **Incorrect Password** | HTTP `401 Unauthorized` (`BadCredentialsException`) |
| **Password Reuse** | HTTP `400 Bad Request` (`IllegalArgumentException`) |
