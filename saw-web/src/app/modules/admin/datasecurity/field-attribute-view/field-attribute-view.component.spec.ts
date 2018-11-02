import { Component } from '@angular/core';
import { async, TestBed, ComponentFixture } from '@angular/core/testing';
import 'hammerjs';
import { MaterialModule } from '../../../../material.module';
import { FieldAttributeViewComponent } from './field-attribute-view.component';
import { UserAssignmentService } from '../userassignment.service';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import {
  DxDataGridModule
} from 'devextreme-angular/ui/data-grid';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  NO_ERRORS_SCHEMA
} from '@angular/core';

/* Stubs */
const UserAssignmentServiceStub = {
  getSecurityAttributes: () => {
    return new Promise(res => res({ data: {} }));
  }
};

@Component({
  selector: 'field-attribute-view',
  template: '<h1> field attribute view </h1>'
})
class FieldAttributeStubComponent {}

describe('field attribute component', () => {
  let fixture: ComponentFixture<FieldAttributeViewComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, DxDataGridModule, DxTemplateModule],
      declarations: [FieldAttributeViewComponent, FieldAttributeStubComponent],
      providers: [ DxDataGridService,
                  { provide: UserAssignmentService, useValue: UserAssignmentServiceStub }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(FieldAttributeViewComponent);
        fixture.detectChanges();
      });
  }));

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });
});
