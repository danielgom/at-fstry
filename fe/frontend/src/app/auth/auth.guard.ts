// src/app/auth/auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean | UrlTree {
    const token = localStorage.getItem('token'); // Check if the token exists

    if (token) {
      // If the token exists, allow access
      return true;
    } else {
      // If not authenticated, redirect to the login page
      this.router.navigate(['/login']);
      return false
    }
  }
}
