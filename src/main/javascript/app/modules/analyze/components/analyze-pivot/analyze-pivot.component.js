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
import fpGroupBy from 'lodash/fp/groupBy';
import fpMapValues from 'lodash/fp/mapValues';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';

import {DATE_TYPES} from '../../consts';

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
      this.settingsReciever = new BehaviorSubject({});
      this.settingsModified = false;
      this.artifactColumns = [];
    }

    $onInit() {
      if (isEmpty(this.model.artifacts)) {
        // new analysis
        this._AnalyzeService.createAnalysis(this.model.semanticId, this.model.type)
          .then(analysis => {
            this.model = assign(this.model, analysis);
            this.model.id = analysis.id;
            this.artifactColumns = sortBy(analysis.artifacts[0].columns, 'displayName');
            this.prepareFields(this.artifactColumns);
            this.toggleSettingsSidenav();
          });
        // if it's a pivot analysis we're only interested in the first artifact
      } else {
        // edit existing analysis
        this.artifactColumns = sortBy(this.model.artifacts[0].columns, 'displayName');
        this.prepareFields(this.artifactColumns);
        this.loadPivotData().then(() => {
          this.filters = map(this.model.filters,
            this._FilterService.backend2FrontendFilter(this.model.artifacts));
          this.sortFields = this.getArtifactColumns2SortFieldMapper()(this.artifactColumns);
          this.sorts = this.mapBackend2FrontendSort(this.model.sorts, this.sortFields);
        });
      }
    }

    onApplySettings(columns) {
      this.artifactColumns = columns;
      this.sortFields = this.getArtifactColumns2SortFieldMapper()(this.artifactColumns);
      this.settingsModified = true;
      const pivotFields = this._PivotService.artifactColumns2PivotFields()(this.artifactColumns);
      this.setDataSource(this.dataSource.store, pivotFields);
    }

    setDataSource(store, fields) {
      this.dataSource = new PivotGridDataSource({store, fields});
      this.pivotGridUpdater.next({
        dataSource: this.dataSource
      });
    }

    prepareFields(artifactColumns) {
      this._$timeout(() => {
        this.setDataSource(this.dataSource.store, this._PivotService.artifactColumns2PivotFields()(artifactColumns));
      }, 400);
    }

    toggleSettingsSidenav() {
      this.settingsReciever.next({
        eventName: 'open',
        payload: {
          artifactColumns: this.artifactColumns
        }
      });
    }

    onRefreshData() {
      this.loadPivotData();
    }

    loadPivotData() {
      const model = this.getModel();
      this._AnalyzeService.getDataBySettings(clone(model))
        .then(({data}) => {
          this.normalizedData = data;
          this.deNormalizedData = this._PivotService.denormalizeData(data,
            this._PivotService.artifactColumns2PivotFields()(this.artifactColumns));
          this.dataSource.store = this.deNormalizedData;
          this.setDataSource(this.dataSource.store,
            this._PivotService.artifactColumns2PivotFields()(this.artifactColumns));
          this.settingsModified = false;
        });
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
        this.settingsModified = true;
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
                fields: this._PivotService.artifactColumns2PivotFields()(this.artifactColumns)
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
          columns: this.artifactColumns
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
      )(this.artifactColumns);

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
