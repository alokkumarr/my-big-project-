import { MenuItem } from '../../../../common/state/common.state.model';
import { Dashboard } from '../../../observe/models/dashboard.interface';
import { Analysis } from '../../../analyze/models';

export interface ExportPageModel {
  selectedModule: string;
  selectedCategory: MenuItem;

  /* Analyses in currently selected category */
  categoryAnalyses: Analysis[];

  /* Dashboards in currently selected category */
  categoryDashboards: Dashboard[];

  shouldExportMetric: boolean;

  /* Data marked for export */
  exportData: {
    analyses: Analysis[];
    dashboards: Dashboard[];
    metrics: any[];
  };
}
