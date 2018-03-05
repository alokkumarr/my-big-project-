import * as map from 'lodash/map';
import * as forEach from 'lodash/forEach';
import * as template from './date-filter.component.html';

import {CUSTOM_DATE_PRESET_VALUE, DATE_PRESETS} from '../../../consts';

export const DateFilterComponent = {
  template,
  bindings: {
    model: '<',
    options: '<',
    onChange: '&'
  },
  controller: class DateFilterController {
    constructor($filter, $translate) {
      this._$filter = $filter;
      this.CUSTOM_DATE_PRESET_VALUE = CUSTOM_DATE_PRESET_VALUE;
      this.presets = DATE_PRESETS;
      $translate(map(this.presets, 'keyword')).then(translations => {
        forEach(this.presets, preset => {
          preset.label = translations[preset.keyword];
        });
      });
    }
    $onInit() {
      /* this.model has string values. Change them to actual date objects
      to be able to work with md-datepicker first.
      */
      this.model = this.model || {};
      this.tempModel = {
        preset: (this.model.gte || this.model.lte) ? CUSTOM_DATE_PRESET_VALUE : this.model.preset || null,
        gte: this.model.gte ? new Date(this.model.gte) : '',
        lte: this.model.lte ? new Date(this.model.lte) : ''
      };
    }

    onPresetSelected() {
      this.tempModel.gte = null;
      this.tempModel.lte = null;
      this.onChange({model: {preset: this.tempModel.preset}});
    }

    onModelChange() {
      this.updatedDate = this.model || {
        gte: '',
        lte: ''
      };
      this.dateFormat = 'yyyy-MM-dd HH:ss:mm';
      this.updatedDate.gte = this._$filter('date')(this.tempModel.gte, this.dateFormat);
      this.updatedDate.lte = this._$filter('date')(this.tempModel.lte, this.dateFormat);
      this.updatedDate.preset = CUSTOM_DATE_PRESET_VALUE;
      this.onChange({model: this.updatedDate});
    }
  }
};
