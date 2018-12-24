import { MenuItem } from '../../../../common/state/common.state.model';
import { Dashboard } from '../../../observe/models/dashboard.interface';
import { Analysis } from '../../../analyze/models';

export class ResetExportPageState {
  static readonly type = '[Admin Export Page OnDestroy] Reset page state';
  constructor() {}
}

export class ExportSelectTreeItem {
  static readonly type = '[Admin Export Page Tree] Select menu item';
  constructor(public moduleName: string, public item: MenuItem) {}
}

export class ExportLoadAnalyses {
  static readonly type = '[Admin Export State] Load analyses';
  constructor(public categoryId: number | string) {}
}

export class ExportLoadDashboards {
  static readonly type = '[Admin Export State] Load dashboards';
  constructor(public categoryId: number | string) {}
}

export class AddAnalysisToExport {
  static readonly type = '[Admin Export Page] Add analysis to export';
  constructor(public analysis: Analysis) {}
}

export class RemoveAnalysisFromExport {
  static readonly type = '[Admin Export Page] Remove analysis from export';
  constructor(public analysis: Analysis) {}
}

export class AddAllAnalysesToExport {
  static readonly type =
    '[Admin Export Page] Add all analyses in selected category to export';
  constructor() {}
}

export class RemoveAllAnalysesFromExport {
  static readonly type =
    '[Admin Export Page] Remove all analyses in selected category from export';
  constructor() {}
}

export class AddDashboardToExport {
  static readonly type = '[Admin Export Page] Add dashboard to export';
  constructor(public dashboard: Dashboard) {}
}

export class RemoveDashboardFromExport {
  static readonly type = '[Admin Export Page] Remove Dashboard from export';
  constructor(public dashboard: Dashboard) {}
}

export class ClearExport {
  static readonly type = '[Admin Export Page] Clear export list';
  constructor() {}
}
