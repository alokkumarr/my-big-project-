import { State, Action, StateContext } from '@ngxs/store';
import {
  CommonStateUpdateMenu,
  AdminExportLoadMenu,
  CommonLoadAllMetrics,
  CommonLoadMetricById,
  CommonStateScheuleJobs,
  CommonResetStateOnLogout
} from '../actions/menu.actions';
import { CommonStateModel, Menu } from './common.state.model';
import { tap } from 'rxjs/operators';

import { MenuService, CommonSemanticService } from '../services';

import * as cloneDeep from 'lodash/cloneDeep';
import * as values from 'lodash/values';

const initialState = {
  analyzeMenu: null,
  observeMenu: null,
  adminMenu: null,
  metrics: {},
  jobs: null
};

@State<CommonStateModel>({
  name: 'common',
  defaults: cloneDeep(initialState)
})
export class CommonState {
  constructor(
    private menuService: MenuService,
    private semantic: CommonSemanticService
  ) {}

  @Action(CommonStateUpdateMenu)
  updateMenu(
    { patchState }: StateContext<CommonStateModel>,
    { moduleName, items }: CommonStateUpdateMenu
  ) {
    patchState({ [`${moduleName.toLowerCase()}Menu`]: cloneDeep(items) });
  }

  @Action(AdminExportLoadMenu)
  async loadMenu(
    ctx: StateContext<CommonStateModel>,
    action: AdminExportLoadMenu
  ) {
    try {
      const menu = await (<Promise<Menu>>(
        this.menuService.getMenu(action.moduleName)
      ));
      ctx.dispatch(new CommonStateUpdateMenu(action.moduleName, menu));
    } catch (err) {
      // TODO: Handle error
    }
  }

  @Action(CommonLoadAllMetrics)
  loadMetric({
    patchState,
    getState,
    dispatch
  }: StateContext<CommonStateModel>) {
    const metrics = getState().metrics;
    if (values(metrics).length > 0) {
      return patchState({ metrics: { ...metrics } });
    }

    return this.semantic.getMetricList$().pipe(
      tap(resp => {
        const resultMetrics = {};
        resp.forEach(metric => {
          resultMetrics[metric.id] = metric;
        });

        patchState({ metrics: resultMetrics });
        return dispatch(
          resp.map(metric => new CommonLoadMetricById(metric.id))
        );
      })
    );
  }

  @Action(CommonLoadMetricById)
  loadArtifactsForMetric(
    { patchState, getState, dispatch }: StateContext<CommonStateModel>,
    { metricId }: CommonLoadMetricById
  ) {
    return this.semantic.getArtifactsForDataSet$(metricId).pipe(
      tap((metric: any) => {
        const metrics = getState().metrics;
        patchState({ metrics: { ...metrics, [metric.id]: metric } });
      })
    );
  }

  @Action(CommonStateScheuleJobs)
  updateSchedule({ patchState, getState }, { cronJobs }) {
    return patchState({
      jobs: { ...cronJobs }
    });
  }

  @Action(CommonResetStateOnLogout)
  resetState({ patchState }: StateContext<CommonStateModel>) {
    return patchState(cloneDeep(initialState));
  }
}
