import {
  Format,
  Artifact,
  AnalysisChart,
  AnalysisReport,
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumnReport,
  Sort,
  Filter,
  FilterModel
} from './models';

export type ArtifactColumns = ArtifactColumnPivot[] | ArtifactColumnChart[] | ArtifactColumnReport[];
export type ArtifactColumn = ArtifactColumnPivot | ArtifactColumnChart | ArtifactColumnReport;

export {
  ArtifactColumnPivot,
  ArtifactColumnChart,
  Sort,
  Format,
  Filter,
  FilterModel,
  Artifact
};


export type DesignerMode = 'edit' | 'fork' | 'new';
export type AnalysisType = 'report' | 'chart' | 'pivot';
export type ChartType = 'line' | 'column' | 'bar' | 'area' | 'pie' | 'scatter' | 'bubble';

export type Analysis = AnalysisChart | AnalysisReport;

export type AnalysisStarter = {
  name: string,
  description: string,
  scheduled: null,
  semanticId: string;
  categoryId: string;
  metricName: string;
  type: AnalysisType;
  chartType?: ChartType;
}

export type AnalysisDialogData = {
  designerMode: DesignerMode,
  analysisStarter?: AnalysisStarter,
  analysis?: Analysis
};

export type DesignerToolbarAciton = 'description' | 'sort' | 'preview' | 'filter' | 'save';
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
  isSaveSuccessful?: boolean;
}
