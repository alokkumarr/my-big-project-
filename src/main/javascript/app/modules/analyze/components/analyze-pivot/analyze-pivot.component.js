import 'devextreme/ui/pivot_grid';
import forEach from 'lodash/forEach';
import map from 'lodash/map';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import find from 'lodash/find';
import take from 'lodash/take';
import isEmpty from 'lodash/isEmpty';
import omit from 'lodash/omit';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';

import {ANALYZE_FILTER_SIDENAV_IDS} from '../analyze-filter-sidenav/analyze-filter-sidenav.component';
import {ANALYZE_PIVOT_SETTINGS_SIDENAV_ID} from '../analyze-pivot-settings/analyze-pivot-settings.component';

export const AnalyzePivotComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzePivotController {
    constructor($mdDialog, $timeout, PivotService, AnalyzeService, FilterService, $mdSidenav) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$mdSidenav = $mdSidenav;
      this._PivotService = PivotService;
      this._$timeout = $timeout;
      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;

      this.deNormalizedData = [];
      this.normalizedData = [];
      this.dataSource = {};
      this.sorts = [];
      this.filters = null;
      this.sortFields = null;
      this.pivotGridUpdater = new BehaviorSubject({});
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      if (isEmpty(this.model.artifacts)) {
        // new analysis
        this._AnalyzeService.createAnalysis(this.model.artifactsId)
          .then(analysis => {
            this.prepareFields(analysis.artifacts[0].artifactAttributes);
            this.toggleSettingsSidenav();
          });
        // if it's a pivot analysis we're only interested in the first artifact
      } else {
        // edit existing analysis
        this.prepareFields(this.model.artifacts[0].artifactAttributes);
        this.loadPivotData().then(() => {
          this.filters = this._PivotService.mapFieldsToFilters(this.normalizedData, this.fields);
          const selectedFilters = map(this.model.filters, this._FilterService.getBackEnd2FrontEndFilterMapper());
          this.mergeSelectedFiltersWithFilters(selectedFilters, this.filters);
          this.sortFields = this.getFieldToSortFieldMapper()(this.fields);
          this.sorts = this.mapBackend2FrontendSort(this.model.sorts, this.sortFields);
        });
      }
    }

    mergeSelectedFiltersWithFilters(selectedFilters, filters) {
      forEach(selectedFilters, selectedFilter => {
        const targetFilter = find(filters, ({name}) => name === selectedFilter.name);
        targetFilter.model = selectedFilter.model;
      });
    }

    onApplyFieldSettings() {
      this.sortFields = this.getFieldToSortFieldMapper()(this.fields);
    }

    prepareFields(artifactAttributes) {
      this.fields = this._PivotService.getBackend2FrontendFieldMapper()(artifactAttributes);

      const dataSource = new PivotGridDataSource({
        fields: this.fields
      });

      this.fieldChooserOptions = {
        onContentReady: e => {
          this.gridIsntance = e.component;
        },
        dataSource,
        layout: 1,
        width: 400,
        height: 800
      };

      // repaint the field chooser so it fills the cointainer
      this._$timeout(() => {
        this.gridIsntance.repaint();
      }, 400);

      this.pivotGridUpdater.next({
        dataSource
      });
    }

    toggleSettingsSidenav() {
      this._$mdSidenav(ANALYZE_PIVOT_SETTINGS_SIDENAV_ID).toggle();
    }

    onRefreshData() {
      this.updateFields();
      this.loadPivotData();
    }

    loadPivotData() {
      return this._AnalyzeService.getPivotData().then(pivotData => {
        this.normalizedData = pivotData;
        this.deNormalizedData = this._PivotService.denormalizeData(pivotData, this.fields);

        if (isEmpty(this.filters)) {
          this.filters = this._PivotService.mapFieldsToFilters(this.normalizedData, this.fields);
        }

        this.pivotGridUpdater.next({
          dataSource: {
            store: this.deNormalizedData,
            fields: this.fields
          }
        });
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
        fpFilter(field => field.dataType !== 'date' && field.area === 'row'),
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

    onGetFields(fields) {
      // pivotgrid adds some other fields in plus, so we have to take only the real ones
      this.fieldsToSave = take(fields, this.fields.length);
    }

    updateFields() {
      this.pivotGridUpdater.next({
        getFields: true
      });
    }

    openSaveModal(ev) {
      this.updateFields();

      this.model.artifacts = [{artifactAttributes: this._PivotService.getFrontend2BackendFieldMapper()(this.fieldsToSave)}];

      this.model.filters = fpPipe(
        this._FilterService.getSelectedFilterMapper(),
        fpMap(this._FilterService.getFrontEnd2BackEndFilterMapper())
      )(this.filters);
      this.model.sorts = this.mapFrontend2BackendSort(this.sorts);
      const tpl = '<analyze-report-save model="model" on-save="onSave($data)"></analyze-report-save>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = clone(omit(this.model, 'metric'));

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
