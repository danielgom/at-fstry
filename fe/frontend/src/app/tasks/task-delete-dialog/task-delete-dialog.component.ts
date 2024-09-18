import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-task-delete-dialog',
  standalone: true,
  imports: [
    MatDialogContent,
    MatDialogClose,
    MatDialogActions,
    MatDialogTitle,
    MatButton
  ],
  templateUrl: './task-delete-dialog.component.html',
  styleUrl: './task-delete-dialog.component.css'
})
export class TaskDeleteDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { title: string, message: string }) {}
}
