import {
  ArtifactColumnPivot,
  ArtifactColumnChart
} from '../../models/artifact-column.model';
import {
  SqlBuilder,
  SqlBuilderPivot
} from '../../models/sql-builder.model';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisType
} from '../../types';

export {
  ArtifactColumnPivot,
  ArtifactColumnChart,
  Analysis,
  DesignerMode,
  AnalysisStarter,
  AnalysisType,
  SqlBuilder
};

export type ArtifactColumns = ArtifactColumnPivot[] | ArtifactColumnChart[];
export type ArtifactColumn = ArtifactColumnPivot | ArtifactColumnChart;

export type ArtifactColumnFilter = {
  keyword: string,
  type: '' | 'number' | 'date' | 'string';
};

export type PivotArea = 'data' | 'row' | 'column';

export interface IMoveFieldToEvent {
  name: 'moveTo',
  artifactColumn: ArtifactColumn,
  toIndex: number,
  toGroup: IDEsignerSettingGroupAdapter
}

export interface IMoveFieldFromEvent {
  name: 'moveFrom',
  artifactColumn: ArtifactColumn,
  fromGroup: IDEsignerSettingGroupAdapter
}
export interface IDEsignerSettingGroupAdapter {
  title: string;
  marker: string;
  artifactColumns: ArtifactColumns;
  canAcceptArtifactColumn: (groupAdapter: IDEsignerSettingGroupAdapter) =>
    (artifactColumn: ArtifactColumn) => boolean;
  transform: (artifactColumn: ArtifactColumn) => void;
  reverseTransform: (ArtifactColumn: ArtifactColumn) => void;
}
