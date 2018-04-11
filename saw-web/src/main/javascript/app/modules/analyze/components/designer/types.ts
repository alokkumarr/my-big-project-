
import {
  SqlBuilder,
  SqlBuilderPivot,
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
  Format
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
  JsPlumbCanvasChangeEvent
};

export type ArtifactColumnFilter = {
  keyword: string,
  types: ('number' | 'date' | 'string')[];
};

export type PivotArea = 'data' | 'row' | 'column';

export interface IDEsignerSettingGroupAdapter {
  title: string;
  marker: string;
  type: AnalysisType;
  artifactColumns: ArtifactColumns;
  canAcceptArtifactColumn: (groupAdapter: IDEsignerSettingGroupAdapter) =>
    (artifactColumn: ArtifactColumn) => boolean;
  // a callback to possibly transform the artifactColumn added to a group
  transform: (artifactColumn: ArtifactColumn) => void;
  // a callback to undo any transformations done to the element
  reverseTransform: (artifactColumn: ArtifactColumn) => void;
  // a callback to change soomething when the indexes change in artifactColumns
  onReorder: (artifactColumns: ArtifactColumns) => void;
}

export type DesignerChangeEvent = {
  subject: 'format'    |
    'aggregate'        |
    'dateInterval'     |
    'aliasName'        |
    'sort'             |
    'filter'           |
    // adding | removing | changing fields in the field chooser for pivot grid and chart designer
    'selectedFields'   |
    'joins'            |
    'artifactPosition' |
    'column'           |
    'visibleIndex'     |
    'submitQuery'      |
    'changeQuery';
  column?: ArtifactColumnReport;
}
