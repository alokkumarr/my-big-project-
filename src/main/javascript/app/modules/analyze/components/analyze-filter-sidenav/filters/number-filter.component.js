import values from 'lodash/values';

import template from './number-filter.component.html';

export const OPERATORS = {
  GREATER: 'x >',
  LESS: 'x <',
  EQUALS: 'x =',
  NOT_EQUALS: 'x <>',
  BETWEEN: '<= x <='
};

export const NumberFilterComponent = {
  template,
  bindings: {
    filter: '<'
  },
  controller: class NumberFilterController {
    constructor() {
      this.OPERATORS = OPERATORS;
      this.operators = values(OPERATORS);
    }
  }
};
