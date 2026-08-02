# Docker & Containerization Interview Knowledge Base

## Q1: Why do we use multi-stage Docker builds for Java Spring Boot and Python FastAPI?

### Level 1 — Campus Placement Answer
> "Multi-stage Docker builds allow us to compile our code inside a build stage and then copy only the final runnable jar file into a clean runtime stage. This makes the Docker image much smaller."

### Level 2 — Product Company Answer
> "A single-stage Dockerfile includes Maven build tools, JDK compilers, and source code, resulting in large image sizes (>800MB). A multi-stage Dockerfile uses a builder image (`maven:3.9-eclipse-temurin-21`) to compile the project, and then copies the output `.jar` into a lightweight JRE runtime image (`eclipse-temurin:21-jre-alpine`). This reduces image size to ~200MB, speeds up CI/CD deployment pipeline transfers, and reduces the attack surface."

### Level 3 — Senior Engineer Answer
> "Multi-stage builds enforce security and container best practices. By leaving Maven, compilers, and source files out of the final runtime image, we eliminate build-time security vulnerabilities (CVEs). In our Dockerfiles, we also create non-root application users (`USER appuser:appgroup`), preventing privilege escalation attacks if a container runtime is compromised. Furthermore, we leverage Docker layer caching by copying `pom.xml` or `requirements.txt` and running dependency downloads before copying source code, ensuring fast incremental builds."
