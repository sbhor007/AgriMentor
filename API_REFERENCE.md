# API Reference Guide

## E-Consultancy for Farmers - REST API Documentation

### Base Configuration

**Base URL**: `http://localhost:8080/api`

**Authentication**: Bearer Token (JWT)

**Content-Type**: `application/json`

---

## Authentication APIs

### 1. Register Farmer

**Endpoint**: `POST /auth/register/farmer`

**Description**: Register a new farmer account

**Request Headers**: None (public endpoint)

**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.farmer@example.com",
  "phone": "+1234567890",
  "password": "SecurePass123!",
  "address": {
    "street": "123 Farm Road",
    "city": "Springfield",
    "state": "Illinois",
    "country": "USA",
    "pincode": "62701",
    "latitude": 39.7817,
    "longitude": -89.6501
  }
}
```

**Success Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.farmer@example.com",
    "phone": "+1234567890",
    "role": "FARMER",
    "isActive": true,
    "isVerified": true,
    "createdAt": "2025-12-10T10:30:00"
  }
}
```

**Error Responses**:
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Email or phone already exists

---

### 2. Register Consultant

**Endpoint**: `POST /auth/register/consultant`

**Description**: Register a new consultant account

**Request Headers**: None (public endpoint)

**Request Body**: Same as farmer registration

**Success Response** (200 OK): Same structure as farmer registration with `role: "CONSULTANT"`

---

### 3. Login

**Endpoint**: `POST /auth/login`

**Description**: Authenticate user and receive JWT token

**Request Headers**: None (public endpoint)

**Request Body**:
```json
{
  "email": "john.farmer@example.com",
  "password": "SecurePass123!"
}
```

**Success Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.farmer@example.com",
    "role": "FARMER",
    "profilePicture": {
      "id": 5,
      "imageUrl": "/uploads/profiles/user1.jpg"
    }
  }
}
```

**Error Responses**:
- `401 Unauthorized`: Invalid credentials
- `403 Forbidden`: Account not active or verified

---

### 4. Forgot Password

**Endpoint**: `POST /auth/forgot-password`

**Description**: Request password reset OTP via email

**Request Body**:
```json
{
  "email": "john.farmer@example.com"
}
```

**Success Response** (200 OK):
```json
{
  "message": "OTP sent to your email",
  "otpId": "uuid-string"
}
```

---

### 5. Verify OTP

**Endpoint**: `POST /auth/verify-otp`

**Request Body**:
```json
{
  "email": "john.farmer@example.com",
  "otp": "123456"
}
```

**Success Response** (200 OK):
```json
{
  "message": "OTP verified successfully",
  "resetToken": "temporary-reset-token"
}
```

---

### 6. Reset Password

**Endpoint**: `POST /auth/reset-password`

**Request Body**:
```json
{
  "resetToken": "temporary-reset-token",
  "newPassword": "NewSecurePass123!"
}
```

**Success Response** (200 OK):
```json
{
  "message": "Password reset successfully"
}
```

---

## Farmer APIs

### 1. Get Farmer Dashboard

**Endpoint**: `GET /farmer/dashboard`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
{
  "totalConsultations": 15,
  "pendingRequests": 3,
  "approvedConsultations": 10,
  "completedConsultations": 2,
  "recentConsultations": [
    {
      "id": 1,
      "topic": "Wheat Disease Management",
      "status": "APPROVED",
      "consultant": {
        "id": 5,
        "firstName": "Dr. Sarah",
        "lastName": "Smith"
      },
      "createdAt": "2025-12-08T14:30:00"
    }
  ]
}
```

---

### 2. Get All Consultations

**Endpoint**: `GET /farmer/consultations`

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `status` (optional): Filter by status (PENDING, APPROVED, REJECTED, COMPLETED)
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Success Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "topic": "Wheat Disease Management",
      "description": "My wheat crop is showing yellow spots...",
      "consultationRequestStatus": "APPROVED",
      "crop": {
        "id": 3,
        "name": "Wheat",
        "scientificName": "Triticum aestivum"
      },
      "consultant": {
        "id": 5,
        "firstName": "Dr. Sarah",
        "lastName": "Smith",
        "email": "sarah.consultant@example.com"
      },
      "farmAddress": {
        "city": "Springfield",
        "state": "Illinois"
      },
      "createdAt": "2025-12-08T14:30:00",
      "updatedAt": "2025-12-08T15:00:00"
    }
  ],
  "totalElements": 15,
  "totalPages": 2,
  "currentPage": 0
}
```

---

### 3. Create Consultation Request

**Endpoint**: `POST /farmer/consultation/request`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "topic": "Wheat Disease Management",
  "description": "My wheat crop is showing yellow spots on leaves. Need urgent consultation.",
  "cropId": 3,
  "consultantId": 5,
  "farmAddress": {
    "street": "Farm Plot 45",
    "city": "Springfield",
    "state": "Illinois",
    "country": "USA",
    "pincode": "62701",
    "latitude": 39.7817,
    "longitude": -89.6501
  }
}
```

**Success Response** (201 Created):
```json
{
  "id": 1,
  "topic": "Wheat Disease Management",
  "consultationRequestStatus": "PENDING",
  "message": "Consultation request sent successfully"
}
```

---

### 4. Get Consultation Details

**Endpoint**: `GET /farmer/consultation/{id}`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
{
  "id": 1,
  "topic": "Wheat Disease Management",
  "description": "My wheat crop is showing yellow spots...",
  "consultationRequestStatus": "APPROVED",
  "crop": {
    "id": 3,
    "name": "Wheat"
  },
  "consultant": {
    "id": 5,
    "firstName": "Dr. Sarah",
    "lastName": "Smith",
    "email": "sarah.consultant@example.com",
    "phone": "+1234567891"
  },
  "farmVisits": [
    {
      "id": 1,
      "scheduledDate": "2025-12-15T10:00:00",
      "status": "SCHEDULED",
      "notes": "Will inspect the wheat field"
    }
  ],
  "consultationReports": [
    {
      "id": 1,
      "title": "Initial Assessment Report",
      "content": "The yellow spots indicate fungal infection...",
      "createdAt": "2025-12-09T16:00:00",
      "attachments": []
    }
  ],
  "chatRoom": {
    "id": 1,
    "isActive": true
  },
  "createdAt": "2025-12-08T14:30:00"
}
```

---

## Consultant APIs

### 1. Get Consultant Dashboard

**Endpoint**: `GET /consultant/dashboard`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
{
  "totalRequests": 25,
  "pendingRequests": 5,
  "approvedConsultations": 18,
  "completedConsultations": 12,
  "upcomingFarmVisits": [
    {
      "id": 1,
      "scheduledDate": "2025-12-15T10:00:00",
      "consultation": {
        "id": 1,
        "topic": "Wheat Disease Management",
        "farmer": {
          "firstName": "John",
          "lastName": "Doe"
        }
      }
    }
  ]
}
```

---

### 2. Get Consultation Requests

**Endpoint**: `GET /consultant/consultation-requests`

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `status` (optional): PENDING, APPROVED, REJECTED
- `page`, `size`: Pagination

**Success Response** (200 OK): Similar structure to farmer consultations

---

### 3. Approve Consultation

**Endpoint**: `PUT /consultant/consultation/{id}/approve`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
{
  "id": 1,
  "consultationRequestStatus": "APPROVED",
  "message": "Consultation approved successfully",
  "chatRoom": {
    "id": 1,
    "isActive": true
  }
}
```

> **Note**: Approving a consultation automatically creates a chat room for communication.

---

### 4. Reject Consultation

**Endpoint**: `PUT /consultant/consultation/{id}/reject`

**Headers**: `Authorization: Bearer <token>`

**Request Body** (optional):
```json
{
  "reason": "Currently unavailable for this crop type"
}
```

**Success Response** (200 OK):
```json
{
  "id": 1,
  "consultationRequestStatus": "REJECTED",
  "message": "Consultation rejected"
}
```

---

### 5. Schedule Farm Visit

**Endpoint**: `POST /consultant/farm-visit/schedule`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "consultationId": 1,
  "scheduledDate": "2025-12-15T10:00:00",
  "notes": "Will inspect the wheat field and collect samples"
}
```

**Success Response** (201 Created):
```json
{
  "id": 1,
  "scheduledDate": "2025-12-15T10:00:00",
  "status": "SCHEDULED",
  "notes": "Will inspect the wheat field and collect samples",
  "consultation": {
    "id": 1,
    "topic": "Wheat Disease Management"
  }
}
```

---

### 6. Create Consultation Report

**Endpoint**: `POST /consultant/consultation/{id}/report`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "title": "Initial Assessment Report",
  "content": "The yellow spots on wheat leaves indicate a fungal infection, specifically leaf rust. Recommended treatment includes...",
  "attachments": [
    {
      "fileName": "field_photo.jpg",
      "fileUrl": "/uploads/reports/field_photo.jpg"
    }
  ]
}
```

**Success Response** (201 Created):
```json
{
  "id": 1,
  "title": "Initial Assessment Report",
  "content": "The yellow spots...",
  "createdAt": "2025-12-09T16:00:00",
  "attachments": [...]
}
```

---

## Chat APIs

### 1. Get All Chat Rooms

**Endpoint**: `GET /chat/rooms`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "farmer": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "profilePicture": {
        "imageUrl": "/uploads/profiles/user1.jpg"
      }
    },
    "consultant": {
      "id": 5,
      "firstName": "Dr. Sarah",
      "lastName": "Smith",
      "profilePicture": {
        "imageUrl": "/uploads/profiles/user5.jpg"
      }
    },
    "consultation": {
      "id": 1,
      "topic": "Wheat Disease Management"
    },
    "lastMessageAt": "2025-12-10T15:30:00",
    "isActive": true,
    "unreadCount": 3
  }
]
```

---

### 2. Get Chat Room Messages

**Endpoint**: `GET /chat/room/{roomId}/messages`

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 50)
- `before` (optional): Get messages before this timestamp

**Success Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "content": "Hello, I've reviewed your case",
      "messageType": "TEXT",
      "status": "READ",
      "sender": {
        "id": 5,
        "firstName": "Dr. Sarah",
        "lastName": "Smith"
      },
      "receiver": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe"
      },
      "sentAt": "2025-12-10T14:00:00",
      "deliveredAt": "2025-12-10T14:00:05",
      "readAt": "2025-12-10T14:05:00",
      "isEdited": false,
      "isDeleted": false
    }
  ],
  "totalElements": 45,
  "currentPage": 0
}
```

---

### 3. Send Message (REST)

**Endpoint**: `POST /chat/room/{roomId}/message`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "content": "Thank you for the advice!",
  "messageType": "TEXT",
  "attachmentUrl": null
}
```

**Success Response** (201 Created):
```json
{
  "id": 2,
  "content": "Thank you for the advice!",
  "messageType": "TEXT",
  "status": "SENT",
  "sentAt": "2025-12-10T15:30:00"
}
```

---

### 4. Mark Message as Read

**Endpoint**: `PUT /chat/message/{messageId}/read`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
{
  "id": 1,
  "status": "READ",
  "readAt": "2025-12-10T15:35:00"
}
```

---

### 5. Mark All Messages as Read

**Endpoint**: `PUT /chat/room/{roomId}/read-all`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
{
  "message": "All messages marked as read",
  "updatedCount": 5
}
```

---

## WebSocket API

### Connection Setup

**Endpoint**: `ws://localhost:8080/ws`

**Protocol**: STOMP over SockJS

**JavaScript Example**:
```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
  connectHeaders: {
    Authorization: `Bearer ${token}`
  },
  onConnect: () => {
    console.log('Connected to WebSocket');
  }
});

client.activate();
```

---

### Subscribe to Chat Room

**Destination**: `/topic/chat/{roomId}`

**Example**:
```javascript
client.subscribe(`/topic/chat/${roomId}`, (message) => {
  const chatMessage = JSON.parse(message.body);
  console.log('New message:', chatMessage);
});
```

**Message Format**:
```json
{
  "id": 3,
  "content": "New message content",
  "messageType": "TEXT",
  "sender": {
    "id": 5,
    "firstName": "Dr. Sarah",
    "lastName": "Smith"
  },
  "receiver": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  },
  "sentAt": "2025-12-10T16:00:00",
  "status": "SENT"
}
```

---

### Send Message via WebSocket

**Destination**: `/app/chat.send`

**Example**:
```javascript
client.publish({
  destination: '/app/chat.send',
  body: JSON.stringify({
    chatRoomId: 1,
    content: 'Hello via WebSocket!',
    messageType: 'TEXT'
  })
});
```

---

### Subscribe to Message Status Updates

**Destination**: `/topic/chat/{roomId}/status`

**Message Format**:
```json
{
  "messageId": 3,
  "status": "READ",
  "readAt": "2025-12-10T16:05:00"
}
```

---

## User Profile APIs

### 1. Get Current User Profile

**Endpoint**: `GET /user/profile`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.farmer@example.com",
  "phone": "+1234567890",
  "role": "FARMER",
  "address": {
    "street": "123 Farm Road",
    "city": "Springfield",
    "state": "Illinois",
    "country": "USA",
    "pincode": "62701"
  },
  "profilePicture": {
    "id": 5,
    "imageUrl": "/uploads/profiles/user1.jpg"
  },
  "isActive": true,
  "isVerified": true,
  "createdAt": "2025-11-01T10:00:00"
}
```

---

### 2. Update Profile

**Endpoint**: `PUT /user/profile`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "address": {
    "street": "123 Farm Road",
    "city": "Springfield",
    "state": "Illinois",
    "country": "USA",
    "pincode": "62701"
  }
}
```

**Success Response** (200 OK): Returns updated user object

---

### 3. Upload Profile Picture

**Endpoint**: `POST /user/profile-picture`

**Headers**: 
- `Authorization: Bearer <token>`
- `Content-Type: multipart/form-data`

**Request Body**: Form data with `file` field

**Success Response** (200 OK):
```json
{
  "id": 5,
  "imageUrl": "/uploads/profiles/user1.jpg",
  "message": "Profile picture uploaded successfully"
}
```

---

## Crop APIs

### 1. Get All Crops

**Endpoint**: `GET /crops`

**Headers**: `Authorization: Bearer <token>`

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "Rice",
    "scientificName": "Oryza sativa",
    "category": "Cereal"
  },
  {
    "id": 2,
    "name": "Wheat",
    "scientificName": "Triticum aestivum",
    "category": "Cereal"
  }
]
```

---

## Error Handling

### Standard Error Response Format

```json
{
  "timestamp": "2025-12-10T16:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'email'",
  "path": "/api/auth/register/farmer"
}
```

### HTTP Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT requests |
| 201 | Created | Successful POST requests |
| 400 | Bad Request | Invalid input data |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource (email, phone) |
| 500 | Internal Server Error | Server-side error |

---

## Rate Limiting

Currently not implemented. Recommended limits:
- Authentication endpoints: 5 requests/minute
- General API: 100 requests/minute
- WebSocket messages: 50 messages/minute

---

## Pagination

Paginated endpoints support these query parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (0-indexed) |
| `size` | integer | 10 | Items per page |
| `sort` | string | - | Sort field and direction (e.g., `createdAt,desc`) |

**Example**: `GET /farmer/consultations?page=0&size=20&sort=createdAt,desc`

---

## Testing the API

### Using cURL

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.farmer@example.com","password":"SecurePass123!"}'

# Get consultations (replace TOKEN with actual JWT)
curl -X GET http://localhost:8080/api/farmer/consultations \
  -H "Authorization: Bearer TOKEN"
```

### Using Postman

1. Import the API endpoints
2. Set up environment variable for `baseUrl` and `token`
3. Use Bearer Token authentication
4. Test WebSocket using Postman's WebSocket feature

---

**Last Updated**: December 10, 2025
