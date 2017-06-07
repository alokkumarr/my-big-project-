import 'devextreme/ui/pivot_grid';
import map from 'lodash/map';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import find from 'lodash/find';
import filter from 'lodash/filter';
import isEmpty from 'lodash/isEmpty';
import assign from 'lodash/assign';
import unset from 'lodash/unset';
import cloneDeep from 'lodash/cloneDeep';
import fpGroupBy from 'lodash/fp/groupBy';
import fpMapValues from 'lodash/fp/mapValues';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';

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
      this.filters = [];
      this.sortFields = null;
      this.pivotGridUpdater = new BehaviorSubject({});
      this.settingsModified = false;
      this.artifacts = [];
    }

    $onInit() {
      if (isEmpty(this.model.artifacts)) {
        // new analysis
        this._AnalyzeService.createAnalysis(this.model.semanticId, this.model.type)
          .then(analysis => {
            this.model = assign(this.model, analysis);
            this.model.repository = {
              storageType: 'ES',
              indexName: 'mct_data',
              type: 'session_type'
            };
            this.model.id = analysis.id;
            this.artifacts = analysis.artifacts;
            this.prepareFields(analysis.artifacts[0].columns);
            this.toggleSettingsSidenav();
          });
        // if it's a pivot analysis we're only interested in the first artifact
      } else {
        // edit existing analysis
        this.artifacts = this.model.artifacts;
        this.prepareFields(this.artifacts[0].columns);
        this.loadPivotData().then(() => {
          this.filters = map(this.model.filters,
            this._FilterService.backend2FrontendFilter(this.model.artifacts));
          this.sortFields = this.getFieldToSortFieldMapper()(this.fields);
          this.sorts = this.mapBackend2FrontendSort(this.model.sorts, this.sortFields);
        });
      }
    }

    onApplyFieldSettings() {
      this.sortFields = this.getFieldToSortFieldMapper()(this.fields);
      this.settingsModified = true;
    }

    setDataSource(store, fields) {
      this.dataSource = new PivotGridDataSource({store, fields});
      this.pivotGridUpdater.next({
        dataSource: this.dataSource
      });
      this.fieldChooserIsntance.option({
        dataSource: this.dataSource
      });
    }

    prepareFields(artifactAttributes) {
      this.fields = this._PivotService.getBackend2FrontendFieldMapper()(artifactAttributes);

      this.fieldChooserOptions = {
        onContentReady: e => {
          this.fieldChooserIsntance = e.component;
        },
        layout: 1,
        width: 400,
        height: 800
      };

      // repaint the field chooser so it fills the cointainer
      this._$timeout(() => {
        this.setDataSource(this.dataSource.store, this.fields);
        this.fieldChooserIsntance.repaint();
      }, 400);
    }

    toggleSettingsSidenav() {
      this._$mdSidenav(ANALYZE_PIVOT_SETTINGS_SIDENAV_ID).toggle();
    }

    onRefreshData() {
      this.updateFields();
      this.loadPivotData();
    }

    loadPivotData() {
      const model = this.getModel();
      this._AnalyzeService.getDataBySettings(clone(model))
        .then(({analysis, data}) => {
          console.log(analysis, data);
        });
      // return this._AnalyzeService.getPivotData().then(pivotData => {
      //   this.normalizedData = pivotData;
      //   this.deNormalizedData = this._PivotService.denormalizeData(pivotData, this.fields);

      //   if (isEmpty(this.filters.possible)) {
      //     this.filters.possible = this._PivotService.mapFieldsToFilters(this.normalizedData, this.fields);
      //     this.filters.selected = [];
      //   }
      //   this.dataSource.store = this.deNormalizedData;

      //   this.setDataSource(this.dataSource.store, this.fields);
      //   this.settingsModified = false;
      // });
    }

// filters
    openFiltersModal(ev) {
      const tpl = '<analyze-filter-modal filters="filters" artifacts="artifacts"></analyze-filter-modal>';
      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.filters = cloneDeep(this.filters);
          scope.artifacts = this.artifacts;
        },
        targetEvent: ev,
        fullscreen: true,
        autoWrap: false,
        multiple: true
      }).then(this.onApplyFilters.bind(this));
    }

    onApplyFilters(filters) {
      if (filters) {
        this.filters = filters;
      }
    }

    onClearAllFilters() {
      this.filters = [];
    }

    onFilterRemoved(index) {
      this.filters.splice(index, 1);
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
      this.settingsModified = true;
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
      this.updateFields();

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = {
              pivot: this.model,
              dataSource: {
                store: this.dataSource.store,
                fields: this.fields
              }
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
      this.fields = fields;
      this.fieldsToSave = filter(fields, 'area');
    }

    updateFields() {
      this.pivotGridUpdater.next({
        getFields: true
      });
    }

    getModel() {
      this.updateFields();
      const model = assign(this.model, {
        artifacts: [{
          columns: this._PivotService.getFrontend2BackendFieldMapper()(this.fieldsToSave)
        }],
        sqlBuilder: this.getSqlBuilder()
      });
      unset(model, 'supports');
      return model;
    }

    getSqlBuilder() {
      const groupedFields = fpPipe(
        fpGroupBy('area'),
        fpMapValues(
          fpMap(field => {
            return {
              type: field.type,
              columnName: field.dataField
            };
          })
        ),
      )(this.fieldsToSave);

      return {
        filters: map(this.filters, this._FilterService.frontend2BackendFilter()),
        sorts: this.mapFrontend2BackendSort(this.sorts),
        rowFields: groupedFields.row,
        columnFields: groupedFields.column,
        dataFields: groupedFields.data
      };
    }

    openSaveModal(ev) {
      const model = this.getModel();
      // this.model.filters = map(this.filters, this._FilterService.frontend2BackendFilter());
      // this.model.sorts = this.mapFrontend2BackendSort(this.sorts);
      const tpl = '<analyze-report-save model="model" on-save="onSave($data)"></analyze-report-save>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = clone(model);

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
        }).then(successfullySaved => {
          if (successfullySaved) {
            this.onAnalysisSaved(successfullySaved);
          }
        });
    }

    onAnalysisSaved(successfullySaved) {
      this.$dialog.hide(successfullySaved);
    }
  }
};
