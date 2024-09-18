import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskCreateDialogComponent } from './task-create-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations'; // For Material components

describe('TaskCreateDialogComponent', () => {
  let component: TaskCreateDialogComponent;
  let fixture: ComponentFixture<TaskCreateDialogComponent>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<TaskCreateDialogComponent>>;

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [
        TaskCreateDialogComponent,
        FormsModule,
        NoopAnimationsModule // Add NoopAnimationsModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: {} } // Provide empty data for now
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskCreateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default task data', () => {
    expect(component.task.title).toBe('');
    expect(component.task.description).toBe('');
    expect(component.task.status).toBe('NONE');
    // Check if due_date is initialized to today's date (YYYY-MM-DDTHH:mm format)
    const today = new Date().toISOString().slice(0, 16); // Adjust the slice to include time
    expect(component.task.due_date).toBe(today);
  });

  it('should close the dialog with task data on submit', () => {
    component.task.title = 'Test Task';
    component.task.description = 'Test Description';
    component.task.status = 'IN_PROGRESS';
    component.task.due_date = '2024-12-31'; // Set a specific date

    component.onSubmit();

    expect(dialogRefSpy.close).toHaveBeenCalledWith({
      title: 'Test Task',
      description: 'Test Description',
      status: 'IN_PROGRESS',
      due_date: '2024-12-31 00:00:00' // Check for the formatted date
    });
  });

  it('should close the dialog without data on cancel', () => {
    component.onCancel();
    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });
});
