import { State, Action, StateContext, Selector } from '@ngxs/store';
import { ObserveStateModel } from './observe-state.model';
import {
  ObserveLoadMetrics,
  ObserveLoadArtifactsForMetric
} from '../actions/observe.actions';
import { ObserveService } from '../services/observe.service';

import * as cloneDeep from 'lodash/cloneDeep';
import * as values from 'lodash/values';
import { tap } from 'rxjs/operators';

const defaultObserveState: ObserveStateModel = {
  metrics: {}
};

@State<ObserveStateModel>({
  name: 'observe',
  defaults: <ObserveStateModel>cloneDeep(defaultObserveState)
})
export class ObserveState {
  constructor(private observe: ObserveService) {}

  @Selector()
  static metrics(state: ObserveStateModel) {
    return state.metrics;
  }

  @Action(ObserveLoadMetrics)
  loadMetric({
    patchState,
    getState,
    dispatch
  }: StateContext<ObserveStateModel>) {
    const metrics = getState().metrics;
    if (values(metrics).length > 0) {
      return patchState({ metrics: { ...metrics } });
    }

    return this.observe.getMetricList$().pipe(
      tap(resp => {
        const resultMetrics = {};
        resp.forEach(metric => {
          resultMetrics[metric.id] = metric;
        });

        patchState({ metrics: resultMetrics });
        return dispatch(
          resp.map(metric => new ObserveLoadArtifactsForMetric(metric.id))
        );
      })
    );
  }

  @Action(ObserveLoadArtifactsForMetric)
  loadArtifactsForMetric(
    { patchState, getState, dispatch }: StateContext<ObserveStateModel>,
    { semanticId }: ObserveLoadArtifactsForMetric
  ) {
    return this.observe.getArtifactsForDataSet$(semanticId).pipe(
      tap((metric: any) => {
        const metrics = getState().metrics;
        patchState({ metrics: { ...metrics, [metric.id]: metric } });
      })
    );
  }
}
