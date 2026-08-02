# ADR-006: Modular Monolith Architecture for Domain Services

## Status
**ACCEPTED**

## Context
A startup enterprise platform (V1.0 MVP target: 50-100 employees, extensible to 100,000+ users) needs to balance fast developer velocity with clean domain boundaries. Deciding between a microservices architecture vs. a modular monolith determines operational overhead and initial delivery speed.

## Alternatives Considered

### 1. Distributed Microservices from Day 1
- **Pros**: Independent deployment pipelines and database per microservice.
- **Cons**: Excessive operational overhead (service meshes, distributed tracing, network latency, distributed transaction saga patterns) for a early-to-mid scale startup team.

### 2. Unstructured Monolith ("Big Ball of Mud")
- **Pros**: Quickest initial speed.
- **Cons**: High risk of tight coupling across features; impossible to separate domain logic or extract microservices later.

### 3. Modular Monolith with Isolated AI Service (SELECTED)
- **Pros**: All backend modules live in a single Spring Boot application artifact but maintain strict internal package boundaries (`com.intelliflow.modules.*`). Downstream AI workloads run in a separate FastAPI service.
- **Cons**: Requires developer discipline to prevent cross-module direct database access or tight package coupling.

## Decision
We select a **Modular Monolith architecture for Spring Boot**, paired with a specialized **FastAPI AI microservice**.

## Interview Defense & Key Summary
> *"We chose a Modular Monolith for our core platform because Premature Microservice Optimization creates unnecessary network complexity and saga management overhead. By maintaining strict package isolation inside a Spring Boot app, we preserve the speed of single-process deployment while keeping the door open to extract microservices cleanly if specific domain modules require independent scaling."*
