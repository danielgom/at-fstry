import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import {TaskApiResponse, TaskService} from '../../tasks/task.service';
import { of, throwError } from 'rxjs';
import { Task, TaskFilter } from '../../../models/task.model';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;

  beforeEach(async () => {
    taskServiceSpy = jasmine.createSpyObj('TaskService', ['getTasks']);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch tasks on init', () => {
    const mockTasks: Task[] = [
      { id: '1', title: 'Task 1', description: 'Description 1', due_date: '2024-03-10', status: 'IN_PROGRESS' },
      { id: '2', title: 'Task 2', description: 'Description 2', due_date: '2024-03-15', status: 'COMPLETED' },
      { id: '3', title: 'Task 3', description: 'Description 3', due_date: '2024-03-12', status: 'IN_PROGRESS' }
    ];
    const mockResponse: TaskApiResponse = {
      tasks: mockTasks,
      total_elements: 3,
      total_pages: 1 // Add the missing total_pages property
    };
    taskServiceSpy.getTasks.and.returnValue(of(mockResponse));

    fixture.detectChanges();// Trigger ngOnInit

    expect(taskServiceSpy.getTasks).toHaveBeenCalledWith(component.taskFilter);
    expect(component.tasks).toEqual(mockTasks);
    expect(component.totalTasks).toBe(3);
  });

  it('should calculate tasks due soon and completed tasks', () => {
    const today = new Date();
    const threeDaysFromNow = new Date();
    threeDaysFromNow.setDate(today.getDate() + 3);
    const dueDateWithinThreeDays = threeDaysFromNow.toISOString().slice(0, 10); // Format as 'YYYY-MM-DD'
    const mockTasks: Task[] = [
      { id: '1', title: 'Task 1', description: 'Description 1', due_date: dueDateWithinThreeDays, status: 'IN_PROGRESS' }, // Due soon
      // ... other mock tasks ...
    ];
    const mockResponse: TaskApiResponse = {
      tasks: mockTasks,
      total_elements: 3,
      total_pages: 1 // Add total_pages here
    };

    taskServiceSpy.getTasks.and.returnValue(of(mockResponse));

    fixture.detectChanges();  // Trigger ngOnInit

    expect(component.tasksDueSoon).toBe(1); // One task due soon
    expect(component.completedTasks).toBe(0); // One completed task
  });

  it('should handle errors when fetching tasks', () => {
    const mockError = new Error('Error fetching tasks');
    taskServiceSpy.getTasks.and.returnValue(throwError(() => mockError));

    fixture.detectChanges(); // Trigger ngOnInit

    // You might want to add assertions to check how the error is handled in the UI
    // For example, check if an error message is displayed
    // expect(component.errorMessage).toBe('Error fetching tasks');
  });
});
