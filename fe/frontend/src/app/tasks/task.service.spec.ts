import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { AuthService } from '../auth/auth.service';
import { TaskFilter } from '../../models/task.model';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService, AuthService]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure no unmatched requests
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getTasks', () => {
    it('should retrieve tasks from the API', () => {
      const mockTaskFilter: TaskFilter = {
        pageNumber: 1,
        pageSize: 5,
        orderBy: 'due_date',
        orderType: 'ASC',
        status: 'IN_PROGRESS'
      };
      const mockResponse = {
        tasks: [
          { id: '1', title: 'Task 1', description: 'Description 1', due_date: '2024-03-10', status: 'IN_PROGRESS' },
          { id: '2', title: 'Task 2', description: 'Description 2', due_date: '2024-03-15', status: 'COMPLETED' }
        ],
        total_elements: 2,
        total_pages: 1
      };

      // Trigger CSRF token request and mock it
      service.getTasks(mockTaskFilter).subscribe(response => {
        expect(response.tasks.length).toBe(2);
        expect(response).toEqual(mockResponse);
      });

      // Mock the CSRF request
      const csrfReq = httpMock.expectOne('http://localhost:8080/api/auth/csrf');
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Respond with empty data to simulate a successful CSRF request

      // Mock the actual tasks request
      const req = httpMock.expectOne(request => {
        return request.url === 'http://localhost:8080/api/tasks' &&
          request.params.get('status') === 'IN_PROGRESS';
      });
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('getTaskById', () => {
    it('should retrieve a single task by ID', () => {
      const taskId = '1';
      const mockTask = { id: '1', title: 'Task 1', completed: false };

      service.getTaskById(taskId).subscribe(task => {
        expect(task).toEqual(mockTask);
      });

      // Mock the CSRF request
      const csrfReq = httpMock.expectOne('http://localhost:8080/api/auth/csrf');
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Respond with empty data to simulate a successful CSRF request

      // Mock the actual task retrieval request
      const req = httpMock.expectOne(`http://localhost:8080/api/tasks/${taskId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTask);
    });
  });

  describe('createTask', () => {
    it('should create a new task', () => {
      const newTask = { title: 'New Task', completed: false, status: 'IN_PROGRESS', description: "", due_date: "2024-09-11 17:50:20" };
      const mockCreatedTask = { id: '3', ...newTask };

      service.createTask(newTask).subscribe(response => {
        expect(response.task).toEqual(mockCreatedTask);
      });

      // Mock the CSRF request
      const csrfReq = httpMock.expectOne('http://localhost:8080/api/auth/csrf');
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Respond with empty data to simulate a successful CSRF request

      // Mock the actual task creation request
      const req = httpMock.expectOne('http://localhost:8080/api/tasks');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newTask);
      req.flush({ task: mockCreatedTask });
    });
  });

  describe('updateTask', () => {
    it('should update an existing task', () => {
      const taskId = '1';
      const updatedTask = { id: '1', title: 'Updated Task', completed: true, status: 'IN_PROGRESS', description: "", due_date: "2024-09-11 17:50:20" };

      service.updateTask(taskId, updatedTask).subscribe(response => {
        expect(response.task).toEqual(updatedTask);
      });

      // Mock the CSRF request
      const csrfReq = httpMock.expectOne('http://localhost:8080/api/auth/csrf');
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Respond with empty data to simulate a successful CSRF request

      // Mock the actual task update request
      const req = httpMock.expectOne(`http://localhost:8080/api/tasks/${taskId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedTask);
      req.flush({ task: updatedTask });
    });
  });

  describe('deleteTask', () => {
    it('should delete a task by ID', () => {
      const taskId = '1';

      service.deleteTask(taskId).subscribe(() => {
        // You might want to add assertions here based on what your deleteTask
        // method returns (e.g., a success message, the deleted task, etc.)
      });

      // Mock the CSRF request
      const csrfReq = httpMock.expectOne('http://localhost:8080/api/auth/csrf');
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Respond with empty data to simulate a successful CSRF request

      // Mock the actual task deletion request
      const req = httpMock.expectOne(`http://localhost:8080/api/tasks/${taskId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({}); // Respond with an empty object or appropriate response
    });
  });
});
