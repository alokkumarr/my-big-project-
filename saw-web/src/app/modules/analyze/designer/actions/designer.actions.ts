import { ArtifactColumnChart, AnalysisDSL } from '../types';
import { Legend, Axis } from '../../models';
import { LabelOptions } from '../../models';

export class DesignerInitGroupAdapters {
  static readonly type = '[Designer] Init group adapters';
  constructor(
    public artifactColumns: ArtifactColumnChart[],
    public analysisType: string,
    public analysisSubType: string
  ) {}
}

export class DesignerClearGroupAdapters {
  static readonly type = '[Designer] Clear group adapters';
  constructor() {}
}

export class DesignerAddColumnToGroupAdapter {
  static readonly type = '[Designer] Add column to group adapter';
  constructor(
    public artifactColumn: ArtifactColumnChart,
    public columnIndex: number,
    public adapterIndex: number
  ) {}
}
export class DesignerRemoveColumnFromGroupAdapter {
  static readonly type = '[Designer] Remove column from group adapter';
  constructor(public columnIndex: number, public adapterIndex: number) {}
}

export class DesignerMoveColumnInGroupAdapter {
  static readonly type = '[Designer] Move column in group adapter';
  constructor(
    public previousColumnIndex: number,
    public currentColumnIndex: number,
    public adapterIndex: number
  ) {}
}

export class DesignerInitNewAnalysis {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Initialise analysis object for create';
  constructor(public analysis: AnalysisDSL) {}
}

export class DesignerInitEditAnalysis {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Initialise analysis object for edit';
  constructor(public analysis: AnalysisDSL) {}
}

export class DesignerInitForkAnalysis {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Initialise analysis object for fork';
  constructor(public analysis: AnalysisDSL) {}
}

export class DesignerUpdateAnalysisCategory {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update category for analysis';
  constructor(public category: string | number) {}
}

export class DesignerUpdateAnalysisChartType {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chartType for analysis';
  constructor(public chartType: string) {}
}

export class DesignerUpdateAnalysisChartTitle {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chart title for analysis';
  constructor(public chartTitle: string) {}
}

export class DesignerUpdateAnalysisChartInversion {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chart inversion for analysis';
  constructor(public isInverted: boolean) {}
}

export class DesignerUpdateAnalysisChartLegend {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chart legend for analysis';
  constructor(public legend: Legend) {}
}

export class DesignerUpdateAnalysisChartLabelOptions {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chart label options for analysis';
  constructor(public labelOptions: LabelOptions) {}
}

export class DesignerUpdateAnalysisChartXAxis {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chart x axis for analysis';
  constructor(public xAxis: Axis) {}
}

export class DesignerUpdateAnalysisChartYAxis {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chart y axis for analysis';
  constructor(public yAxis: Axis) {}
}
