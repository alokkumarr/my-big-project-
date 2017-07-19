import {truncateFilter} from './truncate.filter';
import {changeCaseFilter} from './change-case.filter';
import HighlightFilter from './highlight.filter';

export const CommonFilterModule = 'CommonModule.Filter';

angular
  .module(CommonFilterModule, [])
  .filter('truncate', truncateFilter)
  .filter('changeCase', changeCaseFilter)
  .filter('highlight', HighlightFilter);
