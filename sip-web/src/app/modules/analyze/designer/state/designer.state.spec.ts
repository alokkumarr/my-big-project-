import { async, TestBed } from '@angular/core/testing';
import { DesignerService } from '../designer.module';
import { AnalyzeService } from '../../services/analyze.service';
import { NgxsModule, Store } from '@ngxs/store';
import { DesignerState, defaultDesignerState } from './designer.state';
import { DesignerSetData, DesignerLoadMetric } from '../actions/designer.actions';
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
    store.reset(defaultDesignerState);
  }));

  it('should initialise designer state', () => {
    store
      .selectOnce(state => state)
      .subscribe(s => {
        expect(Array.isArray(s.groupAdapters)).toEqual(true);
        expect(s.hasOwnProperty('metric')).toEqual(true);
      });
  });

  it('should allow setting data to state', async () => {
    await store.dispatch(new DesignerSetData([1])).toPromise();
    store
      .selectOnce(s => s.data)
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
});
