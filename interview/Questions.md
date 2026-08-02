# Core System Architecture Interview Question Bank

This document contains key interview questions based on the architecture, technical choices, and infrastructure designed in Phase 1 of IntelliFlow AI.

## Category 1: System Architecture & Design
1. **Q1.1**: Why did you choose a Modular Monolith with an isolated Python service instead of a pure microservices architecture?
2. **Q1.2**: How does your platform handle multi-tenancy and data isolation across departments?
3. **Q1.3**: What is the purpose of using Virtual Threads in Java 21 Spring Boot 3.2?

## Category 2: Database & Storage Strategy
4. **Q2.1**: Why did you select PostgreSQL with `pgvector` instead of a standalone Vector DB like Pinecone?
5. **Q2.2**: How do database migrations work in production with zero downtime?
6. **Q2.3**: What is the difference between B-Tree indexing and HNSW vector indexing in PostgreSQL?

## Category 3: Security & Caching Strategy
7. **Q3.1**: Why do we use dual-token JWT authentication (Access Token + Refresh Token) signed with RS256?
8. **Q3.2**: How does Redis assist with API Rate Limiting and Session Revocation?
9. **Q3.3**: How do you prevent OWASP Top 10 security vulnerabilities in REST APIs?
