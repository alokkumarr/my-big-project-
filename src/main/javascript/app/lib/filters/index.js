import {truncateFilter} from './truncate.filter';
import {changeCaseFilter} from './change-case.filter';
import HighlightFilter from './highlight.filter';

export const FiltersModule = 'FiltersModule';

angular
  .module(FiltersModule, [])
  .filter('truncate', truncateFilter)
  .filter('changeCase', changeCaseFilter)
  .filter('highlight', HighlightFilter);
