import forEach from 'lodash/forEach';
import assign from 'lodash/assign';
import values from 'lodash/values';
import fpSortBy from 'lodash/fp/sortBy';
import find from 'lodash/find';
import isEmpty from 'lodash/isEmpty';
import mapValues from 'lodash/mapValues';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import fpMap from 'lodash/fp/map';
import fpToPairs from 'lodash/fp/toPairs';

import template from './pivot-grid.component.html';
import style from './pivot-grid.component.scss';
import summaryChooserTpl from './pivot-summary-chooser.component.html';

const SUMMARY_TYPES = [{
  label: 'Sum',
  value: 'sum',
  icon: 'icon-Sum'
}, {
  label: 'Average',
  value: 'avg',
  icon: 'icon-AVG'
}, {
  label: 'Mininum',
  value: 'min',
  icon: 'icon-MIN'
}, {
  label: 'Maximum',
  value: 'max',
  icon: 'icon-MAX'
}, {
  label: 'Count',
  value: 'count',
  icon: 'icon-group-by-column'
}];

const DEFAULT_SUMMARY_TYPE = SUMMARY_TYPES[0];

export const PivotGridComponent = {
  template,
  styles: [style],
  bindings: {
    updater: '<',
    sendFields: '&',
    mode: '@'
  },
  controller: class PivotGridController {
    constructor($timeout, $translate, FilterService, $compile, $scope) {
      'ngInject';
      this._$translate = $translate;
      this._$timeout = $timeout;
      this._$compile = $compile;
      this._$scope = $scope;
      this._FilterService = FilterService;
      this.warnings = {
        'Drop Data Fields Here': 'SELECT_DATA_FIELDS',
        'Drop Column Fields Here': 'SELECT_COLUMN_FIELDS',
        'Drop Row Fields Here': 'SELECT_ROW_FIELDS'
      };

      $translate(values(this.warnings)).then(translations => {
        this.warnings = mapValues(this.warnings, warning => translations[warning]);
      });
    }

    $onInit() {
      this.pivotGridOptions = assign({
        onInitialized: e => {
          this._gridInstance = e.component;
        }
      }, this.getDefaultOptions());

      if (this.mode === 'designer') {
        this.pivotGridOptions.onContentReady = () => {
          this.addSummaryChooserToDataFields();
        };
      }

      this._$timeout(() => {
        // have to repaint the grid because of the animation of the modal
        // if it's not repainted it appears smaller
        this._gridInstance.repaint();
        this.subscription = this.updater.subscribe(updates => this.update(updates));
        this.replaceWarningLables();
      }, 500);
    }

    $onDestroy() {
      this.subscription.unsubscribe();
    }

    addSummaryChooserToDataFields() {
      /* eslint-disable */
      this._$timeout(() => {
        const dataFields = this.getDataFields();
        if (isEmpty(dataFields)) {
          return;
        }
        let dataFieldNodesContainer = angular.element(document
          .querySelector('.dx-pivotgridfieldchooser-container .dx-col:nth-child(2) .dx-area:last-child div[group=data] div.dx-scrollable-content div.dx-area-field-container'));
        const dataFieldNodes = Array.from(dataFieldNodesContainer[0].childNodes);

        forEach(dataFieldNodes, (dataFieldNode, key) => {
          const dataField = dataFields[key];
          const fieldLabel = angular.element(dataFieldNode)[0].firstChild.innerText;

          angular.element(angular.element(dataFieldNode)[0].firstChild).css({
            display: 'inline',
            verticalAlign:'middle'
          });
          if (!dataField.summaryType) {
            dataField.summaryType = DEFAULT_SUMMARY_TYPE.value;
          }
          angular.element(dataFieldNode).append(this.getSummaryChooserMenu(dataField));
        });
        this._$scope.$apply();
      });
      /* eslint-enable */
    }

    getSummaryChooserMenu(dataField) {
      const scope = this._$scope.$new(true);
      scope.SUMMARY_TYPES = SUMMARY_TYPES;
      scope.dataField = dataField;
      scope.selectedSummaryType = find(SUMMARY_TYPES,
        summaryType => dataField.summaryType === summaryType.value);

      scope.openMenu = ($mdMenu, ev) => {
        $mdMenu.open(ev);
      };

      scope.selectSummaryType = (summaryType, dataField) => {
        const pivotGridDataSource = this._gridInstance.getDataSource();

        scope.selectedSummaryType = summaryType;
        pivotGridDataSource.field(dataField.name, {
          summaryType: summaryType.value
        });

      };
      return this._$compile(summaryChooserTpl)(scope);
    }

    replaceWarningLables() {
      this._$timeout(() => {
        /* eslint-disable angular/document-service */
        const headers = document.querySelectorAll('.dx-empty-area-text');
        forEach(headers, header => {
          if (this.warnings[header.innerHTML]) {
            header.innerHTML = this.warnings[header.innerHTML];
          }
        });
        /* eslint-enable angular/document-service */
      });
    }

    getDataFields() {
      const pivotGridDataSource = this._gridInstance.getDataSource();
      return fpPipe(
        fpFilter(field => field.area === 'data'),
        fpSortBy('areaIndex')
      )(pivotGridDataSource.fields());
    }

    update(updates) {
      /* eslint-disable no-unused-expressions */
      updates.dataSource && this.updateDataSource(updates.dataSource);
      updates.field && this.updateField(updates.field);
      updates.filters && this.updateFilters(updates.filters);
      updates.sorts && this.updateSorts(updates.sorts);
      updates.getFields && this.sendFields({fields: this._gridInstance.getDataSource().fields()});
      updates.export && this.exportToExcel();
      /* eslint-disable no-unused-expressions */

      this.replaceWarningLables();
    }

    exportToExcel() {
      this._gridInstance.exportToExcel();
    }

    updateDataSource(dataSource) {
      this._gridInstance.option('dataSource', dataSource);
    }

    updateField(field) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      pivotGridDataSource.field(field.dataField, field.modifierObj);
      pivotGridDataSource.load();
    }

    updateFilters(filters) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      forEach(filters, filter => {
        if (this._FilterService.isFilterModelNonEmpty(filter.model)) {
          const filterValues = fpPipe(
            fpToPairs,
            fpFilter(pair => pair[1]),
            fpMap(pair => pair[0])
          )(filter.model);
          pivotGridDataSource.field(filter.name, {
            filterType: 'include',
            filterValues
          });
        } else {
          pivotGridDataSource.field(filter.name, {
            filterType: null,
            filterValues: null
          });
        }
      });
      pivotGridDataSource.load();
    }

    updateSorts(sorts) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      // reset other sorts
      forEach(pivotGridDataSource.fields(), field => {
        if (field.sortOrder) {
          pivotGridDataSource.field(field.dataField, {
            sortOrder: null
          });
        }
      });

      forEach(sorts, sort => {
        pivotGridDataSource.field(sort.field.dataField, {
          sortOrder: sort.order
        });
      });
      pivotGridDataSource.load();
    }

    getDefaultOptions() {
      return {
        rowHeaderLayout: 'tree',
        allowSortingBySummary: false,
        showBorders: true,
        allowSorting: false,
        allowFiltering: false,
        allowExpandAll: false,
        fieldChooser: {
          enabled: false
        },
        fieldPanel: {
          visible: true,
          showColumnFields: true, // hides the column field area
          showRowFields: true, // hides the row field area
          showDataFields: true, // hides the data field area
          showFilterFields: false, // hides the filter field area
          allowFieldDragging: false
        },
        export: {
          enabled: false,
          fileName: 'export'
        },
        dataSource: {
          store: [],
          fields: []
        }
      };
    }
  }
};
