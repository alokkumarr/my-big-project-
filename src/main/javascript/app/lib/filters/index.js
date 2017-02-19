import HighlightFilter from './highlight.filter';

export const FiltersModule = 'FiltersModule';

angular
  .module(FiltersModule, [])
  .filter('highlight', HighlightFilter);
