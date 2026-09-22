# 📱 Phonebook Application

A full-stack Phonebook Application for managing contacts with a modern Vue.js frontend, FastAPI backend, and PostgreSQL database.

The application supports creating, viewing, updating, deleting, searching, and filtering contacts. It is containerized using Docker Compose and includes automated frontend and backend testing through GitHub Actions.

---

## 🚀 Features

* Create new contacts
* View contact details
* Update existing contacts
* Delete contacts
* Search contacts by:

  * Name
  * Phone number
* Filter contacts by category:

  * WORK
  * FAMILY
  * FRIEND
* Pagination for contact lists
* Phone number validation
* Email validation
* RESTful API using FastAPI
* PostgreSQL database
* Responsive Vue.js frontend
* Dockerized frontend, backend, and database
* Backend testing with Pytest
* Frontend testing with Vitest
* GitHub Actions CI workflow

---

## 🏗️ Technology Stack

### Frontend

* Vue 3
* Vite
* Vue Router
* Pinia
* Axios
* Vitest
* Vue Test Utils

### Backend

* Python
* FastAPI
* SQLAlchemy
* Pydantic
* PostgreSQL
* Pytest
* Uvicorn

### DevOps

* Docker
* Docker Compose
* GitHub Actions

---

## 📂 Project Structure

```text
phonebook-application/
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── backend/
│   ├── app/
│   │   ├── __init__.py
│   │   ├── crud.py
│   │   ├── database.py
│   │   ├── main.py
│   │   ├── models.py
│   │   └── schemas.py
│   │
│   ├── tests/
│   │   ├── test_contacts.py
│   │   └── test_health.py
│   │
│   ├── Dockerfile
│   └── requirements.txt
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── router/
│   │   ├── services/
│   │   ├── stores/
│   │   └── views/
│   │
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   ├── package-lock.json
│   ├── vite.config.js
│   └── vitest.config.js
│
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## 🔄 Application Architecture

```text
                    ┌─────────────────────┐
                    │      Vue 3          │
                    │     Frontend        │
                    │                     │
                    │ Vue Router          │
                    │ Pinia               │
                    │ Axios               │
                    └──────────┬──────────┘
                               │
                            HTTP/REST
                               │
                               ▼
                    ┌─────────────────────┐
                    │      FastAPI        │
                    │       Backend       │
                    │                     │
                    │ API Routes          │
                    │ Pydantic Schemas    │
                    │ CRUD Operations     │
                    └──────────┬──────────┘
                               │
                          SQLAlchemy
                               │
                               ▼
                    ┌─────────────────────┐
                    │     PostgreSQL      │
                    │      Database       │
                    └─────────────────────┘
```

Docker Compose manages the PostgreSQL, backend, and frontend services.

---

## 🔌 REST API

### Health Check

```http
GET /health
```

Checks whether the API and database are available.

Example response:

```json
{
  "api": "online",
  "database": "online"
}
```

---

### Get Contacts

```http
GET /contacts
```

Supports searching, category filtering, and pagination.

#### Query Parameters

| Parameter   | Description                    | Example |
| ----------- | ------------------------------ | ------- |
| `search`    | Search by name or phone number | `john`  |
| `category`  | Filter by category             | `WORK`  |
| `page`      | Page number                    | `1`     |
| `page_size` | Number of contacts per page    | `10`    |

Example:

```http
GET /contacts?search=john&category=WORK&page=1&page_size=10
```

Example response:

```json
{
  "items": [],
  "total": 0,
  "page": 1,
  "page_size": 10,
  "total_pages": 1,
  "category": "WORK"
}
```

---

### Create Contact

```http
POST /contacts
```

Example request:

```json
{
  "name": "John Doe",
  "phone_number": "+919876543210",
  "email": "john@example.com",
  "address": "Mumbai, India",
  "category": "WORK"
}
```

Returns the newly created contact.

---

### Get Contact

```http
GET /contacts/{contact_id}
```

Example:

```http
GET /contacts/1
```

Returns a single contact.

---

### Update Contact

```http
PUT /contacts/{contact_id}
```

Example:

```http
PUT /contacts/1
```

Updates the specified contact.

---

### Delete Contact

```http
DELETE /contacts/{contact_id}
```

Example:

```http
DELETE /contacts/1
```

Deletes the specified contact.

---

## 🗄️ Database Model

The application uses a PostgreSQL `contacts` table.

| Field          | Type     | Description                |
| -------------- | -------- | -------------------------- |
| `id`           | Integer  | Primary key                |
| `name`         | String   | Contact name               |
| `phone_number` | String   | Unique phone number        |
| `email`        | String   | Optional unique email      |
| `address`      | Text     | Optional address           |
| `category`     | String   | WORK, FAMILY, or FRIEND    |
| `created_at`   | DateTime | Contact creation timestamp |

---

## 🧪 Validation

The backend uses Pydantic for request validation.

### Phone Number

Phone numbers must contain 7–20 digits and may optionally start with `+`.

Example:

```text
+919876543210
```

### Email

Email addresses are validated using Pydantic's `EmailStr`.

### Category

Only the following categories are accepted:

```text
WORK
FAMILY
FRIEND
```

---

## 🐳 Running with Docker

Make sure Docker Desktop is installed and running.

From the project root:

```bash
docker compose up --build
```

This starts:

```text
PostgreSQL
    ↓
FastAPI Backend
    ↓
Vue Frontend
```

### Services

| Service    |   Port | Purpose     |
| ---------- | -----: | ----------- |
| PostgreSQL | `5432` | Database    |
| FastAPI    | `8000` | Backend API |
| Vue/Nginx  | `5173` | Frontend    |

### Application

Frontend:

```text
http://localhost:5173
```

Backend:

```text
http://localhost:8000
```

FastAPI Swagger documentation:

```text
http://localhost:8000/docs
```

---

## 💻 Running Backend Locally

Navigate to the backend:

```bash
cd backend
```

Create and activate a virtual environment:

### Windows

```powershell
python -m venv .venv
.venv\Scripts\activate
```

Install dependencies:

```bash
pip install -r requirements.txt
```

Set the database connection if PostgreSQL is running locally:

```powershell
$env:DATABASE_URL="postgresql://postgres:postgres@localhost:5432/phonebook"
```

Start the FastAPI application:

```bash
uvicorn app.main:app --reload
```

The API will be available at:

```text
http://localhost:8000
```

Swagger documentation:

```text
http://localhost:8000/docs
```

---

## 🎨 Running Frontend Locally

Navigate to the frontend:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend will normally be available at:

```text
http://localhost:5173
```

---

## 🧪 Testing

### Backend Tests

From the `backend` directory:

```bash
pytest -q
```

Backend tests cover API health and contact functionality.

### Frontend Tests

From the `frontend` directory:

```bash
npm test
```

### Frontend Build

To create a production build:

```bash
npm run build
```

---

## 🔄 Continuous Integration

The project includes a GitHub Actions workflow:

```text
.github/workflows/ci.yml
```

The CI pipeline performs:

### Backend

```text
Checkout code
      ↓
Set up Python
      ↓
Install dependencies
      ↓
Run Pytest
```

### Frontend

```text
Checkout code
      ↓
Set up Node.js
      ↓
npm ci
      ↓
Run Vitest
      ↓
Build frontend
```

This helps ensure that changes do not break the backend tests, frontend tests, or production frontend build.

---

## 🔐 Environment Variables

The backend supports the following environment variable:

```text
DATABASE_URL
```

Example:

```text
DATABASE_URL=postgresql://postgres:postgres@localhost:5432/phonebook
```

For Docker Compose, the backend connects to PostgreSQL using the Docker service name:

```text
postgresql://postgres:postgres@postgres:5432/phonebook
```

Do not commit real credentials or `.env` files to GitHub.

---

## 🛡️ Git Ignore

The project ignores files and directories that should not be committed, including:

```text
.venv/
venv/
node_modules/
frontend/dist/
__pycache__/
.pytest_cache/
.env
.env.*
.vscode/
.idea/
```

---

## 📌 Future Improvements

Possible improvements for future versions include:

* Authentication and authorization
* Database migrations using Alembic
* More comprehensive API test coverage
* PostgreSQL service integration in CI
* Improved error handling
* Contact sorting options
* Import/export contacts
* Profile/contact photos
* Deployment to a cloud platform
* Production environment configuration

---

## 👨‍💻 Project

**Phonebook Application**

A full-stack CRUD application demonstrating modern frontend development, REST API design, database integration, testing, containerization, and continuous integration.
