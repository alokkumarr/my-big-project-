import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminExportTreeComponent } from './tree.component';

describe('TreeComponent', () => {
  let component: AdminExportTreeComponent;
  let fixture: ComponentFixture<AdminExportTreeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminExportTreeComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminExportTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
