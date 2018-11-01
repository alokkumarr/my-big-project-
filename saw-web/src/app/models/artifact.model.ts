import {
  ArtifactColumnReport,
  ArtifactColumnChart,
  ArtifactColumnPivot
} from './artifact-column.model';

export interface Artifact {
  artifactName:      string;
  columns:           ArtifactColumnReport[] | ArtifactColumnChart[] | ArtifactColumnPivot[];
  artifactPosition?: number[];
  data?:             any[];
}
