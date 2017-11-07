import FilterModel from './filter-model.model';

export default interface Filter {
  isRuntimeFilter: boolean;
  tableName:       string;
  columnName:      string;
  model?:          FilterModel;
  type:            string;
}
