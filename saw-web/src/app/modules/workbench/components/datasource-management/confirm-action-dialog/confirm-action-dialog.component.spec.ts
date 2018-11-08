import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MaterialModule } from '../../../../../material.module';
import { MatDialogRef } from '@angular/material';

import { ConfirmActionDialogComponent } from './confirm-action-dialog.component';

describe('ConfirmActionDialogComponent', () => {
  let component: ConfirmActionDialogComponent;
  let fixture: ComponentFixture<ConfirmActionDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule],
      providers: [
        // workaround: Can't inject MatDialogRef in the unit test?
        { provide: MatDialogRef, useValue: {} }
      ],
      declarations: [ConfirmActionDialogComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmActionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
