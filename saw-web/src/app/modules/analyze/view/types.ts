import { Analysis, AnalysisChart } from '../types';

export interface AnalyzeViewActionEvent {
  action: 'edit' | 'fork' | 'delete' | 'publish' | 'export' | 'execute';
  analysis?: Analysis;
  requestExecution?: boolean;
}
export { Analysis, AnalysisChart };
