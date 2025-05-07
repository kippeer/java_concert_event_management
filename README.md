# Event Management API

A robust REST API for managing events, shows, and parties built with Spring Boot.

## Features

- 🔐 JWT Authentication & Authorization
- 👥 User Management with Role-based Access Control
- 📅 Event Management (CRUD operations)
- 🎭 Artist Management
- 🏟️ Venue Management
- 🎫 Ticket Management
- 📑 Category Management
- ✅ Input Validation
- 🔄 Exception Handling
- 📝 Swagger Documentation

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- H2 Database
- JWT Authentication
- Maven
- Swagger/OpenAPI

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### API Documentation

Access the Swagger UI at: `http://localhost:8080/api/swagger-ui.html`

### Default Users

The system initializes with three roles:
- ROLE_USER
- ROLE_ORGANIZER
- ROLE_ADMIN

### Authentication

All secured endpoints require a JWT token. To get a token:

1. Register a new user:
   ```http
   POST /api/auth/signup
   ```

2. Login to get JWT token:
   ```http
   POST /api/auth/signin
   ```

3. Use the token in the Authorization header:
   ```
   Bearer <your_token>
   ```

## API Endpoints

### Authentication
- POST `/api/auth/signin` - Login
- POST `/api/auth/signup` - Register new user

### Events
- GET `/api/events` - List all events
- GET `/api/events/{id}` - Get event by ID
- POST `/api/events` - Create new event
- PUT `/api/events/{id}` - Update event
- DELETE `/api/events/{id}` - Delete event
- GET `/api/events/search` - Search events
- GET `/api/events/category/{categoryId}` - Get events by category
- GET `/api/events/date-range` - Get events by date range
- GET `/api/events/city/{city}` - Get events by city
- PUT `/api/events/{id}/publish` - Publish event
- PUT `/api/events/{id}/cancel` - Cancel event

### Artists
- GET `/api/artists` - List all artists
- GET `/api/artists/{id}` - Get artist by ID
- POST `/api/artists` - Create new artist
- PUT `/api/artists/{id}` - Update artist
- DELETE `/api/artists/{id}` - Delete artist
- GET `/api/artists/search` - Search artists
- GET `/api/artists/genre/{genre}` - Get artists by genre

### Venues
- GET `/api/venues` - List all venues
- GET `/api/venues/{id}` - Get venue by ID
- POST `/api/venues` - Create new venue
- PUT `/api/venues/{id}` - Update venue
- DELETE `/api/venues/{id}` - Delete venue
- GET `/api/venues/search` - Search venues
- GET `/api/venues/city/{city}` - Get venues by city
- GET `/api/venues/capacity/{minCapacity}` - Get venues by minimum capacity

### Tickets
- GET `/api/tickets/user` - Get current user's tickets
- GET `/api/tickets/{id}` - Get ticket by ID
- POST `/api/tickets/purchase` - Purchase ticket
- PUT `/api/tickets/{id}/cancel` - Cancel ticket
- GET `/api/tickets/event/{eventId}` - Get tickets by event
- GET `/api/tickets/validate/{ticketNumber}` - Validate ticket
- PUT `/api/tickets/{id}/mark-used` - Mark ticket as used

### Categories
- GET `/api/categories` - List all categories
- GET `/api/categories/{id}` - Get category by ID
- POST `/api/categories` - Create new category
- PUT `/api/categories/{id}` - Update category
- DELETE `/api/categories/{id}` - Delete category

## Security

- JWT-based authentication
- Role-based access control
- Password encryption
- Input validation
- Exception handling

## Database

The application uses H2 in-memory database by default. Access the H2 console at:
`http://localhost:8080/api/h2-console`

Database credentials (default):
- URL: `jdbc:h2:mem:eventdb`
- Username: `sa`
- Password: `password`