import { Menu } from '../state/common.state.model';

export class CommonStateUpdateMenu {
  static readonly type = '[Common State] Update Menu';
  constructor(public moduleName: string, public items: Menu) {}
}

export class AdminExportLoadMenu {
  static readonly type = '[Admin Export Page] Load Menu ';

  constructor(public moduleName: string) {}
}
