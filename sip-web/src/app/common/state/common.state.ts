import { State, Action, StateContext } from '@ngxs/store';
import {
  CommonStateUpdateMenu,
  AdminExportLoadMenu,
  CommonLoadAllMetrics,
  CommonStateScheuleJobs,
  CommonResetStateOnLogout
} from '../actions/menu.actions';
import { CommonStateModel, Menu } from './common.state.model';
import { forkJoin } from 'rxjs';
import { tap, switchMap } from 'rxjs/operators';

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
      /* Load all metrics individually. The above request gets
       the list of all metrics, but those don't have the artifacts.
       Individual loading of all metrics is necessary for that */
      switchMap(resp => {
        const resultMetrics = {};
        const metrics$ = resp.map(metric =>
          this.semantic.getArtifactsForDataSet$(metric.id)
        );
        return forkJoin(metrics$).pipe(
          tap((ms: Array<any>) => {
            ms.forEach(metric => (resultMetrics[metric.id] = metric));
            patchState({ metrics: resultMetrics });
          })
        );
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
