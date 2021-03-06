import { Format } from './format.model';
import { Region } from './region.model';

export interface ArtifactColumn {
  checked?: null | boolean | 'x' | 'y' | 'z' | 'g';
  y;
  z;
  g;
  columnName: string;
  dataField?: string;
  table: string;
  displayName: string;
  filterEligible: boolean;
  type: string;
  expression?: string;
  formula?: string;
  format: Format | string | null;
  aliasName: string;
  alias?: string;
  aggregate?:
    | 'sum'
    | 'avg'
    | 'min'
    | 'max'
    | 'count'
    | 'distinctCount'
    | 'percentage'
    | 'percentagebyrow';
  name?: string;
  geoType?: string;
  seriesColor?: string;
  colorSetFromPicker?: boolean;
}

export interface ArtifactColumnChart extends ArtifactColumn {
  area?: 'x' | 'y' | 'z' | 'g';
  comboType?: string;
  limitValue?: number;
  limitType?: string;
  dateFormat?: string;
  groupInterval?: string;
  geoType?: string;
  geoRegion?: Region;
  displayType?: string;
}

export interface ArtifactColumnPivot extends ArtifactColumn {
  area?: 'row' | 'column' | 'data';
  areaIndex?: number;
  groupInterval?: string;
  dateFormat?: string;
}

export interface ArtifactColumnReport extends ArtifactColumn {
  joinEigible?: boolean;
  dateFormat?: string;
  hide?: boolean;
  artifactsName?: string;
  joinEligible?: boolean;
  visibleIndex?: number;
  visible: boolean;
}
