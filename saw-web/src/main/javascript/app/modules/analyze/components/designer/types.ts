import ArtifactColumnPivot from '../../models/artifact-column-pivot.model';
import ArtifactColumnChart from '../../models/artifact-column-chart.model';
import Artifact from '../../models/artifact.model';
import ArtifactColumn from '../../models/artifact-column.model';

export type ArtifactColumns = ArtifactColumnPivot[] | ArtifactColumnChart[];
export type ArtifactColumn = ArtifactColumnPivot | ArtifactColumnChart;
export {
  ArtifactColumnPivot,
  ArtifactColumnChart
};

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
