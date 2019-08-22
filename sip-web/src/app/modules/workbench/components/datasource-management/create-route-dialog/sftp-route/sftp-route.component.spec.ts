import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SftpRouteComponent } from './sftp-route.component';

describe('SftpRouteComponent', () => {
  let component: SftpRouteComponent;
  let fixture: ComponentFixture<SftpRouteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SftpRouteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SftpRouteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
