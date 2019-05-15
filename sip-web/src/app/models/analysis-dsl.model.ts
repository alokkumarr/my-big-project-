import { AnalysisType } from './analysis-type.model';
import { Legend } from './legend.model';
import { LabelOptions } from './label-options.model';
import { Axis } from './axis.model';
import { Schedule } from './schedule.model';
import { Filter } from './filter.model';
import { Sort } from './sort.model';
import { Format } from '../modules/analyze/types';

export interface ArtifactColumnDSL {
  aggregate?: string;
  alias: string;
  area: string;
  columnName: string;
  dataField: string;
  displayType?: string;
  dateFormat?: string;
  format?: string | Format;
  displayName: string;
  groupInterval: string;
  limitType?: string; // todo
  limitValue?: any; // todo
  name: string;
  type: string;
  table: string;
}

export interface ArtifactDSL {
  artifactsName: string;
  fields: ArtifactColumnDSL[];
}

export interface StorageDSL {
  dataStore: string;
  storageType: string;
}

export interface QueryDSL {
  artifacts: ArtifactDSL[];
  booleanCriteria: string;
  filters: Filter[];
  sorts: Sort[];
  orderByColumns?: Sort[]; // this is legacy field. Should be removed after migration
  store: StorageDSL;
}

// All fields marked todo have not yet been implemented in
// backend in new API.

// Don't use this base interface directly
export interface AnalysisBaseDSL {
  artifacts?: any[]; // this field needs to be removed from analysis structure before saving
  category: number | string;
  createdBy?: string; // email
  createdTime?: number;
  crondetails?: any; // todo
  customerCode: string;
  dataSecurityKey?: string; // todo
  description: string; // todo
  edit: boolean; // todo - this field needs to be removed and worked around
  id: string;
  metricName?: string; // required for exporting analyses from admin module
  modifiedBy: string; // email
  modifiedTime: string;
  module: string;
  name: string;
  parentAnalysisId?: string; // todo
  parentCategoryId?: string | number; // todo
  parentLastModified?: number; // todo
  projectCode: string;
  query?: string;
  queryManual?: string;
  saved: boolean; // todo
  schedule?: Schedule; // todo
  scheduled: null; // todo
  scheduleHuman?: string; // todo
  semanticId: string;
  type: AnalysisType;

  sipQuery: QueryDSL;
}

export interface AnalysisChartDSL extends AnalysisBaseDSL {
  chartOptions: {
    chartType: string;
    chartTitle: string;
    isInverted: boolean;
    legend?: Legend;
    labelOptions?: LabelOptions;
    xAxis?: Axis;
    yAxis?: Axis;
  };
}

export type AnalysisPivotDSL = AnalysisBaseDSL;
export interface AnalysisMapDSL extends AnalysisBaseDSL {
  mapOptions: {
    mapType: string;
    mapStyle: string;
    legend?: Legend;
    labelOptions?: LabelOptions;
  };
}

export type AnalysisDSL = AnalysisChartDSL | AnalysisMapDSL;
