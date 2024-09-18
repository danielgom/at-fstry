import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, switchMap, tap, throwError } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import {environment} from "../../environments/environment";

export interface RefreshTokenResponse {
  token: string;
  expires_at: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  apiUrl = `${environment.apiUrl}/auth`;
  public refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient, private cookieService: CookieService) {
    this.refreshTokenSubject.next(localStorage.getItem('refresh_token') || null);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // Method to ensure CSRF token is initialized
  private initializeCsrfToken(): Observable<any> {
    return this.http.get(`${this.apiUrl}/csrf`, { withCredentials: true }).pipe(
      tap(() => {
        // Log or handle the CSRF token initialization success
        console.log('CSRF token initialized successfully.');
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Failed to initialize CSRF token:', error);
        return throwError(() => new Error('Failed to initialize CSRF token.'));
      })
    );
  }

  // Method to perform a request ensuring CSRF is initialized
  private ensureCsrfInitialized<T>(request: Observable<T>): Observable<T> {
    return this.initializeCsrfToken().pipe(
      switchMap(() => request) // Only proceed with the request if CSRF initialization succeeds
    );
  }

  signup(userData: any): Observable<any> {
    const signupRequest = this.http.post(`${this.apiUrl}/signup`, userData, { withCredentials: true });
    return this.ensureCsrfInitialized(signupRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Signup failed. Please try again.';
        if (error.status === 400 && error.error.reason) {
          errorMessage = error.error.reason;
        } else if (error.status === 409) {
          errorMessage = 'Email already exists.';
        }
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  login(credentials: { email: string; password: string }): Observable<any> {
    const loginRequest = this.http.post(`${this.apiUrl}/login`, credentials, { withCredentials: true });
    return this.ensureCsrfInitialized(loginRequest).pipe(
      tap((response: any) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('refresh_token', response.refresh_token);
        localStorage.setItem('user', JSON.stringify(response.user));
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Login failed. Please check your credentials.';
        if (error.status === 401) {
          errorMessage = 'Unauthorized: Incorrect email or password.';
        }
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  logout(): Observable<any> {
    const token = localStorage.getItem('token');
    if (!token) {
      return throwError(() => new Error('No token available for logout.'));
    }

    const csrfToken = this.cookieService.get('XSRF-TOKEN');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'X-XSRF-TOKEN': csrfToken
    });

    return this.http.post(`${this.apiUrl}/logout`, {}, { headers, withCredentials: true }).pipe(
      tap(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('user');
        this.refreshTokenSubject.next(null);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error during logout request:', error);
        return throwError(() => error);
      })
    );
  }

  refreshToken(): Observable<any> {
    const refreshToken = localStorage.getItem('refresh_token');
    if (!refreshToken) {
      return throwError(() => new Error('No Refresh token available'));
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    const refreshRequest = this.http.post<RefreshTokenResponse>(`${this.apiUrl}/refresh`, {}, { headers, withCredentials: true });
    return this.ensureCsrfInitialized(refreshRequest).pipe(
      tap((response: RefreshTokenResponse) => {
        localStorage.setItem('token', response.token);
        this.refreshTokenSubject.next(refreshToken);
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.logout();
        }
        return throwError(() => new Error('Refresh token is invalid or expired.'));
      })
    );
  }
}
