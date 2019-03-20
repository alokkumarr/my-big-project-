import { AnalysisType } from './analysis-type.model';
import { Legend } from './legend.model';
import { LabelOptions } from './label-options.model';
import { Axis } from './axis.model';
import { Schedule } from './schedule.model';

export interface FilterModelDSL {
  operator: string;
  otherValue: any;
  value: any;
}

export interface FilterDSL {
  artifactsName: string;
  columnName: string;
  isGlobalFilter: string;
  isOptional: boolean;
  isRuntimeFilter: string;
  model: FilterModelDSL;
  type: string;
}

export interface SortDSL {
  aggregate: string;
  artifacts: string;
  columnName: string;
  order: 'asc' | 'desc';
  type: string;
}

export interface FieldDSL {
  aggregate: string;
  alias: string;
  area: string;
  columnName: string;
  dataField: string;
  displayName: string;
  groupInterval: string;
  name: string;
  type: string;
}

export interface ArtifactDSL {
  artifactsName: string;
  fields: FieldDSL[];
}

export interface StorageDSL {
  dataStore: string;
  storageType: string;
}

export interface QueryDSL {
  artifacts: ArtifactDSL[];
  booleanCriteria: string;
  filters: FilterDSL[];
  sorts: SortDSL[];
  store: StorageDSL;
}

// All fields marked todo have not yet been implemented in
// backend in new API.
export interface AnalysisDSL {
  categoryId: number; // todo
  createdBy?: string; // email
  createdTime?: number;
  crondetails?: any; // todo
  customerCode: string;
  dataSecurityKey?: string; // todo
  description: string; // todo
  id: string;
  modifiedBy: string; // email
  modifiedTime: string;
  module: string;
  name: string;
  parentAnalysisId?: string; // todo
  parentCategoryId?: string | number; // todo
  parentLastModified?: number; // todo
  projectCode: string;
  schedule?: Schedule; // todo
  scheduled: null; // todo
  scheduleHuman?: string; // todo
  semanticId: string;
  type: AnalysisType;

  sipQuery: QueryDSL;
}

export interface AnalysisChartDSL {
  chartTitle: string; // todo
  chartType: string; // todo
  isInverted: boolean; // todo
  labelOptions?: LabelOptions; // todo
  legend?: Legend; // todo
  xAxis?: Axis; // todo
  yAxis?: Axis; // todo
}
