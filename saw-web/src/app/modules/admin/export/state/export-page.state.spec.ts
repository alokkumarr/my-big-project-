import { async, TestBed } from '@angular/core/testing';
import { Store, NgxsModule } from '@ngxs/store';
import { tap, flatMap } from 'rxjs/operators';

import { ExportPageState } from './export-page.state';
import { ExportService } from '../export.service';
import * as actions from '../actions/export-page.actions';

class ExportServiceStub {}

describe('Export Page State', () => {
  let store: Store;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgxsModule.forRoot([ExportPageState], { developmentMode: true })
      ],
      providers: [{ provide: ExportService, useValue: new ExportServiceStub() }]
    }).compileComponents();

    store = TestBed.get(Store);
  }));

  it('adds analyses to state', async(() => {
    const analysis: any = { id: 123 };
    store.dispatch(new actions.AddAnalysisToExport(analysis));

    store
      .selectOnce(state => state.exportPage.exportData.analyses)
      .subscribe(analyses => {
        expect(analyses.length).toBe(1);
      });
  }));

  it('removes analyses to state', async(() => {
    const analysis: any = { id: 123 };
    store.dispatch(new actions.AddAnalysisToExport(analysis));

    store
      .selectOnce(state => state.exportPage.exportData.analyses)
      .pipe(
        tap(analyses => {
          expect(analyses.length).toBe(1);

          store.dispatch(new actions.RemoveAnalysisFromExport(analysis));
        }),
        flatMap(() =>
          store.selectOnce(state => state.exportPage.exportData.analyses)
        ),
        tap(analyses => {
          expect(analyses.length).toBe(0);
        })
      )
      .subscribe();
  }));

  it('removes all current category analyses from state', async(() => {
    const input: any[] = [{ id: 123 }, { id: 456 }];
    store.reset({
      exportPage: { categoryAnalyses: input, exportData: { analyses: input } }
    });
    store
      .selectOnce(state => state.exportPage.exportData.analyses)
      .pipe(
        tap(analyses => {
          expect(analyses.length).toBe(2);
          store.dispatch(new actions.RemoveAllAnalysesFromExport());
        }),
        flatMap(() => {
          return store.selectOnce(
            state => state.exportPage.exportData.analyses
          );
        }),
        tap(analyses => {
          expect(analyses.length).toBe(0);
        })
      )
      .subscribe();
  }));

  it('adds all current category analyses to state', async(() => {
    const input: any[] = [{ id: 123 }, { id: 456 }];
    store.reset({
      exportPage: { categoryAnalyses: input, exportData: { analyses: [] } }
    });
    store
      .selectOnce(state => state.exportPage.exportData.analyses)
      .pipe(
        tap(analyses => {
          expect(analyses.length).toBe(0);
          store.dispatch(new actions.AddAllAnalysesToExport());
        }),
        flatMap(() => {
          return store.selectOnce(
            state => state.exportPage.exportData.analyses
          );
        }),
        tap(analyses => {
          expect(analyses.length).toBe(2);
        })
      )
      .subscribe();
  }));
});
