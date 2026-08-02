# Deployment Diagram: Cloud AWS Infrastructure Architecture

```mermaid
graph TD
    Client[Client Browsers / Mobile Apps] --> |HTTPS / TLS 1.3| ALB[Application Load Balancer]

    subgraph AWS VPC (Virtual Private Cloud)
        subgraph Public Subnets (Multi-AZ)
            ALB
            NGINX[NGINX Ingress Controller]
        end

        subgraph Private Application Subnets (Multi-AZ)
            SpringBootPods[EKS Kubernetes Cluster<br/>Java Spring Boot Core Service Pods]
            FastAPIPods[EKS Kubernetes Cluster<br/>Python FastAPI AI Service Pods]
        end

        subgraph Private Data Subnets (Multi-AZ)
            RDSPrimary[(PostgreSQL Aurora Primary Master)]
            RDSReplica[(PostgreSQL Aurora Read Replica)]
            ElastiCache[(Amazon ElastiCache Redis Cluster)]
        end
    end

    ALB --> NGINX
    NGINX --> SpringBootPods
    NGINX --> FastAPIPods
    SpringBootPods --> RDSPrimary
    SpringBootPods --> RDSReplica
    SpringBootPods --> ElastiCache
    FastAPIPods --> RDSReplica
    FastAPIPods --> ElastiCache
```
