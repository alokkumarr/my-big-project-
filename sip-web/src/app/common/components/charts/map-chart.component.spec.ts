import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { Subject } from 'rxjs';
import { MapChartComponent, IChartUpdate, IChartAction } from './map-chart.component';

@Component({
  selector: 'map-chart',
  template: 'Chart'
})
class ChartStubComponent {
  @Input() updater: Subject<IChartUpdate>;
  @Input() actionBus: Subject<IChartAction>;
  @Input() options: any;
}

describe('Designer Map Chart Component', () => {
  let fixture: ComponentFixture<MapChartComponent>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        declarations: [MapChartComponent, ChartStubComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      })
        .compileComponents()
        .then(() => {
          fixture = TestBed.createComponent(MapChartComponent);
          const comp = fixture.componentInstance;
          comp.updater = new Subject<IChartUpdate>();
          comp.actionBus = new Subject<IChartAction>();
          fixture.detectChanges();
        });
    })
  );

  it('should call chart.update after a timeout so that the chart has a size', () => {
    const component = fixture.componentInstance;
    const addExportSizeSpy = spyOn(component, 'addExportSize');
    component.options = {};
    expect(addExportSizeSpy).toHaveBeenCalled();
  });
});
