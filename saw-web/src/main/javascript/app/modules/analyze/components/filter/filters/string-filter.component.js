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
    }

    onPresetSelected() {
      if (this.tempModel.operator === 'IsIn' || this.tempModel.operator === 'IsNotIn') {
        this.displayChips = true;
      } else {
        this.displayChips = false;
      }
      this.tempModel.value = null;
      this.onChange({model: {operator: this.tempModel.operator}});
    }

    onModelChange() {
      this.updatedDate = this.model || {
        value: ''
      };
      this.updatedDate.stringValue = this.tempModel.value;
      this.updatedDate.operator = this.tempModel.operator;
      this.onChange({model: this.updatedDate});
    }
  }
};
