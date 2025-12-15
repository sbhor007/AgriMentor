import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-card',
  imports: [CommonModule],
  templateUrl: './user-card.html',
  styleUrl: './user-card.css',
})
export class UserCard {
  @Input() user: any;
  @Input() cardType: 'consultant' | 'farmer' = 'consultant';
  @Output() onAction = new EventEmitter<{ action: string; user: any }>();

  getInitials(firstName: string, lastName: string): string {
    return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`;
  }

  handleAction(action: string): void {
    this.onAction.emit({ action, user: this.user });
  }
}
