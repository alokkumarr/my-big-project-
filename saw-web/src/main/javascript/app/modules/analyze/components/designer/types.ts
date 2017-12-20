import {
  ArtifactColumn,
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

export interface IDEsignerSettingGroupAdapter {
  title: string;
  marker: string;
  artifactColumns: Array<ArtifactColumn>;
  canAcceptArtifactColumn: (artifactColumn: ArtifactColumn) => boolean;
  transform: (artifactColumn: ArtifactColumn) => void;
  reverseTransform: (ArtifactColumn: ArtifactColumn) => void;
}
