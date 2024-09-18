import { Component, OnInit } from '@angular/core';
import {TaskService} from "../../tasks/task.service";
import {Task, TaskFilter} from '../../../models/task.model';
import {MatCard, MatCardContent, MatCardTitle} from "@angular/material/card";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatCardTitle,
    MatCardContent,
    MatCard
  ], // Add any necessary Angular Material imports here
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  totalTasks: number = 0;
  tasksDueSoon: number = 0;
  completedTasks: number = 0;
  tasks: Task[] = []; // Assuming you have a Task model
  errorMessage: string | null = null;

  taskFilter: TaskFilter = {
    orderBy: 'due_date',
    orderType: 'ASC',
    pageNumber: 1,
    pageSize: 100,
    status: null
  };

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.getTasks();
  }

  getTasks(): void {
    this.taskService.getTasks(this.taskFilter).subscribe({
      next: (response) => {
        this.tasks = response.tasks;
        this.totalTasks = response.total_elements;
        this.tasksDueSoon = this.tasks.filter(task => this.isTaskDueSoon(task)).length;
        this.completedTasks = this.tasks.filter(task => task.status === 'COMPLETED').length;
      },
      error: (error: any) => {
        console.error('Error fetching tasks:', error);
        this.errorMessage = error.message; // Set the error message
      }
    });
  }

  isTaskDueSoon(task: Task): boolean {
    const today = new Date();
    const dueDate = new Date(task.due_date);
    const threeDaysFromNow = new Date();
    threeDaysFromNow.setDate(today.getDate() + 3);
    // Make sure dueDate is in the future and within 3 days
    return dueDate > today && dueDate <= threeDaysFromNow;
  };
}
