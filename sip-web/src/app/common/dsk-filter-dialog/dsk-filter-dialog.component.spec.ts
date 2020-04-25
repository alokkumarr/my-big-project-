import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DskFilterDialogComponent } from './dsk-filter-dialog.component';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { DskFiltersService } from './../services/dsk-filters.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';

const DskFiltersServiceStub: Partial<DskFiltersService> = {
  getFiltersFor: group => of(null),
  clearDSKEligibleFields: () => {}
};

describe('DskFilterDialogComponent', () => {
  let component: DskFilterDialogComponent;
  let fixture: ComponentFixture<DskFilterDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DskFilterDialogComponent],
      providers: [
        { provide: DskFiltersService, useValue: DskFiltersServiceStub },
        {
          provide: MAT_DIALOG_DATA,
          useValue: { groupSelected: { secGroupSysId: 1 } }
        },
        { provide: MatDialogRef, useValue: {} }
      ],
      imports: [MaterialModule, NoopAnimationsModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    }).compileComponents();
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
