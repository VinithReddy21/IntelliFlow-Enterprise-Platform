# Section 05: Enterprise Task Management Architecture & REST API Specification

---

## 1. Prerequisites

Before reading this section, you should understand:
- Spring MVC `@RestController` and `@RequestMapping` conventions.
- Method Security `@PreAuthorize("hasAnyRole(...)")` annotations.
- `Authentication` parameter injection for SecurityContext identity extraction without calling static methods.
- Spring Data `Pageable` and `Specification` dynamic querying.

---

## 2. Learning Objectives

After completing Step 6, you will master:
- **Thin REST Controller Design**: Delegating all business state transitions and validation to `TaskService`.
- **Identity Parameter Injection**: Extracting current user UUID safely via `Authentication authentication`.
- **Unified API Response Enveloping**: Returning standard `ResponseEntity<ApiResponse<T>>` payloads across all 12 task REST endpoints.
- **RESTful Endpoints**: Implementing clean resource routes for tasks, bulk updates, subtasks, comments, dependencies, and audit logs.

---

## 3. Task REST API Catalog (`/api/v1/tasks`)

| Method | Endpoint Route | Access Authority | Purpose |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/tasks` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Creates a new task |
| `GET` | `/api/v1/tasks/{id}` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Retrieves task detail projection |
| `GET` | `/api/v1/tasks` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Paginated search of active tasks |
| `PUT` | `/api/v1/tasks/{id}` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Updates task attribute fields |
| `PATCH` | `/api/v1/tasks/{id}/status` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Executes finite state machine transition |
| `DELETE` | `/api/v1/tasks/{id}` | `MANAGER`, `ADMIN` | Soft-deletes task to ARCHIVED |
| `PATCH` | `/api/v1/tasks/bulk-status` | `MANAGER`, `ADMIN` | Bulk updates status for multiple tasks |
| `POST` | `/api/v1/tasks/{id}/comments` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Adds a comment entry |
| `GET` | `/api/v1/tasks/{id}/comments` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Paginate task comment thread |
| `POST` | `/api/v1/tasks/dependencies` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Links directed DAG task dependency |
| `DELETE` | `/api/v1/tasks/dependencies` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Removes task dependency link |
| `GET` | `/api/v1/tasks/{id}/activity-logs` | `EMPLOYEE`, `MANAGER`, `ADMIN` | Fetches activity audit trail |
