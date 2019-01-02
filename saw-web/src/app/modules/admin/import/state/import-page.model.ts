import { Analysis } from '../../../analyze/models';

export interface ImportAnalysisRecord {
  analysis: Analysis;
  checked: boolean;
  log: string;
  overwrite: boolean;
  selectedCategory: string | number;
}

export interface ImportAnalysesDictionary {
  [categoryId: string]: {
    [analysisReference: string]: Analysis;
  };
}

export interface ImportPageModel {
  /* Global analysis category */
  analysisGlobalCategory: string | number;

  /* List of json files added to import page by user */
  importFiles: File[];

  /* Map of all metrics in the system for easy lookup by name */
  metrics: { [metricName: string]: any };

  /* Map of analyses (instead of list) for easy lookup by name, metric and type.
   Contains analyses from selected global analyze category only. */
  referenceAnalyses: ImportAnalysesDictionary;

  /* All the categories (including subcategories) for modules */
  categories: {
    analyze: any[];
    observe: any[];
  };

  /* Final data to be imported to system */
  importData: {
    analyses: ImportAnalysisRecord[];
  };
}
