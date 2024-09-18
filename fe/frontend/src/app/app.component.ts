import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavbarComponent} from "./navbar/navbar/navbar.component";
import {NgClass} from "@angular/common";
import {NotificationComponent} from "./components/notifications/notifications.component";
import {UserPreferencesComponent} from "./components/user-preferences/user-preferences.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, NgClass, NotificationComponent, UserPreferencesComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';
}
