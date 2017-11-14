import LabelOptions from './label-options.model';
import Legend from './legend.model';
import Axis from './axis.model';
import Analysis from './analysis.model';

export default interface AnalysisChart extends Analysis {
  legend?:          Legend;
  chartType?:       string;
  labelOptions?:    LabelOptions;
  xAxis?:           Axis;
  yAxis?:           Axis;
}
