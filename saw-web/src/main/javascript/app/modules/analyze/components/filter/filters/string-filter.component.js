import * as map from 'lodash/map';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as template from './string-filter.component.html';

export const StringFilterComponent = {
  template,
  bindings: {
    model: '<',
    onChange: '&'
  },
  controller: class StringFilterController {
    constructor($mdConstant, $filter, $translate) {
      'ngInject';
      this.isEmpty = isEmpty;
      const semicolon = 186;
      this.separatorKeys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA, semicolon];
      this.presets = [{
        value: 'EQ',
        keyword: 'EQUALS'
      }, {
        value: 'NE',
        keyword: 'NOT_EQUAL'
      }, {
        value: 'IsIn',
        keyword: 'IS_IN'
      }, {
        value: 'IsNotIn',
        keyword: 'IS_NOT_IN'
      }, {
        value: 'Contains',
        keyword: 'CONTAINS'
      }, {
        value: 'SW',
        keyword: 'STARTS_WITH'
      }, {
        value: 'LQ',
        keyword: 'ENDS_WITH'
      }];
      $translate(map(this.presets, 'keyword')).then(translations => {
        forEach(this.presets, preset => {
          preset.label = translations[preset.keyword];
        });
      });
    }

    $onInit() {
      this.keywords = this.model || {modelValues: []};
    }
  }
};
