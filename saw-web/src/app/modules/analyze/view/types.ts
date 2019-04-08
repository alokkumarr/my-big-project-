import { Analysis, AnalysisDSL, AnalysisChart } from '../types';

export interface AnalyzeViewActionEvent {
  action: 'edit' | 'fork' | 'delete' | 'publish' | 'export' | 'execute';
  analysis?: Analysis | AnalysisDSL;
  requestExecution?: boolean;
}
export { Analysis, AnalysisDSL, AnalysisChart };
