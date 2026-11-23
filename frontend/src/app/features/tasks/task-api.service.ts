import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Task, TaskStatus } from './task.model';
import { CreateTaskRequest } from './create-task-request.model';
import { UpdateTaskRequest } from './update-task-request.model';

@Injectable({
  providedIn: 'root',
})
export class TaskApiService {
  private readonly apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private readonly http: HttpClient) {}

  getAllTasks(status?: TaskStatus, assigneeId?: string): Observable<Task[]> {
    let params = new HttpParams();

    if (status) {
      params = params.set('status', status);
    }

    if (assigneeId) {
      params = params.set('assigneeId', assigneeId);
    }

    return this.http.get<Task[]>(this.apiUrl, { params });
  }

  getTaskById(taskId: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${taskId}`);
  }

  getMyTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/my`);
  }

  updateMyTaskStatus(taskId: number, status: TaskStatus): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/my/${taskId}/status`, { status });
  }

  createTask(request: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, request);
  }

  updateTask(taskId: number, request: UpdateTaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${taskId}`, request);
  }

  deleteTask(taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${taskId}`);
  }
}
