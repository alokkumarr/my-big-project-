import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminExportContentComponent } from './content.component';

describe('ContentComponent', () => {
  let component: AdminExportContentComponent;
  let fixture: ComponentFixture<AdminExportContentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminExportContentComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminExportContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
