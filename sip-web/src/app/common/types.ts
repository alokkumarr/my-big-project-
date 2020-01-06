import { Analysis, AnalysisDSL } from '../models';

export {
  AnalysisType,
  AnalysisDSL,
  AnalysisTypeFE,
  AnalysisChart,
  ArtifactColumnReport,
  SqlBuilderChart,
  AnalysisChartDSL,
  ChartOptions
} from '../models';
export interface ConfirmDialogData {
  title: string;
  content: string;
  positiveActionLabel: string;
  negativeActionLabel: string;
}

export const isDSLAnalysis = (
  analysis: Analysis | AnalysisDSL
): analysis is AnalysisDSL => {
  return (<AnalysisDSL>analysis).sipQuery !== undefined;
};
