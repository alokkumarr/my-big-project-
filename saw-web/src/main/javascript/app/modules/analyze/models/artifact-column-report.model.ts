import ArtifactColumn from './artifact-column.model';

export default interface ArtifactColumnReport extends ArtifactColumn {
  joinEigible?:   boolean;
  dateFormat?:    string;
  hide?:          boolean;
  tableName?:     string;
  joinEligible?:  boolean;
}
