import { Component } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import 'hammerjs';
import { MaterialModule } from '../../../../material.module';
import { AddAttributeDialogComponent } from './add-attribute-dialog.component';
import { DataSecurityService } from '../datasecurity.service';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogRef,
  MAT_DIALOG_DATA
} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

const DataSecurityServiceStub = {
  attributetoGroup: () => {
    return new Promise(res => res({ data: {} }));
  }
};

const mockService = {};
const dataStub = { securityGroupName: '' };

@Component({
  selector: 'security-group',
  template: '<h1> Security Group </h1>'
})
class AddAttributeDialogStubComponent {}

describe('Create AddAttributeDialogStubComponent', () => {
  let fixture: ComponentFixture<AddAttributeDialogComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MaterialModule,
        ReactiveFormsModule,
        FormsModule,
        BrowserAnimationsModule
      ],
      declarations: [
        AddAttributeDialogComponent,
        AddAttributeDialogStubComponent
      ],
      providers: [
        { provide: DataSecurityService, useValue: DataSecurityServiceStub },
        { provide: MatDialog, useValue: mockService },
        { provide: MatDialogRef, useValue: mockService },
        { provide: MAT_DIALOG_DATA, useValue: mockService },
        { provide: MatDialogConfig, useValue: mockService }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(AddAttributeDialogComponent);
        const comp = fixture.componentInstance;
        comp.data.groupSelected = dataStub;
        fixture.detectChanges();
      });
  }));

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });
});
