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
import { ObserveKPIBulletComponent } from './observe-kpi-bullet.component';
import { AddWidgetModule } from '../add-widget/add-widget.module';
import { ObserveService } from '../../services/observe.service';
import { GlobalFilterService } from '../../services/global-filter.service';

const ObserveServiceStub: Partial<ObserveService> = {};
const GlobalFilterServiceStub: Partial<GlobalFilterService> = {
  onApplyKPIFilter: new BehaviorSubject(null)
};

const _executedKPI = {
  booleanCriteria: 'AND',
  bulletPalette: 'rog',
  dataFields: [{
        aggregate: [
           'sum',
           'avg',
           'min',
           'max',
           'count',
           'distinctCount'
        ],
        columnName: 'AVAILABLE_ITEMS',
        displayName: 'Available Items',
        name: 'AVAILABLE_ITEMS'
     }
  ],
  esRepository: {
     indexName: 'mct_test',
     storageType: 'ES',
     type: 'session'
  },
  filters: [{
        columnName: 'AVAILABLE_ITEMS',
        isGlobalFilter: false,
        isOptional: false,
        isRuntimeFilter: false,
        model: {
           operator: 'BTW',
           otherValue: 10,
           value: 200
        },
        tableName: 'mct_test',
        type: 'double'
     },
     {
        columnName: 'SOURCE_OS.keyword',
        isGlobalFilter: false,
        isOptional: false,
        isRuntimeFilter: false,
        model: {
           modelValues: [
              'A'
           ],
           operator: 'SW'
        },
        tableName: 'mct_test',
        type: 'string'
     },
     {
        columnName: 'TRANSFER_DATE',
        model: {
           gte: '2018-02-01 00:00:00',
           lte: '2018-02-28 23:59:59',
           preset: 'NA'
        },
        primaryKpiFilter: true,
        type: 'date'
     }
  ],
  id: '044cf2ca-a42e-a855-2d12-48a834c281da',
  kpiBgColor: 'green',
  name: 'Available Items',
  semanticId: 'tf-es-201912345678',
  tableName: 'mct_test'
};

describe('Observe KPI Bullet Component', () => {
  let fixture: ComponentFixture<ObserveKPIBulletComponent>;
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
      declarations: [ObserveKPIBulletComponent],
      providers: [
        { provide: ObserveService, useValue: ObserveServiceStub },
        { provide: GlobalFilterService, useValue: GlobalFilterServiceStub }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ObserveKPIBulletComponent);
        const component = fixture.componentInstance;
        component.updater = new BehaviorSubject([]);
        component.item = { bullet: {} };
        component._kpi = _executedKPI;
        fixture.detectChanges();
      });
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance).not.toBeNull();
  });

  it('should fetch filter label to display to the user', () => {
    const value = fixture.componentInstance.getFilterLabel();
    expect(value).toEqual('2018/02/01 - 2018/02/28');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.executeKPI).toEqual('function');
  });
});
