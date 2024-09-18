import { TestBed } from '@angular/core/testing';
import { NotificationService } from './notification.service';
import { TaskService } from '../tasks/task.service';
import { UserPreferencesService } from './user-preferences.service';
import { of, throwError } from 'rxjs';
import { Task } from '../../models/task.model';

describe('NotificationService', () => {
  let service: NotificationService;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;
  let userPreferencesServiceSpy: jasmine.SpyObj<UserPreferencesService>;

  beforeEach(() => {
    taskServiceSpy = jasmine.createSpyObj('TaskService', ['getTasks']);
    userPreferencesServiceSpy = jasmine.createSpyObj('UserPreferencesService', ['getNotificationPreference']);

    TestBed.configureTestingModule({
      providers: [
        NotificationService,
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: UserPreferencesService, useValue: userPreferencesServiceSpy }
      ]
    });

    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not check for notifications if preference is disabled', () => {
    userPreferencesServiceSpy.getNotificationPreference.and.returnValue(false);
    service.checkForNotifications();
    expect(taskServiceSpy.getTasks).not.toHaveBeenCalled();
  });

  it('should check for notifications if preference is enabled', () => {
    userPreferencesServiceSpy.getNotificationPreference.and.returnValue(true);
    const mockTasks: Task[] = [
      // ... (add mock tasks with due dates as needed)
    ];
    const mockResponse = { tasks: mockTasks, total_elements: mockTasks.length, total_pages: 1 };
    taskServiceSpy.getTasks.and.returnValue(of(mockResponse));

    service.checkForNotifications();

    expect(taskServiceSpy.getTasks).toHaveBeenCalled();
  });

  it('should emit due soon notifications 2', () => {
    userPreferencesServiceSpy.getNotificationPreference.and.returnValue(true);

    // Calculate dueTomorrow to be exactly within 24 hours
    const now = new Date();
    const dueTomorrow = new Date(now);
    dueTomorrow.setDate(now.getDate() + 1);
    dueTomorrow.setHours(now.getHours(), now.getMinutes(), now.getSeconds() + 1);

    const mockTasks: Task[] = [
      {
        id: '1',
        title: 'Task 1',
        description: '...',
        due_date: dueTomorrow.toISOString().slice(0, 10),
        status: 'IN_PROGRESS'
      }
    ];
    const mockResponse = { tasks: mockTasks, total_elements: mockTasks.length, total_pages: 1 };

    // Make sure getTasks returns immediately
    taskServiceSpy.getTasks.and.returnValue(of(mockResponse));

    let emittedNotifications: string[] = [];
    service.notifications$.subscribe(notifications => {
      emittedNotifications = notifications; // Capture emitted notifications
    });

    service.checkForNotifications();

    expect(emittedNotifications).toEqual([
      'Task "Task 1" is due within the next 24 hours.'
    ]);
  });

  it('should handle errors when fetching tasks', () => {
    userPreferencesServiceSpy.getNotificationPreference.and.returnValue(true);
    const mockError = new Error('Error fetching tasks');
    taskServiceSpy.getTasks.and.returnValue(throwError(() => mockError));

    spyOn(console, 'error'); // Spy on console.error

    service.checkForNotifications();

    expect(console.error).toHaveBeenCalledWith('Error fetching tasks:', mockError);
  });
});
