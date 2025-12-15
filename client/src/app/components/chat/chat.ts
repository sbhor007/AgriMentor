import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  AfterViewChecked,
  Inject,
  PLATFORM_ID,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatRoom, ChatMessage } from '../../services/chat.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',
  styleUrls: ['./chat.css'],
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('messageContainer') private messageContainer!: ElementRef;

  chatRooms: ChatRoom[] = [];
  selectedRoom: ChatRoom | null = null;
  messages: ChatMessage[] = [];
  newMessage = '';
  loading = false;

  private refreshInterval: any;
  private isBrowser: boolean;

  constructor(
    private chatService: ChatService,
    @Inject(PLATFORM_ID) platformId: Object,
    private cdr: ChangeDetectorRef
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  // Subscription holder
  private roomSubscription: any;

  ngOnInit(): void {
    if (this.isBrowser) {
      this.loadRooms();
    }
  }

  ngOnDestroy(): void {
    if (this.roomSubscription) {
      this.roomSubscription.unsubscribe();
    }
  }

  ngAfterViewChecked(): void {}

  loadRooms() {
    if (!this.isBrowser) return;

    const role = sessionStorage.getItem('role') || 'FARMER';
    this.chatService.getChatRooms(role).subscribe({
      next: (rooms) => {
        const uniqueParticipants = new Map<number, ChatRoom>();

        rooms.forEach((room) => {
          const other = this.getOtherParticipant(room);
          if (!other || !other.id) return;
          const otherId = other.id;

          if (!uniqueParticipants.has(otherId)) {
            uniqueParticipants.set(otherId, room);
          } else {
            const existingRoom = uniqueParticipants.get(otherId)!;
            const existingTime = existingRoom.lastMessageAt || existingRoom.createdAt || '';
            const newTime = room.lastMessageAt || room.createdAt || '';

            if (newTime > existingTime) {
              uniqueParticipants.set(otherId, room);
            }
          }
        });

        this.chatRooms = Array.from(uniqueParticipants.values());
        this.sortRooms();
        this.cdr.detectChanges();
      },
      error: (e) => console.error('Error loading rooms', e),
    });
  }

  loadMessages() {
    if (!this.selectedRoom) return;

    this.chatService.getMessages(this.selectedRoom.id).subscribe({
      next: (page) => {
        this.messages = page.content.reverse();
        this.cdr.detectChanges();

        if (this.selectedRoom) {
          this.selectedRoom.unreadCount = 0;
        }

        this.chatService.markAsRead(this.selectedRoom!.id).subscribe();
      },
    });
  }

  selectRoom(room: ChatRoom) {
    if (this.selectedRoom?.id === room.id) return;

    // Unsubscribe previous
    if (this.roomSubscription) {
      this.roomSubscription.unsubscribe();
      this.roomSubscription = null;
    }

    this.selectedRoom = room;
    this.loadMessages(); // Initial fetch via REST

    // Subscribe to WebSocket
    this.roomSubscription = this.chatService.subscribeToRoom(room.id, (msg: ChatMessage) => {
      // Create new array reference for Angular change detection if needed, or just push
      this.messages = [...this.messages, msg];

      // Update last message time for sorting
      if (this.selectedRoom) {
        this.selectedRoom.lastMessageAt = msg.sentAt || new Date().toISOString();
        this.sortRooms();

        // If I am not the sender, mark as read (optimistic)
        if (!this.isSentByMe(msg)) {
          this.selectedRoom.unreadCount = 0; // Reset unread locally
          // Ideally call markAsRead backend too.
          this.chatService.markAsRead(this.selectedRoom.id).subscribe();
        }
      }

      this.cdr.detectChanges();
      setTimeout(() => this.scrollToBottom(), 50);
    });

    setTimeout(() => this.scrollToBottom(), 100);
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.selectedRoom) return;

    const content = this.newMessage;
    this.newMessage = ''; // Clear input immediately

    // Send via WebSocket
    const email = sessionStorage.getItem('email') || '';
    this.chatService.sendWebSocketMessage(this.selectedRoom.id, content, email);

    // We do NOT push to this.messages here.
    // We rely on the WebSocket echo (subscription) to add the message.
    // This proves 2-way communication works.
    setTimeout(() => this.scrollToBottom(), 50);
  }

  scrollToBottom(): void {
    try {
      this.messageContainer.nativeElement.scrollTop =
        this.messageContainer.nativeElement.scrollHeight;
    } catch (err) {}
  }

  // Helpers
  getOtherParticipant(room: ChatRoom): any {
    if (!this.isBrowser) return {};
    const myEmail = sessionStorage.getItem('email');
    if (room.farmer.email === myEmail) {
      return room.consultant;
    }
    return room.farmer;
  }

  isSentByMe(msg: ChatMessage): boolean {
    if (!this.isBrowser) return false;
    const myEmail = sessionStorage.getItem('email');
    return msg.sender.email === myEmail;
  }

  formatTime(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  trackByRoomId(index: number, room: ChatRoom): number {
    return room.id;
  }

  sortRooms() {
    this.chatRooms.sort((a, b) => {
      const timeA = new Date(a.lastMessageAt || a.createdAt || 0).getTime();
      const timeB = new Date(b.lastMessageAt || b.createdAt || 0).getTime();
      return timeB - timeA;
    });
  }
}
