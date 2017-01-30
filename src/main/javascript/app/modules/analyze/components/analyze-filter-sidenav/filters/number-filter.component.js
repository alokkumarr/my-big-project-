import template from './number-filter.component.html';

export const NumberFilterComponent = {
  template,
  bindings: {
    filter: '<'
  },
  controller: class NumberFilterController {
    constructor() {
      this.modifiers = ['Greater than', 'Lesser than', 'Equals', '>=, <=', '<>'];
    }
  }
}
