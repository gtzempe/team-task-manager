import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Task, TaskStatus } from '../task.model';
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
  canUpdateTaskStatus = false;
  errorMessage = '';
  statusErrorMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly taskApiService: TaskApiService,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.canUpdateTaskStatus = this.authService.isAssignee();

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
        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Unable to load tasks';
        this.isLoading = false;
        this.changeDetectorRef.markForCheck();
      },
    });
  }

  updateTaskStatus(task: Task, event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const newStatus = selectElement.value as TaskStatus;

    this.taskApiService.updateMyTaskStatus(task.id, newStatus).subscribe({
      next: (updatedTask) => {
        task.status = updatedTask.status;
        this.statusErrorMessage = '';
        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.statusErrorMessage = 'Unable to update task status';
        selectElement.value = task.status;
        this.changeDetectorRef.markForCheck();
      },
    });
  }
}
