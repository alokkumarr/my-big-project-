import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { AlertUnsubscribeService } from './alert-unsubscribe.service';
import { JwtService } from './../../../../common/services/jwt.service';
import { UserService } from './../../../../common/services/user.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { AlertUnsubscribe } from './alert-unsubscribe.component';
import { RouterTestingModule } from '@angular/router/testing';

class UserServiceStub {
  isLoggedIn() {
    return true;
  }
}

const alertDetailsStub = {
  alertId: '1',
  alertDesc: '123abc'
};

describe('AlertUnsubscribe', () => {
  let component: AlertUnsubscribe;
  let fixture: ComponentFixture<AlertUnsubscribe>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlertUnsubscribe],
      providers: [
        { provide: AlertUnsubscribeService, useValue: {} },
        { provide: ToastService, useValue: {} },
        { provide: UserService, useClass: UserServiceStub },
        { provide: JwtService, useValue: { parseJWT: () => {} } }
      ],
      imports: [RouterTestingModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertUnsubscribe);
    component = fixture.componentInstance;
    component.alertDetails = alertDetailsStub;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
