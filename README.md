# Real-time Chat Application

A modern, real-time chat application built with Java Spring Boot, WebSocket, and H2 database. Features include user authentication, multiple chat rooms, real-time messaging, and a beautiful responsive UI.

## Features

- **Real-time Messaging**: WebSocket-based instant messaging
- **Multiple Chat Rooms**: Create and join different chat rooms
- **User Authentication**: JWT-based authentication system
- **Message Persistence**: H2 database for storing messages and user data
- **Responsive UI**: Modern, mobile-friendly interface
- **Room Management**: Create, join, and leave chat rooms
- **Message History**: View previous messages in rooms
- **User Sessions**: Track user activity and login status

## Technologies Used

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebSocket** - Real-time messaging
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **H2 Database** - In-memory database
- **JWT** - Token-based authentication

### Frontend
- **HTML5/CSS3** - Modern responsive design
- **JavaScript (ES6+)** - Client-side functionality
- **WebSocket (STOMP)** - Real-time communication
- **Font Awesome** - Icons

## Project Structure

```
src/
├── main/
│   ├── java/com/chatapp/
│   │   ├── ChatApplication.java          # Main application class
│   │   ├── config/
│   │   │   └── WebSocketConfig.java     # WebSocket configuration
│   │   ├── controller/
│   │   │   ├── AuthController.java      # Authentication endpoints
│   │   │   ├── ChatRoomController.java  # Room management endpoints
│   │   │   ├── MessageController.java   # Message endpoints
│   │   │   └── WebSocketController.java # WebSocket message handling
│   │   ├── dto/
│   │   │   ├── UserDto.java            # User data transfer object
│   │   │   ├── MessageDto.java         # Message data transfer object
│   │   │   └── ChatRoomDto.java        # Room data transfer object
│   │   ├── entity/
│   │   │   ├── User.java               # User entity
│   │   │   ├── ChatRoom.java           # Chat room entity
│   │   │   └── Message.java            # Message entity
│   │   ├── repository/
│   │   │   ├── UserRepository.java     # User database operations
│   │   │   ├── ChatRoomRepository.java # Room database operations
│   │   │   └── MessageRepository.java  # Message database operations
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java   # JWT token management
│   │   │   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   │   │   ├── JwtAuthenticationEntryPoint.java # Authentication entry point
│   │   │   └── SecurityConfig.java     # Security configuration
│   │   └── service/
│   │       ├── UserService.java        # User business logic
│   │       ├── ChatRoomService.java    # Room business logic
│   │       └── MessageService.java     # Message business logic
│   └── resources/
│       ├── static/
│       │   ├── index.html              # Main HTML file
│       │   ├── css/
│       │   │   └── style.css           # Stylesheet
│       │   └── js/
│       │       └── app.js              # Frontend JavaScript
│       └── application.properties      # Application configuration
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd realtime-chat
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Open your browser and navigate to `http://localhost:8080`
   - The application will start with an empty database

### Database Access
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:chatdb`
- Username: `sa`
- Password: `password`

## API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "john_doe"
}
```

#### Get Current User
```http
GET /api/auth/me
Authorization: Bearer <token>
```

### Chat Room Endpoints

#### Create Room
```http
POST /api/rooms
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "General Chat",
  "description": "General discussion room",
  "isPrivate": false,
  "maxParticipants": 50
}
```

#### Get All Public Rooms
```http
GET /api/rooms
Authorization: Bearer <token>
```

#### Get User's Rooms
```http
GET /api/rooms/my
Authorization: Bearer <token>
```

#### Join Room
```http
POST /api/rooms/{roomId}/join
Authorization: Bearer <token>
```

#### Leave Room
```http
POST /api/rooms/{roomId}/leave
Authorization: Bearer <token>
```

#### Search Rooms
```http
GET /api/rooms/search?keyword=general
Authorization: Bearer <token>
```

#### Get Room Participants
```http
GET /api/rooms/{roomId}/participants
Authorization: Bearer <token>
```

### Message Endpoints

#### Get Room Messages
```http
GET /api/messages/room/{roomId}?page=0&size=50
Authorization: Bearer <token>
```

#### Search Messages in Room
```http
GET /api/messages/room/{roomId}/search?keyword=hello
Authorization: Bearer <token>
```

#### Update Message
```http
PUT /api/messages/{messageId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Updated message content"
}
```

#### Delete Message
```http
DELETE /api/messages/{messageId}
Authorization: Bearer <token>
```

### WebSocket Endpoints

#### Connect to WebSocket
```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
```

#### Send Message
```javascript
stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
  content: "Hello world!",
  roomId: 1
}));
```

#### Join Room
```javascript
stompClient.send("/app/chat.addUser", {}, JSON.stringify({
  username: "john_doe",
  roomId: 1
}));
```

#### Subscribe to Room Messages
```javascript
stompClient.subscribe(`/topic/room.${roomId}`, function(message) {
  const receivedMessage = JSON.parse(message.body);
  // Handle received message
});
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_login TIMESTAMP,
  is_active BOOLEAN DEFAULT TRUE
);
```

### Chat Rooms Table
```sql
CREATE TABLE chat_rooms (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  description VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_private BOOLEAN DEFAULT FALSE,
  max_participants INT,
  created_by BIGINT REFERENCES users(id)
);
```

### Messages Table
```sql
CREATE TABLE messages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  content VARCHAR(1000) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  message_type VARCHAR(20) DEFAULT 'TEXT',
  user_id BIGINT REFERENCES users(id),
  room_id BIGINT REFERENCES chat_rooms(id),
  is_edited BOOLEAN DEFAULT FALSE,
  edited_at TIMESTAMP
);
```

### Junction Tables
```sql
-- User-Room relationship
CREATE TABLE room_participants (
  room_id BIGINT REFERENCES chat_rooms(id),
  user_id BIGINT REFERENCES users(id),
  PRIMARY KEY (room_id, user_id)
);
```

## Features in Detail

### Real-time Messaging
- WebSocket-based communication for instant message delivery
- Message persistence in H2 database
- Support for different message types (TEXT, SYSTEM)
- Message editing and deletion capabilities

### User Management
- User registration and authentication
- JWT token-based session management
- User activity tracking
- Profile management

### Room Management
- Create public and private rooms
- Join and leave rooms
- Room search functionality
- Participant management
- Room descriptions and metadata

### Security Features
- JWT-based authentication
- Password encryption using BCrypt
- CORS configuration for cross-origin requests
- Input validation and sanitization

### UI/UX Features
- Responsive design for mobile and desktop
- Modern gradient background
- Real-time message updates
- Room selection interface
- Message history with timestamps
- User-friendly error handling

## Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:chatdb
spring.datasource.username=sa
spring.datasource.password=password

# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# WebSocket Configuration
spring.websocket.max-text-message-size=8192
```

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
java -jar target/realtime-chat-1.0.0.jar
```

### Customization
- Modify `application.properties` for different database configurations
- Update JWT secret in production
- Customize UI styles in `src/main/resources/static/css/style.css`
- Extend functionality by adding new controllers and services

## Troubleshooting

### Common Issues

1. **Port already in use**
   - Change `server.port` in `application.properties`
   - Or kill the process using the port

2. **WebSocket connection failed**
   - Ensure the application is running on `http://localhost:8080`
   - Check browser console for connection errors

3. **Database connection issues**
   - Verify H2 database is properly configured
   - Check `application.properties` for correct database settings

4. **Authentication errors**
   - Ensure JWT token is included in request headers
   - Check token expiration and validity

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please open an issue in the repository. 