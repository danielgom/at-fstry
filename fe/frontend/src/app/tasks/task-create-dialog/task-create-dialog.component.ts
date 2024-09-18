import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {Task} from '../../../models/task.model';
import {MatFormField, MatFormFieldModule} from "@angular/material/form-field";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {MatButton} from "@angular/material/button";
import {FormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";

@Component({
  selector: 'app-task-create-dialog',
  templateUrl: './task-create-dialog.component.html',
  standalone: true,
  imports: [
    MatDialogContent,
    MatFormField,
    MatDatepickerToggle,
    MatOption,
    MatDatepicker,
    MatSelect,
    MatButton,
    FormsModule,
    MatDialogTitle,
    MatInputModule,
    MatDatepickerInput,
    MatFormFieldModule,
    MatDialogActions,
    MatDialogClose
  ],
  styleUrls: ['./task-create-dialog.component.css']
})

export class TaskCreateDialogComponent {
  task: Task = {
    title: '',
    description: '',
    status: 'NONE',
    due_date: new Date().toISOString().slice(0, 16)
  };

  constructor(
    public dialogRef: MatDialogRef<TaskCreateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
  }

  onSubmit() {
    this.task.due_date = new Date(this.task.due_date).toISOString().slice(0, 19).replace('T', ' ');
    this.dialogRef.close(this.task);
  }

  onCancel() {
    this.dialogRef.close(); // This will close the dialog without saving
  }
}
