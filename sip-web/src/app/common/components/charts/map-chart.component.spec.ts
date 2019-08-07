import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { Subject } from 'rxjs';
import {
  MapChartComponent,
  IChartUpdate,
  IChartAction
} from './map-chart.component';

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

  beforeEach(async(() => {
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
  }));

  it('should call chart.update after a timeout so that the chart has a size', () => {
    const component = fixture.componentInstance;
    const addExportSizeSpy = spyOn(component, 'addExportSize');
    component.options = {};
    expect(addExportSizeSpy).toHaveBeenCalled();
  });

  describe('fillEmptyData', () => {
    it('should fill empty data for map', () => {
      const series = {
        data: [{ value: 12, x: '1' }],
        joinBy: ['postal-code'],
        mapData: {
          features: [
            { properties: { 'postal-code': '1' } },
            { properties: { 'postal-code': '2' } }
          ]
        }
      };
      fixture.componentInstance.fillEmptyData(series);
      expect(series.data.length).toEqual(2);
    });
  });

  describe('transformUpdateIfNeeded', () => {
    it('should transform updates to object if array is given', () => {
      const update = fixture.componentInstance.transformUpdateIfNeeded([
        { path: 'abc', data: '123' }
      ]);
      expect(update.abc).toEqual('123');
    });

    it('should not transform updates if object is given', () => {
      const update = { dummy: 'property' };
      expect(fixture.componentInstance.transformUpdateIfNeeded(update)).toEqual(
        update
      );
    });
  });

  describe('onAction', () => {
    it('should only execute on export', () => {
      const spy = spyOn(fixture.componentInstance, 'onExport').and.returnValue(
        true
      );

      fixture.componentInstance.onAction({});
      expect(spy).not.toHaveBeenCalled();

      fixture.componentInstance.onAction({ export: true });
      expect(spy).toHaveBeenCalled();
    });
  });
});
