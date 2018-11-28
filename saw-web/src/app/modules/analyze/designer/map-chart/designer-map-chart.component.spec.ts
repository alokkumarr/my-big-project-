import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { DesignerMapChartComponent, MapChartStates } from './designer-map-chart.component';
import { ChartService } from '../../services/chart.service';
import { MapDataService } from '../../../../common/components/charts/map-data.service';
import { ArtifactColumn, ArtifactColumnChart } from '../../../../models/artifact-column.model';

@Component({
  // tslint:disable-next-line
  selector: 'map-chart',
  template: 'Chart'
})
class ChartStubComponent {
  @Input() updater: any;
  @Input() options: any;
}

class ChartStubService {}
class MapDataStubService {}

const normalSqlBuilder = {
  nodeFields: [{ checked: 'x' } as ArtifactColumn],
  dataFields: [{ checked: 'y' } as ArtifactColumn],
  filters: [],
  booleanCriteria: 'AND'
};
const sqlBuilderWithXFieldContainingRegion = {
  nodeFields: [{
    checked: 'x',
    region: { name: '', path: ''}
  } as ArtifactColumnChart],
  dataFields: [{ checked: 'y' } as ArtifactColumn],
  filters: [],
  booleanCriteria: 'AND'
};

describe('Designer Map Chart Component', () => {
  let fixture: ComponentFixture<DesignerMapChartComponent>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        providers: [
          { provide: ChartService, useValue: ChartStubService },
          { provide: MapDataService, useValue: MapDataStubService }
        ],
        declarations: [DesignerMapChartComponent, ChartStubComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      })
        .compileComponents()
        .then(() => {
          fixture = TestBed.createComponent(DesignerMapChartComponent);
          spyOn(fixture.componentInstance, 'getLegendConfig').and.returnValue(
            []
          );
        });
    })
  );

  it('should set x and y fields from sqlBuilder', () => {
    const component = fixture.componentInstance;
    component.sqlBuilder = normalSqlBuilder;
    expect(component._fields.x).not.toBeNull();
    expect(component._fields.y).not.toBeNull();
  });

  it('should change the state to OK when we get sqlBuilder with a nodeField that has a region', () => {
    const component = fixture.componentInstance;
    component.sqlBuilder = normalSqlBuilder;
    expect(component.currentState).toEqual(MapChartStates.NO_MAP_SELECTED);
  });

  it('should change the state to OK when we get sqlBuilder with a nodeField that has a region', () => {
    const component = fixture.componentInstance;
    component.sqlBuilder = sqlBuilderWithXFieldContainingRegion;
    expect(component.currentState).toEqual(MapChartStates.OK);
  });
});
