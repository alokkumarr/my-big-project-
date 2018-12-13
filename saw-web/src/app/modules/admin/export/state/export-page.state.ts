import { State, Action, StateContext, Selector } from '@ngxs/store';
import {
  ExportSelectTreeItem,
  ExportLoadAnalyses,
  ExportLoadDashboards,
  AddAnalysisToExport,
  RemoveAnalysisFromExport,
  AddDashboardToExport,
  RemoveDashboardFromExport,
  ResetExportPageState,
  ClearExport,
  AddAllAnalysesToExport,
  RemoveAllAnalysesFromExport
} from '../actions/export-page.actions';
import { ExportPageModel } from './export-page.model';
import { ExportService } from '../export.service';
import { tap } from 'rxjs/operators';
import * as cloneDeep from 'lodash/clone';

const defaultState: ExportPageModel = {
  selectedModule: null,
  selectedCategory: null,
  shouldExportMetric: false,
  categoryAnalyses: [],
  categoryDashboards: [],
  exportData: {
    analyses: [],
    dashboards: [],
    metrics: []
  }
};

@State<ExportPageModel>({
  name: 'exportPage',
  defaults: <ExportPageModel>cloneDeep(defaultState)
})
export class ExportPageState {
  constructor(private exportService: ExportService) {}

  @Selector()
  static exportList(state: ExportPageModel): any[] {
    return [...state.exportData.analyses, ...state.exportData.dashboards];
  }

  @Action(ExportSelectTreeItem)
  treeItemSelected(
    { patchState, dispatch }: StateContext<ExportPageModel>,
    { moduleName, item }: ExportSelectTreeItem
  ) {
    patchState({
      selectedModule: moduleName,
      selectedCategory: item,
      ...(moduleName === 'ANALYZE' ? { categoryDashboards: [] } : {}),
      ...(moduleName === 'OBSERVE' ? { categoryAnalyses: [] } : {})
    });
    switch (moduleName) {
      case 'ANALYZE':
        return dispatch(new ExportLoadAnalyses(item.id));
      case 'OBSERVE':
        return dispatch(new ExportLoadDashboards(item.id));
    }
  }

  @Action(ExportLoadAnalyses)
  loadAnalyses(
    { patchState }: StateContext<ExportPageModel>,
    { categoryId }: ExportLoadAnalyses
  ) {
    return this.exportService.getAnalysesByCategoryId(categoryId).pipe(
      tap(analyses => {
        patchState({
          categoryAnalyses: analyses
        });
      })
    );
  }

  @Action(ExportLoadDashboards)
  loadDashboards(
    { patchState }: StateContext<ExportPageModel>,
    { categoryId }: ExportLoadDashboards
  ) {
    return this.exportService.getDashboardsForCategory(categoryId).pipe(
      tap(dashboards => {
        patchState({
          categoryDashboards: dashboards
        });
      })
    );
  }

  @Action(AddAnalysisToExport)
  addAnalysisToExport(
    { patchState, getState }: StateContext<ExportPageModel>,
    { analysis }: AddAnalysisToExport
  ) {
    const { exportData } = getState();
    const alreadyInExport = exportData.analyses.some(
      exportAnalysis => exportAnalysis.id === analysis.id
    );

    !alreadyInExport &&
      patchState({
        exportData: {
          ...exportData,
          analyses: [...exportData.analyses, analysis]
        }
      });
  }

  @Action(RemoveAnalysisFromExport)
  removeAnalysisFromExport(
    { patchState, getState }: StateContext<ExportPageModel>,
    { analysis }: AddAnalysisToExport
  ) {
    const { exportData } = getState();
    patchState({
      exportData: {
        ...exportData,
        analyses: exportData.analyses.filter(a => a.id !== analysis.id)
      }
    });
  }

  @Action(AddAllAnalysesToExport)
  addAllAnalysesToExport({
    getState,
    dispatch
  }: StateContext<ExportPageModel>) {
    const { categoryAnalyses } = getState();
    dispatch(
      categoryAnalyses.map(analysis => new AddAnalysisToExport(analysis))
    );
  }

  @Action(RemoveAllAnalysesFromExport)
  removeAllAnalysesFromExport({
    getState,
    dispatch
  }: StateContext<ExportPageModel>) {
    const { categoryAnalyses } = getState();
    dispatch(
      categoryAnalyses.map(analysis => new RemoveAnalysisFromExport(analysis))
    );
  }

  @Action(AddDashboardToExport)
  addDashboardToExport(
    { patchState, getState }: StateContext<ExportPageModel>,
    { dashboard }: AddDashboardToExport
  ) {
    const { exportData } = getState();
    patchState({
      exportData: {
        ...exportData,
        dashboards: [...exportData.dashboards, dashboard]
      }
    });
  }

  @Action(RemoveDashboardFromExport)
  removeDashboardFromExport(
    { patchState, getState }: StateContext<ExportPageModel>,
    { dashboard }: AddDashboardToExport
  ) {
    const { exportData } = getState();
    patchState({
      exportData: {
        ...exportData,
        dashboards: exportData.dashboards.filter(
          d => d.entityId !== dashboard.entityId
        )
      }
    });
  }

  @Action(ClearExport)
  clearExportList({ dispatch, getState }: StateContext<ExportPageModel>) {
    const { exportData } = getState();
    const actions = [
      ...exportData.analyses.map(
        analysis => new RemoveAnalysisFromExport(analysis)
      ),
      ...exportData.dashboards.map(
        dashboard => new RemoveDashboardFromExport(dashboard)
      )
    ];
    dispatch(actions);
  }

  @Action(ResetExportPageState)
  resetState({ setState }: StateContext<ExportPageModel>) {
    setState(cloneDeep(defaultState));
  }
}
