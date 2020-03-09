import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { DesignerChartComponent } from './designer-chart.component';
import { ChartService } from '../../../../common/services/chart.service';

import { Observable, BehaviorSubject, of } from 'rxjs';
@Component({
  // tslint:disable-next-line
  selector: 'chart',
  template: 'Chart'
})
class ChartStubComponent {
  @Input() updater: any;
  @Input() options: any;
  @Input() chartType: string;
}

class ChartStubService {
  LEGEND_POSITIONING: {};
  LAYOUT_POSITIONS: {};
  getChartConfigFor = () => {};
  initLegend = () => {};
  dataToChangeConfig = () => {};
}

const sipQuery = {
  artifacts: [
    {
      artifactsName: 'sales',
      fields: [
        {
          area: 'x',
          columnName: 'date',
          alias: '',
          dataField: 'date',
          displayName: 'Date',
          groupInterval: null,
          name: 'date',
          table: 'sales',
          type: 'date'
        },
        {
          area: 'y',
          columnName: 'double',
          alias: '',
          dataField: 'double',
          displayName: 'Double',
          groupInterval: null,
          name: 'double',
          table: 'sales',
          type: 'double'
        }
      ]
    }
  ],
  filters: [],
  sorts: [],
  joins: [],
  store: {
    dataStore: null,
    storageType: null
  },
  semanticId: 'workbench::sample-elasticsearch',
  booleanCriteria: 'AND'
};

const chartUpdater = {
  path: 'series',
  data: []
};

const analysis = {
  chartOptions: {
    chartType: 'map',
    chartTitle: 'title',
    isInverted: false,
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

const auxSettings = {
  legend: {
    align: 'right',
    layout: 'vertical'
  },
  labelOptions: {
    enabled: false,
    value: ''
  },
  isInverted: false
};

describe('Designer Chart Component', () => {
  let fixture: ComponentFixture<DesignerChartComponent>;
  let component;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: ChartService, useValue: new ChartStubService() }],
      declarations: [DesignerChartComponent, ChartStubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DesignerChartComponent);
        component = fixture.componentInstance;
        component.analysis = analysis;
        component.updater = new BehaviorSubject<Object[]>([]);
        component.chartTyoe = 'column';
        spyOn(fixture.componentInstance, 'getLegendConfig').and.returnValue([]);
      });
  }));

  it('should import chart types object', () => {
    const chartObj = fixture.componentInstance.CHART_TYPES_OBJ;
    expect(chartObj['chart:column']).not.toBeNull();
  });

  it('should set sqlbuilder', () => {
    component.setSipQuery = sipQuery;
    expect(component.sipQuery).not.toBeNull();
  });

  it('should reload chart on data change', () => {
    const reloadChartSpy = spyOn(component, 'reloadChart');
    component.sipQuery = sipQuery;
    component.data = [];
    expect(reloadChartSpy).not.toHaveBeenCalled();

    component.data = [{}];
    expect(reloadChartSpy).toHaveBeenCalled();
  });

  it('should reload chart on aux settings change', () => {
    const reloadChartSpy = spyOn(component, 'reloadChart');
    component.auxSettings = [{}];
    expect(reloadChartSpy).not.toHaveBeenCalled();

    component.sipQuery = sipQuery;
    component.data = [{}];
    component.auxSettings = [{}, {}];
    expect(reloadChartSpy).toHaveBeenCalledTimes(2);
  });

  it('should set type when chart is changed ', () => {
    const reloadChartSpy = spyOn(component, 'reloadChart');
    component.setChartType = 'pie';
    expect(reloadChartSpy).not.toHaveBeenCalled();

    component.sipQuery = sipQuery;
    component.chartType = 'bar';
    component.data = [{}];
    expect(reloadChartSpy).toHaveBeenCalled();
  });

  it('should save the chartupdater if series is present', () => {
    component.updater.next(chartUpdater);
    component.updater.subscribe(result => {
      expect(result).toEqual(chartUpdater);
    });
  });

  it('should reverse the data if needed', () => {
    const dummyData = [1, 2, 3];
    const reversedData = component.reverseDataIfNeeded(
      analysis.sipQuery,
      dummyData
    );
    expect(reversedData).toEqual([3, 2, 1]);
  });

  it('should get the chart height', () => {
    component.chartHgt.height = 10;
    expect(component.getChartHeight()).toEqual(component.chartHgt.height);
  });

  it('should set chart height after view init ', () => {
    const heightSpy = spyOn(component, 'getChartHeight');
    component.ngAfterViewInit();
    expect(heightSpy).toHaveBeenCalled();
  });

  it('should set updater when chart updater is changed. ', () => {
    const updater = of(chartUpdater);
    component.updater = updater;
    component.updater.subscribe(res => {
      expect(res).toEqual(chartUpdater);
    });
  });

  it('should set chart options on init', () => {
    component.chartHgt = { height: 4000 };
    component.chartType = 'map';
    component.sipQuery = sipQuery;
    component.updater = undefined; // setting the updater to undefined to cover the statement.
    component.ngOnInit();
    expect(component.updater instanceof Observable).toBeTruthy();
  });
  it('should return the legend configuration', () => {
    component._auxSettings = auxSettings;
    expect(component.getLegendConfig()).toEqual([]);
  });

  it('should conver sipQuery to Sql Builder ', () => {
    const dummyQueryBuilder = {
      nodeFields: [],
      dataFields: [],
      filters: [],
      booleanCriteria: false
    };

    const withFieldQB = {
      booleanCriteria: false
    };

    const builder = component.sipQueryToSQLBuilderFields(dummyQueryBuilder);
    expect(builder).toEqual(dummyQueryBuilder);

    // For statement coverage.
    const builderRes = component.sipQueryToSQLBuilderFields(sipQuery);
    expect(builderRes).toBeTruthy();

    // For branch coverage.
    const res = component.sipQueryToSQLBuilderFields(withFieldQB);
    expect(res).toBeTruthy();
  });
});
