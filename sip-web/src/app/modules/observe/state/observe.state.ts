import { State } from '@ngxs/store';
import { ObserveStateModel } from './observe-state.model';

import * as cloneDeep from 'lodash/cloneDeep';

const defaultObserveState: ObserveStateModel = {};

@State<ObserveStateModel>({
  name: 'observe',
  defaults: <ObserveStateModel>cloneDeep(defaultObserveState)
})
export class ObserveState {
  constructor() {}
}
