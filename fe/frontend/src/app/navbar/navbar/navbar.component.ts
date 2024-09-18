import {ChangeDetectorRef, Component} from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {Router} from '@angular/router'; // Import Router
import {AuthService} from '../../auth/auth.service';
import {NgIf} from "@angular/common"; // Adjust

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    NgIf
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  constructor(private authService: AuthService, private router: Router, private cd: ChangeDetectorRef) {
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  onLogout() {
    this.authService.logout().subscribe({
      next: () => {
        // Remove tokens and user data from local storage
        localStorage.removeItem('token');
        localStorage.removeItem('refresh_token');

        // Trigger change detection
        this.cd.detectChanges();

        // Redirect to login page
        this.router.navigate(['/login']).then(() => {
          console.log('Redirected to login page');
        });
      },
      error: (err) => {
        console.error('Error logging out:', err);
      }
    });
  }
}
