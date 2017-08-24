import 'devextreme/ui/pivot_grid';
import map from 'lodash/map';
import clone from 'lodash/clone';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import find from 'lodash/find';
import isEmpty from 'lodash/isEmpty';
import assign from 'lodash/assign';
import defaults from 'lodash/defaults';
import unset from 'lodash/unset';
import filter from 'lodash/filter';
import cloneDeep from 'lodash/cloneDeep';
import fpSortBy from 'lodash/fp/sortBy';
import sortBy from 'lodash/sortBy';
import forEach from 'lodash/forEach';
import fpGroupBy from 'lodash/fp/groupBy';
import groupBy from 'lodash/groupBy';
import values from 'lodash/values';
import fpMapValues from 'lodash/fp/mapValues';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';
import AbstractDesignerComponentController from '../analyze-abstract-designer-component';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';
import {DEFAULT_GROUP_INTERVAL} from '../pivot/settings/analyze-pivot-settings.component';
import {DATE_TYPES, NUMBER_TYPES, ENTRY_MODES, MAX_POSSIBLE_FIELDS_OF_SAME_AREA, DEFAULT_AGGREGATE_TYPE} from '../../consts';

export const AnalyzePivotComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    mode: '@?'
  },
  controller: class AnalyzePivotController extends AbstractDesignerComponentController {
    constructor($timeout, PivotService, AnalyzeService, FilterService, $mdSidenav, toastMessage, $translate, $injector) {
      'ngInject';
      super($injector);
      this._$mdSidenav = $mdSidenav;
      this._PivotService = PivotService;
      this._$timeout = $timeout;
      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._toastMessage = toastMessage;
      this._$translate = $translate;
      this.deNormalizedData = [];
      this.normalizedData = [];
      this.dataSource = {};
      this.sortFields = null;
      this.pivotGridUpdater = new BehaviorSubject({});
      this.artifacts = [];
      this.backupColumns = [];
    }

    $onInit() {
      switch (this.mode) {
        case ENTRY_MODES.NEW:
          this.loadNewAnalysis();
          break;
        case ENTRY_MODES.EDIT:
          this.loadExistingAnalysis();
          break;
        case ENTRY_MODES.FORK:
          this.loadForkedAnalysis();
          break;
        default:
          break;
      }
    }

    loadNewAnalysis() {
      this._AnalyzeService.createAnalysis(this.model.semanticId, this.model.type)
        .then(analysis => {
          this.initModel(analysis);
          this.analysisUnSynched();
          this.artifacts = this.getSortedArtifacts(this.model.artifacts);
          this.artifacts[0].columns = this._PivotService.takeOutKeywordFromArtifactColumns(this.artifacts[0].columns);
        });
    }

    initModel(analysis) {
      this.model = assign(this.model, analysis);
      this.model.id = analysis.id;
      this.model.sqlBuilder = {booleanCriteria: DEFAULT_BOOLEAN_CRITERIA.value};
    }

    loadExistingAnalysis() {
      this.artifacts = this.getSortedArtifacts(this.model.artifacts);
      this.artifacts[0].columns = this._PivotService.takeOutKeywordFromArtifactColumns(this.artifacts[0].columns);
      this.initExistingSettings();
      this.loadPivotData();
    }

    loadForkedAnalysis() {
      this._AnalyzeService.createAnalysis(this.model.semanticId, this.model.type)
        .then(analysis => {
          this.model = defaults(this.model, analysis);
          this.model.id = analysis.id;
          this.analysisUnSynched();
          this.startDraftMode();
          this.artifacts = this.getSortedArtifacts(this.model.artifacts);
          this.artifacts[0].columns = this._PivotService.takeOutKeywordFromArtifactColumns(this.artifacts[0].columns);
          this.loadPivotData();
        });
    }

    initExistingSettings() {
      this.filters = map(this.model.sqlBuilder.filters,
                         this._FilterService.backend2FrontendFilter(this.artifacts));
      this.sortFields = this.getArtifactColumns2SortFieldMapper()(this.model.artifacts[0].columns);
      this.sorts = this.mapBackend2FrontendSort(this.model.sqlBuilder.sorts, this.sortFields);
    }

    getSortedArtifacts(artifacts) {
      return [{
        artifactName: artifacts[0].artifactName,
        columns: sortBy(artifacts[0].columns, 'displayName')
      }];
    }

    onApplySettings(columns) {
      this.artifacts[0].columns = columns;
      this.sortFields = this.getArtifactColumns2SortFieldMapper()(this.artifacts[0].columns);
      this.analysisUnSynched();
      this.startDraftMode();
      const pivotFields = this._PivotService.artifactColumns2PivotFields()(this.artifacts[0].columns);
      this.setDataSource(this.dataSource.store, pivotFields);
    }

    setDataSource(store, fields) {
      this.dataSource = new PivotGridDataSource({store, fields});
      this.pivotGridUpdater.next({
        dataSource: this.dataSource
      });
    }

    onRefreshData() {
      this.loadPivotData();
    }

    checkModelValidity(model) {
      let isValid = true;
      const errors = [];

      if (isEmpty(model.sqlBuilder.dataFields)) {
        isValid = false;
        errors[0] = 'ERROR_PIVOT_DATA_FIELD_REQUIRED';
      }
      if (!isValid) {
        this._$translate(errors).then(translations => {
          this._toastMessage.error(values(translations).join('\n'), '', {
            timeOut: 3000
          });
        });
      }
      return isValid;
    }

    loadPivotData() {
      const model = this.getModel();
      if (!this.checkModelValidity(model)) {
        return;
      }
      this.startProgress();
      return this._AnalyzeService.getDataBySettings(clone(model))
        .then(({data}) => {
          const fields = this._PivotService.artifactColumns2PivotFields()(this.artifacts[0].columns);
          this.normalizedData = data;
          this.analysisSynched();
          this.deNormalizedData = this._PivotService.parseData(data, model.sqlBuilder);
          this.dataSource = new PivotGridDataSource({store: this.deNormalizedData, fields});
          this.pivotGridUpdater.next({
            dataSource: this.dataSource
          });
          // different updates have to be done with a timeout
          // there might be a bug in devextreme pivotgrid
          this._$timeout(() => {
            this.pivotGridUpdater.next({
              sorts: this.sorts
            });
          });
          this.endProgress();
        })
        .catch(error => {
          this.endProgress();
          throw error;
        });
    }

    onPivotContentReady(fields) {
      if (isEmpty(this.artifacts) || isEmpty(fields)) {
        return;
      }
      const selectedArtifactColumns = filter(this.artifacts[0].columns, 'checked');

      forEach(selectedArtifactColumns, artifactColumn => {
        const targetField = find(fields, ({dataField}) => {
          return dataField === artifactColumn.columnName;
        });
        artifactColumn.areaIndex = targetField.areaIndex;
        artifactColumn.area = targetField.area;
        this.applyDefaultsBasedOnAreaChange(artifactColumn);
      });

      if (this.checkValidStates(selectedArtifactColumns)) {

        this.backupColumns = cloneDeep(this.artifacts[0].columns);
      } else if (!isEmpty(this.backupColumns)) {

        this.artifacts[0].columns = this.backupColumns;
        const pivotFields = this._PivotService.artifactColumns2PivotFields()(this.artifacts[0].columns);
        this.setDataSource(this.dataSource.store, pivotFields);
      }
    }

    applyDefaultsBasedOnAreaChange(artifactColumn) {
      if (DATE_TYPES.includes(artifactColumn.type) &&
          !artifactColumn.groupInterval) {

        artifactColumn.groupInterval = DEFAULT_GROUP_INTERVAL.value;
      }
      if (artifactColumn.area === 'data' &&
          NUMBER_TYPES.includes(artifactColumn.type) &&
          !artifactColumn.aggregate) {
        artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
      }
    }

    checkValidStates(artifactColumns) {
      const grouped = groupBy(artifactColumns, 'area');
      let valid = true;
      const errors = [];
      const interpolationValues = {
        fieldNr: MAX_POSSIBLE_FIELDS_OF_SAME_AREA
      };

      if (grouped.column && grouped.column.length > MAX_POSSIBLE_FIELDS_OF_SAME_AREA) {
        errors[0] = 'ERROR_PIVOT_MAX_FIELDS';
        interpolationValues.area = 'column';
        valid = false;
      }
      if (grouped.row && grouped.row.length > MAX_POSSIBLE_FIELDS_OF_SAME_AREA) {
        errors[0] = 'ERROR_PIVOT_MAX_FIELDS';
        interpolationValues.area = 'row';
        valid = false;
      }
      if (grouped.data && grouped.data.length > MAX_POSSIBLE_FIELDS_OF_SAME_AREA) {
        errors[0] = 'ERROR_PIVOT_MAX_FIELDS';
        interpolationValues.area = 'data';
        valid = false;
      }

      forEach(grouped.data, dataColumn => {
        if (!NUMBER_TYPES.includes(dataColumn.type)) {
          errors[1] = 'ERROR_PIVOT_DATA_FIELD';
          valid = false;
        }
      });

      if (!valid) {
        this._$translate(errors, interpolationValues).then(translations => {
          this._toastMessage.error(values(translations).join('\n'), '', {
            timeOut: 3000
          });
        });
      }

      return valid;
    }

    getArtifactColumns2SortFieldMapper() {
      return fpPipe(
        fpFilter(artifactColumn => artifactColumn.checked &&
          (artifactColumn.area === 'row' || artifactColumn.area === 'column')),
        // fpFilter(artifactColumn => !DATE_TYPES.includes(artifactColumn.dataType)),
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
      this.analysisUnSynched();
      this.startDraftMode();
    }

    openPivotSortModal(ev) {
      this.openSortModal(ev, {
        fields: this.sortFields,
        sorts: map(this.sorts, sort => clone(sort))
      })
        .then(sorts => {
          this.sorts = sorts;
          this.applySorts(sorts);
        });
    }

    openPivotPreviewModal(ev) {
      const tpl = '<analyze-pivot-preview model="model"></analyze-pivot-preview>';

      this.openPreviewModal(tpl, ev, {
        pivot: this.model,
        dataSource: this.dataSource
      });
    }

    mapBackend2FrontendSort(sorts, sortFields) {
      return map(sorts, sort => {
        const targetField = find(sortFields, ({dataField}) => dataField === sort.columnName);
        return {
          field: targetField,
          order: sort.order
        };
      });
    }

    mapFrontend2BackendSort(sorts) {
      return map(sorts, sort => {
        return {
          columnName: sort.field.dataField,
          type: sort.field.type,
          order: sort.order
        };
      });
    }

    getModel() {
      const model = assign(this.model, {
        artifacts: [{
          artifactName: this.artifacts[0].artifactName,
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
        fpSortBy('areaIndex'),
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
            } else if (DATE_TYPES.includes(field.type)) {
              backendField.groupInterval = field.groupInterval;
            }
            return backendField;
          })
        ),
      )(this.artifacts[0].columns);

      return {
        booleanCriteria: this.model.sqlBuilder.booleanCriteria,
        filters: map(this.filters, this._FilterService.frontend2BackendFilter()),
        sorts: this.mapFrontend2BackendSort(this.sorts),
        rowFields: groupedFields.row || [],
        columnFields: groupedFields.column || [],
        dataFields: groupedFields.data
      };
    }

    openSavePivotModal(ev) {
      const model = this.getModel();
      this.openSaveModal(ev, model);
    }
  }
};
