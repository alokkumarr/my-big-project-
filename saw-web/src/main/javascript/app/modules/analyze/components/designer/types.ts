import {
  SqlBuilder,
  SqlBuilderPivot,
  SqlBuilderChart,
  SqlBuilderReport,
  SqlBuilderEsReport,
  Join,
  AnalysisType
} from '../../models';
import { JsPlumbCanvasChangeEvent } from '../../../../common/components/js-plumb/types';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisReport,
  Sort,
  Filter,
  FilterModel,
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumnReport,
  ArtifactColumn,
  ArtifactColumns,
  IToolbarActionData,
  DesignerToolbarAciton,
  IToolbarActionResult,
  Artifact,
  Format,
  AnalysisDialogData
} from '../../types';

export {
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumnReport,
  Analysis,
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
  AnalysisDialogData
};

export type ArtifactColumnFilter = {
  keyword: string;
  types: ('number' | 'date' | 'string')[];
};

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
  canAcceptArtifactColumn: (
    groupAdapter: IDEsignerSettingGroupAdapter,
    groupAdapters: Array<IDEsignerSettingGroupAdapter>
  ) => (artifactColumn: ArtifactColumn) => boolean;
  // a callback to possibly transform the artifactColumn added to a group
  transform: (artifactColumn: ArtifactColumn) => void;
  // a callback to undo any transformations done to the element
  reverseTransform: (artifactColumn: ArtifactColumn) => void;
  // a callback to change soomething when the indexes change in artifactColumns
  onReorder: (artifactColumns: ArtifactColumns) => void;
}
export type DesignerChangeEvent = {
  subject:
    | 'format'
    | 'aggregate'
    | 'dateInterval'
    | 'aliasName'
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
    | 'visibleIndex'
    | 'submitQuery'
    | 'chartTitle'
    | 'changeQuery';
  column?: ArtifactColumn;
  data?: any;
};

export type DesignerSaveEvent = {
  requestExecution: boolean;
  analysis: Analysis;
};
