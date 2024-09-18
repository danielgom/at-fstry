import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { HttpClientTestingModule } from '@angular/common/http/testing'; // Import HttpClientTestingModule
import { RouterTestingModule } from '@angular/router/testing'; // Import RouterTestingModule
import { NavbarComponent } from './navbar/navbar/navbar.component';
import { NotificationComponent } from './components/notifications/notifications.component';
import { UserPreferencesComponent } from './components/user-preferences/user-preferences.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NgClass } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog'; // Import MatDialogModule

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule, // Add HttpClientTestingModule here
        RouterTestingModule, // Provide a mock router environment
        NoopAnimationsModule, // Provide a mock animations environment
        MatDialogModule, // Add MatDialogModule here to handle MatDialog
        AppComponent, // Import the standalone component
        NavbarComponent,
        NotificationComponent,
        UserPreferencesComponent,
        NgClass // Include NgClass for the test environment
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the AppComponent', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'frontend'`, () => {
    expect(component.title).toEqual('frontend');
  });
});
