import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as forEach from 'lodash/forEach';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as unset from 'lodash/unset';

import * as template from './analyze-chart-settings.component.html';
import style from './analyze-chart-settings.component.scss';
import {AGGREGATE_TYPES, DEFAULT_AGGREGATE_TYPE,
  AGGREGATE_TYPES_OBJ, NUMBER_TYPES, DATE_TYPES} from '../../../consts';

const DATE_FORMATS = [{
  value: 'MMMM Do YYYY, h:mm:ss a',
  label: 'September 1st 2017, 1:28:31 pm'
}, {
  value: 'MMM Do YYYY',
  label: ' Sep 1st 2017'
}, {
  value: 'MMM YYYY',
  label: 'September 2017'
}, {
  value: 'MM YYYY',
  label: '09 2017'
}];

const DEFAULT_DATE_FORMAT = DATE_FORMATS[1];

export const AnalyzeChartSettingsComponent = {
  template,
  styles: [style],
  bindings: {
    settings: '<?',
    viewOptions: '<?',
    onChange: '&',
    chartType: '<'
  },
  controller: class AnalyzeChartSettingsController {
    constructor(AnalyzeService, FilterService, $mdSidenav, $scope) {
      'ngInject';

      this.AGGREGATE_TYPES = AGGREGATE_TYPES;
      this.AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
      this.DEFAULT_AGGREGATE_TYPE = DEFAULT_AGGREGATE_TYPE;
      this.DATE_TYPES = DATE_TYPES;
      this.DATE_FORMATS = DATE_FORMATS;

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
      this._$scope = $scope;
      this.selected = {};
      this.multipleYAxes = {
        enabled: false,
        fields: []
      };
    }

    $onInit() {
      this.multipleYAxes.enabled = this.chartType !== 'bubble' && this.chartType !== 'pie';
      this._clearWatcher = this._$scope.$watch(() => this.settings, newVal => {
        if (newVal) {
          this.markSelected();
          this._clearWatcher();
        }
      });
      this.groupRemovalAction = {
        icon: 'icon-close',
        class: 'md-warn',
        isVisible: () => this.selected.g,
        callback: () => {
          this.selected.g.checked = false;
          this.selected.g = null;
          this.inputChanged(this.settings.groupBy, this.selected.g, 'g');
        }
      };
    }

    markSelected() {
      if (this.multipleYAxes.enabled) {
        this.multipleYAxes.fields = map(this.settings.yaxis, field => {
          const clonedField = clone(field);
          if (clonedField.checked && clonedField.checked !== 'y') {
            clonedField.disabled = true;
          } else if (clonedField.checked === 'y') {
            clonedField.checked = true;
          }
          return clonedField;
        });
      } else {
        this.selected.y = find(this.settings.yaxis, attr => attr.checked === 'y');
      }
      this.selected.x = find(this.settings.xaxis, attr => attr.checked === 'x');
      this.selected.g = find(this.settings.groupBy, attr => attr.checked === 'g');
      this.selected.z = find(this.settings.zaxis, attr => attr.checked === 'z');
    }

    inputChanged(axisOptions, selectedAttr, marker) {
      if (selectedAttr && (this.multipleYAxes.enabled === false || marker !== 'y')) {
        this.setRadioButtonSelection(axisOptions, selectedAttr, marker);
        this.markSelected();
      } else if (selectedAttr) {
        this.setCheckBoxSelection(axisOptions, selectedAttr);
      }
      this.onChange({settings: this.settings});
    }

    setRadioButtonSelection(axisOptions, selectedAttr, marker) {
      forEach(axisOptions, attr => {
        if (selectedAttr === attr) {
          attr.checked = marker;
          if (marker === 'x' && DATE_TYPES.includes(selectedAttr.type)) {
            attr.dateFormat = DEFAULT_DATE_FORMAT.value;
          }
          if (['y', 'z'].includes(marker) && NUMBER_TYPES.includes(selectedAttr.type)) {
            attr.aggregate = DEFAULT_AGGREGATE_TYPE.value;
          }
        } else if (attr.checked === marker) {
          attr.checked = false;
          if (attr.aggregate) {
            unset(attr, 'aggregate');
          }
        }
      });
    }

    setCheckBoxSelection(axisOptions, selectedAttr) {
      const target = find(axisOptions, ({columnName}) => columnName === selectedAttr.columnName);
      if (selectedAttr.checked === true) {
        target.checked = 'y';
        // when selecting an axis set a default aggregate type
        if (!selectedAttr.aggregate) {
          selectedAttr.aggregate = DEFAULT_AGGREGATE_TYPE.value;
          target.aggregate = DEFAULT_AGGREGATE_TYPE.value;
        }
        if (!selectedAttr.comboType) {
          if (['line', 'column', 'area'].includes(this.chartType)) {
            target.comboType = this.chartType;
          }
          if (['combo', 'bar'].includes(this.chartType)) {
            target.comboType = 'column';
          }
          if (this.chartType === 'combo') {
            if (filter(this.multipleYAxes.fields, 'checked').length > 1) {
              target.comboType = 'line';
            }
          }
        }
      } else {
        target.checked = false;
        // when deselecting an attribute, unset the aggregate type
        if (selectedAttr.aggregate) {
          unset(selectedAttr, 'aggregate');
          unset(target, 'aggregate');
        }
      }
    }

    onSelectAggregateType(aggregateType, attr, container) {
      attr.aggregate = aggregateType.value;
      if (container === 'checkbox') {
        const target = find(this.settings.yaxis, ({columnName}) => columnName === attr.columnName);
        target.aggregate = aggregateType.value;
      }
    }

    onSelectDateFormat(dateFormat, attr) {
      attr.dateFormat = dateFormat.value;
      this.onChange({settings: this.settings});
    }
  }
};
