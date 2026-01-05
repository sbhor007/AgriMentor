import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface ChatRoom {
  id: number;
  consultation: any;
  farmer: any;
  consultant: any;
  isActive: boolean;
  lastMessageAt: string;
  createdAt: string;
  unreadCount?: number;
}

export interface ChatMessage {
  id?: number;
  chatRoomId: number;
  sender: any;
  receiver: any;
  content: string;
  status?: string;
  sentAt?: string;
  deliveredAt?: string;
  readAt?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';

  private stompClient: Client | undefined;

  constructor(private http: HttpClient) {
    this.initializeWebSocketConnection();
  }

  initializeWebSocketConnection() {
    // Only init if in browser
    if (typeof window !== 'undefined') {
      const socket = new SockJS('http://localhost:8080/ws');
      this.stompClient = new Client({
        webSocketFactory: () => socket,
        debug: (str) => {
          console.log(str);
        },
        reconnectDelay: 5000,
        onConnect: (frame) => {
          console.log('Connected to WebSocket');
        },
      });

      this.stompClient.activate();
    }
  }

  getChatRooms(role: string): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>(`${this.apiUrl}/rooms?role=${role}`);
  }

  getMessages(roomId: number, page: number = 0, size: number = 50): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/room/${roomId}/messages?page=${page}&size=${size}`);
  }

  sendMessage(roomId: number, content: string): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(`${this.apiUrl}/room/${roomId}/send`, { content });
  }

  markAsRead(roomId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/room/${roomId}/read`, {});
  }

  getUnreadCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/unread-count`);
  }

  syncChatRooms(): Observable<{ created: number }> {
    return this.http.post<{ created: number }>(`${this.apiUrl}/sync`, {});
  }

  // WebSocket Methods
  sendWebSocketMessage(roomId: number, content: string, email: string) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: `/app/chat/${roomId}/send`,
        body: JSON.stringify({ content, email }),
      });
    } else {
      console.error('STOMP Client not connected. Message not sent.');
    }
  }

  subscribeToRoom(roomId: number, callback: (message: ChatMessage) => void): any {
    if (this.stompClient && this.stompClient.connected) {
      return this.stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
        if (message.body) {
          callback(JSON.parse(message.body));
        }
      });
    }
    // If not connected yet, retry in 1s (simple logic for demo)
    setTimeout(() => this.subscribeToRoom(roomId, callback), 1000);
    return null;
  }
}
