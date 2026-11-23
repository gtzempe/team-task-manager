import { Routes } from '@angular/router';
import { TaskList } from './features/tasks/task-list/task-list';
import { TaskForm } from './features/tasks/task-form/task-form';

export const routes: Routes = [
  {
    path: 'tasks/new',
    component: TaskForm,
  },
  {
    path: 'tasks/:taskId/edit',
    component: TaskForm,
  },
  {
    path: '',
    component: TaskList,
    pathMatch: 'full',
  },
];
