import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule } from 'src/app/material.module';

import { DesignerFilterRowComponent } from './designer-filter-row.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DesignerFilterRowComponent', () => {
  let component: DesignerFilterRowComponent;
  let fixture: ComponentFixture<DesignerFilterRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerFilterRowComponent],
      providers: [],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerFilterRowComponent);
    component = fixture.componentInstance;
    component.filter = {
      isOptional: true,
      columnName: 'abc',
      tableName: 'xyz',
      isRuntimeFilter: false,
      type: 'double'
    };
    component.artifactColumns = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.onGlobalCheckboxToggle).toEqual('function');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.onRuntimeCheckboxToggle).toEqual('function');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.onOptionalCheckboxToggle).toEqual('function');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.displayWith).toEqual('function');
  });

  it('should fetch display name', () => {
    const filterModel = fixture.componentInstance.nameFilter('sample');
    expect(filterModel).not.toBeNull();
  });
});

