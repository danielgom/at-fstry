import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SignupComponent } from './signup.component';
import {ReactiveFormsModule, Validators} from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';
import {Router} from "@angular/router";

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['signup']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, SignupComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a form with required controls', () => {
    expect(component.signupForm.contains('name')).toBeTrue();
    expect(component.signupForm.contains('lastName')).toBeTrue();
    expect(component.signupForm.contains('email')).toBeTrue();
    expect(component.signupForm.contains('password')).toBeTrue();

    expect(component.signupForm.get('name')?.hasValidator(Validators.required)).toBeTrue();
    expect(component.signupForm.get('lastName')?.hasValidator(Validators.required)).toBeTrue();
    expect(component.signupForm.get('email')?.hasValidator(Validators.required)).toBeTrue();
    expect(component.signupForm.get('email')?.hasValidator(Validators.email)).toBeTrue(); // Check for email validator
    expect(component.signupForm.get('password')?.hasValidator(Validators.required)).toBeTrue();
  });

  it('should mark email as invalid if not a valid email address', () => {
    const emailControl = component.signupForm.get('email');
    emailControl?.setValue('test');
    expect(emailControl?.valid).toBeFalse();
  });

  it('should call authService.signup on submit with valid form', () => {
    authServiceSpy.signup.and.returnValue(of({})); // Simulate successful signup

    component.signupForm.setValue({
      name: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      password: 'testpassword'
    });

    component.onSubmit();

    expect(authServiceSpy.signup).toHaveBeenCalledWith({
      name: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      password: 'testpassword'
    });
  });

  it('should handle successful signup', () => {
    authServiceSpy.signup.and.returnValue(of({}));

    component.signupForm.setValue({
      name: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      password: 'TestValidPass1234@'
    });

    component.onSubmit();

    expect(component.successMessage).toBe('Signup successful! You can now log in.');
    expect(component.errorMessage).toBe('');
    // Add expectation for navigation or other success actions
  });

  it('should handle signup errors', () => {
    const mockError = new Error('Email already exists.');
    authServiceSpy.signup.and.returnValue(throwError(() => mockError));

    component.signupForm.setValue({
      name: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      password: 'testpassword'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('Email already exists.');
    expect(component.successMessage).toBe('');
  });
});
