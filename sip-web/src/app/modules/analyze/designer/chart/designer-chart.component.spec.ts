import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { DesignerChartComponent } from './designer-chart.component';
import { ChartService } from '../../../../common/services/chart.service';
import { ArtifactColumnChart } from '../../../../models/artifact-column.model';

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

class ChartStubService {}

describe('Designer Chart Component', () => {
  let fixture: ComponentFixture<DesignerChartComponent>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        providers: [{ provide: ChartService, useValue: ChartStubService }],
        declarations: [DesignerChartComponent, ChartStubComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      })
        .compileComponents()
        .then(() => {
          fixture = TestBed.createComponent(DesignerChartComponent);
          spyOn(fixture.componentInstance, 'getLegendConfig').and.returnValue(
            []
          );
        });
    })
  );

  it('should import chart types object', () => {
    const chartObj = fixture.componentInstance.CHART_TYPES_OBJ;
    expect(chartObj['chart:column']).not.toBeNull();
  });

  it('should set sqlbuilder', () => {
    const component = fixture.componentInstance;
    component.sqlBuilder = {
      nodeFields: [{ area: 'x' } as ArtifactColumnChart],
      dataFields: [{ area: 'y' } as ArtifactColumnChart],
      filters: [],
      booleanCriteria: 'AND'
    };
    expect(component.settings.xaxis).not.toBeNull();
    expect(component.settings.xaxis.length).toEqual(1);
  });

  it('should reload chart on data change', () => {
    const component = fixture.componentInstance;
    const reloadChartSpy = spyOn(component, 'reloadChart');
    component.data = [{}];
    expect(reloadChartSpy).toHaveBeenCalled();
  });

  it('should reload chart on aux settings change', () => {
    const component = fixture.componentInstance;
    const reloadChartSpy = spyOn(component, 'reloadChart');
    component.auxSettings = [{}];
    expect(reloadChartSpy).not.toHaveBeenCalled();

    component.data = [{}];
    component.auxSettings = [{}, {}];
    expect(reloadChartSpy).toHaveBeenCalledTimes(2);
  });
});
