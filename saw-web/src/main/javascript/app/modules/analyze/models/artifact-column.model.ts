import Format from './format.model';

export default interface ArtifactColumn {
  columnName:     string;
  checked:        null | boolean | string;
  table:          string;
  displayName:    string;
  filterEligible: boolean;
  type:           string;
  format:         Format | null;
  aliasName:      string;
  aggregate?:     string;
  name?:          string;
}
