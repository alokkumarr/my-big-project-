import LabelOptions from './label-options.model';
import Legend from './legend.model';
import Axis from './axis.model';
import Analyze from './analyze.model';

export default interface AnalyzeChart extends Analyze {
  legend?:          Legend;
  chartType?:       string;
  labelOptions?:    LabelOptions;
  xAxis?:           Axis;
  yAxis?:           Axis;
}
