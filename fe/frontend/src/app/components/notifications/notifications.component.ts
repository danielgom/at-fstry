import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../services/notification.service';
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css'],
  imports: [
    NgForOf,
    NgIf
  ],
  standalone: true
})
export class NotificationComponent implements OnInit {
  notifications: string[] = [];


  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    // Subscribe to notifications from the service
    this.notificationService.notifications$.subscribe((notifications) => {
      this.notifications = notifications;
    });

    // Optionally, check for notifications periodically
    setInterval(() => this.notificationService.checkForNotifications(), 60 * 60 * 1000); // Check every hour
  }
}
