import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DskFilterGroupComponent } from './dsk-filter-group.component';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DataSecurityService } from '../datasecurity.service';
import { of } from 'rxjs';
import { JwtService } from 'src/app/common/services';
import { NgxPopperModule } from 'ngx-popper';
import { NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';

const DataSecurityServiceStub: Partial<DataSecurityService> = {
  getEligibleDSKFieldsFor: (customerId, productId) => {
    return of([]);
  }
};

const JwtServiceStub: Partial<JwtService> = {
  customerId: '',
  productId: ''
};

describe('DskFilterGroupComponent', () => {
  let component: DskFilterGroupComponent;
  let fixture: ComponentFixture<DskFilterGroupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DskFilterGroupComponent],
      providers: [
        {
          provide: DataSecurityService,
          useValue: DataSecurityServiceStub
        },
        { provide: JwtService, useValue: JwtServiceStub }
      ],
      imports: [
        MaterialModule,
        NoopAnimationsModule,
        NgxPopperModule,
        FormsModule
      ],
      schemas: [NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DskFilterGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
