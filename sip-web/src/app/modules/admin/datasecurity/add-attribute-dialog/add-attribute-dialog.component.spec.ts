import { Component } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import 'hammerjs';
import { MaterialModule } from '../../../../material.module';
import { AddAttributeDialogComponent } from './add-attribute-dialog.component';
import { UserAssignmentService } from './../userassignment.service';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogRef,
  MAT_DIALOG_DATA
} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { JwtService } from 'src/app/common/services';

const UserAssignmentServiceStub = {
  attributetoGroup: () => {
    return new Promise(res => res({ data: {} }));
  },

  getEligibleDSKFieldsFor: () => ({ subscribe: () => {} })
};

const JwtServiceStub = {};

const mockService = {};
const dataStub = { securityGroupName: '' };

@Component({
  selector: 'security-group',
  template: '<h1> Security Group </h1>'
})
class AddAttributeDialogStubComponent {}

describe('Create AddAttributeDialogStubComponent', () => {
  let fixture: ComponentFixture<AddAttributeDialogComponent>;
  let component: AddAttributeDialogComponent;
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
        { provide: UserAssignmentService, useValue: UserAssignmentServiceStub },
        { provide: JwtService, useValue: JwtServiceStub },
        { provide: MatDialog, useValue: mockService },
        { provide: MatDialogRef, useValue: mockService },
        { provide: MAT_DIALOG_DATA, useValue: mockService },
        { provide: MatDialogConfig, useValue: mockService }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(AddAttributeDialogComponent);
        component = fixture.componentInstance;
        component.data.groupSelected = dataStub;
        fixture.detectChanges();
      });
  }));

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });

  it('should filter autocompletions', () => {
    component.data.attributeName = 'abc';
    component.dskEligibleFields = [
      { displayName: 'abcdef', columnName: 'abcdef' }
    ];
    component.filterAutocompleteFields();
    expect(component.filteredEligibleFields.length).toEqual(1);
  });
});
