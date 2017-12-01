import Format from './format.model';

export interface ArtifactColumn {
  columnName:     string;
  table:          string;
  displayName:    string;
  filterEligible: boolean;
  type:           string;
  format:         Format | null;
  aliasName:      string;
  aggregate?:     string;
  name?:          string;
}

export interface ArtifactColumnChart extends ArtifactColumn {
  checked:        null | 'x', 'y', 'z', 'g';
  comboType?:     string;
  dateFormat?:    string;
}

export interface ArtifactColumnPivot extends ArtifactColumn {
  checked:        null | boolean;
  area?:          'row' | 'column' | 'data';
  areaIndex?:     number;
  dateInterval?:  string;
  // dateInterval is used instead of groupInterval
  dateFormat?:    string;
}

export interface ArtifactColumnReport extends ArtifactColumn {
  checked:        null | boolean;
  joinEigible?:   boolean;
  dateFormat?:    string;
  hide?:          boolean;
  tableName?:     string;
  joinEligible?:  boolean;
}
