import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, switchMap, catchError, throwError } from 'rxjs';
import { Task, TaskFilter } from '../../models/task.model';
import { AuthService } from '../auth/auth.service';
import {environment} from "../../environments/environment"; // Import AuthService to use the CSRF initialization logic

export interface TaskApiResponse {
  tasks: Task[];
  total_elements: number;
  total_pages: number;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Ensure that the CSRF token is initialized before any request
  private ensureCsrfInitialized<T>(request: Observable<T>): Observable<T> {
    return this.authService['ensureCsrfInitialized'](request);
  }

  getTasks(taskFilter: TaskFilter): Observable<TaskApiResponse> {
    let params = new HttpParams({
      fromObject: {
        pageNumber: taskFilter.pageNumber.toString(),
        pageSize: taskFilter.pageSize.toString(),
        orderBy: taskFilter.orderBy,
        orderType: taskFilter.orderType,
      }
    });

    if (taskFilter.status) {
      params = params.set('status', taskFilter.status);
    }

    // Prepare the request with CSRF token handling
    const request = this.http.get<TaskApiResponse>(`${this.apiUrl}`, {
      headers: this.getAuthHeaders(),
      params,
      withCredentials: true // Ensure cookies are included in the request
    });

    return this.ensureCsrfInitialized(request);
  }

  getTaskById(id: string): Observable<any> {
    const request = this.http.get(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders(),
      withCredentials: true // Include credentials
    });

    return this.ensureCsrfInitialized(request);
  }

  createTask(taskData: any): Observable<{ task: Task }> {
    const request = this.http.post<{ task: Task }>(`${this.apiUrl}`, taskData, {
      headers: this.getAuthHeaders(),
      withCredentials: true // Include credentials
    });

    return this.ensureCsrfInitialized(request);
  }

  updateTask(id: string, taskData: any): Observable<{ task: Task }> {
    const request = this.http.put<{ task: Task }>(`${this.apiUrl}/${id}`, taskData, {
      headers: this.getAuthHeaders(),
      withCredentials: true // Include credentials
    });

    return this.ensureCsrfInitialized(request);
  }

  deleteTask(id: string): Observable<any> {
    const request = this.http.delete(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders(),
      withCredentials: true // Include credentials
    });

    return this.ensureCsrfInitialized(request);
  }

  // Helper method to get Authorization headers
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
