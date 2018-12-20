import { State, Action, StateContext } from '@ngxs/store';
import {
  CommonStateUpdateMenu,
  AdminExportLoadMenu
} from '../actions/menu.actions';
import { CommonStateModel, Menu } from './common.state.model';

import { MenuService } from '../services';

import * as cloneDeep from 'lodash/cloneDeep';

@State<CommonStateModel>({
  name: 'common',
  defaults: { analyzeMenu: null, observeMenu: null, adminMenu: null }
})
export class CommonState {
  constructor(private menuService: MenuService) {}

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
}
