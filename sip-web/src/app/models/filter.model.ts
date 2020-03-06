import { FilterModel } from './filter-model.model';

export interface Filter {
  isRuntimeFilter: boolean;
  isOptional: boolean;
  isGlobalFilter?: boolean;
  isAggregationFilter?: boolean;
  aggregate?: string;
  tableName: string;
  artifactsName?: string;
  columnName: string;
  model?: FilterModel;
  type: string;
  displayName?: string;
  description?: string;
}
