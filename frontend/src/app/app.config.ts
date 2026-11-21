import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideKeycloak } from 'keycloak-angular';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideKeycloak({
      config: {
        url: 'http://localhost:8081',
        realm: 'team-task-manager',
        clientId: 'task-manager-frontend',
      },
      initOptions: {
        onLoad: 'login-required',
        pkceMethod: 'S256',
      },
    }),
    provideRouter(routes),
  ],
};
