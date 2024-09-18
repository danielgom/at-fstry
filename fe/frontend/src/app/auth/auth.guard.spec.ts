import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [AuthGuard],
    });
    guard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if token exists', () => {
    localStorage.setItem('token', 'some_test_token');
    expect(guard.canActivate()).toBeTrue();
  });

  it('should redirect to login if token does not exist', () => {
    localStorage.removeItem('token');
    const navigateSpy = spyOn(router, 'navigate');
    expect(guard.canActivate()).toBeFalse();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
