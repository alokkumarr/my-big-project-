import Analysis from './analysis.model';

export default interface AnalysisReport extends Analysis {
  query?:           string;
  queryManual?:     string;
}
