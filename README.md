# 📚 TPRS — Thesis & Project Repository System

A full-stack web application for managing academic thesis and project submissions, supervisor assignments, and approval workflows.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![Servlet](https://img.shields.io/badge/Servlet_API-4.0-green)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven&logoColor=white)

---

## ✨ Features

- **Student Portal** — Register, submit thesis/projects with file uploads, track approval status
- **Supervisor Dashboard** — Review submissions, approve/reject projects, manage assigned students
- **Smart Login** — Single login form with automatic role detection (student or teacher)
- **File Management** — Upload project documents (up to 50 MB), download anytime
- **Notifications** — Real-time alerts for submissions, approvals, rejections, and assignments
- **Analytics Dashboard** — Statistics by department, year, and project status
- **Supervisor Assignment** — Assign/unassign students to supervisors per year & semester

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (HTML/CSS/JS)                   │
│              login.html · home.html · upload.html            │
│                  supervisor-dashboard.html                   │
│                                                             │
│                     api.js  ← centralized API client         │
└──────────────────────────┬──────────────────────────────────┘
                           │  HTTP (JSON / Multipart)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                   Backend (Java Servlets)                    │
│                                                             │
│   Servlet Layer     AuthServlet · ProjectServlet             │
│                     DashboardServlet · NotificationServlet   │
│                     SupervisorAssignmentServlet              │
│                                                             │
│   Service Layer     StudentService · TeacherService          │
│                     ProjectService · NotificationService     │
│                     SupervisorStudentService                 │
│                                                             │
│   DAO Layer         StudentDAO · TeacherDAO · ProjectDAO     │
│                     NotificationDAO · SupervisorStudentDAO   │
└──────────────────────────┬──────────────────────────────────┘
                           │  JDBC
                           ▼
                 ┌───────────────────┐
                 │   MySQL (tprs_db) │
                 └───────────────────┘
```

---

## 📁 Project Structure

```
TPRS/
├── home.html                        # Student home page
├── login.html                       # Login page
├── signup.html                      # Student registration
├── upload.html                      # Project submission
├── supervisor-dashboard.html        # Supervisor panel
├── api.js                           # Frontend API client
├── script.js                        # Frontend logic
├── style.css                        # Main styles
├── login.css                        # Login page styles
├── start-project.ps1                # One-click startup script
│
└── backend/
    ├── pom.xml                      # Maven build config
    ├── WEB-INF/web.xml              # Servlet URL mappings
    ├── sql/
    │   ├── create_database.sql      # DB schema + views + procedures
    │   ├── fix_procedure.sql        # Procedure patches
    │   └── update_stats.sql         # Stats procedure updates
    └── src/main/
        ├── java/com/tprs/
        │   ├── Main.java
        │   ├── config/              # DatabaseConfig
        │   ├── model/               # Student, Teacher, Project, Notification
        │   ├── dao/                 # Data access objects
        │   ├── service/             # Business logic
        │   └── servlet/             # HTTP endpoints + CORS filter
        └── resources/
            └── db.properties        # Database credentials (not tracked)
```

---

## 🚀 Getting Started

### Prerequisites

| Tool      | Version   |
|-----------|-----------|
| Java JDK  | 21+       |
| Maven     | 3.9+      |
| MySQL     | 8.0+      |
| Python    | 3.x (for frontend server) |

### 1. Set Up the Database

```sql
-- Run the schema script in MySQL
SOURCE backend/sql/create_database.sql;
```

### 2. Configure Database Credentials

Create the file `backend/src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/tprs_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=your_username
db.password=your_password
```

> ⚠️ This file is git-ignored. Never commit real credentials.

### 3. Run the Project

**Option A — One command:**

```powershell
.\start-project.ps1
```

This builds the backend, starts the frontend on port 3000, and launches the backend on port 8080.

**Option B — Manual:**

```powershell
# Terminal 1: Backend
cd backend
mvn clean package -DskipTests
mvn jetty:run

# Terminal 2: Frontend
python -m http.server 3000
```

### 4. Open in Browser

| Page | URL |
|------|-----|
| Home | http://localhost:3000/home.html |
| Login | http://localhost:3000/login.html |
| Signup | http://localhost:3000/signup.html |
| Backend API | http://localhost:8080/tprs/api |

---

## 📡 API Reference

Base URL: `http://localhost:8080/tprs/api`

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/login` | Login (auto-detects role) |
| `POST` | `/auth/register` | Register student |
| `POST` | `/auth/register-teacher` | Register teacher |
| `PUT`  | `/auth/{id}` | Update profile |

### Projects

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/projects` | List projects (filterable by status, department, studentId, etc.) |
| `GET` | `/projects/recent` | Recent projects |
| `GET` | `/projects/pending` | Pending projects |
| `GET` | `/projects/approved` | Approved projects |
| `GET` | `/projects/{id}` | Get project details |
| `GET` | `/projects/{id}/download` | Download project file |
| `POST` | `/projects` | Submit project (multipart/form-data) |
| `PUT` | `/projects/{id}` | Update project |
| `PUT` | `/projects/{id}/approve` | Approve project |
| `PUT` | `/projects/{id}/reject` | Reject project |
| `DELETE` | `/projects/{id}` | Delete project |

### Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/dashboard/stats` | Aggregate statistics |
| `GET` | `/dashboard/recent` | Recent projects |
| `GET` | `/dashboard/by-department` | Projects by department |
| `GET` | `/dashboard/by-year` | Projects by year |

### Notifications

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/notifications` | Get notifications (`?userId=&userType=`) |
| `GET` | `/notifications/count` | Unread count |
| `PUT` | `/notifications/{id}` | Mark as read |
| `PUT` | `/notifications/read-all` | Mark all as read |

### Supervisor Assignments

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/assignments/by-supervisor` | Students assigned to supervisor |
| `GET` | `/assignments/unassigned` | Available students |
| `GET` | `/assignments/by-student` | Supervisors for a student |
| `POST` | `/assignments` | Assign student to supervisor |
| `DELETE` | `/assignments` | Remove assignment |

---

## 🗄️ Database Schema

| Table | Purpose |
|-------|---------|
| `student` | Student accounts and profiles |
| `teacher` | Teacher/supervisor accounts |
| `project` | Thesis and project submissions |
| `supervisor_student` | Student–supervisor assignments |
| `notification` | In-app notifications |

Key views and stored procedures:
- `project_details` — joins project with student and supervisor names
- `GetDashboardStats()` — aggregate counts for the dashboard
- `GetRecentProjects(limit)` — latest N projects
- `GetProjectsCountByDepartment()` / `GetProjectsCountByYear()`

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Backend | Java 21, Servlet API 4.0, Jetty 10 |
| Database | MySQL 8.0 |
| Build | Apache Maven |
| JSON | Google Gson 2.10 |
| JDBC | MySQL Connector/J 9.0 |

---

## 📄 License

This project is for academic purposes.
