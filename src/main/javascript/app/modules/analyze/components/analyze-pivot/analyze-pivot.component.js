import 'devextreme/ui/pivot_grid';
import map from 'lodash/map';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import find from 'lodash/find';
import isEmpty from 'lodash/isEmpty';
import assign from 'lodash/assign';
import unset from 'lodash/unset';
import cloneDeep from 'lodash/cloneDeep';
import sortBy from 'lodash/sortBy';
import forEach from 'lodash/forEach';
import fpGroupBy from 'lodash/fp/groupBy';
import fpMapValues from 'lodash/fp/mapValues';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

import mapKeys from 'lodash/mapKeys';
import isString from 'lodash/isString';

import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';

import {DATE_TYPES, ENTRY_MODES} from '../../consts';

export const AnalyzePivotComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    mode: '@?'
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
      this.settingsReciever = new BehaviorSubject({});
      this.settingsModified = false;
      this.artifacts = [];
    }

    $onInit() {
      if (isEmpty(this.model.artifacts) || this.mode === ENTRY_MODES.FORK) {
        // new analysis
        this._AnalyzeService.createAnalysis(this.model.semanticId, this.model.type)
          .then(analysis => {
            this.model.id = analysis.id;
            if (this.mode !== ENTRY_MODES.FORK) {
              this.model = assign(this.model, analysis);
              this.model.sqlBuilder = {booleanCriteria: DEFAULT_BOOLEAN_CRITERIA.value};
            } else {
              this.settingsModified = true;
            }
            this.artifacts = [{
              artifactName: this.model.artifacts[0].artifactName,
              columns: sortBy(this.model.artifacts[0].columns, 'displayName')
            }];
            this.prepareFields(this.artifacts[0].columns);
            this.toggleSettingsSidenav();
          });
        // if it's a pivot analysis we're only interested in the first artifact
      } else {
        // edit existing analysis
        this.artifacts = [{
          artifactName: this.model.artifacts[0].artifactName,
          columns: sortBy(this.model.artifacts[0].columns, 'displayName')
        }];
        this.prepareFields(this.artifacts[0].columns);
        this.loadPivotData().then(() => {
          this.toggleSettingsSidenav();
          this.filters = map(this.model.filters,
            this._FilterService.backend2FrontendFilter(this.model.artifacts));
          this.sortFields = this.getArtifactColumns2SortFieldMapper()(this.artifacts[0].columns);
          this.sorts = this.mapBackend2FrontendSort(this.model.sorts, this.sortFields);
        });
      }
    }

    toggleSettingsSidenav() {
      this.settingsReciever.next({
        eventName: 'open',
        payload: {
          artifactColumns: this.artifacts[0].columns
        }
      });
    }

    onApplySettings(columns) {
      this.artifacts[0].columns = columns;
      this.sortFields = this.getArtifactColumns2SortFieldMapper()(this.artifacts[0].columns);
      this.settingsModified = true;
      const pivotFields = this._PivotService.artifactColumns2PivotFields()(this.artifacts[0].columns);
      this.setDataSource(this.dataSource.store, pivotFields);
    }

    setDataSource(store, fields) {
      const {transformedStore, transFormedFields} = this.takeOutKeyword(store, fields);
      this.dataSource = new PivotGridDataSource({
        store: transformedStore,
        fields: transFormedFields
      });
      this.pivotGridUpdater.next({
        dataSource: this.dataSource
      });
    }

    /**
     * The string type artifact columns' columnNames, have a .keyword at the end
     * // which triggers some kind of bug in pivot grid, so they have to be removed
     */
    takeOutKeyword(store, fields) {
      let transformedStore = [];
      if (!isEmpty(store)) {
        transformedStore = map(store, dataObj => {
          return mapKeys(dataObj, (v, key) => {
            if (isString(key)) {
              const split = key.split('.');
              if (split[1] === 'keyword') {
                return split[0];
              }
            }
            return key;
          });
        });
      }

      forEach(fields, field => {
        if (field.dataField && field.type === 'string') {
          const split = field.dataField.split('.');
          if (split[1] === 'keyword') {
            field.dataField = split[0];
          }
        }
      });

      return {transformedStore, transFormedFields: fields};
    }

    prepareFields(artifactColumns) {
      this._$timeout(() => {
        this.setDataSource(this.dataSource.store, this._PivotService.artifactColumns2PivotFields()(artifactColumns));
      }, 400);
    }

    onRefreshData() {
      this.loadPivotData();
    }

    loadPivotData() {
      const model = this.getModel();
      return this._AnalyzeService.getDataBySettings(clone(model))
        .then(({data}) => {
          const fields = this._PivotService.artifactColumns2PivotFields()(this.artifacts[0].columns);
          this.normalizedData = data;
          this.settingsModified = false;
          this.deNormalizedData = this._PivotService.denormalizeData(data, fields);
          this.dataSource.store = this.deNormalizedData;
          this.setDataSource(this.dataSource.store, fields);
        });
    }

// filters
    openFiltersModal(ev) {
      const tpl = '<analyze-filter-modal filters="filters" artifacts="artifacts" filter-boolean-criteria="booleanCriteria"></analyze-filter-modal>';
      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.filters = cloneDeep(this.filters);
          scope.artifacts = this.artifacts;
          scope.booleanCriteria = this.model.sqlBuilder.booleanCriteria;
        },
        targetEvent: ev,
        fullscreen: true,
        autoWrap: false,
        multiple: true
      }).then(this.onApplyFilters.bind(this));
    }

    onApplyFilters({filters, filterBooleanCriteria} = {}) {
      if (filters) {
        this.filters = filters;
        this.settingsModified = true;
      }
      if (filterBooleanCriteria) {
        this.model.sqlBuilder.booleanCriteria = filterBooleanCriteria;
      }
    }

    onClearAllFilters() {
      this.filters = [];
      this.settingsModified = true;
    }

    onFilterRemoved(index) {
      this.filters.splice(index, 1);
      this.settingsModified = true;
    }
// END filters

    getArtifactColumns2SortFieldMapper() {
      return fpPipe(
        fpFilter(artifactColumn => artifactColumn.checked &&
          (artifactColumn.area === 'row' || artifactColumn.area === 'column')),
        fpFilter(artifactColumn => !DATE_TYPES.includes(artifactColumn.dataType)),
        fpMap(artifactColumn => {
          return {
            type: artifactColumn.type,
            dataField: artifactColumn.columnName,
            label: artifactColumn.alias || artifactColumn.displayName
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

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = {
              pivot: this.model,
              dataSource: {
                store: this.dataSource.store,
                fields: this._PivotService.artifactColumns2PivotFields()(this.artifacts[0].columns)
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

    getModel() {
      const model = assign(this.model, {
        artifacts: [{
          columns: this.artifacts[0].columns
        }],
        sqlBuilder: this.getSqlBuilder()
      });
      unset(model, 'supports');
      return model;
    }

    getSqlBuilder() {
      const groupedFields = fpPipe(
        fpFilter(field => field.checked && field.area),
        fpGroupBy('area'),
        fpMapValues(
          fpMap(field => {
            const backendField = {
              type: field.type,
              columnName: field.columnName
            };
            if (field.area === 'data') {
              backendField.aggregate = field.aggregate;
              // name field is needed for the elastic search request
              backendField.name = field.columnName;
            }
            return backendField;
          })
        ),
      )(this.artifacts[0].columns);

      return {
        booleanCriteria: this.model.sqlBuilder.booleanCriteria,
        filters: map(this.filters, this._FilterService.frontend2BackendFilter()),
        sorts: this.mapFrontend2BackendSort(this.sorts),
        rowFields: groupedFields.row,
        columnFields: groupedFields.column,
        dataFields: groupedFields.data
      };
    }

    openSaveModal(ev) {
      const model = this.getModel();
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
