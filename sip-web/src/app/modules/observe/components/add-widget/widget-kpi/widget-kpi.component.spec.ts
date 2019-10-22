import {
  ReactiveFormsModule,
  FormControl,
  FormGroup,
  FormsModule
} from '@angular/forms';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import 'hammerjs';
import { MaterialModule } from '../../../../../material.module';
import { WidgetKPIComponent } from './widget-kpi.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

const _metricStub = {
  dateColumns: [{
    aliasName: '',
    columnName: 'TRANSFER_DATE',
    displayName: 'Transfer Date',
    filterEligible: true,
    format: 'yyyy-MM-dd HH:mm:ss',
    joinEligible: false,
    kpiEligible: true,
    name: 'TRANSFER_DATE',
    table: 'mct_tgt_session',
    type: 'date'
  }],
  artifacts: [{
    artifactName: 'mct_tgt_session',
    columns: [{
      aliasName: '',
      columnName: 'TRANSFER_DATE',
      displayName: 'Transfer Date',
      filterEligible: true,
      format: 'yyyy-MM-dd HH:mm:ss',
      joinEligible: false,
      kpiEligible: true,
      name: 'TRANSFER_DATE',
      table: 'mct_tgt_session',
      type: 'date'
    }]
  }]
};

const userOptedFiltersStub = [{
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
];

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

describe('KPI Form Widget', () => {
  let fixture: ComponentFixture<WidgetKPIComponent>;
  let component;
  beforeEach(() => {
    return TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        MaterialModule,
        ReactiveFormsModule,
        FormsModule
      ],
      declarations: [WidgetKPIComponent]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(WidgetKPIComponent);
        component = fixture.componentInstance;
        component._kpi = _kpiStub;
        component.userOptedFilters = userOptedFiltersStub;
        component._metric = _metricStub;
        fixture.detectChanges();
      });
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.applyKPI).toEqual('function');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.createForm).toEqual('function');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.constructRequestParamsFilters).toEqual('function');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.prepareDateFilterModel).toEqual('function');
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.updateSecondaryAggregations).toEqual('function');
  });

  it('should disable secondary aggregation if it is selected in primary', done => {
    const pAggr = fixture.componentInstance.kpiForm.get(
      'primAggregate'
    ) as FormControl;
    pAggr.setValue('avg');

    fixture.detectChanges();
    fixture
      .whenStable()
      .then(() => {
        const sAggrForm = fixture.componentInstance.kpiForm.get(
          'secAggregates'
        ) as FormGroup;
        const avgControl = sAggrForm.get('avg') as FormControl;

        expect(avgControl.disabled).toBe(true);
        done();
      })
      .catch(() => {
        done();
      });
  });

  it('should uncheck secondary aggregation if it is selected in primary', done => {
    const sAggrForm = fixture.componentInstance.kpiForm.get(
      'secAggregates'
    ) as FormGroup;
    const avgControl = sAggrForm.get('avg') as FormControl;
    expect(avgControl.disabled).toBe(false);

    avgControl.setValue(true);

    const pAggr = fixture.componentInstance.kpiForm.get(
      'primAggregate'
    ) as FormControl;
    pAggr.setValue('avg');

    fixture.detectChanges();
    fixture
      .whenStable()
      .then(() => {
        expect(avgControl.disabled).toBe(true);
        expect(avgControl.value).toBe(false);
        done();
      })
      .catch(() => {
        done();
      });
  });

  it('should fetch filters model based on user selection in kpi', () => {
    const filterModel = fixture.componentInstance.prepareDateFilterModel();
    const preset = {preset: 'Yesterday'};
    expect(filterModel).toEqual(preset);
  });

  it('should construct filter params including primary and secondary filters', () => {
    const defaultFilter = {
      columnName: 'TRANSFER_DATE',
        model: {
           gte: '2018-02-01 00:00:00',
           lte: '2018-02-28 23:59:59',
           preset: 'NA'
        },
        primaryKpiFilter: true,
        type: 'date'
    };
    const filters = fixture.componentInstance.constructRequestParamsFilters(defaultFilter);
    expect(filters).not.toBeNull();
  });

  it('should construct filter without custom values', () => {
    const defaultFilter = {
      columnName: 'TRANSFER_DATE',
        model: {
           preset: 'LY'
        },
        primaryKpiFilter: true,
        type: 'date'
    };
    const filters = fixture.componentInstance.constructRequestParamsFilters(defaultFilter);
    expect(filters).not.toBeNull();
  });

  it('should check filterSelectedFilter function ', () => {
    const filtersColumns = fixture.componentInstance.filterSelectedFilter();
    expect(filtersColumns).toEqual([]);
  });

  it('create form function to exist as its form which is input to the request.', () => {
    const filters = fixture.componentInstance.createForm();
    expect(filters).not.toBeDefined();
  });
});
