import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  Input,
  EventEmitter,
  Output
} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ChartGridComponent } from './chart-grid.component';
import { UChartModule } from '../../components/charts';
import { ChartService } from '../../services';
import { HeaderProgressService } from './../../../common/services';
import { setReverseProperty } from './../../../common/utils/dataFlattener';

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

class ChartStubService {
  getChartConfigFor = () => ({});
}

class HeaderProgressStubService {
  show = () => (true);
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
      providers: [{ provide: ChartService, useClass: ChartStubService },
        { provide: HeaderProgressService, useClass: HeaderProgressStubService }],
      declarations: [ChartGridComponent, DxDataGridStubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ChartGridComponent);
        component = fixture.componentInstance;
        component.analysis = {
          chartOptions: {
            chartType: 'map',
            xAxis: {
              reversed: false
            }
          },
          chartType: 'map',
          type: 'chart',
          mapOptions: {mapType: 'map'},
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
    expect(setReverseProperty(component.analysis.chartOptions, component.analysis.sipQuery))
    .toBeTruthy(reversePropertyTrue.xAxis.reversed);
  });

  it('should update chart options based on sorts applied in sipquery', () => {
    component.analysis.sipQuery.sorts[0].order = 'asc';
    expect(setReverseProperty(component.analysis.chartOptions, component.analysis.sipQuery))
    .toBeTruthy(reversePropertyFalse.xAxis.reversed);
  });
});
