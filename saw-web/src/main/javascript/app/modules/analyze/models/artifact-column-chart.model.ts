import ArtifactColumn from './artifact-column.model';

export default interface ArtifactColumnChart extends ArtifactColumn {
  checked:        null | 'x', 'y', 'z', 'g';
  comboType?:     string;
  dateFormat?:    string;
}
