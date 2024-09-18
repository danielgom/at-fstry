import { NgModule, importProvidersFrom } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {provideHttpClient, withInterceptors} from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { LoginComponent } from './auth/login/login.component';
import { AuthService } from './auth/auth.service';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {MatDialogModule} from "@angular/material/dialog";
import {AppComponent} from "./app.component";
import {provideAnimationsAsync} from "@angular/platform-browser/animations/async";
import {authInterceptor} from "./inter";
import {NotificationComponent} from "./components/notifications/notifications.component";
import {UserPreferencesComponent} from "./components/user-preferences/user-preferences.component";
import {CookieService} from "ngx-cookie-service";

@NgModule({
  declarations: [
    // Include other non-standalone components here
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    MatDialogModule,
    FormsModule,
    UserPreferencesComponent,
    NotificationComponent,
    LoginComponent,
  ],
  providers: [
    provideAnimationsAsync(), // Use provideAnimationsAsync instead of provideAnimations
    provideHttpClient(withInterceptors([authInterceptor])), // Use provideHttpClient instead of HttpClientModule
    AuthService,
    CookieService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
