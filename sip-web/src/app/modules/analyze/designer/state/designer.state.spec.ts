import { async, TestBed } from '@angular/core/testing';
import { DesignerService } from '../designer.module';
import { AnalyzeService } from '../../services/analyze.service';
import { NgxsModule, Store } from '@ngxs/store';
import { DesignerState, defaultDesignerState } from './designer.state';
import {
  DesignerSetData,
  DesignerLoadMetric,
  DesignerUpdateAnalysisMetadata,
  DesignerCheckAggregateFilterSupport
} from '../actions/designer.actions';
import { tap } from 'rxjs/operators';

describe('Designer State', () => {
  let store: Store;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([DesignerState])],
      providers: [
        {
          provide: DesignerService,
          useValue: {}
        },
        {
          provide: AnalyzeService,
          useValue: {}
        }
      ]
    }).compileComponents();
    store = TestBed.get(Store);
    store.reset({ designerState: defaultDesignerState });
  }));

  it('should initialise designer state', () => {
    store.selectOnce(DesignerState).subscribe(s => {
      expect(Array.isArray(s.groupAdapters)).toEqual(true);
      expect(s.hasOwnProperty('metric')).toEqual(true);
    });
  });

  it('should allow setting data to state', async () => {
    await store.dispatch(new DesignerSetData([1])).toPromise();
    store
      .selectOnce(s => s.designerState.data)
      .subscribe(data => {
        expect(data.length).toEqual(1);
        expect(data[0]).toEqual(1);
      });
  });

  it('should check metric name', async(() => {
    const metric = {
      metricName: 'sample',
      artifacts: []
    };

    store.dispatch(new DesignerLoadMetric(metric));
    store
      .selectOnce(DesignerState.metricName)
      .pipe(
        tap(metricName => {
          expect(metricName).toEqual(metric.metricName);
        })
      )
      .subscribe();
  }));

  it('should remove aggregation filters if they are not supported', async () => {
    await store
      .dispatch(
        new DesignerUpdateAnalysisMetadata({
          type: 'esReport',
          sipQuery: {
            artifacts: [],
            booleanCriteria: 'AND',
            filters: [
              {
                isAggregationFilter: true,
                isRuntimeFilter: false,
                isOptional: false,
                tableName: 'abc',
                columnName: 'def',
                type: 'double'
              }
            ],
            joins: [],
            sorts: [],
            store: { dataStore: '123', storageType: '123' },
            semanticId: '123'
          }
        })
      )
      .toPromise();

    expect(store.selectSnapshot(DesignerState.analysisFilters).length).toEqual(
      1
    );

    await store.dispatch(new DesignerCheckAggregateFilterSupport()).toPromise();

    expect(store.selectSnapshot(DesignerState.analysisFilters).length).toEqual(
      0
    );
  });
});
