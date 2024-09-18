import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskEditDialogComponent } from './task-edit-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TaskService } from '../task.service';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Task } from '../../../models/task.model';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('TaskEditDialogComponent', () => {
  let component: TaskEditDialogComponent;
  let fixture: ComponentFixture<TaskEditDialogComponent>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<TaskEditDialogComponent>>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;

  const mockTask: Task = {
    id: '1',
    title: 'Test Task',
    description: 'Test Description',
    due_date: '2024-12-31',
    status: 'IN_PROGRESS'
  };

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
    taskServiceSpy = jasmine.createSpyObj('TaskService', ['updateTask']);

    await TestBed.configureTestingModule({
      imports: [
        TaskEditDialogComponent,
        FormsModule,
        NoopAnimationsModule // Add NoopAnimationsModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: MAT_DIALOG_DATA, useValue: { task: { ...mockTask } } }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle errors when updating the task', () => {
    const error = new Error('Update failed');
    taskServiceSpy.updateTask.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.onUpdateTask();

    expect(taskServiceSpy.updateTask).toHaveBeenCalledWith('1', mockTask);
    expect(console.error).toHaveBeenCalledWith('Error updating task:', error);
    // You might also want to check if an error message is displayed to the user
  });

  it('should close the dialog on cancel', () => {
    component.onCancel();
    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });
});
