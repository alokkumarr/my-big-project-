import {
  ArtifactColumnChart,
  AnalysisDSL,
  Sort,
  ArtifactColumnDSL
} from '../types';
import { Legend, Axis, Artifact } from '../../models';
import { LabelOptions } from '../../models';

export class DesignerSetData {
  static readonly type = '[Designer] Set data';
  constructor(public data: any[]) {}
}

export class DesignerResetState {
  static readonly type = '[Designer] Reset state on destroy';
  constructor() {}
}

export class DesignerLoadMetric {
  static readonly type = '[Designer] Load metric';
  constructor(public metric: { metricName: string; artifacts: Artifact[] }) {}
}

export class DesignerInitGroupAdapters {
  static readonly type = '[Designer] Init group adapters';
  constructor() {}
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

export class DesignerUpdateAnalysisMetadata {
  /* Use for only new DSL analyses. This should only be used for top level analysis fields
  like createdTime, id, semanticId etc. For nested fields like chartOptions, sipQuery etc.,
  use different action and handler. */
  static readonly type = '[Designer] Update top level metadata for analysis';
  constructor(public metadata: Partial<AnalysisDSL>) {}
}

export class DesignerUpdateAnalysisSubType {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update subType for analysis';
  constructor(public subType: string) {}
}

export class DesignerUpdateQuery {
  static readonly type = '[Designer] Update query string';
  constructor(public query: string) {}
}

export class DesignerUpdateEditMode {
  static readonly type =
    '[Designer] Update designer mode between query/designer mode';
  constructor(public designerEdit: boolean) {}
}

export class DesignerUpdateSorts {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update sorts for analysis';
  constructor(public sorts: Sort[]) {}
}

export class DesignerUpdateFilters {
  /* Use for only new DSL analyses. This is for filters in charts,  */
  static readonly type = '[Designer] Update filters for analysis';
  constructor(public filters: any) {}
}

export class DesignerUpdatebooleanCriteria {
  /* Use for only new DSL analyses. This is for booleanCriteria in charts,  */
  static readonly type = '[Designer] Update booleanCriteria for analysis';
  constructor(public booleanCriteria: string) {}
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

export class DesignerUpdateArtifactColumn {
  static readonly type = '[Designer] Update artifact column of analysis';
  constructor(
    public artifactColumn: Partial<ArtifactColumnDSL & ArtifactColumnChart>
  ) {}
}
export class DesignerApplyChangesToArtifactColumns {
  static readonly type = '[Designer] Apply changes to artifactColumns';
  constructor() {}
}

export class DesignerAddArtifactColumn {
  static readonly type = '[Designer] Add artifact column to analysis';
  constructor(
    public artifactColumn: Partial<ArtifactColumnDSL & ArtifactColumnChart>
  ) {}
}
export class DesignerRemoveArtifactColumn {
  static readonly type = '[Designer] Remove artifact column from analysis';
  constructor(
    public artifactColumn: Partial<ArtifactColumnDSL & ArtifactColumnChart>,
    public fieldArea?: string
  ) {}
}

export class DesignerCheckAggregateFilterSupport {
  static readonly type =
    '[Designer] Remove aggregate filters if no aggregate is selected';
}

/**
 * Merges metric's artifacts columns to analysis artifacts fields.
 * This fills out any missing data in both sides.
 *
 * @export
 * @class DesignerMergeMetricColumns
 */
export class DesignerMergeMetricColumns {
  static readonly type =
    '[Designer] Merge metric artifactColumns with analysis artifactColumns';
  constructor(public metricArtifactColumns: ArtifactColumnDSL[]) {}
}

export class DesignerMergeSupportsIntoAnalysis {
  static readonly type =
    '[Designer] Merge supports from metric data into analysis';
  constructor(public supports: any[]) {}
}
export class DesignerRemoveAllArtifactColumns {
  static readonly type = '[Designer] Remove all artifact columns from analysis';
  constructor() {}
}

export class DesignerJoinsArray {
  static readonly type = '[Designer] joins from analysis';
  constructor(public joins: any) {}
}

export class ConstructDesignerJoins {
  static readonly type = '[Designer] construct desinger joins';
  constructor(public analysis: any) {}
}

export class DesignerUpdateAggregateInSorts {
  static readonly type =
    '[Designer] update aggregate in sorts on change of aggregagte';
  constructor(public column: any) {}
}

export class DesignerUpdateQueryFilters {
  static readonly type =
    '[Designer] update query with runtime filters';
    constructor(public filters: any) {}
}
