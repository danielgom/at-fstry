import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserPreferencesComponent } from './user-preferences.component';
import { UserPreferencesService } from '../../services/user-preferences.service';
import { AuthService } from '../../auth/auth.service';

describe('UserPreferencesComponent', () => {
  let component: UserPreferencesComponent;
  let fixture: ComponentFixture<UserPreferencesComponent>;
  let userPreferencesServiceSpy: jasmine.SpyObj<UserPreferencesService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    userPreferencesServiceSpy = jasmine.createSpyObj('UserPreferencesService', ['getNotificationPreference', 'setNotificationPreference']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);

    await TestBed.configureTestingModule({
      imports: [UserPreferencesComponent], // Import the standalone component
      providers: [
        { provide: UserPreferencesService, useValue: userPreferencesServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserPreferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get notification preference on init', () => {
    expect(userPreferencesServiceSpy.getNotificationPreference).toHaveBeenCalled();
  });

  it('should set notification preference on toggle', () => {
    component.notificationsEnabled = false; // Change the value
    component.toggleNotifications();
    expect(userPreferencesServiceSpy.setNotificationPreference).toHaveBeenCalledWith(false);
  });

  it('should check if user is logged in on init', () => {
    expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
  });
});
