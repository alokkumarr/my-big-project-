import Analyze from './analyze.model';

export default interface AnalyzeReport extends Analyze {
  query?:           string;
  queryManual?:     string;
}
