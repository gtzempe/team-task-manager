import { Component, OnInit, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { Task, TaskStatus } from '../task.model';
import { AuthService } from '../../auth/auth.service';
import { TaskApiService } from '../task-api.service';
import { Observable } from 'rxjs';
import { TaskForm } from '../task-form/task-form';
import { Assignee } from '../../assignees/assignee.model';
import { AssigneeApiService } from '../../assignees/assignee-api.service';

@Component({
  imports: [TaskForm],
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
  canCreateTasks = false;
  deleteErrorMessage = '';
  deletingTaskId: number | null = null;
  isTaskFormOpen = false;
  selectedTaskId: number | null = null;
  assignees: Assignee[] = [];
  assigneeFilterErrorMessage = '';
  selectedStatus: TaskStatus | '' = '';
  selectedAssigneeId = '';

  @ViewChild('taskDialog') taskDialog!: ElementRef<HTMLDialogElement>;

  readonly statusOptions: Record<TaskStatus, TaskStatus[]> = {
    TODO: ['TODO', 'IN_PROGRESS', 'BLOCKED'],
    IN_PROGRESS: ['IN_PROGRESS', 'DONE', 'BLOCKED'],
    BLOCKED: ['BLOCKED', 'IN_PROGRESS'],
    DONE: ['DONE'],
  };

  constructor(
    private readonly authService: AuthService,
    private readonly taskApiService: TaskApiService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    private readonly assigneeApiService: AssigneeApiService,
  ) {}

  ngOnInit(): void {
    const isAssigner = this.authService.isAssigner();
    this.canCreateTasks = isAssigner;
    this.canUpdateTaskStatus = this.authService.isAssignee();

    if (isAssigner) {
      this.loadAssignees();
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

  private loadAssignees(): void {
    this.assigneeApiService.getAssignees().subscribe({
      next: (assignees) => {
        this.assignees = assignees;
        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.assigneeFilterErrorMessage = 'Unable to load assignees for filters';
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

  deleteTask(task: Task): void {
    const confirmed = window.confirm(`Delete task "${task.title}"?`);

    if (!confirmed) {
      return;
    }

    this.deleteErrorMessage = '';
    this.deletingTaskId = task.id;

    this.taskApiService.deleteTask(task.id).subscribe({
      next: () => {
        this.tasks = this.tasks.filter((item) => item.id !== task.id);
        this.deletingTaskId = null;
        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.deleteErrorMessage = 'Unable to delete task';
        this.deletingTaskId = null;
        this.changeDetectorRef.markForCheck();
      },
    });
  }

  openTaskForm(taskId: number | null): void {
    this.selectedTaskId = taskId;
    this.isTaskFormOpen = true;
    this.taskDialog.nativeElement.showModal();
  }

  closeTaskForm(): void {
    this.taskDialog.nativeElement.close();
    this.isTaskFormOpen = false;
    this.selectedTaskId = null;
  }

  onTaskSaved(): void {
    this.closeTaskForm();
    this.loadFilteredTasks();
  }

  onStatusFilterChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedStatus = select.value as TaskStatus | '';
    this.loadFilteredTasks();
  }

  onAssigneeFilterChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedAssigneeId = select.value;
    this.loadFilteredTasks();
  }

  private loadFilteredTasks(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const status = this.selectedStatus || undefined;
    const assigneeId = this.selectedAssigneeId || undefined;

    this.loadTasks(this.taskApiService.getAllTasks(status, assigneeId));
  }
}
