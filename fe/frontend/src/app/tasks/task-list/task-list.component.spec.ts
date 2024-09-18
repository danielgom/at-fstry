import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { HttpClientTestingModule } from '@angular/common/http/testing'; // Import HttpClientTestingModule
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TaskService } from '../task.service'; // Import TaskService
import { MatDialogModule } from '@angular/material/dialog'; // Import MatDialogModule

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule, // Add HttpClientTestingModule here
        NoopAnimationsModule,
        MatDialogModule, // Add MatDialogModule here to handle MatDialog
        TaskListComponent,
      ],
      providers: [TaskService] // Provide TaskService if not using standalone
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
