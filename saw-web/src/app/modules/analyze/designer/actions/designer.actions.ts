import { ArtifactColumnChart, AnalysisDSL } from '../types';

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

export class DesignerUpdateAnalysisMetadata {
  /* Use for only new DSL analyses. This should only be used for top level analysis fields
  like createdTime, id, semanticId etc. For nested fields like chartOptions, sipQuery etc.,
  use different action and handler. */
  static readonly type = '[Designer] Update top level metadata for analysis';
  constructor(public metadata: Partial<AnalysisDSL>) {}
}

export class DesignerUpdateAnalysisChartType {
  /* Use for only new DSL analyses */
  static readonly type = '[Designer] Update chartType for analysis';
  constructor(public chartType: string) {}
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