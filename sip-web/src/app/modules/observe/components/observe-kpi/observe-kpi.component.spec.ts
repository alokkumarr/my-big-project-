import { TestBed, ComponentFixture } from '@angular/core/testing';
import 'hammerjs';
import { BehaviorSubject } from 'rxjs';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { DxCircularGaugeModule } from 'devextreme-angular';
import { DxRangeSliderModule } from 'devextreme-angular/ui/range-slider';
import { DxNumberBoxModule } from 'devextreme-angular/ui/number-box';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../../../../material.module';
import { UChartModule } from '../../../../common/components/charts';
import { ObserveKPIComponent } from './observe-kpi.component';
import { AddWidgetModule } from '../add-widget/add-widget.module';
import { ObserveService } from '../../services/observe.service';
import { GlobalFilterService } from '../../services/global-filter.service';

const ObserveServiceStub: Partial<ObserveService> = {};
const GlobalFilterServiceStub: Partial<GlobalFilterService> = {
  onApplyKPIFilter: new BehaviorSubject(null)
};

describe('Observe KPI Bullet Component', () => {
  let fixture: ComponentFixture<ObserveKPIComponent>;
  beforeEach(() => {
    return TestBed.configureTestingModule({
      imports: [
        DxTemplateModule,
        DxDataGridModule,
        DxCircularGaugeModule,
        DxRangeSliderModule,
        DxNumberBoxModule,
        UChartModule,
        NoopAnimationsModule,
        MaterialModule,
        AddWidgetModule
      ],
      declarations: [ObserveKPIComponent],
      providers: [
        { provide: ObserveService, useValue: ObserveServiceStub },
        { provide: GlobalFilterService, useValue: GlobalFilterServiceStub }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ObserveKPIComponent);
        // const component = fixture.componentInstance;
        fixture.detectChanges();
      });
  });

  it('should fetch filters model based on user selection in kpi', () => {
    const value = fixture.componentInstance.fetchValueAsPerFormat(123456);
    const formattedValue = '$ 123,456.000';
    expect(value).toEqual(formattedValue);
  });
});
