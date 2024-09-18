import { Component } from '@angular/core';
import { UserPreferencesService } from '../../services/user-preferences.service';
import {FormsModule} from "@angular/forms";
import {AuthService} from "../../auth/auth.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-user-preferences',
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.css'],
  imports: [
    FormsModule,
    NgIf
  ],
  standalone: true
})
export class UserPreferencesComponent {
  notificationsEnabled: boolean = true;
  isUserLoggedIn: boolean = false;

  constructor(private userPreferencesService: UserPreferencesService, private authService: AuthService) {
    this.isUserLoggedIn = this.authService.isLoggedIn();
    this.notificationsEnabled = this.userPreferencesService.getNotificationPreference();
  }

  toggleNotifications(): void {
    this.userPreferencesService.setNotificationPreference(this.notificationsEnabled);
  }
}
