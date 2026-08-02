# Security & Compliance Interview Knowledge Base

## Q1: How does IntelliFlow AI prevent OWASP Top 10 vulnerabilities like SQL Injection, XSS, and Broken Access Control?

### Level 1 — Campus Placement Answer
> "We prevent SQL Injection by using Spring Data JPA parameterized queries, XSS by sanitizing user inputs, and Broken Access Control by enforcing Spring Security role checks on API endpoints."

### Level 2 — Product Company Answer
> "1. **SQL Injection**: We use Hibernate/JPA parameterized queries for 100% of database interactions.
> 2. **XSS**: We sanitize rich-text fields using OWASP Java HTML Sanitizer before saving to the database.
> 3. **Broken Access Control**: We enforce RBAC and ABAC using `@PreAuthorize` method security annotations in Spring Security to check both user roles (`ROLE_MANAGER`) and department ownership."

### Level 3 — Senior Engineer Answer
> "Beyond basic input sanitization and parameterized JPA queries, our security architecture enforces defense-in-depth:
> - **CSRF/CORS**: Stateless JWT authentication disables CSRF vulnerabilities for cross-origin REST calls while CORS origins are strictly whitelist-restricted at NGINX.
> - **Broken Object Level Authorization (BOLA)**: We enforce ABAC checks at the service layer, verifying that the authenticated `X-User-Id` and `department_id` match the requested resource's tenant boundaries.
> - **Data at Rest & Transit**: Sensitive configuration keys are managed via KMS envelope encryption; internal container traffic uses mTLS."
