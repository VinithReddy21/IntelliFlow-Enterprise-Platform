package com.intelliflow.common.config.cache;

/**
 * Global Constants for Redis Cache Names.
 * 
 * Provides centralized registry of Spring Cache names across domain modules.
 */
public final class CacheNames {

    private CacheNames() {
        // Private constructor to prevent instantiation
    }

    public static final String USERS = "users";
    public static final String USER_DETAILS = "user_details";

    public static final String TASKS = "tasks";
    public static final String TASK_DETAILS = "task_details";

    public static final String DOCUMENTS = "documents";
    public static final String DOCUMENT_DETAILS = "document_details";
}
