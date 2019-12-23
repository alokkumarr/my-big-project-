import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { of } from 'rxjs';
import {
  DesignerMapChartComponent,
  MapChartStates
} from './designer-map-chart.component';
import { ChartService } from '../../../../common/services/chart.service';
import { MapDataService } from '../../../../common/components/charts/map-data.service';

@Component({
  // tslint:disable-next-line
  selector: 'map-chart',
  template: 'Chart'
})
class ChartStubComponent {
  @Input() updater: any;
  @Input() options: any;
}

class ChartStubService {
  splitToSeries = () => [{ data: [] }];
  analysisLegend2ChartLegend = () => {};
  addTooltipsAndLegendAsObject = () => {};
}
class MapDataStubService {
  getMapData = jasmine.createSpy('getMapData').and.returnValue(of('mapData'));
}

const normalSipQuery = {
  artifacts: [
    {
      artifactsName: '',
      fields: [
        {
          area: 'x',
          alias: '',
          columnName: '',
          dataField: '',
          displayName: '',
          groupInterval: '',
          name: '',
          type: '',
          table: ''
        },
        {
          area: 'y',
          alias: '',
          columnName: '',
          dataField: '',
          displayName: '',
          groupInterval: '',
          name: '',
          type: '',
          table: ''
        }
      ]
    }
  ],
  filters: [],
  booleanCriteria: 'AND',
  sorts: [],
  joins: [],
  semanticId: '',
  store: {
    dataStore: '',
    storageType: ''
  }
};

const sipQueryWithXFieldContainingRegion = {
  artifacts: [
    {
      artifactsName: '',
      fields: [
        {
          area: 'x',
          geoRegion: { name: '', path: '' },
          alias: '',
          columnName: '',
          dataField: '',
          displayName: '',
          groupInterval: '',
          name: '',
          type: '',
          table: ''
        },
        {
          area: 'y',
          alias: '',
          columnName: '',
          dataField: '',
          displayName: '',
          groupInterval: '',
          name: '',
          type: '',
          table: ''
        }
      ]
    }
  ],
  filters: [],
  booleanCriteria: 'AND',
  sorts: [],
  joins: [],
  semanticId: 'workbench::sample-elasticsearch',
  store: {
    dataStore: '',
    storageType: ''
  }
};

describe('Designer Map Chart Component', () => {
  let fixture: ComponentFixture<DesignerMapChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: ChartService, useValue: new ChartStubService() },
        { provide: MapDataService, useClass: MapDataStubService }
      ],
      declarations: [DesignerMapChartComponent, ChartStubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DesignerMapChartComponent);
      });
  }));

  it('should set x and y fields from sipQuery', () => {
    const component = fixture.componentInstance;
    component.sipQuery = normalSipQuery;
    expect(component._fields.x).not.toBeNull();
    expect(component._fields.y).not.toBeNull();
  });

  it('should change the state to NO_MAP_SELECTED when we get sipQuery with no region for the x field', () => {
    const component = fixture.componentInstance;
    component.sipQuery = normalSipQuery;
    expect(component.currentState).toEqual(MapChartStates.NO_MAP_SELECTED);
  });

  it('should change the state to OK when we get sipQuery with an x field that has a region', () => {
    const component = fixture.componentInstance;
    component.sipQuery = sipQueryWithXFieldContainingRegion;
    expect(component.currentState).toEqual(MapChartStates.OK);
  });
});
