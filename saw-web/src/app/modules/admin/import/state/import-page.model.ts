import { Analysis } from '../../../analyze/models';

export class ImportAnalysisRecord {
  analysis: Analysis;
  checked: boolean;
  log: string;
  overwrite: boolean;
  selectedCategory: string | number;
}

export interface ImportPageModel {
  analysisGlobalCategory: string | number;
  importFiles: File[];
  metrics: { [metricName: string]: any };
  categories: {
    analyze: any[];
    observe: any[];
  };
  importData: {
    analyses: ImportAnalysisRecord[];
  };
}
