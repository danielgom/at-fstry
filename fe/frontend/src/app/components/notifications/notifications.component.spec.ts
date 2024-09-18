import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NotificationComponent } from './notifications.component';
import { NotificationService } from '../../services/notification.service';
import { of, Subject } from 'rxjs';

describe('NotificationComponent', () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;
  let mockNotifications$: Subject<string[]>;

  beforeEach(async () => {
    mockNotifications$ = new Subject<string[]>();
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['notifications$', 'checkForNotifications']);
    notificationServiceSpy.notifications$ = mockNotifications$.asObservable();

    await TestBed.configureTestingModule({
      imports: [NotificationComponent],
      providers: [
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    }).compileComponents();

    // Spy on subscribe before creating the component
    spyOn(notificationServiceSpy.notifications$, 'subscribe').and.callThrough();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    jasmine.clock().install(); // Install fake clock
  });

  afterEach(() => {
    jasmine.clock().uninstall(); // Uninstall fake clock
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to notifications on init', () => {
    expect(notificationServiceSpy.notifications$.subscribe).toHaveBeenCalled();
  });

  it('should update notifications from service', () => {
    const testNotifications = ['Notification 1', 'Notification 2'];
    mockNotifications$.next(testNotifications);
    expect(component.notifications).toEqual(testNotifications);
  });
});
