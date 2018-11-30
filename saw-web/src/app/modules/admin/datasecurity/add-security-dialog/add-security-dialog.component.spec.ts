import { Component } from '@angular/core';
import {
  ReactiveFormsModule,
  FormsModule
} from '@angular/forms';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import 'hammerjs';
import { MaterialModule } from '../../../../material.module';
import { AddSecurityDialogComponent } from './add-security-dialog.component';
import { UserAssignmentService } from './../userassignment.service';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

const UserAssignmentServiceStub = {
  addSecurityGroup: () => {
    return new Promise(res => res({ data: {} }));
  }
};

const mockService = {};

@Component({
  selector: 'security-group',
  template: '<h1> Security Group </h1>'
})
class AddGroupDialogStubComponent {}

describe('Create AddGroupDialogStubComponent', () => {
  let fixture: ComponentFixture<AddSecurityDialogComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule,
        ReactiveFormsModule,
        FormsModule,
        BrowserAnimationsModule],
      declarations: [AddSecurityDialogComponent, AddGroupDialogStubComponent],
      providers: [{ provide: UserAssignmentService, useValue: UserAssignmentServiceStub },
                  { provide: MatDialog, useValue: mockService },
                  { provide: MatDialogRef, useValue: mockService },
                  { provide: MAT_DIALOG_DATA, useValue: mockService },
                  { provide: MatDialogConfig, useValue: mockService }]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(AddSecurityDialogComponent);
        fixture.detectChanges();
      });
  }));

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });
});
