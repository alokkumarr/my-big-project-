import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, EventEmitter } from '@angular/core';
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
    expect(typeof fixture.componentInstance.onGlobalCheckboxToggle).toEqual(
      'function'
    );
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.displayWith).toEqual('function');
  });

  it('should fetch display name', () => {
    const filter = fixture.componentInstance.displayWith({
      displayName: 'sample'
    });
    expect(filter).not.toBeNull();
  });

  it('should fetch display name', () => {
    const filterModel = fixture.componentInstance.nameFilter('sample');
    expect(filterModel).not.toBeNull();
  });

  it('should communicate changes on aggregate change', () => {
    component.filterModelChange = new EventEmitter();
    const spy = spyOn(component.filterModelChange, 'emit').and.returnValue({});
    component.onAggregateSelected('abc');
    expect(component.filter.aggregate).toEqual('abc');
    expect(spy).toHaveBeenCalled();
  });
});
