import { TestBed } from '@angular/core/testing';
import { UserPreferencesService } from './user-preferences.service';

describe('UserPreferencesService', () => {
  let service: UserPreferencesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserPreferencesService);
    localStorage.clear(); // Clear local storage before each test
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get the default notification preference', () => {
    expect(service.getNotificationPreference()).toBeTrue(); // Default should be true
  });

  it('should set and get the notification preference', () => {
    service.setNotificationPreference(false);
    expect(service.getNotificationPreference()).toBeFalse();

    service.setNotificationPreference(true);
    expect(service.getNotificationPreference()).toBeTrue();
  });
});
