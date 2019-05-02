import { State, Action, StateContext, Selector } from '@ngxs/store';
import { ObserveStateModel } from './observe-state.model';
import { ObserveLoadMetrics } from '../actions/observe.actions';
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
  loadMetric({ patchState, getState }: StateContext<ObserveStateModel>) {
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

        return patchState({ metrics: resultMetrics });
      })
    );
  }
}
