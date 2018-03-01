import { Format } from './format.model';

export interface ArtifactColumn {
  checked?:       null | boolean | 'x', 'y', 'z', 'g';
  columnName:     string;
  table:          string;
  displayName:    string;
  filterEligible: boolean;
  type:           string;
  format:         Format | string | null;
  aliasName:      string;
  aggregate?:     string;
  name?:          string;
}

export interface ArtifactColumnChart extends ArtifactColumn {
  comboType?:     string;
  dateFormat?:    string;
}

export interface ArtifactColumnPivot extends ArtifactColumn {
  area?:          'row' | 'column' | 'data';
  areaIndex?:     number;
  dateInterval?:  string;
  // dateInterval is used instead of groupInterval
  dateFormat?:    string;
}

export interface ArtifactColumnReport extends ArtifactColumn {
  joinEigible?:   boolean;
  dateFormat?:    string;
  hide?:          boolean;
  tableName?:     string;
  joinEligible?:  boolean;
}
