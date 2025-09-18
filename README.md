# Car Station Service

A full-stack web application for managing electric vehicle charging stations. This system provides a comprehensive platform for users to search, manage, and monitor charging stations with real-time statistics and location-based search capabilities.

## Features

### Backend Features
- **RESTful API**: Complete CRUD operations for charging stations
- **User Authentication**: JWT-based authentication system with user roles (USER, ADMIN)
- **Advanced Search**: Location-based search with radius filtering, charger type filtering, and status filtering
- **Pagination**: Support for paginated results
- **Caching**: Caffeine caching for improved performance
- **Validation**: Comprehensive input validation with custom error messages
- **Statistics**: Real-time system statistics including availability percentages
- **Database**: H2 in-memory database for development with MySQL support for production

### Frontend Features
- **React Interface**: Modern, responsive user interface
- **Station Management**: Create, read, update, and delete charging stations
- **Geolocation**: Automatic location detection for nearby station search
- **Search Functionality**: Advanced search with multiple criteria
- **Statistics Dashboard**: Visual representation of station metrics
- **User Authentication**: Login and registration system
- **Responsive Design**: Mobile-friendly interface

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Database**: H2 (development), MySQL (production ready)
- **Security**: Spring Security with JWT
- **Validation**: Bean Validation API
- **Caching**: Spring Cache with Caffeine
- **Build Tool**: Maven
- **Testing**: JUnit, Mockito

### Frontend
- **Framework**: React 18.2.0
- **HTTP Client**: Axios
- **Build Tool**: Create React App
- **Styling**: CSS3 with responsive design

## Installation & Setup

### Prerequisites
- Java 21 JDK
- Node.js 16+ and npm
- MySQL (for production)
- Maven 3.6+

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Cristianftf/carstationservice.git
   cd carstationservice
   ```

2. **Configure database (optional for production)**
   - For development: H2 database is configured by default
   - For production: Update `src/main/resources/application.properties` with MySQL settings:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/carstationdb
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
     spring.jpa.hibernate.ddl-auto=update
     ```

3. **Build and run the backend**
   ```bash
   # Using Maven wrapper
   ./mvnw spring-boot:run
   
   # Or with installed Maven
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8081`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

## Running the Application

1. Start the backend server first (port 8081)
2. Start the frontend development server (port 3000)
3. Open your browser and navigate to `http://localhost:3000`

### Default Access
- **H2 Console**: `http://localhost:8081/h2-console`
  - JDBC URL: `jdbc:h2:mem:carstationdb`
  - Username: `sa`
  - Password: (empty)

## API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Charging Station Endpoints
- `GET /api/charging-stations` - Get all stations
- `GET /api/charging-stations/paged` - Get paginated stations
- `GET /api/charging-stations/{id}` - Get station by ID
- `POST /api/charging-stations` - Create new station
- `PUT /api/charging-stations/{id}` - Update station
- `DELETE /api/charging-stations/{id}` - Delete station
- `GET /api/charging-stations/charger-type/{type}` - Filter by charger type (AC/DC_FAST)
- `GET /api/charging-stations/status/{status}` - Filter by status (AVAILABLE/IN_USE)
- `GET /api/charging-stations/available` - Get available stations
- `GET /api/charging-stations/in-use` - Get in-use stations
- `GET /api/charging-stations/location-range` - Search by location range
- `GET /api/charging-stations/search` - Search by address
- `GET /api/charging-stations/min-points/{minPoints}` - Filter by minimum charging points
- `PATCH /api/charging-stations/{id}/status` - Change station status
- `GET /api/charging-stations/statistics` - Get system statistics

### Data Models

#### Charging Station
```json
{
  "id": 1,
  "address": "123 Main Street",
  "latitude": 23.113592,
  "longitude": -82.366592,
  "chargerType": "AC",
  "chargingPoints": 4,
  "status": "AVAILABLE"
}
```

#### User
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "password": "hashed_password",
  "role": "USER"
}
```

## Project Structure

```
carstationservice/
├── src/                          # Backend source code
│   ├── main/java/com/station/carstationservice/
│   │   ├── config/               # Security configuration
│   │   ├── controller/          # REST controllers
│   │   ├── model/               # Data models
│   │   ├── repository/          # Data access layer
│   │   ├── security/            # JWT authentication
│   │   └── service/             # Business logic
│   └── main/resources/          # Configuration files
├── frontend/                     # React frontend
│   ├── public/                  # Static files
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── utils/              # Utility functions
│   │   └── App.js              # Main application component
│   └── package.json            # Frontend dependencies
├── pom.xml                     # Maven configuration
└── README.md                  # This file
```

## Configuration

### Backend Configuration (`src/main/resources/application.properties`)
- Server port: 8081
- H2 database enabled with console
- JWT secret and expiration settings
- Caffeine cache configuration

### Frontend Configuration (`frontend/package.json`)
- Proxy configured to backend (http://localhost:8081)
- React scripts for development and build

## Development

### Running Tests
```bash
# Backend tests
./mvnw test

# Frontend tests
cd frontend
npm test
```

### Building for Production
```bash
# Build backend JAR
./mvnw clean package

# Build frontend
cd frontend
npm run build
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions, please open an issue on GitHub or contact the development team.

## Acknowledgments

- Spring Boot team for the excellent framework
- React team for the frontend library
- Open source community for various utilities and tools
