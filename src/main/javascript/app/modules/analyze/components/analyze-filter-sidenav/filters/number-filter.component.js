import values from 'lodash/values';

import template from './number-filter.component.html';

export const OPERATORS = {
  GREATER: 'x >',
  LESS: 'x <',
  GREATER_OR_EQUAL: 'x >=',
  LESS_OR_EQUAL: 'x <=',
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
      if (!this.filter.model) {
        this.filter.model = {
          operator: null, // one of the Operators constant
          value: null, // int or double
          otherValue: null // int or double
        };
      }

      this.OPERATORS = OPERATORS;
      this.operators = values(OPERATORS);
    }
  }
};
