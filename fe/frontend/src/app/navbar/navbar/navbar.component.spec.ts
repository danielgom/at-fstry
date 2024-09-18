import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../auth/auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);

    await TestBed.configureTestingModule({
      imports: [
        NavbarComponent,
        RouterTestingModule // Include RouterTestingModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router); // Inject the Router
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should check if the user is logged in', () => {
    localStorage.setItem('token', 'test-token');
    expect(component.isLoggedIn()).toBeTrue();
    localStorage.removeItem('token');
    expect(component.isLoggedIn()).toBeFalse();
  });

  it('should log out the user', fakeAsync(() => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('refresh_token', 'test-refresh-token');
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true)); // Spy on router.navigate
    authServiceSpy.logout.and.returnValue(of({})); // Simulate successful logout

    component.onLogout();
    tick(); // Wait for the asynchronous operations

    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('refresh_token')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']); // Check if navigation happened
  }));

  it('should handle logout errors', fakeAsync(() => {
    const mockError = new Error('Logout error');
    authServiceSpy.logout.and.returnValue(throwError(() => mockError));
    spyOn(console, 'error'); // Spy on console.error

    component.onLogout();
    tick(); // Wait for the asynchronous operations

    expect(console.error).toHaveBeenCalledWith('Error logging out:', mockError);
  }));
});
