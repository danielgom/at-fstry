import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { TaskService } from '../tasks/task.service';
import { UserPreferencesService } from './user-preferences.service';
import {TaskFilter} from "../../models/task.model"; // Import user preferences

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private notificationsSubject: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([]);
  public notifications$: Observable<string[]> = this.notificationsSubject.asObservable();

  taskFilter: TaskFilter = {
    orderBy: 'due_date',
    orderType: 'ASC',
    pageNumber: 1,
    pageSize: 100,
    status: null
  };

  constructor(
    private taskService: TaskService,
    private userPreferencesService: UserPreferencesService
  ) {}

  // Function to check for due soon and overdue tasks
  checkForNotifications(): void {
    // Check user preferences before generating notifications
    if (!this.userPreferencesService.getNotificationPreference()) {
      return; // Exit if notifications are disabled
    }

    this.taskService.getTasks(this.taskFilter).subscribe({
      next: (response) => {
        const now = new Date().getTime();
        const notifications: string[] = [];
        response.tasks.forEach((task) => {
          const dueDate = new Date(task.due_date).getTime();
          const timeDifference = dueDate - now; // Calculate time difference

          // Correct the comparison for due soon tasks
          if (timeDifference > 0 && timeDifference <= 24 * 60 * 60 * 1000) {
            // Task is due within 24 hours
            notifications.push(`Task "${task.title}" is due within the next 24 hours.`);
          } else if (timeDifference < 0) {
            // Task is overdue
            notifications.push(`Task "${task.title}" is overdue!`);
          }
        });

        // Emit notifications
        this.notificationsSubject.next(notifications);
      },
      error: (error) => {
        console.error('Error fetching tasks:', error);
      }
    });

  }
}
