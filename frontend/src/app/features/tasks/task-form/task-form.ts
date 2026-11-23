import {
  ChangeDetectorRef,
  Component,
  OnInit,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Assignee } from '../../assignees/assignee.model';
import { AssigneeApiService } from '../../assignees/assignee-api.service';
import { TaskPriority } from '../task.model';
import { TaskApiService } from '../task-api.service';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-task-form',
  templateUrl: './task-form.html',
  styleUrl: './task-form.scss',
})
export class TaskForm implements OnInit, OnChanges {
  readonly priorityOptions: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH'];

  @Input() taskId: number | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

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
  isLoadingTask = false;
  taskErrorMessage = '';

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

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['taskId'] && this.taskId !== null) {
      this.loadTaskForEdit();
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.submitErrorMessage = '';

    const request = this.form.getRawValue();

    const saveRequest =
      this.taskId === null
        ? this.taskApiService.createTask(request)
        : this.taskApiService.updateTask(this.taskId, request);

    saveRequest.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.saved.emit();
      },
      error: () => {
        this.submitErrorMessage = 'Unable to save task';
        this.isSubmitting = false;
        this.changeDetectorRef.markForCheck();
      },
    });
  }

  cancel(): void {
    this.cancelled.emit();
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
