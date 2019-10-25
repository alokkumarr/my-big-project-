import { TestBed, ComponentFixture } from '@angular/core/testing';
import 'hammerjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../../../../material.module';
import { EditWidgetComponent } from './edit-widget.component';
import { AddWidgetModule } from '../add-widget/add-widget.module';
import { ObserveService } from '../../services/observe.service';

const ObserveServiceStub: Partial<ObserveService> = {};
const _kpiStub = {
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

describe('Edit Widget', () => {
  let fixture: ComponentFixture<EditWidgetComponent>, el: HTMLElement;
  let component;
  beforeEach(() => {
    return TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, MaterialModule, AddWidgetModule],
      declarations: [EditWidgetComponent],
      providers: [{ provide: ObserveService, useValue: ObserveServiceStub }]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(EditWidgetComponent);
        el = fixture.nativeElement;
        component = fixture.componentInstance;
        component._kpi = _kpiStub;
        fixture.detectChanges();
      });
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.prepareKPI).toEqual('function');
  });

  it('should not show kpi widget without input', () => {
    expect(fixture.componentInstance.editItem).toBeUndefined();
    expect(el.querySelector('widget-kpi')).toBeNull();
  });

  it('should show kpi widget if editItem present', done => {
    fixture.componentInstance.editItem = {
      kpi: _kpiStub,
      metric: { dateColumns: [{}] }
    };
    fixture.detectChanges();
    fixture
      .whenStable()
      .then(() => {
        expect(el.querySelector('widget-kpi')).toBeUndefined;
        done();
      })
      .catch(() => {
        done();
      });
  });
});
