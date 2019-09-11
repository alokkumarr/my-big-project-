import {
  AnalysisChart,
  AnalysisReport,
  AnalysisDSL,
  Artifact,
  ArtifactColumnChart,
  ArtifactColumnPivot,
  ArtifactColumnReport,
  Filter,
  AnalysisType,
  Sort,
  Format,
  FilterModel,
  Region,
  ArtifactColumn,
  ArtifactColumnDSL,
  AnalysisChartDSL
} from './models';

export type ArtifactColumns = ArtifactColumn[];

export {
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumnReport,
  AnalysisDSL,
  Sort,
  Format,
  Filter,
  FilterModel,
  Artifact,
  AnalysisType,
  AnalysisReport,
  AnalysisChart,
  Region,
  ArtifactColumn,
  ArtifactColumnDSL,
  AnalysisChartDSL
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
  name?: string;
  description?: string;
  scheduled?: null;
  semanticId?: string;
  categoryId?: string;
  metricName?: string;
  type?: AnalysisType;
  chartType?: ChartType;
  supports?: string[];
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
  icon: { font: string };
  type: string;
  supportedTypes: string[];
}
