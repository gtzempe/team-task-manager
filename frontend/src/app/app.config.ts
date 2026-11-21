import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {
  provideKeycloak,
  createInterceptorCondition,
  IncludeBearerTokenCondition,
  includeBearerTokenInterceptor,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
} from 'keycloak-angular';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

const apiUrlCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: /^http:\/\/localhost:8080\/api(?:\/.*)?$/i,
  bearerPrefix: 'Bearer',
});

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
    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue: [apiUrlCondition],
    },
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    provideRouter(routes),
  ],
};
