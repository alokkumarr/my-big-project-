import { State, Action, StateContext } from '@ngxs/store';
import { AlertsAction } from './alerts.actions';

export class AlertsStateModel {
  public items: string[];
}

@State<AlertsStateModel>({
  name: 'alerts',
  defaults: {
    items: []
  }
})
export class AlertsState {
  @Action(AlertsAction)
  add(ctx: StateContext<AlertsStateModel>, action: AlertsAction) {
    const state = ctx.getState();
    ctx.setState({ items: [ ...state.items, action.payload ] });
  }
}
