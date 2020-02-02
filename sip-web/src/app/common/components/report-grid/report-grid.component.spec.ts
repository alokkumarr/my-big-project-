import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import {
  ReportGridComponent,
  findDuplicateColumns
} from './report-grid.component';
import { MaterialModule } from 'src/app/material.module';
import { DxDataGridModule } from 'devextreme-angular';
import { DEFAULT_PRECISION } from '../data-format-dialog/data-format-dialog.component';
import { ArtifactDSL } from 'src/app/models';

describe('Designer Chart Component', () => {
  let fixture: ComponentFixture<ReportGridComponent>;
  let component: ReportGridComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [],
      imports: [MaterialModule, DxDataGridModule],
      declarations: [ReportGridComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull;
  });

  it('should format data for dates and numbers only', () => {
    expect(component.checkFormatDataCondition('int')).toBeTruthy();
    expect(component.checkFormatDataCondition('date')).toBeTruthy();
    expect(component.checkFormatDataCondition('string')).toBeFalsy();
  });

  describe('preprocessFormatIfNeeded', () => {
    it('should pre process format for numeric types if aggregate is percent or average', () => {
      const formatAvg = component.preprocessFormatIfNeeded(
        {},
        'integer',
        'avg'
      );
      expect(formatAvg.precision).toEqual(DEFAULT_PRECISION);

      const formatPct = component.preprocessFormatIfNeeded(
        {},
        'integer',
        'percentage'
      );
      expect(formatPct.precision).toEqual(DEFAULT_PRECISION);
      expect(formatPct.percentage).toBeTruthy();
    });

    it('should not pre process format for numbers if aggregate is not avg or percent', () => {
      const format = component.preprocessFormatIfNeeded({}, 'integer', 'sum');
      expect(format.precision).toBeFalsy();
    });

    it('should not pre process format for non numeric types', () => {
      const format = component.preprocessFormatIfNeeded({}, 'date', 'avg');
      expect(format.precision).toBeFalsy();
    });
  });

  describe('customizeColumns', () => {
    it('should align columns to left', () => {
      const cols: any[] = [{}];
      component.customizeColumns(cols);
      expect(cols[0].alignment).toBeDefined();
    });
  });

  describe('getDataField', () => {
    it('should concat table name if columnName is customerCode', () => {
      const column: any = {};
      component.getDataField(column);
      expect(column).toBeDefined();
    });
  });

  describe('findDuplicates', () => {
    it('should be able to find duplicate columns', () => {
      const artifacts = [
        { fields: [{ columnName: 'abc' }] },
        { fields: [{ columnName: 'abc' }] }
      ];
      const duplicates = findDuplicateColumns(artifacts as ArtifactDSL[]);
      expect(duplicates.abc).toEqual(true);
    });
  });

  it('should calculate duplicate column when setting analysis', () => {
    const artifacts = [
      { fields: [{ columnName: 'abc' }] },
      { fields: [{ columnName: 'abc' }] }
    ];
    expect(Object.keys(component.duplicateColumns).length).toEqual(0);
    component._analysis = { sipQuery: { artifacts } } as any;
    expect(Object.keys(component.duplicateColumns).length).toEqual(1);
  });
});
