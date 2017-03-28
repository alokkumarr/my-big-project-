import 'devextreme/ui/pivot_grid';
import forEach from 'lodash/forEach';
import map from 'lodash/map';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import keys from 'lodash/keys';
import assign from 'lodash/assign';
import compact from 'lodash/compact';

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
      this.dataSource = {};
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
      this._AnalyzeService.getPivotData().then(data => {
        this.normalizedData = data;
        this.deNormalizedData = this._PivotService.denormalizeData(data);
        const fields = this.getFields();
        this.dataSource = {
          store: this.deNormalizedData,
          fields
        };

        this._gridInstance.option('dataSource', this.dataSource);
        this.fieldChooserOptions = {
          texts: {
            allFields: 'All',
            columnFields: 'Columns',
            dataFields: 'Data',
            rowFields: 'Rows',
            filterFields: 'Filter'
          },
          width: 500,
          heght: 400,
          layout: 1,
          dataSource: this._gridInstance.getDataSource()
        };
      });

      this.pivotGridOptions = assign({
        onInitialized: e => {
          this._gridInstance = e.component;
        }
      }, this.getPivotGridOptions());
    }

// filters
    openFilterSidenav() {
      if (!this.filters.possible) {
        this.filters.possible = this.getFieldToFilterMapper()(this.getFields());
      }
      this._FilterService.openFilterSidenav(this.filters.possible);
    }
    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(filters);
      console.log(this.filters);
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

    // onFilterRemoved(filter) {
    //   filter.model = null;
    //   this.filterGridData();
    // }
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

    openSortModal(ev) {
      const tpl = '<analyze-report-sort model="model"></analyze-report-sort>';
      if (!this.sortFields) {
        this.sortFields = this.getFieldToSortFieldMapper()(this.getFields());
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
          const pivotGridDataSource = this._gridInstance.getDataSource();
          forEach(sorts, sort => {
            pivotGridDataSource.field(sort.field.dataField, {
              sortOrder: sort.order
            });
          });
          pivotGridDataSource.load();
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
              defaultOptions: this.getPivotGridOptions(),
              pivotState: this._gridInstance.getDataSource().state()
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
      this.model.pivotState = this._gridInstance.getDataSource().state();
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
        area: 'row'
      }, {
        caption: 'Product',
        width: 120,
        dataType: 'string',
        dataField: 'row_level_2',
        area: 'row'
      }, {
        caption: 'Date2',
        dataField: 'column_level_1',
        dataType: 'date',
        width: 120,
        area: 'column'
      }, {
        caption: 'Date',
        dataField: 'column_level_2',
        dataType: 'date',
        width: 120,
        area: 'column'
      }, {
        caption: 'Total Price',
        dataField: 'total_price',
        dataType: 'double',
        summaryType: 'sum',
        format: 'currency',
        area: 'data'
      }];
    }

    getPivotGridOptions() {
      return {
        allowSortingBySummary: false,
        allowSorting: false,
        allowFiltering: false,
        allowExpandAll: true,
        fieldChooser: {
          enabled: false
        },
        fieldPanel: {
          visible: true,
          showColumnFields: false, // hides the column field area
          showRowFields: true, // hides the row field area
          showDataFields: true, // hides the data field area
          showFilterFields: false, // hides the filter field area
          allowFieldDragging: false,
          texts: {
            columnFieldArea: 'Date',
            rowFieldArea: 'Date'
          }
        },
        export: {
          enabled: true,
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
