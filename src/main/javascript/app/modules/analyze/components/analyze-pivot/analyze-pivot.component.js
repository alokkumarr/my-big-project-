import 'devextreme/ui/pivot_grid';
import forEach from 'lodash/forEach';
import map from 'lodash/map';
import fpGroupBy from 'lodash/fp/groupBy';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import isEmpty from 'lodash/isEmpty';
import find from 'lodash/find';
import fpPick from 'lodash/fp/pick';
import {BehaviorSubject} from 'rxjs';

import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';

import {ANALYZE_FILTER_SIDENAV_IDS} from '../analyze-filter-sidenav/analyze-filter-sidenav.component';

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
        this.fields = this._PivotService.getFieldsFromData(this.deNormalizedData);
        this.sortFields = this.getFieldToSortFieldMapper()(this.fields);

        if (isEmpty(this.model.settings)) {
          // new analysis
          this.settings = this.getSettingsFromFields(this.fields);
          this.filters = this._PivotService.getFieldToFilterMapper(this.normalizedData)(this.fields);
          this.sorts = [];
        } else {
          // editing existing analysis
          this.settings = this.model.settings;
          this._PivotService.putSettingsDataInFields(this.settings, this.fields);
          this.sorts = this.model.sorts ? this.mapBackend2FrontendSort(this.model.sorts, this.sortFields) : [];

          this.filters = this._PivotService.getFieldToFilterMapper(this.normalizedData)(this.fields);
          if (this.model.filters) {
            const selectedFilters = map(this.model.filters, this._FilterService.getBackEnd2FrontEndFilterMapper());
            this._PivotService.putSelectedFilterModelsIntoFilters(this.filters, selectedFilters);
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
        fpGroupBy(field => this._PivotService.getArea(field.dataField))
      )(fields);
    }

    applyFieldSettings(field) {
      const modifierObj = {};
      const area = this._PivotService.getArea(field.dataField);

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
      this._FilterService.openFilterSidenav(this.filters, ANALYZE_FILTER_SIDENAV_IDS.designer);
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
        fpFilter(field => this._PivotService.getArea(field.dataField) === 'row'),
        fpMap(field => {
          return {
            type: field.dataType,
            dataField: field.dataField,
            label: field.caption
          };
        })
      );
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
  }
};
