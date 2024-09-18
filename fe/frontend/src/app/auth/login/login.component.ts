import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { CommonModule } from '@angular/common'; // Import CommonModule
import { AuthService } from '../auth.service';
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    public router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response: { token: string; refresh_token: string; user: any; }) => {
          // Handle successful login
          localStorage.setItem('token', response.token);
          localStorage.setItem('refresh_token', response.refresh_token);
          localStorage.setItem('user', JSON.stringify(response.user));
          this.router.navigate(['/tasks']); // Redirect to dashboard
        },
        error: (err: HttpErrorResponse) => {
          const reason = err.error?.reason; // Safely access reason

          if (reason === "invalid password please try again") {
            this.errorMessage = 'Invalid password. Please try again.';
            return;
          }
          if (reason === "user not found") {
            this.errorMessage = 'User not found. Please try again.';
            return;
          }

          this.errorMessage = 'Login not available. Please try again later.';
        },
      });
    }
  }
}
