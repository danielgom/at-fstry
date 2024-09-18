import {
  HttpEvent,
  HttpHandlerFn,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { AuthService } from './auth/auth.service'; // Adjust the path to your AuthService
import { inject } from '@angular/core';
import { CookieService } from 'ngx-cookie-service'; // Import CookieService

export function authInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  const authService = inject(AuthService); // Inject AuthService directly
  const cookieService = inject(CookieService); // Inject CookieService

  req = addToken(req);
  req = addCsrfToken(req, cookieService); // Add CSRF token to the request

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/refresh')) {
        console.log('401 Error caught in interceptor:', error);
        return handle401Error(req, next, authService); // Pass the authService instance
      }
      return throwError(() => error);
    })
  );
}

let isRefreshing = false;
let refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

function addToken(request: HttpRequest<any>): HttpRequest<any> {
  const token = localStorage.getItem('token');
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });
}

// Function to add CSRF token
function addCsrfToken(
  request: HttpRequest<any>,
  cookieService: CookieService
): HttpRequest<any> {
  const csrfToken = cookieService.get('XSRF-TOKEN'); // Use CookieService to get the CSRF token
  console.log('CSRF Token from cookie:', csrfToken); // Debugging line
  if (csrfToken) {
    return request.clone({
      setHeaders: {
        'X-XSRF-TOKEN': csrfToken, // Adjust the header name if needed
      },
    });
  }
  return request;
}

function handle401Error(
  request: HttpRequest<any>,
  next: HttpHandlerFn,
  authService: AuthService
) {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((token: any) => {
        console.log('New token received:', token);
        isRefreshing = false;
        refreshTokenSubject.next(token.token);
        return next(addToken(request));
      })
    );
  } else {
    return refreshTokenSubject.pipe(
      filter((token) => token != null),
      take(1),
      switchMap((jwt: any) => {
        return next(addToken(request));
      })
    );
  }
}
