import { TestBed } from '@angular/core/testing';
import { AuthService, RefreshTokenResponse } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear(); // Clear local storage after each test
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should check if user is logged in', () => {
    expect(service.isLoggedIn()).toBeFalse(); // Initially not logged in

    localStorage.setItem('token', 'test_token');
    expect(service.isLoggedIn()).toBeTrue();
  });

  describe('signup', () => {
    it('should signup a user', () => {
      const mockSignupData = {
        name: 'Test User',
        lastName: 'Test Last Name',
        email: 'test@example.com',
        password: 'testpassword123'
      };
      const mockSignupResponse = { message: 'User created successfully' };

      // Trigger signup and mock CSRF request
      service.signup(mockSignupData).subscribe(response => {
        expect(response).toEqual(mockSignupResponse);
      });

      const csrfReq = httpMock.expectOne(`${service.apiUrl}/csrf`);
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Mock the CSRF response

      const req = httpMock.expectOne(`${service.apiUrl}/signup`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockSignupData);
      req.flush(mockSignupResponse);
    });

    it('should handle signup errors', () => {
      const mockSignupData = {
        name: 'Test User',
        lastName: 'Test Last Name',
        email: 'test@example.com',
        password: 'testpassword123'
      };

      const errorScenarios = [
        { status: 400, reason: 'Invalid email format', errorMessage: 'Invalid email format' },
        { status: 409, reason: 'user with this email already exists', errorMessage: 'Email already exists.' },
        { status: 500, reason: 'Internal server error', errorMessage: 'Signup failed. Please try again.' }
      ];

      errorScenarios.forEach(scenario => {
        // Trigger signup and mock CSRF request
        service.signup(mockSignupData).subscribe({
          next: () => fail('Expected an error, but signup succeeded'),
          error: (error) => {
            expect(error.message).toBe(scenario.errorMessage);
          }
        });

        const csrfReq = httpMock.expectOne(`${service.apiUrl}/csrf`);
        expect(csrfReq.request.method).toBe('GET');
        csrfReq.flush({}); // Mock the CSRF response

        const req = httpMock.expectOne(`${service.apiUrl}/signup`);
        req.flush({ reason: scenario.reason }, { status: scenario.status, statusText: 'Error' });
      });
    });
  });

  describe('login', () => {
    it('should log in a user', () => {
      const mockCredentials = { email: 'test@example.com', password: 'testpassword' };
      const mockLoginResponse = { token: 'mock_token', refreshToken: 'mock_refresh_token' };

      // Trigger login and mock CSRF request
      service.login(mockCredentials).subscribe(response => {
        expect(response).toEqual(mockLoginResponse);
      });

      const csrfReq = httpMock.expectOne(`${service.apiUrl}/csrf`);
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Mock the CSRF response

      const req = httpMock.expectOne(`${service.apiUrl}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockCredentials);
      req.flush(mockLoginResponse);
    });
  });

  describe('refreshToken', () => {
    it('should refresh the token', () => {
      const mockRefreshToken = 'mock_refresh_token';
      const mockRefreshTokenResponse: RefreshTokenResponse = {
        token: 'new_mock_token',
        expires_at: '2024-09-13T00:00:00.000Z' // Example future date
      };

      localStorage.setItem('token', 'old_mock_token');
      localStorage.setItem('refresh_token', mockRefreshToken);

      // Trigger refresh token and mock CSRF request
      service.refreshToken().subscribe(response => {
        expect(response).toEqual(mockRefreshTokenResponse);
        expect(localStorage.getItem('token')).toBe('new_mock_token');
      });

      const csrfReq = httpMock.expectOne(`${service.apiUrl}/csrf`);
      expect(csrfReq.request.method).toBe('GET');
      csrfReq.flush({}); // Mock the CSRF response

      const req = httpMock.expectOne(`${service.apiUrl}/refresh`);
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('Authorization')).toBe('Bearer old_mock_token');
      req.flush(mockRefreshTokenResponse);
    });
  });
});
