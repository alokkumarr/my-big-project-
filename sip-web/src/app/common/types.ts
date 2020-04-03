import { Analysis, AnalysisDSL } from '../models';

export {
  AnalysisType,
  AnalysisDSL,
  AnalysisTypeFE,
  AnalysisChart,
  ArtifactColumnReport,
  SqlBuilderChart,
  AnalysisChartDSL,
  ChartOptions,
  QueryDSL,
  Filter,
  FilterModel,
  ArtifactColumnDSL
} from '../models';
export interface ConfirmDialogData {
  title: string;
  content: string;
  primaryColor?: string;
  positiveActionLabel: string;
  negativeActionLabel: string;
}

export const isDSLAnalysis = (
  analysis: Analysis | AnalysisDSL
): analysis is AnalysisDSL => {
  return (<AnalysisDSL>analysis).sipQuery !== undefined;
};
