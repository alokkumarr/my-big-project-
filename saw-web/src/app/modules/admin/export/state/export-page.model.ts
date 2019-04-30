import { MenuItem } from '../../../../common/state/common.state.model';
import { Dashboard } from '../../../observe/models/dashboard.interface';
import { Analysis, AnalysisDSL } from '../../../analyze/models';

export interface ExportPageModel {
  selectedModule: string;
  selectedCategory: MenuItem;

  /* Metrics by metric id */
  metrics: {
    [id: string]: any;
  };

  /* Analyses in currently selected category */
  categoryAnalyses: (Analysis | AnalysisDSL)[];

  /* Dashboards in currently selected category */
  categoryDashboards: Dashboard[];

  shouldExportMetric: boolean;

  /* Data marked for export */
  exportData: {
    analyses: (Analysis | AnalysisDSL)[];
    dashboards: Dashboard[];
    metrics: any[];
  };
}
