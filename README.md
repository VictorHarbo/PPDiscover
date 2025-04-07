# PPDiscover

PPDiscover is a full-stack application consisting of a Java Spring Boot backend and a Vue.js frontend.

## Project Structure

```
PPDiscover-parent/
├── PPDiscover/              # Backend (Java Spring Boot)
│   ├── src/                 # Source code
│   ├── pom.xml             # Maven dependencies
│   └── Dockerfile          # Backend container configuration
│
├── PPDiscover-frontend/     # Frontend (Vue.js)
│   ├── src/                # Source code
│   ├── package.json        # NPM dependencies
│   └── Dockerfile          # Frontend container configuration
│
├── docker-compose.yml       # Docker Compose configuration
├── .gitignore              # Git ignore rules
└── CHANGELOG.md            # Project changelog
```

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Node.js 18+ (for local development)
- Maven (for local development)

## Getting Started

### Using Docker (Recommended)

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd PPDiscover-parent
   ```

2. Build and start the containers:
   ```bash
   docker-compose up --build
   ```

3. Access the application:
   - Frontend: http://localhost:3000
   - Backend: http://localhost:8080

### Local Development

#### Backend

1. Navigate to the backend directory:
   ```bash
   cd PPDiscover
   ```

2. Build and run using Maven:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

#### Frontend

1. Navigate to the frontend directory:
   ```bash
   cd PPDiscover-frontend
   ```

2. Install dependencies and run:
   ```bash
   npm install
   npm run dev
   ```

## Development

### Backend Development

The backend is built with:
- Java 21
- Spring Boot
- Maven

### Frontend Development

The frontend is built with:
- Vue.js
- Node.js
- Vite

## Contributing

1. Create a new branch for your feature
2. Make your changes
3. Submit a pull request

## Versioning

This project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html). For the versions available, see the [CHANGELOG.md](CHANGELOG.md).

## License

[Add your license here] 