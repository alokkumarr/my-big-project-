import { State, Action, StateContext } from '@ngxs/store';
import { ImportPageModel } from './import-page.model';
import {
  SelectAnalysisGlobalCategory,
  LoadAllAnalyzeCategories,
  LoadMetrics,
  LoadAnalysesForCategory,
  ClearImport,
  RefreshAllCategories
} from '../actions/import-page.actions';

import { map, tap } from 'rxjs/operators';
import * as cloneDeep from 'lodash/cloneDeep';
import * as keys from 'lodash/keys';
import { CategoryService } from '../../category';
import { ExportService } from '../../export/export.service';
import { ImportService } from '../import.service';

const defaultImportPageState: ImportPageModel = {
  analysisGlobalCategory: null,
  importFiles: [],
  metrics: {},
  referenceAnalyses: {},
  categories: {
    analyze: [],
    observe: []
  },
  importData: {
    analyses: []
  }
};

@State<ImportPageModel>({
  name: 'importPage',
  defaults: <ImportPageModel>cloneDeep(defaultImportPageState)
})
export class AdminImportPageState {
  constructor(
    private categoryService: CategoryService,
    private exportService: ExportService,
    private importService: ImportService
  ) {}

  @Action(LoadAllAnalyzeCategories)
  loadAllAnalyzeCategories({
    patchState,
    getState
  }: StateContext<ImportPageModel>) {
    const categories = getState().categories;
    return this.categoryService.getList$().pipe(
      map(allCategories =>
        allCategories.filter(category => category.moduleName === 'ANALYZE')
      ),
      map(analyzeCategories =>
        analyzeCategories.map(category => {
          category.analyses = [];
          return category;
        })
      ),
      tap(analyzeCategories =>
        patchState({
          categories: { ...categories, analyze: analyzeCategories }
        })
      )
    );
  }

  @Action(LoadMetrics)
  loadMetrics({ patchState }: StateContext<ImportPageModel>) {
    return this.exportService.getMetricList$().pipe(
      tap(metrics => {
        const metricMap = metrics.reduce((acc, metric) => {
          acc[metric.metricName] = metric;
          return acc;
        }, {});
        patchState({ metrics: metricMap });
      })
    );
  }

  @Action(SelectAnalysisGlobalCategory)
  selectAnalysisGlobalCategory(
    { patchState, dispatch }: StateContext<ImportPageModel>,
    { category }: SelectAnalysisGlobalCategory
  ) {
    patchState({
      analysisGlobalCategory: category
    });
    return dispatch(new LoadAnalysesForCategory(category));
    // TODO: Update overwrite status for each analysis
  }

  @Action(LoadAnalysesForCategory)
  loadAnalysesForCategory(
    { getState, patchState }: StateContext<ImportPageModel>,
    { category }: LoadAnalysesForCategory
  ) {
    const referenceAnalyses = getState().referenceAnalyses;
    if (referenceAnalyses[category.toString()]) {
      return;
    }
    return this.exportService.getAnalysesByCategoryId(category).pipe(
      tap(analyses => {
        const referenceMap = this.importService.createReferenceMapFor(analyses);
        patchState({
          referenceAnalyses: { ...referenceAnalyses, [category]: referenceMap }
        });
      })
    );
  }

  @Action(RefreshAllCategories)
  refreshAllCategories({
    getState,
    patchState,
    dispatch
  }: StateContext<ImportPageModel>) {
    const categoryIds = keys(getState().referenceAnalyses);
    patchState({
      referenceAnalyses: {}
    });
    return dispatch(categoryIds.map(id => new LoadAnalysesForCategory(id)));
  }

  @Action(ClearImport)
  resetImportState({ patchState }: StateContext<ImportPageModel>) {
    return patchState(cloneDeep(defaultImportPageState));
  }
}
