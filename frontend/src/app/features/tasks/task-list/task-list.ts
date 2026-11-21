import { Component, OnInit } from '@angular/core';
import { Task } from '../task.model';
import { AuthService } from '../../auth/auth.service';
import { TaskApiService } from '../task-api.service';
import { Observable } from 'rxjs';

@Component({
  imports: [],
  selector: 'app-task-list',
  styleUrl: './task-list.scss',
  templateUrl: './task-list.html',
})
export class TaskList implements OnInit {
  tasks: Task[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly taskApiService: TaskApiService,
  ) {}

  ngOnInit(): void {
    if (this.authService.isAssigner()) {
      this.loadTasks(this.taskApiService.getAllTasks());
      return;
    }

    if (this.authService.isAssignee()) {
      this.loadTasks(this.taskApiService.getMyTasks());
      return;
    }

    this.errorMessage = 'Your account does not have a task role';
    this.isLoading = false;
  }

  private loadTasks(tasks$: Observable<Task[]>): void {
    tasks$.subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load tasks';
        this.isLoading = false;
      },
    });
  }
}
