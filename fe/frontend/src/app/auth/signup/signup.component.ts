import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../auth.service';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule
  ],
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.signupForm = this.fb.group({
      name: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.signupForm.valid) {
      const userData = this.signupForm.value;
      this.authService.signup(userData).subscribe({
        next: () => {
          this.successMessage = 'Signup successful! You can now log in.'; // Make sure this is set
          // ... other success handling
        },
        error: (error) => {
          // The error message is now more specific from the AuthService
          this.errorMessage = error.message;
          this.successMessage = '';
          console.error('Signup error:', error);
        }
      });
    }
  }
}
