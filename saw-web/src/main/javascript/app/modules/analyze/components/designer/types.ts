
import {
  SqlBuilder,
  SqlBuilderPivot
} from '../../models/sql-builder.model';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisType,
  Sort,
  Filter,
  FilterModel,
  ArtifactColumnPivot,
  ArtifactColumnChart,
  ArtifactColumn,
  ArtifactColumns,
  IToolbarActionData,
  DesignerToolbarAciton,
  IToolbarActionResult,
  Artifact
} from '../../types';

export {
  ArtifactColumnPivot,
  ArtifactColumnChart,
  Analysis,
  DesignerMode,
  AnalysisStarter,
  AnalysisType,
  SqlBuilder,
  Sort,
  Filter,
  FilterModel,
  Artifact,
  ArtifactColumn,
  ArtifactColumns,
  IToolbarActionData,
  DesignerToolbarAciton,
  IToolbarActionResult
};


export type ArtifactColumnFilter = {
  keyword: string,
  type: '' | 'number' | 'date' | 'string';
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

