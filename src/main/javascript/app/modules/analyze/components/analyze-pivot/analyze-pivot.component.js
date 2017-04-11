import 'devextreme/ui/pivot_grid';
import forEach from 'lodash/forEach';
import map from 'lodash/map';
import fpGroupBy from 'lodash/fp/groupBy';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import isEmpty from 'lodash/isEmpty';
import split from 'lodash/split';
import first from 'lodash/first';
import find from 'lodash/find';
import fpPick from 'lodash/fp/pick';
import {BehaviorSubject} from 'rxjs';

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
      this.filters = [];
      this.sortFields = null;
      this._gridInstance = null;
      this.pivotGridUpdater = new BehaviorSubject({});
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      this.loadPivotData();
    }

    loadPivotData() {
      this._AnalyzeService.getPivotData().then(data => {
        this.normalizedData = data;
        this.deNormalizedData = this._PivotService.denormalizeData(data);
        this.fields = this.getFields();
        this.sortFields = this.getFieldToSortFieldMapper()(this.fields);

        if (isEmpty(this.model.settings)) {
          // new analysis
          this.settings = this.getSettingsFromFields(this.fields);
          this.filters = this.getFieldToFilterMapper()(this.fields);
          this.sorts = [];
        } else {
          // editing existing analysis
          this.settings = this.model.settings;
          this.putSettingsDataInFields(this.settings, this.fields);
          this.filters = this.getFieldToFilterMapper()(this.fields);
          this.sorts = this.model.sorts ? this.mapBackend2FrontendSort(this.model.sorts, this.sortFields) : [];
          if (this.model.filters) {
            const selectedFilters = map(this.model.filters, this._FilterService.getBackEnd2FrontEndFilterMapper());
            // const selectedFilters = this._FilterService.getSelectedFilterMapper()(this.filters);
            this.putSelectedFilterModelsIntoFilters(this.filters, selectedFilters);
          }
        }

        this.pivotGridUpdater.next({
          dataSource: {
            store: this.deNormalizedData,
            fields: this.fields
          },
          sorts: this.sorts,
          filters: this.filters
        });
      });
    }

    getSettingsFromFields(fields) {
      return fpPipe(
        fpMap(fpPick(['dataField', 'checked', 'summaryType', 'caption'])),
        fpGroupBy(field => this.getArea(field.dataField))
      )(fields);
    }

    putSettingsDataInFields(settings, fields) {
      forEach(fields, field => {
        const area = this.getArea(field.dataField);
        const targetField = find(settings[area], ({dataField}) => {
          return dataField === field.dataField;
        });
        field.area = targetField.area;
        field.summaryType = targetField.summaryType;
        field.checked = targetField.checked;
      });
    }

    applyFieldSettings(field) {
      const modifierObj = {};
      const area = this.getArea(field.dataField);

      switch (area) {
        case 'row':
        case 'column':
          modifierObj.area = field.checked ? area : null;
          break;
        case 'data':
        default:
          // if it's not row, or column, it should be data
          modifierObj.summaryType = field.summaryType;
          // setting only the visibility is not eough for hiding data fields
          modifierObj.area = field.checked ? 'data' : null;
          break;
      }

      field.area = modifierObj.area;

      this.pivotGridUpdater.next({
        field: {
          dataField: field.dataField,
          modifierObj
        }
      });
    }

// filters
    openFilterSidenav() {
      this._FilterService.openFilterSidenav(this.filters);
    }

    onApplyFilters(filters) {
      this.filters = filters;
      this.filterGridData();
    }

    filterGridData() {
      this.pivotGridUpdater.next({
        filters: this.filters
      });
    }

    onClearAllFilters() {
      this.clearFilters();
    }

    clearFilters() {
      forEach(this.filters, filter => {
        filter.model = null;
      });
      this.filterGridData();
    }
// END filters

    getFieldToSortFieldMapper() {
      return fpPipe(
        fpFilter(field => this.getArea(field.dataField) === 'row'),
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
        fpFilter(field => this.getArea(field.dataField) === 'row'),
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

    putSelectedFilterModelsIntoFilters(filters, selectedFilters) {
      forEach(filters, filter => {
        const targetFilter = find(selectedFilters, ({name}) => name === filter.name);
        if (targetFilter && this._FilterService.isFilterModelNonEmpty(targetFilter.model)) {
          filter.model = targetFilter.model;
        }
      });
    }

    applySorts(sorts) {
      this.pivotGridUpdater.next({sorts});
    }

    openSortModal(ev) {
      const tpl = '<analyze-report-sort model="model"></analyze-report-sort>';

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

    mapBackend2FrontendSort(sorts, sortFields) {
      return map(sorts, sort => {
        const targetField = find(sortFields, ({dataField}) => dataField === sort.dataField);
        return {
          field: targetField,
          order: sort.order
        };
      });
    }

    mapFrontend2BackendSort(sorts) {
      return map(sorts, sort => {
        return {
          dataField: sort.field.dataField,
          order: sort.order
        };
      });
    }

    openSaveModal(ev) {
      this.model.sorts = this.mapFrontend2BackendSort(this.sorts);
      this.model.settings = this.settings;

      const selectedFilters = this._FilterService.getSelectedFilterMapper()(this.filters);
      this.model.filters = map(selectedFilters, this._FilterService.getFrontEnd2BackEndFilterMapper());

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
        dataField: 'row_level_1'
      }, {
        caption: 'Product',
        width: 120,
        dataType: 'string',
        dataField: 'row_level_2'
      }, {
        caption: 'Date Month',
        dataField: 'column_level_1',
        format: datum => {
          const date = new Date(datum);
          return `${date.getFullYear()}-${date.getMonth()}`;
        },
        width: 120
      }, {
        caption: 'Date Day',
        dataField: 'column_level_2',
        dataType: 'string',
        format: datum => {
          const date = new Date(datum);
          return `${date.getDay()}`;
        },
        width: 120
      }, {
        caption: 'Total Price',
        dataField: 'total_price',
        dataType: 'double',
        summaryType: 'sum',
        format: 'currency'
      }];
    }

    getArea(key) {
      const area = first(split(key, '_'));
      if (area !== 'row' && area !== 'column') {
        return 'data';
      }
      return area;
    }
  }
};
