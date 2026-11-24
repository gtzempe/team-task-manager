# Team Task Manager

A full-stack task management application for a small team. Assigners create and manage tasks, while assignees view and update only the tasks assigned to them.

## Features

- Login and logout through Keycloak.
- Role-based access for `assigner` and `assignee`.
- Assigners can create, edit, delete, filter, and update tasks.
- Assignees can view only their own tasks and update their status.
- Tasks are sorted by creation date, newest first.
- Overdue tasks are clearly marked.
- Keycloak realm configuration is imported automatically during initial setup.

## Technology stack

- Frontend: Angular and TypeScript
- Backend: Java and Spring Boot REST API
- Database: PostgreSQL
- Authentication: Keycloak with OAuth 2.0 / OpenID Connect
- Containers: Docker Compose

## Run the application

Prerequisite: Docker Desktop must be running.

From the project root, start all services with:

```bash
docker compose up --build
```

This builds the backend and frontend images, then starts PostgreSQL, Keycloak, the backend, and the frontend. On first startup with a new database, Keycloak imports the realm configuration from `infra/keycloak/team-task-manager-realm.json`. No manual Keycloak admin-console setup is required.

### URLs

- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- Keycloak: http://localhost:8081

To stop the services, press `Ctrl+C`. PostgreSQL data is stored in a Docker volume and remains available when the services are started again.

To remove the containers and delete the stored database data, run:

```bash
docker compose down -v
```

## Test accounts

These accounts are for local development and demonstration only.

| Role     | Username    | Password    |
| -------- | ----------- | ----------- |
| Assigner | `assigner`  | `assigner`  |
| Assignee | `assignee1` | `assignee1` |
| Assignee | `assignee2` | `assignee2` |

## Roles and permissions

### Assigner

- Views all tasks.
- Filters tasks by status and assignee.
- Creates, edits, and deletes tasks.
- Changes the status of any task.

### Assignee

- Views only tasks assigned to their Keycloak user ID.
- Updates the status of their own tasks.
- Cannot create, edit, delete, or reassign tasks.

The backend enforces authorization on every API request. The frontend hides actions that the current role cannot use.

## Task status flow

Assignees can make these status changes:

- `TODO` → `IN_PROGRESS` or `BLOCKED`
- `IN_PROGRESS` → `DONE` or `BLOCKED`
- `BLOCKED` → `IN_PROGRESS`
- `DONE` is a final status for assignees

Assigners can update the status of any task.

## API overview

| Method   | Endpoint                        | Access                                               |
| -------- | ------------------------------- | ---------------------------------------------------- |
| `GET`    | `/api/tasks`                    | Assigner; optional `status` and `assigneeId` filters |
| `GET`    | `/api/tasks/my`                 | Assignee; returns only their tasks                   |
| `GET`    | `/api/tasks/{taskId}`           | Assigner                                             |
| `POST`   | `/api/tasks`                    | Assigner                                             |
| `PUT`    | `/api/tasks/{taskId}`           | Assigner                                             |
| `DELETE` | `/api/tasks/{taskId}`           | Assigner                                             |
| `PATCH`  | `/api/tasks/{taskId}/status`    | Assigner                                             |
| `PATCH`  | `/api/tasks/my/{taskId}/status` | Assignee; own tasks only                             |
| `GET`    | `/api/assignees`                | Assigner                                             |

### Swagger UI

When the application is running, interactive API documentation is available at:

http://localhost:8080/swagger-ui/index.html

To call protected endpoints, sign in through the frontend and copy the access token from an authenticated API request in the browser's Network tools. In Swagger UI, select **Authorize** and paste the token **without** the `Bearer ` prefix; Swagger adds that prefix to the request automatically. Use an assigner token for assigner endpoints and an assignee token for assignee endpoints. Do not share access tokens.

## Design decisions and assumptions

- Each task has exactly one assignee.
- Keycloak manages users and roles. The application stores the assignee's Keycloak ID and username with the task.
- The backend obtains the creator's identity from the authenticated token and sets the creation date automatically.
- Task filtering and role-based access checks happen in the backend.
- PostgreSQL stores task data. The Keycloak realm configuration is stored in `infra/keycloak/team-task-manager-realm.json`.

## Validation and error handling

The backend validates task input and verifies that the selected assignee exists in Keycloak. Invalid input and invalid status transitions are rejected with `400 Bad Request`.

If an assignee requests a task that does not exist or is assigned to another user, the API returns `404 Not Found`. Requests made without the required role are denied by the backend.

## Tests

The backend uses JUnit Jupiter through Spring Boot's test dependencies. The Angular project is configured to run unit tests with Vitest.

Run backend tests from the `backend` directory:

```bash
./mvnw test
```

Run frontend tests from the `frontend` directory:

```bash
npm test
```

## Possible future improvements

- Add task search or pagination.
- Add a task status-change history or comments.
- Use a database migration tool such as Flyway as the schema evolves.
