import {
  AnalysisChart,
  AnalysisReport,
  Artifact,
  ArtifactColumnChart,
  ArtifactColumnPivot,
  ArtifactColumnReport,
  Filter,
  AnalysisType,
  Sort,
  Format,
  FilterModel,
  Region
} from './models';

export type ArtifactColumns =
  | ArtifactColumnPivot[]
  | ArtifactColumnChart[]
  | ArtifactColumnReport[];
export type ArtifactColumn =
  | ArtifactColumnPivot
  | ArtifactColumnChart
  | ArtifactColumnReport;

export {
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumnReport,
  Sort,
  Format,
  Filter,
  FilterModel,
  Artifact,
  AnalysisType,
  AnalysisReport,
  AnalysisChart,
  Region
};

export type DesignerMode = 'edit' | 'fork' | 'new';
export type ChartType =
  | 'line'
  | 'column'
  | 'bar'
  | 'area'
  | 'pie'
  | 'scatter'
  | 'bubble';

export type Analysis = AnalysisChart | AnalysisReport;

export interface AnalysisStarter {
  name: string;
  description: string;
  scheduled: null;
  semanticId: string;
  categoryId: string;
  metricName: string;
  type: AnalysisType;
  chartType?: ChartType;
}

export interface AnalysisDialogData {
  designerMode: DesignerMode;
  analysisStarter?: AnalysisStarter;
  analysis?: Analysis;
}

export type DesignerToolbarAciton =
  | 'description'
  | 'sort'
  | 'preview'
  | 'filter'
  | 'save'
  | 'saveAndClose'
  | 'refresh'
  | 'modeToggle';
export interface IToolbarActionData {
  action: DesignerToolbarAciton;
  artifactColumns?: ArtifactColumns;
  artifacts?: Artifact[];
  sorts?: Sort[];
  filters?: Filter[];
  booleanCriteria?: string;
  description?: string;
  analysis?: Analysis;
}

export interface IToolbarActionResult {
  sorts?: Sort[];
  description?: string;
  filters?: Filter[];
  booleanCriteria?: string;
  analysis?: Analysis;
  action?: string;
}

export interface IAnalysisMethod {
  label: string;
  icon: {font: string};
  type: string;
  supportedTypes: string[];
}
