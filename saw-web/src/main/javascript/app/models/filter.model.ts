import { FilterModel } from './filter-model.model';

export interface Filter {
  isRuntimeFilter: boolean;
  isOptional: boolean;
  isGlobalFilter?: boolean;
  tableName: string;
  columnName: string;
  model?: FilterModel;
  type: string;
}
