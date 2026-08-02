# Section 01 Interview Questions: Beginner Tier

## Q1: What is the purpose of `.gitignore`?
- **Ideal Answer**: A `.gitignore` file specifies intentionally untracked files that Git should ignore. It prevents committing binaries, OS metadata, IDE configurations, and secret `.env` files.
- **Common Wrong Answer**: *"It deletes unwanted files from your computer."*
- **Follow-up Question**: How do you ignore a file already committed to Git history?
- **Interview Tip**: Emphasize security credentials first.

## Q2: What is the difference between Git and GitHub?
- **Ideal Answer**: Git is a local distributed version control software tool. GitHub is a cloud platform for hosting Git repositories and facilitating team code reviews via Pull Requests.
- **Common Wrong Answer**: *"They are the exact same thing."*
- **Follow-up Question**: Name two alternative Git hosts (GitLab, Bitbucket).
- **Interview Tip**: Clarify that Git works completely offline.

## Q3: What is Docker Compose?
- **Ideal Answer**: Docker Compose is a tool for defining and orchestrating multi-container Docker applications using a single `docker-compose.yml` file.
- **Common Wrong Answer**: *"It installs Docker on Windows."*
- **Follow-up Question**: Which command starts containers in detached mode? (`docker-compose up -d`).
- **Interview Tip**: Highlight single-command infrastructure setup.

## Q4: Why do we use Flyway for database migrations?
- **Ideal Answer**: Flyway applies version-controlled SQL scripts (`V1__init_schema.sql`) to guarantee schema consistency across development, staging, and production without relying on risky ORM auto-generation.
- **Common Wrong Answer**: *"It automatically fixes database SQL errors."*
- **Follow-up Question**: Where does Flyway track executed migration history? (`flyway_schema_history` table).
- **Interview Tip**: Mention zero schema drift.
