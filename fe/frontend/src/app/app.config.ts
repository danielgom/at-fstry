import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideClientHydration } from '@angular/platform-browser';
import { authInterceptor } from './inter'; // Adjust the path
import { AuthService } from './auth/auth.service'; // Adjust the path
import { routes } from './app.routes'; // Adjust the path

export const appConfig: ApplicationConfig = {
  providers: [
    provideClientHydration(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    AuthService, // Ensure AuthService is provided
  ],
};
