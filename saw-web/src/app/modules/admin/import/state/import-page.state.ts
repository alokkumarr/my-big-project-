import { State, Action, StateContext } from '@ngxs/store';
import { ImportPageModel } from './import-page.model';
import {
  SelectAnalysisGlobalCategory,
  LoadAllAnalyzeCategories,
  LoadMetrics
} from '../actions/import-page.actions';

import { map, tap } from 'rxjs/operators';
import * as cloneDeep from 'lodash/cloneDeep';
import { CategoryService } from '../../category';
import { ExportService } from '../../export/export.service';

const defaultImportPageState: ImportPageModel = {
  analysisGlobalCategory: null,
  importFiles: [],
  metrics: {},
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
    private exportService: ExportService
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
    { patchState }: StateContext<ImportPageModel>,
    { category }: SelectAnalysisGlobalCategory
  ) {
    patchState({
      analysisGlobalCategory: category
    });
  }
}
