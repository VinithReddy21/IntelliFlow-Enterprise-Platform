# ADR-005: Containerization via Docker & Docker Compose

## Status
**ACCEPTED**

## Context
Developers work on varied operating systems (Windows, macOS, Linux). We must guarantee environment parity between developer workstations, CI/CD testing environments, and production cloud infrastructure (AWS EKS / ECS).

## Alternatives Considered

### 1. Local Native Installations
- **Pros**: Direct execution on developer host machine.
- **Cons**: Severe "works on my machine" bugs due to OS-level version mismatches (e.g. Postgres version differences, missing C compilers for pgvector).

### 2. Full Virtual Machines (Vagrant / VirtualBox)
- **Pros**: Complete OS isolation.
- **Cons**: Extremely heavy resource consumption (gigabytes of RAM per VM), slow boot times.

### 3. Docker Containerization & Docker Compose (SELECTED)
- **Pros**: Lightweight kernel-level isolation; deterministic execution environment; single-command infrastructure orchestration (`docker-compose up`); production-ready deployment artifacts.
- **Cons**: Requires developer familiarity with Docker CLI and networking concepts.

## Decision
We select **Docker multi-stage builds** and **Docker Compose** for local orchestration.

## Interview Defense & Key Summary
> *"Containerizing our services with Docker guarantees complete environmental parity across development, staging, and production. Multi-stage builds allow us to compile artifacts cleanly while keeping our final runtime container footprints small and secure."*
