import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        LoginComponent // Import LoginComponent here
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a form with email and password controls', () => {
    expect(component.loginForm.contains('email')).toBeTrue();
    expect(component.loginForm.contains('password')).toBeTrue();
  });

  it('should mark email as invalid if empty', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('');
    expect(emailControl?.valid).toBeFalse();
  });

  it('should mark email as invalid if not a valid email address', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('test');
    expect(emailControl?.valid).toBeFalse();
  });

  it('should mark password as invalid if empty', () => {
    const passwordControl = component.loginForm.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.valid).toBeFalse();
  });

  it('should mark password as invalid if less than 8 characters', () => {
    const passwordControl = component.loginForm.get('password');
    passwordControl?.setValue('test');
    expect(passwordControl?.valid).toBeFalse();
  });

  it('should call authService.login on submit with valid form', () => {
    const mockUser = {
      token: 'mockToken',
      refresh_token: 'mockRefreshToken',
      user: { /* ... user data */ }
    };
    authServiceSpy.login.and.returnValue(of(mockUser));

    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'testpassword'
    });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'testpassword'
    });
  });

  it('should handle successful login', () => {
    const mockUser = {
      token: 'mockToken',
      refresh_token: 'mockRefreshToken',
      user: { /* ... user data */ }
    };
    authServiceSpy.login.and.returnValue(of(mockUser));
    const routerSpy = spyOn(component.router, 'navigate');

    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'testpassword'
    });

    component.onSubmit();

    expect(localStorage.getItem('token')).toBe('mockToken');
    expect(localStorage.getItem('refresh_token')).toBe('mockRefreshToken');
    expect(localStorage.getItem('user')).toEqual(JSON.stringify(mockUser.user));
    expect(routerSpy).toHaveBeenCalledWith(['/tasks']);
  });

  it('should handle login errors', () => {
    const mockError = { error: { reason: 'invalid password please try again' } };
    authServiceSpy.login.and.returnValue(throwError(() => mockError));

    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'wrongpassword'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('Invalid password. Please try again.');
  });

  // Add more test cases for other error scenarios (user not found, etc.)
});
