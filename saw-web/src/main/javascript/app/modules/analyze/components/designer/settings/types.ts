import ArtifactColumnPivot from '../../../models/artifact-column-pivot.model';
import ArtifactColumnChart from '../../../models/artifact-column-chart.model';

export type ArtifactColumns = ArtifactColumnPivot[] | ArtifactColumnChart[];
export type ArtifactColumn = ArtifactColumnPivot | ArtifactColumnChart;

export interface IDEsignerSettingGroupAdapter {
  title: string;
  canAcceptArtifactColumn: (artifactColumn: ArtifactColumn) => boolean;
}
