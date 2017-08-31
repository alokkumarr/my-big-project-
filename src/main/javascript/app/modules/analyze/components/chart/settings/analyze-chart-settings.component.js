import find from 'lodash/find';
import forEach from 'lodash/forEach';
import map from 'lodash/map';
import clone from 'lodash/clone';
import unset from 'lodash/unset';

import template from './analyze-chart-settings.component.html';
import style from './analyze-chart-settings.component.scss';
import {AGGREGATE_TYPES, DEFAULT_AGGREGATE_TYPE, AGGREGATE_TYPES_OBJ, NUMBER_TYPES} from '../../../consts';

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
      if (this.multipleYAxes.enabled === false || marker !== 'y') {
        this.setRadioButtonSelection(axisOptions, selectedAttr, marker);
        this.markSelected();
      } else {
        this.setCheckBoxSelection(axisOptions, selectedAttr);
      }
      this.onChange({settings: this.settings});
    }

    setRadioButtonSelection(axisOptions, selectedAttr, marker) {
      forEach(axisOptions, attr => {
        if (selectedAttr === attr) {
          attr.checked = marker;
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
      } else {
        target.checked = false;
        // when deselecting an attribute, unset the aggregate type
        if (selectedAttr.aggregate) {
          unset(selectedAttr, 'aggregate');
          unset(target, 'aggregate');
        }
      }
    }

    onSelectAggregateType(aggregateType, artifactColumn, container) {
      artifactColumn.aggregate = aggregateType.value;
      if (container === 'checkbox') {
        const target = find(this.settings.yaxis, ({columnName}) => columnName === artifactColumn.columnName);
        target.aggregate = aggregateType.value;
      }
    }
  }
};
