import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  Input,
  EventEmitter,
  Output
} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ChartGridComponent, dataFieldToHuman } from './chart-grid.component';
import { UChartModule } from '../../components/charts';
import { ChartService } from '../../services';
import { HeaderProgressService } from './../../../common/services';
import {
  setReverseProperty,
  shouldReverseChart
} from './../../../common/utils/dataFlattener';
import { QueryDSL } from 'src/app/models';

@Component({
  selector: 'dx-data-grid',
  template: '<h1>DataGrid</h1>'
})
class DxDataGridStubComponent {
  @Input() customizeColumns;
  @Input() columnAutoWidth;
  @Input() columnMinWidth;
  @Input() columnResizingMode;
  @Input() allowColumnReordering;
  @Input() allowColumnResizing;
  @Input() showColumnHeaders;
  @Input() showColumnLines;
  @Input() showRowLines;
  @Input() showBorders;
  @Input() rowAlternationEnabled;
  @Input() hoverStateEnabled;
  @Input() wordWrapEnabled;
  @Input() scrolling;
  @Input() sorting;
  @Input() dataSource;
  @Input() columns;
  @Input() pager;
  @Input() paging;
  @Output() onRowClick = new EventEmitter();
}

const analysis = {
  chartOptions: {
    chartType: 'map',
    xAxis: {
      reversed: false
    }
  },
  chartType: 'map',
  type: 'chart',
  mapOptions: { mapType: 'map' },
  sipQuery: {
    artifacts: [
      {
        artifactsName: 'sample',
        fields: [
          {
            dataField: 'string',
            area: 'x',
            alias: 'String',
            columnName: 'string.keyword',
            displayName: 'String',
            type: 'string'
          },
          {
            dataField: 'integer',
            area: 'y',
            columnName: 'integer',
            displayName: 'Integer',
            type: 'integer',
            aggregate: 'avg'
          }
        ]
      }
    ],
    booleanCriteria: 'AND',
    filters: [],
    sorts: [
      {
        artifacts: 'sample',
        columnName: 'string.keyword',
        type: 'integer',
        order: 'desc'
      }
    ]
  }
};

class ChartStubService {
  getChartConfigFor = () => ({});
  getMomentDateFormat = () => ({});
  dataToChangeConfig = () => ({});
}

class HeaderProgressStubService {
  show = () => true;
}

describe('Chart Grid Component', () => {
  let fixture: ComponentFixture<ChartGridComponent>;
  let component;
  const reversePropertyTrue = {
    chartType: 'map',
    xAxis: {
      reversed: true
    }
  };

  const reversePropertyFalse = {
    chartType: 'map',
    xAxis: {
      reversed: false
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [UChartModule],
      providers: [
        { provide: ChartService, useClass: ChartStubService },
        { provide: HeaderProgressService, useClass: HeaderProgressStubService }
      ],
      declarations: [ChartGridComponent, DxDataGridStubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ChartGridComponent);
        component = fixture.componentInstance;
        component.analysis = analysis;
        component.updater = new BehaviorSubject<Object[]>([]);
        fixture.detectChanges();
      });
  }));

  it('should toggle to grid when toggleToGrid changes, and toggle to chart, if changed back', () => {
    component.toggleToGrid = true;
    fixture.detectChanges();
    const gridElement = fixture.debugElement.nativeElement.querySelector(
      '.report-toggle'
    );
    expect(gridElement).toBeTruthy();

    component.toggleToGrid = false;
    fixture.detectChanges();
    const chartElement = fixture.debugElement.nativeElement.querySelector(
      'chart'
    );
    expect(chartElement).toBeTruthy();

    const mapElement = fixture.debugElement.nativeElement.querySelector(
      'map-box'
    );
    expect(mapElement).toBeTruthy();
  });

  it('should update chart options based on sorts applied in sipquery', () => {
    expect(
      setReverseProperty(
        component.analysis.chartOptions,
        component.analysis.sipQuery
      )
    ).toBeTruthy(reversePropertyTrue.xAxis.reversed);
  });

  it('should update chart options based on sorts applied in sipquery', () => {
    component.analysis.sipQuery.sorts[0].order = 'asc';
    expect(
      setReverseProperty(
        component.analysis.chartOptions,
        component.analysis.sipQuery
      )
    ).toBeTruthy(reversePropertyFalse.xAxis.reversed);
  });

  it('should convert datafield to human friendly format', () => {
    const result = dataFieldToHuman('sum@@double');
    expect(result).toEqual('sum(double)');
  });

  it('should not fetch column if no data is available ', () => {
    const spy = spyOn(component, 'fetchColumnData');
    component.trimKeyword({});
    expect(spy).not.toHaveBeenCalled();
  });

  it('should be able to get chart height  ', () => {
    const chartUpdateSpy = spyOn(component, 'getChartUpdates');
    expect(chartUpdateSpy).not.toHaveBeenCalled();
    component.data = [{}];
    expect(chartUpdateSpy).toBeTruthy();
  });

  it('should be able to  set the analysis  ', () => {
    const chartUpdateSpy = spyOn(component, 'initChartOptions');
    expect(chartUpdateSpy).not.toHaveBeenCalled();
    component.analysis = analysis;
    expect(chartUpdateSpy).toBeTruthy();
  });

  it('should reverse chart data based on sorts applied in sipquery', () => {
    component.analysis.sipQuery.sorts[0].order = 'desc';
    expect(shouldReverseChart(analysis.sipQuery as QueryDSL)).toBeTruthy();
  });
});
