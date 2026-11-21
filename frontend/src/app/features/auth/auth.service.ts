import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private readonly keycloak: Keycloak) {}

  getUsername(): string {
    return this.keycloak.tokenParsed?.['preferred_username'] ?? '';
  }

  isAssigner(): boolean {
    return this.keycloak.realmAccess?.roles?.includes('assigner') ?? false;
  }

  isAssignee(): boolean {
    return this.keycloak.realmAccess?.roles?.includes('assignee') ?? false;
  }

  logout(): Promise<void> {
    return this.keycloak.logout({
      redirectUri: window.location.origin,
    });
  }
}
