import * as isEmpty from 'lodash/isEmpty';
import * as template from './string-filter.component.html';

export const StringFilterComponent = {
  template,
  bindings: {
    model: '<',
    onChange: '&'
  },
  controller: class StringFilterController {
    constructor($mdConstant) {
      'ngInject';
      this.isEmpty = isEmpty;
      const semicolon = 186;
      this.separatorKeys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA, semicolon];
    }

    $onInit() {
      this.keywords = this.model || {modelValues: []};
    }
  }
};
