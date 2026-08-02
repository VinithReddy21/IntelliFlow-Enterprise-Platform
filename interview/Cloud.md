# Cloud Infrastructure & Deployment Interview Knowledge Base

## Q1: How do you deploy this dual-service application on AWS with high availability and autoscaling?

### Level 1 — Campus Placement Answer
> "We deploy our Spring Boot and FastAPI Docker containers on AWS EKS (Elastic Kubernetes Service) behind an Application Load Balancer (ALB). PostgreSQL runs on AWS Aurora RDS and Redis runs on AWS ElastiCache."

### Level 2 — Product Company Answer
> "1. **Networking**: We deploy across 2 Availability Zones (Multi-AZ) inside an AWS VPC with Public Subnets for the ALB and Private Subnets for Kubernetes application nodes and database clusters.
> 2. **Compute**: Spring Boot and FastAPI pods run on EKS. We configure Horizontal Pod Autoscaler (HPA) to scale Spring Boot pods based on CPU/RAM utilization.
> 3. **Database**: PostgreSQL Aurora Primary handles writes while Read Replicas handle read queries."

### Level 3 — Senior Engineer Answer
> "For enterprise SLA commitments (99.99% uptime):
> - **Ingress & Security**: AWS WAF inspects incoming HTTP traffic for DDoS and SQLi before passing traffic to ALB and NGINX Ingress.
> - **KEDA Autoscaling**: Python AI pods use KEDA (Kubernetes Event-driven Autoscaling) to scale based on Redis Stream job backlog size rather than just CPU usage.
> - **Zero-Downtime Releases**: EKS deployment manifests use RollingUpdate strategies (`maxSurge: 25%`, `maxUnavailable: 0`) paired with Spring Boot Actuator readiness and liveness probes to achieve zero-downtime deployments."
