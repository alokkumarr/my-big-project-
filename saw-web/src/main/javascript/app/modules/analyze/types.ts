import Analysis from './models/analysis.model';

export type DesignerMode = 'edit' | 'fork' | 'new';
export type AnalysisType = 'report' | 'chart' | 'pivot';
export type ChartType = 'line' | 'column' | 'bar' | 'area' | 'pie' | 'scatter' | 'bubble';

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
