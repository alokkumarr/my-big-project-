import { Analysis, AnalysisChart } from '../types';

export type AnalyzeViewActionEvent = {
  action: 'edit' | 'fork' | 'delete' | 'publish' | 'export' | 'execute';
  analysis?: Analysis;
}
export { Analysis, AnalysisChart };
