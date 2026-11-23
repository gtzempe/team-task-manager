import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Assignee } from '../../assignees/assignee.model';
import { AssigneeApiService } from '../../assignees/assignee-api.service';
import { CreateTaskRequest } from '../create-task-request.model';
import { Task, TaskPriority } from '../task.model';
import { TaskApiService } from '../task-api.service';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-task-create',
  templateUrl: './task-create.html',
})
export class TaskCreate implements OnInit {
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

  constructor(
    private readonly assigneeApiService: AssigneeApiService,
    private readonly taskApiService: TaskApiService,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
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

    const request: CreateTaskRequest = this.form.getRawValue();

    this.taskApiService.createTask(request).subscribe({
      next: (task) => {
        this.createdTask = task;
        this.isSubmitting = false;
        this.form.reset({
          title: '',
          description: '',
          priority: 'MEDIUM',
          dueDate: '',
          assigneeId: '',
        });
        this.changeDetectorRef.markForCheck();
      },
      error: () => {
        this.submitErrorMessage = 'Unable to create task';
        this.isSubmitting = false;
        this.changeDetectorRef.markForCheck();
      },
    });
  }
}
