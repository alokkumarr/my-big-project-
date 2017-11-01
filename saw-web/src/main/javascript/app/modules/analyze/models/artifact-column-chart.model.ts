import ArtifactColumn from './artifact-column.model';

export default interface ArtifactColumnChart extends ArtifactColumn {
  comboType?:     string;
  dateFormat?:    string;
}
