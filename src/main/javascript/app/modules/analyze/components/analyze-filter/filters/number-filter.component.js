import values from 'lodash/values';
import get from 'lodash/get';
import find from 'lodash/find';

import template from './number-filter.component.html';

export const OPERATORS = {
  GREATER: {
    value: 'gt',
    shortName: 'GT',
    hint: 'Greater than'
  },
  LESS: {
    value: 'lt',
    shortName: 'LT',
    hint: 'Less than'
  },
  GREATER_OR_EQUAL: {
    value: 'gte',
    shortName: 'GTE',
    hint: 'Greater than or equal to'
  },
  LESS_OR_EQUAL: {
    value: 'lte',
    shortName: 'LTE',
    hint: 'Less than or equal to'
  },
  EQUALS: {
    value: 'eq',
    shortName: 'EQ',
    hint: 'Equal to'
  },
  NOT_EQUALS: {
    value: 'neq',
    shortName: 'NEQ',
    hint: 'Not equal to'
  },
  BETWEEN: {
    value: 'btw',
    shortName: 'BTW',
    hint: 'BETWEEN'
  }
};

export const NumberFilterComponent = {
  template,
  bindings: {
    model: '<',
    onChange: '&'
  },
  controller: class NumberFilterController {
    constructor() {
      this.OPERATORS = OPERATORS;
      this.operators = values(OPERATORS);
    }

    $onInit() {
      this.tempModel = this.model || {
        value: null, // int or double
        otherValue: null, // int or double
        operator: OPERATORS.EQUALS.value
      };
    }

    onModelChange() {
      this.onChange({model: this.tempModel});
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
