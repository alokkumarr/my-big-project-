import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import 'hammerjs';
import { BehaviorSubject } from 'rxjs';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
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
import { CountoModule } from 'angular2-counto';

const ObserveServiceStub: Partial<ObserveService> = {};
const GlobalFilterServiceStub: Partial<GlobalFilterService> = {
  onApplyKPIFilter: new BehaviorSubject(null)
};

const dataFormatStub = {
  precision: 2,
  comma: true,
  prefix: '$',
  suffix: 'cents'
};

const _kpiStub = {
  dataFields: [{
    name: 'Available_items',
    columnName: 'Available_items',
    displayName: 'Available_items'
  }]
};

describe('Observe KPI Bullet Component', () => {
  let fixture: ComponentFixture<ObserveKPIComponent>;
  let component;
  beforeEach(async(() => {
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
        AddWidgetModule,
        ReactiveFormsModule,
        FormsModule,
        HttpClientTestingModule,
        CountoModule
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [ObserveKPIComponent],
      providers: [
        { provide: ObserveService, useValue: ObserveServiceStub },
        { provide: GlobalFilterService, useValue: GlobalFilterServiceStub }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ObserveKPIComponent);
        component = fixture.componentInstance;
        component.dataFormat = dataFormatStub;
        component._kpi = _kpiStub;
        fixture.detectChanges();
      });
  }));

  it('should format values as per properties selected', () => {
    const value = fixture.componentInstance.fetchValueAsPerFormat(123456);
    expect(value).toEqual('$ 123,456.00 cents');
  });

  it('should add comma separators to input param', () => {
    const value = fixture.componentInstance.fetchCommaValue(123456);
    expect(value).toEqual('123,456');
  });
});
