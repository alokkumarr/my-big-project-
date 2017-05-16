import values from 'lodash/values';
import get from 'lodash/get';
import find from 'lodash/find';

import template from './number-filter.component.html';

export const OPERATORS = {
  GREATER: {value: '>', shortName: 'GT', hint: 'Greater than'},
  LESS: {value: '<', shortName: 'LT', hint: 'Less than'},
  GREATER_OR_EQUAL: {value: '>=', shortName: 'GTE', hint: 'Greater than or equal to'},
  LESS_OR_EQUAL: {value: '<=', shortName: 'LTE', hint: 'Less than or equal to'},
  EQUALS: {value: '=', shortName: 'EQ', hint: 'Equal to'},
  NOT_EQUALS: {value: '<>', shortName: 'NEQ', hint: 'Not equal to'},
  BETWEEN: {value: 'between', shortName: 'BTW', hint: 'BETWEEN'}
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
          value: null, // int or double
          otherValue: null // int or double
        };
      }

      if (!this.filter.operator) {
        this.filter.operator = OPERATORS.EQUALS.value;
      }

      this.OPERATORS = OPERATORS;
      this.operators = values(OPERATORS);
    }

    hintFor(operator) {
      return get(
        find(this.operators, op => op.value === operator),
        'hint',
        null
      );
    }
  }
};
