import { Format } from './format.model';
import { Region } from './region.model';

export interface ArtifactColumn {
  checked?: null | boolean | 'x';
  y;
  z;
  g;
  columnName: string;
  table: string;
  displayName: string;
  filterEligible: boolean;
  type: string;
  format: Format | string | null;
  aliasName: string;
  alias?: string;
  aggregate?: 'sum' | 'avg' | 'min' | 'max' | 'count' | 'percentage';
  name?: string;
}

export interface ArtifactColumnChart extends ArtifactColumn {
  area?: 'x' | 'y' | 'z' | 'g';
  comboType?: string;
  limitValue?: number;
  limitType?: string;
  dateFormat?: string;
  dateInterval?: string;
  geoType?: 'state' | 'country' | 'zip';
  regionIdentifier?: 'name' | 'iso-a2' | 'iso-a3' | 'postal-code' | 'fips';
  region?: Region;
}

export interface ArtifactColumnPivot extends ArtifactColumn {
  area?: 'row' | 'column' | 'data';
  areaIndex?: number;
  dateInterval?: string;
  // dateInterval is used instead of groupInterval
  dateFormat?: string;
}

export interface ArtifactColumnReport extends ArtifactColumn {
  joinEigible?: boolean;
  dateFormat?: string;
  hide?: boolean;
  tableName?: string;
  joinEligible?: boolean;
  visibleIndex?: number;
  visible: boolean;
}
