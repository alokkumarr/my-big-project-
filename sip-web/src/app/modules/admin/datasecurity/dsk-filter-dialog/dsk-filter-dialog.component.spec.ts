import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DskFilterDialogComponent } from './dsk-filter-dialog.component';

describe('DskFilterDialogComponent', () => {
  let component: DskFilterDialogComponent;
  let fixture: ComponentFixture<DskFilterDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DskFilterDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DskFilterDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
