import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Task, TaskFilter} from '../../../models/task.model';
import {TaskService} from "../task.service";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faPen, faTrash} from '@fortawesome/free-solid-svg-icons';
import {TaskEditDialogComponent} from "../task-edit-dialog/task-edit-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {TaskDeleteDialogComponent} from "../task-delete-dialog/task-delete-dialog.component";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {TaskCreateDialogComponent} from "../task-create-dialog/task-create-dialog.component";
import {MatPaginator} from "@angular/material/paginator";
import {MatFormField} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {Subscription} from "rxjs";

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    NgClass,
    FaIconComponent,
    MatIcon,
    MatIconButton,
    MatPaginator,
    MatFormField,
    MatSelect,
    MatOption,
    MatFormFieldModule,
    MatSelectModule
  ],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TaskListComponent implements OnInit {
  faPen = faPen; // Correct import
  faTrash = faTrash; // Trashcan icon for delete
  tasks: Task[] = [];
  loading: boolean = false; // Add a loading indicator
  errorMessage: string | null = null; // For error display
  filteredTasks: Task[] = [];
  selectedStatus: string = '';
  sortOrder: 'ASC' | 'DESC' = 'DESC'; // Default sort order

  private dialogSubscription: Subscription | undefined;

  taskFilter: TaskFilter = {
    orderBy: 'due_date',
    orderType: 'ASC',
    pageNumber: 1,
    pageSize: 5,
    status: null
  };

  pageSizeOptions = [5, 10, 20];
  totalPages = 0;
  totalElements = 0;

  constructor(private taskService: TaskService, public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.fetchTasks();
  }

  ngOnDestroy() {
    if (this.dialogSubscription) {
      this.dialogSubscription.unsubscribe(); // Unsubscribe in ngOnDestroy
    }
  }

  fetchTasks(): void {
    this.loading = true;
    this.errorMessage = null; // Clear any previous errors
    this.taskService.getTasks(this.taskFilter).subscribe({
      next: (response) => {
        this.tasks = response.tasks;
        this.totalPages = response.total_pages;
        this.totalElements = response.total_elements; // Update totalElements
        this.applyFilter();
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Error fetching tasks. Please try again later.';
        console.error('Error fetching tasks:', error);
      }
    });
  }

  onPageChange(event: any): void {
    this.taskFilter.pageNumber = event.pageIndex + 1; // Adjust for API (starts at 1)
    this.fetchTasks();
  }

  createTask() {
    const dialogRef = this.dialog.open(TaskCreateDialogComponent, { // Use create dialog
      width: '600px'
    });

    this.dialogSubscription = dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.taskService.createTask(result).subscribe({
          next: (response) => {
            console.log('Task created:', response.task);
            this.tasks.push(response.task);
            this.applyFilter();
          },
          error: (error) => {
            console.error('Error creating task:', error);
          }
        });
      } else {
        console.error('TaskService is undefined in createTask');
      }
    });
  }

  // Method to handle the taskUpdated event
  onTaskUpdated(updatedTask: Task): void {
    const index = this.tasks.findIndex(t => t.id === updatedTask.id);
    if (index !== -1) {
      this.tasks[index] = updatedTask;
    }
  }

  onStartEditing(task: Task) {
    const dialogRef = this.dialog.open(TaskEditDialogComponent, {
      width: '600px',
      data: {task: {...task}}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.onTaskUpdated(result);
      }
    });
  }

  deleteTask(task: Task) {
    const dialogRef = this.dialog.open(TaskDeleteDialogComponent, {
      data: {
        title: 'Confirm Delete',
        message: `Do you really want to remove this task: ${task.title}?`
      }
    });

    this.dialogSubscription = dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.taskService.deleteTask(task.id!).subscribe({
          next: () => {
            console.log('Task deleted successfully!');
            this.tasks = this.tasks.filter(t => t.id !== task.id);
            this.applyFilter();
          },
          error: (error) => {
            console.error('Error deleting task:', error);
            // Handle error, e.g., show error message to the user
          }
        });
      } else {
        console.error('TaskService is undefined in deleteTask');
      }
    });
  }

  onStatusFilterChange(event: any) {
    this.selectedStatus = event.target.value;
    this.fetchTasks(); // Re-fetch tasks when the filter changes
  }

  applyFilter() {
    if (this.selectedStatus) {
      this.filteredTasks = this.tasks.filter(task => task.status === this.selectedStatus);
    } else {
      this.filteredTasks = this.tasks;
    }
  }

  toggleSortOrder() {
    this.sortOrder = this.sortOrder === 'ASC' ? 'DESC' : 'ASC';
    this.taskFilter.orderType = this.sortOrder === 'ASC' ? 'DESC' : 'ASC';
    this.taskFilter.pageNumber = 1;
    this.fetchTasks();
  }
}
