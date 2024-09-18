import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { importProvidersFrom } from '@angular/core';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app/app.component';
import { appRoutes } from './app/app-routing.module';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {authInterceptor} from "./app/inter";

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(appRoutes),
    provideHttpClient(withInterceptors([authInterceptor])), // Replaces the deprecated HttpClientModule import
    importProvidersFrom(ReactiveFormsModule), provideAnimationsAsync()
  ]
}).catch(err => console.error(err));
