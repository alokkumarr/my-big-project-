import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DesignerContainerComponent } from './designer-container.component';
import { DesignerService } from '../designer.service';
import { AnalyzeDialogService } from '../../services/analyze-dialog.service';
import { ChartService } from '../../../../common/services/chart.service';
import { AnalyzeService } from '../../services/analyze.service';
import { JwtService } from '../../../../common/services';
import { Store } from '@ngxs/store';
import { MatDialog } from '@angular/material';
import { of } from 'rxjs';
import { FilterService } from '../../services/filter.service';
​
const dialogStub = {
  open: () => {}
};
​
const analysisStub = {
  type: 'pivot'
};
​
const storeStub = {
  dispatch: () => {},
  selectSnapshot: () => ({ analysis: { sipQuery: {} } })
};
​
describe('Designer Component', () => {
  let component: DesignerContainerComponent;
  let fixture: ComponentFixture<DesignerContainerComponent>;
​
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: DesignerService, useValue: {} },
        { provide: AnalyzeDialogService, useValue: {} },
        { provide: ChartService, useValue: {} },
        { provide: AnalyzeService, useValue: {} },
        { provide: JwtService, useValue: {} },
        { provide: Store, useValue: storeStub },
        { provide: MatDialog, useValue: dialogStub },
        { provide: FilterService, useValue: {} }
      ],
      declarations: [DesignerContainerComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));
​
  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerContainerComponent);
    component = fixture.componentInstance;
    component.artifacts = [
      { artifactName: 'xyz', columns: [{ columnName: 'abc' }] }
    ] as any;
    component.analysis = analysisStub as any;
    fixture.detectChanges();
  });
​
  it('should create', () => {
    expect(component).toBeDefined();
  });
​
  it('should recreate data object', () => {
    // check if object is different, even if its contents is not.
    const data = [];
    component.data = data;
​
    component.refreshDataObject();
    expect(component.data).not.toBe(data);
  });
​
  it('should affect state when changing to query mode permanently', () => {
    const store = TestBed.get(Store);
    const spy = spyOn(store, 'dispatch').and.returnValue({});
    component.changeToQueryModePermanently();
    expect(spy).toHaveBeenCalled();
  });
​
  it('should construct filters for DSL format', () => {
    const filters = [
      {
        type: 'date',
        artifactsName: 'SALES',
        isOptional: false,
        columnName: 'date',
        isRuntimeFilter: false,
        isGlobalFilter: false,
        model: {
          operator: 'BTW',
          value: '01-01-2017',
          otherValue: '01-31-2017'
        }
      }
    ];
​
    const output = [
      {
        type: 'date',
        artifactsName: 'SALES',
        isOptional: false,
        columnName: 'date',
        isRuntimeFilter: false,
        isGlobalFilter: false,
        model: {
          operator: 'BTW',
          value: '01-01-2017',
          otherValue: '01-31-2017',
          gte: '2017-01-01',
          lte: '2017-01-31',
          preset: 'NA'
        }
      }
    ];
    const DSLFilters = component.generateDSLDateFilters(filters);
    expect(DSLFilters).toEqual(output);
  });
​
  describe('Derived metrics dialog', () => {
    it('should replace column if it already exists', async(() => {
      const column = {
        columnName: 'abc',
        table: 'xyz',
        type: 'double',
        dataField: 'abc'
      };
      const dialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({
        afterClosed: () => of(column)
      });
      const changesSpy = spyOn(
        component,
        'handleOtherChangeEvents'
      ).and.returnValue({});
​
      component.openDerivedMetricDialog(column as any);
      expect(dialogSpy).toHaveBeenCalled();
​
      expect(changesSpy).toHaveBeenCalledWith({
        subject: 'expressionUpdated',
        column
      });
    }));
​
    it('should add column if it does not already exists', async(() => {
      const column = {
        columnName: 'pqr',
        table: 'xyz',
        type: 'double',
        dataField: 'pqr'
      };
      const dialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({
        afterClosed: () => of(column)
      });
      const changesSpy = spyOn(
        component,
        'handleOtherChangeEvents'
      ).and.returnValue({});
​
      component.openDerivedMetricDialog(column as any);
      expect(dialogSpy).toHaveBeenCalled();
​
      expect(changesSpy).toHaveBeenCalledWith({
        subject: 'derivedMetricAdded',
        column
      });
    }));
  });
​
  it('should check filterSelectedFilter function ', () => {
    const filtersColumns = fixture.componentInstance.checkNodeForSorts();
    expect(filtersColumns).not.toBeNull();
  });
​
  it('should refresh validity on updating alias', () => {
    const spy = spyOn(component, 'canRequestData').and.returnValue(true);
    component.handleReportChangeEvents({
      subject: 'alias',
      column: { alias: '', columnName: 'col1', table: 'table', format: {} }
    } as any);
    expect(spy).toHaveBeenCalled();
  });
​
  it('should clean sorts', () => {
    component.sorts = [{ columnName: 'abc', order: 'desc', type: 'double' }];
    component.cleanSorts();
    expect(component.sorts.length).toEqual(0);
  });
​
  describe('canRequestReport', () => {
    it('should not be able to request report for default blank analysis', () => {
      expect(component.canRequestReport(null)).toEqual(false);
    });
  });
});
