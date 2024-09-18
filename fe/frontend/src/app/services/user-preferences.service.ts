import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UserPreferencesService {
  private readonly NOTIFICATION_PREF_KEY = 'notificationsEnabled';

  getNotificationPreference(): boolean {
    const pref = localStorage.getItem(this.NOTIFICATION_PREF_KEY);
    return pref ? JSON.parse(pref) : true; // Default to true if not set
  }

  setNotificationPreference(enabled: boolean): void {
    localStorage.setItem(this.NOTIFICATION_PREF_KEY, JSON.stringify(enabled));
  }
}
