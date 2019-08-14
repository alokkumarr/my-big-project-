import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SftpSourceComponent } from './sftp-source.component';

describe('SftpSourceComponent', () => {
  let component: SftpSourceComponent;
  let fixture: ComponentFixture<SftpSourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SftpSourceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SftpSourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
