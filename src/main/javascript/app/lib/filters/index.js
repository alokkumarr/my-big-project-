import {truncateFilter} from './truncate.filter';
import HighlightFilter from './highlight.filter';

export const FiltersModule = 'FiltersModule';

angular
  .module(FiltersModule, [])
  .filter('truncate', truncateFilter)
  .filter('highlight', HighlightFilter);
