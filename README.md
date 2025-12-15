# E-Consultancy for Farmers

> A comprehensive platform connecting farmers with agricultural consultants for expert advice, farm visits, and real-time communication.

[![Angular](https://img.shields.io/badge/Angular-20.3-red)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)

---

## 📋 Overview

**E-Consultancy for Farmers** is a full-stack web application that bridges the gap between farmers and agricultural consultants. The platform enables farmers to request expert consultations, communicate in real-time via chat, schedule farm visits, and receive detailed consultation reports.

### Key Features

- 🌾 **Consultation Management**: Request and manage agricultural consultations
- 💬 **Real-Time Chat**: WebSocket-based messaging between farmers and consultants
- 📅 **Farm Visit Scheduling**: Schedule and track on-site farm visits
- 📊 **Consultation Reports**: Detailed reports with attachments
- 🔐 **Secure Authentication**: JWT-based authentication with role-based access control
- 👥 **User Profiles**: Comprehensive profiles for farmers and consultants
- 📱 **Responsive Design**: Modern UI with Tailwind CSS

---

## 🏗️ Architecture

### Technology Stack

**Frontend**
- Angular 20.3 with TypeScript
- Tailwind CSS 4.1 for styling
- STOMP.js + SockJS for WebSocket communication
- RxJS for reactive programming

**Backend**
- Spring Boot 3.5.7 with Java 21
- Spring Security with JWT authentication
- Spring Data JPA with Hibernate
- Spring WebSocket for real-time chat
- MySQL 8.0 database

**Communication**
- REST API for CRUD operations
- WebSocket (STOMP) for real-time messaging
- JWT tokens for authentication

---

## 🚀 Quick Start

### Prerequisites

- Java JDK 21
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd E-Consultancy-for-Farmers
   ```

2. **Setup Database**
   ```sql
   CREATE DATABASE Efarming;
   ```

3. **Configure Backend**
   
   Edit `server/src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       username: your_db_user
       password: your_db_password
     mail:
       username: your_email@gmail.com
       password: your_app_password
   jwt:
     secret-key: "your_secret_key"
   ```

4. **Start Backend**
   ```bash
   cd server
   mvn spring-boot:run
   ```

5. **Start Frontend**
   ```bash
   cd client
   npm install
   ng serve
   ```

6. **Access Application**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080/api

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [PROJECT_DOCUMENTATION.md](./PROJECT_DOCUMENTATION.md) | Complete project architecture, database schema, and features |
| [API_REFERENCE.md](./API_REFERENCE.md) | Detailed API endpoints with request/response examples |
| [DEVELOPER_SETUP.md](./DEVELOPER_SETUP.md) | Step-by-step development environment setup |
| [USER_GUIDE.md](./USER_GUIDE.md) | Complete user guide for farmers, consultants, and admins |
| [COMPONENT_REFERENCE.md](./COMPONENT_REFERENCE.md) | Technical reference for all backend and frontend components |
| [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) | Production deployment guide (Docker, AWS, Azure, GCP) |
| [TESTING_GUIDE.md](./TESTING_GUIDE.md) | Comprehensive testing guide with examples |
| [CONTRIBUTING.md](./CONTRIBUTING.md) | Guidelines for contributing to the project |
| [SECURITY.md](./SECURITY.md) | Security best practices and guidelines |
| [PERFORMANCE.md](./PERFORMANCE.md) | Performance optimization strategies |

---

## 🎯 Core Features

### For Farmers

- ✅ Register and create profile
- ✅ Browse and select consultants
- ✅ Request consultations for specific crops
- ✅ Real-time chat with consultants
- ✅ View consultation reports
- ✅ Track consultation status

### For Consultants

- ✅ Register with verification documents
- ✅ Review and approve/reject consultation requests
- ✅ Schedule farm visits
- ✅ Real-time chat with farmers
- ✅ Create detailed consultation reports
- ✅ Manage consultation lifecycle

### Admin Features

- ✅ User verification and management
- ✅ Platform oversight
- ✅ System administration

---

## 🗄️ Database Schema

### Core Entities

- **User** (base entity with inheritance)
  - Farmer
  - Consultant
  - Admin
- **Consultation** - Consultation requests and details
- **ChatRoom** - Chat sessions between farmer and consultant
- **ChatMessage** - Individual messages with status tracking
- **Farmvisit** - Scheduled farm visits
- **ConsultationReport** - Detailed reports with attachments
- **Crop** - Crop information
- **Address** - Location data with geolocation

See [PROJECT_DOCUMENTATION.md](./PROJECT_DOCUMENTATION.md#database-schema) for detailed schema.

---

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register/farmer` - Register farmer
- `POST /api/auth/register/consultant` - Register consultant
- `POST /api/auth/login` - User login
- `POST /api/auth/forgot-password` - Password recovery

### Consultations
- `GET /api/farmer/consultations` - Get farmer's consultations
- `POST /api/farmer/consultation/request` - Request consultation
- `PUT /api/consultant/consultation/{id}/approve` - Approve request
- `PUT /api/consultant/consultation/{id}/reject` - Reject request

### Chat
- `GET /api/chat/rooms` - Get all chat rooms
- `GET /api/chat/room/{roomId}/messages` - Get messages
- `POST /api/chat/room/{roomId}/message` - Send message
- `WS /ws` - WebSocket connection for real-time chat

See [API_REFERENCE.md](./API_REFERENCE.md) for complete API documentation.

---

## 🎨 UI Components

### Public Pages
- Landing page with features showcase
- Login and registration forms
- Password recovery

### Farmer Dashboard
- Dashboard with consultation overview
- Consultation request form
- Consultation list and details
- Real-time chat interface
- Profile management

### Consultant Dashboard
- Dashboard with request overview
- Consultation request management
- Farm visit scheduling
- Report creation
- Real-time chat interface
- Profile management

---

## 🔐 Security

- **Authentication**: JWT-based token authentication
- **Authorization**: Role-based access control (FARMER, CONSULTANT, ADMIN)
- **Password Security**: BCrypt hashing
- **API Security**: CORS configuration, request validation
- **WebSocket Security**: Token-based authentication
- **Data Protection**: SQL injection prevention, XSS protection

---

## 🛠️ Development

### Project Structure

```
E-Consultancy-for-Farmers/
├── client/                 # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   ├── services/
│   │   │   ├── layouts/
│   │   │   └── models/
│   │   └── styles.css
│   └── package.json
│
├── server/                 # Spring Boot backend
│   ├── src/main/java/com/server/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── config/
│   └── pom.xml
│
├── PROJECT_DOCUMENTATION.md
├── API_REFERENCE.md
├── DEVELOPER_SETUP.md
└── README.md
```

### Running Tests

**Backend**:
```bash
cd server
mvn test
```

**Frontend**:
```bash
cd client
ng test
```

### Building for Production

**Backend**:
```bash
cd server
mvn clean package
java -jar target/server-0.0.1-SNAPSHOT.jar
```

**Frontend**:
```bash
cd client
ng build --configuration production
```

---

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL is running
   - Check credentials in `application.yml`

2. **Port Already in Use**
   - Backend: Change port in `application.yml`
   - Frontend: Use `ng serve --port 4201`

3. **CORS Errors**
   - Verify CORS configuration in backend
   - Check API URL in frontend environment

See [DEVELOPER_SETUP.md](./DEVELOPER_SETUP.md#troubleshooting) for detailed troubleshooting.

---

## 🚧 Future Enhancements

- [ ] Admin dashboard with analytics
- [ ] Payment integration for consultations
- [ ] Video consultation via WebRTC
- [ ] Push notifications
- [ ] Consultant rating and reviews
- [ ] Advanced search and filtering
- [ ] Multi-language support
- [ ] Mobile applications (iOS/Android)
- [ ] Document preview and management
- [ ] Analytics and reporting dashboard

---

## 📝 License

This project is proprietary and confidential.

---

## 👥 Contributors

Development Team

---

## 📧 Contact

For questions or support, please contact the development team.

---

**Version**: 1.0.0  
**Last Updated**: December 15, 2025

---

## 🙏 Acknowledgments

- Angular Team for the excellent framework
- Spring Boot Team for the robust backend framework
- Tailwind CSS for the utility-first CSS framework
- All open-source contributors

---

Made with ❤️ for farmers and agricultural consultants
