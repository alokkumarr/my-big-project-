import {truncateFilter} from './truncate.filter';

export const FiltersModule = 'FiltersModule';

angular
  .module(FiltersModule, [])
  .filter('truncate', truncateFilter);
