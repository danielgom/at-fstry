import {Component, Inject, ViewEncapsulation} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Task} from '../../../models/task.model'; // Adjust the path if needed
import {TaskService} from '../task.service';
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-task-edit-dialog',
  templateUrl: './task-edit-dialog.component.html',
  standalone: true,
  imports: [
    FormsModule
  ],
  styleUrls: ['./task-edit-dialog.component.css'], // Add styling if needed
  encapsulation: ViewEncapsulation.None
})
export class TaskEditDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<TaskEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { task: Task },
    private taskService: TaskService
  ) {
  }

  onUpdateTask(): void {
      if (this.data.task && this.data.task.id) {
        this.taskService.updateTask(this.data.task.id, this.data.task)
          .subscribe({
            next: (updatedTask) => {
              console.log('Task updated:', updatedTask);
              this.dialogRef.close(updatedTask);
            },
            error: (error) => {
              console.error('Error updating task:', error);
              // Handle the error appropriately, e.g., show an error message
            }
          });
      } else {
        console.error('Cannot update task: Task ID is missing.');
      }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
