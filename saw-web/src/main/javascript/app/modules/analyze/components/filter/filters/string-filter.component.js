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
      this.displayChips = false;
      this.separatorKeys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA, semicolon];
      this.presets = [{
        value: 'EQ',
        keyword: 'EQUALS'
      }, {
        value: 'NEQ',
        keyword: 'NOT_EQUAL'
      }, {
        value: 'ISIN',
        keyword: 'IS_IN'
      }, {
        value: 'ISNOTIN',
        keyword: 'IS_NOT_IN'
      }, {
        value: 'CONTAINS',
        keyword: 'CONTAINS'
      }, {
        value: 'SW',
        keyword: 'STARTS_WITH'
      }, {
        value: 'EW',
        keyword: 'ENDS_WITH'
      }];
      $translate(map(this.presets, 'keyword')).then(translations => {
        forEach(this.presets, operator => {
          operator.label = translations[operator.keyword];
        });
      });
    }

    $onInit() {
      this.keywords = this.model || {modelValues: []};
      this.model = this.model || {};
      this.tempModel = {};
      this.tempModel.value = this.keywords.modelValues[0];
    }

    onPresetSelected() {
      this.tempModel.value = null;
      this.keywords.modelValues = [];
    }

    onModelChange() {
      this.keywords.modelValues = [];
      this.keywords.modelValues.push(this.tempModel.value);
      this.onChange({model: this.keywords});
    }
  }
};
