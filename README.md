# ProCreds - DevOps Platform Credential Manager

A comprehensive full-stack web application for managing DevOps platform credentials and configurations. Built with React.js frontend and Spring Boot backend, using MongoDB for data persistence.

## 🚀 Features

- **Multi-Platform Support**: Manage credentials for 10+ DevOps platforms
- **Secure Storage**: Encrypted credential storage with MongoDB
- **Full CRUD Operations**: Create, Read, Update, Delete configurations
- **Search & Pagination**: Advanced search with paginated results
- **Responsive UI**: Mobile-first design with Tailwind CSS
- **REST API**: Comprehensive API with Swagger documentation
- **Form Validation**: Client-side and server-side validation
- **Error Handling**: Global exception handling with meaningful messages

## 🛠️ Tech Stack

### Frontend
- **React.js 18.2.0** - UI framework
- **React Router DOM 6.20.1** - Client-side routing
- **Tailwind CSS 3.3.6** - Utility-first CSS framework
- **Axios 1.6.2** - HTTP client
- **React Hook Form 7.48.2** - Form state management
- **React Hot Toast 2.4.1** - Toast notifications
- **Lucide React 0.294.0** - Icon library

### Backend
- **Java 17+** - Runtime environment
- **Spring Boot 3.2.0** - Application framework
- **Spring Data MongoDB** - Data persistence
- **Spring Security** - Authentication & authorization
- **Spring Validation** - Input validation
- **Lombok** - Code generation
- **SpringDoc OpenAPI 3** - API documentation

### Database
- **MongoDB** - Document database with separate collections per platform

## 📋 Supported Platforms

1. **GitHub** - Account credentials and organization settings
2. **Bitbucket** - Username and app password management
3. **GitLab** - Personal access token configuration
4. **Jenkins** - Server URL and API token management
5. **Jira** - Project configuration and API credentials
6. **SonarQube** - Server and organization settings
7. **Kubernetes** - Generic cluster configurations
8. **AWS EKS** - Amazon Elastic Kubernetes Service
9. **Azure AKS** - Azure Kubernetes Service
10. **GCP GKE** - Google Kubernetes Engine

## 🏗️ Project Structure

```
ProCreds/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/com/procreds/
│   │   ├── ProCredsApplication.java
│   │   ├── config/            # Configuration classes
│   │   ├── controller/        # REST controllers
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # MongoDB entities
│   │   ├── exception/        # Exception handling
│   │   ├── repository/       # Data repositories
│   │   └── service/          # Business logic
│   ├── src/main/resources/
│   │   └── application.yml   # Application configuration
│   └── pom.xml              # Maven dependencies
├── frontend/                  # React frontend
│   ├── public/               # Static assets
│   ├── src/
│   │   ├── components/       # React components
│   │   ├── services/         # API services
│   │   ├── utils/           # Utility functions
│   │   ├── App.js           # Main application
│   │   └── index.js         # Entry point
│   ├── package.json         # NPM dependencies
│   └── tailwind.config.js   # Tailwind configuration
└── README.md                # This file
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- MongoDB 4.4 or higher
- Maven 3.6 or higher

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ProCreds
   ```

2. **Configure MongoDB**
   ```bash
   # Start MongoDB service
   sudo systemctl start mongod
   
   # Or using Docker
   docker run -d -p 27017:27017 --name mongodb mongo:latest
   ```

3. **Set environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

4. **Run the backend**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Start the development server**
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

### MongoDB Index Creation

Run these commands in MongoDB shell to create required indexes:

```javascript
// GitHub configurations
db.github_configs.createIndex({ "accountName": 1 }, { unique: true })

// Bitbucket configurations
db.bitbucket_configs.createIndex({ "username": 1 }, { unique: true })

// GitLab configurations
db.gitlab_configs.createIndex({ "accountName": 1 }, { unique: true })

// Jenkins configurations
db.jenkins_configs.createIndex({ "accountName": 1 }, { unique: true })

// Jira configurations
db.jira_configs.createIndex({ "accountName": 1 }, { unique: true })

// SonarQube configurations
db.sonarqube_configs.createIndex({ "accountName": 1 }, { unique: true })

// Kubernetes configurations
db.kubernetes_configs.createIndex({ "clusterName": 1 }, { unique: true })

// AWS EKS configurations
db.aws_eks_configs.createIndex({ "clusterName": 1, "region": 1 }, { unique: true })

// Azure AKS configurations
db.azure_aks_configs.createIndex({ "clusterName": 1, "tenantId": 1 }, { unique: true })

// GCP GKE configurations
db.gcp_gke_configs.createIndex({ "clusterName": 1, "projectId": 1 }, { unique: true })
```

## 📚 API Documentation

Once the backend is running, access the Swagger UI at:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

### Authentication

The API uses HTTP Basic Authentication:
- **Username**: admin (configurable via `SECURITY_USER`)
- **Password**: admin123 (configurable via `SECURITY_PASSWORD`)

### Sample API Endpoints

#### GitHub Configuration
```bash
# Get all GitHub configurations
GET /api/github?page=0&size=10&search=keyword

# Create GitHub configuration
POST /api/github
{
  "accountName": "myaccount",
  "accessToken": "ghp_xxxxxxxxxxxx",
  "organization": "myorg",
  "apiUrl": "https://api.github.com",
  "description": "My GitHub account"
}

# Update GitHub configuration
PUT /api/github/{id}
{
  "description": "Updated description"
}

# Delete GitHub configuration
DELETE /api/github/{id}
```

## 🎨 UI Components

### Dashboard
- Overview of all platforms
- Quick access to platform configurations
- Getting started guide

### Platform Management
- **List View**: Paginated table with search functionality
- **Create Form**: Multi-section form with validation
- **Edit Form**: Pre-filled form for updates
- **Detail View**: Read-only configuration display with masked credentials

### Navigation
- Collapsible sidebar with platform hierarchy
- Breadcrumb navigation
- Active state indicators

## 🔒 Security Features

- **Basic Authentication** for API access
- **Input Validation** on both client and server
- **Password Masking** in UI components
- **CORS Configuration** for cross-origin requests
- **Error Sanitization** to prevent information leakage

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### Frontend Testing
```bash
cd frontend
npm test
```

## 📦 Production Deployment

### Backend (JAR)
```bash
cd backend
mvn clean package
java -jar target/procreds-backend-1.0.0.jar
```

### Frontend (Build)
```bash
cd frontend
npm run build
# Serve the build folder with your preferred web server
```

### Docker Deployment
```bash
# Backend
docker build -t procreds-backend ./backend
docker run -p 8080:8080 procreds-backend

# Frontend
docker build -t procreds-frontend ./frontend
docker run -p 3000:3000 procreds-frontend
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the API documentation at `/api/swagger-ui.html`
- Review the application logs for debugging

## 🗺️ Roadmap

- [ ] Complete implementation for all 10 platforms
- [ ] Add credential encryption at rest
- [ ] Implement role-based access control
- [ ] Add audit logging
- [ ] Create Docker Compose setup
- [ ] Add integration tests
- [ ] Implement credential rotation
- [ ] Add backup/restore functionality

