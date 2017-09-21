import * as template from './filter-chips.component.html';
import style from './filter-chips.component.scss';

export const FilterChipsComponent = {
  template,
  styles: [style],
  bindings: {
    filters: '<',
    onClearAllFilters: '&',
    onFilterRemoved: '&'
  },
  controller: class FilterChipsController {

  }
};
