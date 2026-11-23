import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Assignee } from '../../assignees/assignee.model';
import { AssigneeApiService } from '../../assignees/assignee-api.service';
import { Task, TaskPriority } from '../task.model';
import { TaskApiService } from '../task-api.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  selector: 'app-task-form',
  templateUrl: './task-form.html',
})
export class TaskForm implements OnInit {
  readonly priorityOptions: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH'];

  readonly form = new FormGroup({
    title: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    description: new FormControl('', { nonNullable: true }),
    priority: new FormControl<TaskPriority>('MEDIUM', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    dueDate: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    assigneeId: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  assignees: Assignee[] = [];
  isLoadingAssignees = true;
  isSubmitting = false;
  assigneeErrorMessage = '';
  submitErrorMessage = '';
  createdTask: Task | null = null;
  taskId: number | null = null;
  isLoadingTask = false;
  taskErrorMessage = '';

  constructor(
    private readonly assigneeApiService: AssigneeApiService,
    private readonly taskApiService: TaskApiService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const taskId = this.route.snapshot.paramMap.get('taskId');

    if (taskId !== null) {
      this.taskId = Number(taskId);
      this.loadTaskForEdit();
    }
    this.assigneeApiService.getAssignees().subscribe({
      next: (assignees) => {
        this.assignees = assignees;
        this.isLoadingAssignees = false;
        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.assigneeErrorMessage = 'Unable to load assignees';
        this.isLoadingAssignees = false;
        this.changeDetectorRef.markForCheck();
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.submitErrorMessage = '';
    this.createdTask = null;

    const request = this.form.getRawValue();

    const saveRequest =
      this.taskId === null
        ? this.taskApiService.createTask(request)
        : this.taskApiService.updateTask(this.taskId, request);

    saveRequest.subscribe({
      next: (task) => {
        this.createdTask = task;
        this.isSubmitting = false;

        if (this.taskId !== null) {
          this.router.navigateByUrl('/');
          return;
        }

        if (this.taskId === null) {
          this.form.reset({
            title: '',
            description: '',
            priority: 'MEDIUM',
            dueDate: '',
            assigneeId: '',
          });
        }

        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.submitErrorMessage = 'Unable to save task';
        this.isSubmitting = false;
        this.changeDetectorRef.markForCheck();
      },
    });
  }

  private loadTaskForEdit(): void {
    if (this.taskId === null) {
      return;
    }

    this.isLoadingTask = true;
    this.taskErrorMessage = '';

    this.taskApiService.getTaskById(this.taskId).subscribe({
      next: (task) => {
        this.form.patchValue({
          title: task.title,
          description: task.description ?? '',
          priority: task.priority,
          dueDate: task.dueDate,
          assigneeId: task.assigneeId,
        });
        this.isLoadingTask = false;
        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.taskErrorMessage = 'Unable to load task';
        this.isLoadingTask = false;
        this.changeDetectorRef.markForCheck();
      },
    });
  }
}
