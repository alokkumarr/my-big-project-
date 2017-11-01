import ArtifactColumnReport from './artifact-column-report.model';
import ArtifactColumnChart from './artifact-column-chart.model';
import ArtifactColumnPivot from './artifact-column-pivot.model';

export default interface Artifact {
  artifactName:      string;
  columns:           ArtifactColumnReport[] | ArtifactColumnChart[] | ArtifactColumnPivot[];
  artifactPosition?: number[];
  data?:             any[];
}
