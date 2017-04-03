import 'devextreme/ui/pivot_grid';
import forEach from 'lodash/forEach';
import map from 'lodash/map';
import groupBy from 'lodash/groupBy';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import keys from 'lodash/keys';
import concat from 'lodash/concat';
import assign from 'lodash/assign';
import compact from 'lodash/compact';
import isEmpty from 'lodash/isEmpty';

import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';

export const AnalyzePivotComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzePivotController {
    constructor($mdDialog, $timeout, PivotService, AnalyzeService, FilterService) {
      this._$mdDialog = $mdDialog;
      this._PivotService = PivotService;
      this._$timeout = $timeout;
      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;

      this.deNormalizedData = [];
      this.normalizedData = [];
      this.dataSource = {};
      this.settings = {};
      this.sorts = [];
      this.filters = {
        possible: null,
        selected: null
      };
      this.sortFields = null;
      this._gridInstance = null;
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      this._$timeout(() => {
        // have to repaint the grid because of the animation of the modal
        // if it's not repainted it appears smaller
        this._gridInstance.repaint();
      }, 500);

      this.loadPivotData();

      this.pivotGridOptions = assign({
        onInitialized: e => {
          this._gridInstance = e.component;
        }
      }, this.getDefaultOptions());
    }

    loadPivotData() {
      this._AnalyzeService.getPivotData().then(data => {
        if (isEmpty(this.settings)) {
          // new analysis
          this.fields = this.getFields();
          this.settings = groupBy(this.fields, 'area');
          this.hideDataFieldsOnFirstLoad();
          this.sorts = [];
        } else {
          // editing existing analysis
          this.settings = this.model.settings;
          this.fields = concat(this.settings.row, this.settings.column, this.settings.data);
          this.sorts = this.model.sorts || [];
          this.applySorts(this.sorts);
        }
        this.normalizedData = data;
        this.deNormalizedData = this._PivotService.denormalizeData(data);
        this.dataSource = {
          store: this.deNormalizedData,
          fields: this.fields
        };
        // this.toggleColumnFields(true);
        this._gridInstance.option('dataSource', this.dataSource);
        // this.toggleColumnFields(false);
        // this.hideColumnFields();
      });
    }

    toggleColumnFields(visible) {
      forEach(this.settings.column, column => {
        column.visible = visible;
      });
    }

    hideColumnFields() {
      const pivotGridDataSource = this._gridInstance.getDataSource();
      forEach(this.settings.column, column => {
        pivotGridDataSource.field(column.dataField, {
          visible: false
        });
      });
      pivotGridDataSource.load();
    }

    applyFieldSettings(field) {
      const pivotGridDataSource = this._gridInstance.getDataSource();
      const modifierObj = {
        visible: field.visible
      };

      if (field.area !== 'row' && field.area !== 'column') {
        // if it's not row, or column, it should be data
        modifierObj.summaryType = field.summaryType;
        // setting only the visibility is not eough for hiding data fields
        modifierObj.area = field.visible ? 'data' : null;
      }

      pivotGridDataSource.field(field.dataField, modifierObj);
      pivotGridDataSource.load();
    }

    hideDataFieldsOnFirstLoad() {
      // setting visible to false on datafields is not enough so we have to make them null
      forEach(this.settings.data, datum => {
        datum.area = null;
      });
    }

// filters
    openFilterSidenav() {
      if (!this.filters.possible) {
        this.filters.possible = this.getFieldToFilterMapper()(this.fields);
      }
      this._FilterService.openFilterSidenav(this.filters.possible);
    }
    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(filters);
      this.filterGridData();
    }

    filterGridData() {
      const pivotGridDataSource = this._gridInstance.getDataSource();
      forEach(this.filters.selected, filter => {
        const filterValues = compact(keys(filter.model));
        pivotGridDataSource.field(filter.name, {
          filterType: 'include',
          filterValues
        });
      });
      pivotGridDataSource.load();
    }

    onClearAllFilters() {
      this.clearFilters();
    }

    clearFilters() {
      const pivotGridDataSource = this._gridInstance.getDataSource();
      this.filters.possible = this._FilterService.getFilterClearer()(this.filters.possible);
      forEach(this.filters.selected, filter => {
        pivotGridDataSource.field(filter.name, {
          filterType: null,
          filterValues: null
        });
      });
      this.filters.selected = [];
      pivotGridDataSource.load();
    }
// END filters

    getFieldToSortFieldMapper() {
      return fpPipe(
        fpFilter(field => field.area === 'row'),
        fpMap(field => {
          return {
            type: field.dataType,
            dataField: field.dataField,
            label: field.caption
          };
        })
      );
    }

    getFieldToFilterMapper() {
      return fpPipe(
        fpFilter(field => field.area === 'row'),
        fpMap(field => {
          return {
            name: field.dataField,
            type: field.dataType,
            items: field.dataType === 'string' ?
            this._PivotService.getUniquesFromNormalizedData(this.normalizedData, field.dataField) :
            null,
            label: field.caption,
            model: null
          };
        })
      );
    }

    applySorts(sorts) {
      const pivotGridDataSource = this._gridInstance.getDataSource();
      forEach(sorts, sort => {
        pivotGridDataSource.field(sort.field.dataField, {
          sortOrder: sort.order
        });
      });
      pivotGridDataSource.load();
    }

    openSortModal(ev) {
      const tpl = '<analyze-report-sort model="model"></analyze-report-sort>';
      if (!this.sortFields) {
        this.sortFields = this.getFieldToSortFieldMapper()(this.fields);
      }

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = {
              fields: this.sortFields,
              sorts: map(this.sorts, sort => clone(sort))
            };
          },
          targetEvent: ev,
          fullscreen: true,
          autoWrap: false,
          multiple: true
        })
        .then(sorts => {
          this.sorts = sorts;
          this.applySorts(sorts);
        });
    }

    openDescriptionModal(ev) {
      const tpl = '<analyze-report-description model="model" on-save="onSave($data)"></analyze-report-description>';

      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.model = {
            description: this.model.description
          };

          scope.onSave = data => {
            this.model.description = data.description;
          };
        },
        autoWrap: false,
        focusOnOpen: false,
        multiple: true,
        targetEvent: ev,
        clickOutsideToClose: true
      });
    }

    openPreviewModal(ev) {
      const tpl = '<analyze-pivot-preview model="model"></analyze-pivot-preview>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = {
              pivot: this.model,
              dataSource: this.dataSource,
              defaultOptions: this.getDefaultOptions()
            };
          },
          targetEvent: ev,
          fullscreen: true,
          autoWrap: false,
          multiple: true
        });
    }

    openSaveModal(ev) {
      this.model.sorts = this.sorts;
      this.model.settings = this.settings;
      this.model.filters = map(this.filters.selected, this._FilterService.getFrontEnd2BackEndFilterMapper());
      const tpl = '<analyze-report-save model="model" on-save="onSave($data)"></analyze-report-save>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = clone(this.model);

            scope.onSave = data => {
              this.model.id = data.id;
              this.model.name = data.name;
              this.model.description = data.description;
            };
          },
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          targetEvent: ev,
          clickOutsideToClose: true
        });
    }

    getFields() {
      // const obj = deNormalizedData[0];
      // const objKeys = keys(obj);
      // const fields = map(objKeys, key => {
      //   return {
      //     caption: key,
      //     dataField: key,
      //     width: 120,
      //     area: this.getArea(key),
      //     format: key.includes('price') ? 'currency' : null
      //   };
      // });
      return [{
        caption: 'Affiliate Name',
        width: 120,
        dataType: 'string',
        dataField: 'row_level_1',
        areaIndex: 0,
        visible: false,
        area: 'row'
      }, {
        caption: 'Product',
        width: 120,
        dataType: 'string',
        dataField: 'row_level_2',
        areaIndex: 1,
        visible: false,
        area: 'row'
      }, {
        caption: 'Date Month',
        dataField: 'column_level_1',
        format: datum => {
          const date = new Date(datum);
          return `${date.getFullYear()}-${date.getMonth()}`;
        },
        areaIndex: 0,
        width: 120,
        visible: false,
        area: 'column'
      }, {
        caption: 'Date Day',
        dataField: 'column_level_2',
        areaIndex: 1,
        dataType: 'string',
        format: datum => {
          const date = new Date(datum);
          return `${date.getDay()}`;
        },
        width: 120,
        visible: false,
        area: 'column'
      }, {
        caption: 'Total Price',
        dataField: 'total_price',
        dataType: 'double',
        summaryType: 'sum',
        format: 'currency',
        visible: false,
        area: 'data'
      }];
    }

    getDefaultOptions() {
      return {
        allowSortingBySummary: false,
        allowSorting: false,
        allowFiltering: false,
        allowExpandAll: true,
        fieldChooser: {
          enabled: true
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
          fileName: 'Sales'
        },
        dataSource: {
          store: [],
          fields: []
        }
      };
    }

    getArea(key) {
      if (key.includes('row_level')) {
        return 'row';
      } else if (key.includes('column_level')) {
        return 'column';
      }
      return 'data';
    }
  }
};
