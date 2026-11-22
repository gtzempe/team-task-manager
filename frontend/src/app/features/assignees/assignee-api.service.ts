import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Assignee } from './assignee.model';

@Injectable({
  providedIn: 'root',
})
export class AssigneeApiService {
  private readonly apiUrl = 'http://localhost:8080/api/assignees';

  constructor(private readonly http: HttpClient) {}

  getAssignees(): Observable<Assignee[]> {
    return this.http.get<Assignee[]>(this.apiUrl);
  }
}
