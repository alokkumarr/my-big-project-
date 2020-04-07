import {
  SqlBuilder,
  SqlBuilderPivot,
  SqlBuilderChart,
  SqlBuilderReport,
  SqlBuilderEsReport,
  Join,
  AnalysisType,
  MapSettings,
  LabelOptions,
  Legend,
  Axis,
  AnalysisChartDSL,
  AnalysisMapDSL
} from '../models';
import { JsPlumbCanvasChangeEvent } from '../../../common/components/js-plumb/types';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisDSL,
  AnalysisChart,
  AnalysisReport,
  Sort,
  Filter,
  FilterModel,
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumnReport,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnDSL,
  IToolbarActionData,
  DesignerToolbarAciton,
  IToolbarActionResult,
  Artifact,
  Format,
  AnalysisDialogData,
  Region
} from '../types';
import { isDSLAnalysis } from 'src/app/common/types';

export {
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumnReport,
  ArtifactColumnDSL,
  Analysis,
  AnalysisDSL,
  AnalysisChart,
  AnalysisReport,
  DesignerMode,
  AnalysisStarter,
  AnalysisType,
  SqlBuilder,
  SqlBuilderPivot,
  SqlBuilderChart,
  SqlBuilderReport,
  SqlBuilderEsReport,
  Sort,
  Filter,
  FilterModel,
  Artifact,
  ArtifactColumn,
  ArtifactColumns,
  IToolbarActionData,
  DesignerToolbarAciton,
  IToolbarActionResult,
  Format,
  Join,
  JsPlumbCanvasChangeEvent,
  AnalysisDialogData,
  Region,
  MapSettings,
  isDSLAnalysis,
  AnalysisChartDSL,
  AnalysisMapDSL
};

export interface ArtifactColumnFilter {
  keyword: string;
  types: {
    number: boolean;
    date: boolean;
    string: boolean;
    geo: boolean;
    coordinate: boolean;
  };
  adapters: boolean[];
}

export type PivotArea = 'data' | 'row' | 'column';
export type ChartArea = 'x' | 'y' | 'z' | 'g';

export interface IDEsignerSettingGroupAdapter {
  title: string;
  marker: string;
  type: AnalysisType;
  maxAllowed?: (
    groupAdapter: IDEsignerSettingGroupAdapter,
    groupAdapters: Array<IDEsignerSettingGroupAdapter>
  ) => number;
  artifactColumns: ArtifactColumns;
  canAcceptArtifactColumnOfType: (artifactColumn: ArtifactColumn) => boolean;
  canAcceptArtifactColumn: (
    groupAdapter: IDEsignerSettingGroupAdapter,
    groupAdapters: Array<IDEsignerSettingGroupAdapter>
  ) => (artifactColumn: ArtifactColumn) => boolean;
  // a callback to possibly transform the artifactColumn added to a group
  transform: (
    artifactColumn: ArtifactColumn,
    columns?: ArtifactColumn[],
    options?: any
  ) => void;
  // a callback to undo any transformations done to the element
  reverseTransform: (artifactColumn: ArtifactColumn) => void;
  // a callback to change soomething when the indexes change in artifactColumns
  onReorder: (artifactColumns: ArtifactColumns) => void;
}
export interface DesignerChangeEvent {
  subject:
    | 'format'
    | 'alias'
    | 'aliasname'
    | 'aggregate'
    | 'dateInterval'
    | 'alias'
    | 'sort'
    | 'filter'
    | 'filterRemove'
    | 'comboType'
    | 'labelOptions'
    | 'legend'
    | 'inversion'
    // adding | removing | changing fields in the field chooser for pivot grid and chart designer
    | 'selectedFields'
    | 'joins'
    | 'artifactPosition'
    | 'column'
    | 'removeColumn'
    | 'reorder'
    | 'submitQuery'
    | 'chartTitle'
    | 'fetchLimit'
    | 'changeQuery'
    | 'geoRegion'
    | 'chartType'
    | 'expressionUpdated'
    | 'derivedMetricAdded'
    | 'addNewDerivedMetric'
    | 'updateDerivedMetric'
    | 'mapSettings'
    | 'filters'
    | 'seriesColorChange';
  column?: ArtifactColumn;
  data?: any;
}

export interface DesignerSaveEvent {
  requestExecution: boolean;
  analysis: Analysis | AnalysisDSL;
  publishTo?: string;
}

export interface DesignerStateModel {
  groupAdapters: IDEsignerSettingGroupAdapter[];
  analysis: AnalysisDSL;
  data: any[];
  metric: {
    metricName: string;
    artifacts: Artifact[];
  };
}

export interface DSLChartOptionsModel {
  chartType: string;
  chartTitle: string;
  isInverted: boolean;
  legend?: Legend;
  labelOptions?: LabelOptions;
  xAxis: Axis;
  yAxis: Axis;
}
