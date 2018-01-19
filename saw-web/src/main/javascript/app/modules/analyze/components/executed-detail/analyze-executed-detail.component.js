import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import * as get from 'lodash/get';
import * as fpPick from 'lodash/fp/pick';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpMap from 'lodash/fp/map';
import * as map from 'lodash/map';
import * as flatMap from 'lodash/flatMap';
import * as replace from 'lodash/replace';
import * as indexOf from 'lodash/indexOf';
import * as slice from 'lodash/slice';
import {json2csv} from 'json-2-csv';
import * as keys from 'lodash/keys';
import * as moment from 'moment';
import * as forEach from 'lodash/forEach';

import {Events} from '../../consts';

import * as template from './analyze-executed-detail.component.html';
import style from './analyze-executed-detail.component.scss';
import AbstractComponentController from 'app/common/components/abstractComponent';

export const AnalyzeExecutedDetailComponent = {
  template,
  styles: [style],
  controller: class AnalyzeExecutedDetailController extends AbstractComponentController {
    constructor($injector, AnalyzeService, $state, $rootScope, JwtService, $mdDialog, fileService,
                $window, toastMessage, FilterService, AnalyzeActionsService, $scope, $q, $translate) {
      'ngInject';
      super($injector);

      this._AnalyzeService = AnalyzeService;
      this._$translate = $translate;
      this._fileService = fileService;
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._$state = $state;
      this._$rootScope = $rootScope;
      this._$scope = $scope;
      this._$q = $q;
      this._FilterService = FilterService;
      this._toastMessage = toastMessage;
      this._$window = $window; // used for going back from the template
      this._$mdDialog = $mdDialog;
      this._JwtService = JwtService;
      this._executionId = $state.params.executionId;
      this._executionWatcher = null;
      this._executionToast = null;
      this.canUserPublish = false;
      this.canUserFork = false;
      this.canUserEdit = false;
      this.isPublished = false;
      this.isExecuting = false;

      this.requester = new BehaviorSubject({});
    }

    $onInit() {
      const analysisId = this._$state.params.analysisId;
      const analysis = this._$state.params.analysis;

      this._destroyHandler = this.on(Events.AnalysesRefresh, () => {
        this.loadAnalysisById(analysisId).then(() => {
          this._executionId = null;
          this.loadExecutedAnalyses(analysisId);
        });
      });

      if (analysis) {
        this.analysis = analysis;
        this.watchAnalysisExecution();
        this.setPrivileges();
        if (!this.analysis.schedule) {
          this.isPublished = false;
        }
        this.loadExecutedAnalyses(analysisId);
      } else {
        this.loadAnalysisById(analysisId).then(() => {
          this.watchAnalysisExecution();
          this.loadExecutedAnalyses(analysisId);
        });
      }
    }

    watchAnalysisExecution() {
      this.isExecuting = this._AnalyzeService.isExecuting(this.analysis.id);

      this._executionWatcher = this._$scope.$watch(
        () => this._AnalyzeService.isExecuting(this.analysis.id),
        (newVal, prevVal) => {
          if (newVal === prevVal) {
            return;
          }

          this.isExecuting = newVal;

          if (!newVal && !this._AnalyzeService.didExecutionFail(this.analysis.id)) {
            this.refreshData();
          }
        }
      );
    }

    $onDestroy() {
      this._destroyHandler();
      this._executionWatcher();

      if (this._executionToast) {
        this._toastMessage.clear(this._executionToast);
      }
    }

    refreshData() {
      const gotoLastPublished = () => {
        this._$state.go('analyze.executedDetail', {
          analysisId: this.analysis.id,
          analysis: this.analysis,
          executionId: null
        }, {reload: true});
      };

      if (this._executionToast) {
        this._toastMessage.clear(this._executionToast);
      }

      this._executionToast = this._toastMessage.success('Tap this message to reload data.', 'Execution finished', {
        timeOut: 0,
        extendedTimeOut: 0,
        closeButton: true,
        tapToDismiss: true,
        onTap: gotoLastPublished.bind(this)
      });
    }

    executeAnalysis() {
      this._AnalyzeActionsService.execute(this.analysis);
    }

    setPrivileges() {
      this.canUserPublish = this._JwtService.hasPrivilege('PUBLISH', {
        subCategoryId: this.analysis.categoryId
      });
      this.canUserFork = this._JwtService.hasPrivilege('FORK', {
        subCategoryId: this.analysis.categoryId
      });
      this.canUserEdit = this._JwtService.hasPrivilege('EDIT', {
        subCategoryId: this.analysis.categoryId,
        creatorId: this.analysis.userId
      });
    }

    getCheckedFieldsForExport(analysis, data) {
      /* If report was using designer mode, find checked columns */
      if (!analysis.edit) {
        return flatMap(this.analysis.artifacts, artifact => fpPipe(
          fpFilter('checked'),
          fpMap(fpPick(['columnName', 'aliasName', 'displayName']))
        )(artifact.columns));
      }
      /* If report was using sql mode, we don't really have any info
         about columns. Keys from individual data nodes are used as
         column names */
      if (data.length > 0) {
        return map(keys(data[0]), col => ({
          label: col,
          columnName: col,
          displayName: col,
          type: 'string'
        }));
      }
    }

    exportData() {
      if (this.analysis.type === 'pivot') {
        this.requester.next({
          exportAnalysis: true
        });
      } else {
        const analysisId = this.analysis.id;
        const executionId = this._executionId || this.analyses[0].id;
        this._AnalyzeActionsService.exportAnalysis(analysisId, executionId).then(data => {
          const fields = this.getCheckedFieldsForExport(this.analysis, data);
          const keys = map(fields, 'columnName');
          const exportOptions = {
            trimHeaderFields: false,
            emptyFieldValue: '',
            checkSchemaDifferences: false,
            delimiter: {
              wrap: '"',
              eol: '\r\n'
            },
            keys
          };
          json2csv(this.formatDates(data), (err, csv) => {
            if (err) {
              this._$translate('ERROR_EXPORT_FAILED').then(translation => {
                this._toastMessage.error(translation);
              });
            }
            const csvWithDisplayNames = this.replaceCSVHeader(csv, fields);
            this._fileService.exportCSV(csvWithDisplayNames, this.analysis.name);
          }, exportOptions
        );
        });
      }
    }

    formatDates(data) {
      const ks = keys(data[0] || {});
      const formats = [
        moment.ISO_8601,
        'MM/DD/YYYY  :)  HH*mm*ss'
      ];
      forEach(data, data => {
        forEach(ks, key => {
          if (moment(data[key], formats, true).isValid()) {
            data[key] = moment(data[key]).format('MM/DD/YYYY');
          }
        });
      });
      return data;
    }

    replaceCSVHeader(csv, fields) {
      const firstNewLine = indexOf(csv, '\n');
      const firstRow = slice(csv, 0, firstNewLine).join('');
      const displayNames = map(fields, ({aliasName, displayName}) => aliasName || displayName).join(',');
      return replace(csv, firstRow, displayNames);
    }

    loadExecutionData(options = {}) {
      if (this._executionId) {
        options.analysisType = this.analysis.type;
        return this._AnalyzeService.getExecutionData(this.analysis.id, this._executionId, options)
          .then(({data, count}) => {
            this.requester.next({data});
            return {data, count};
          });
      }
      return this._$q.reject(new Error('No execution id selected'));
    }

    loadAnalysisById(analysisId) {
      return this._AnalyzeService.readAnalysis(analysisId)
        .then(analysis => {
          this.analysis = analysis;
          this.setPrivileges();
          if (!this.analysis.schedule) {
            this.isPublished = false;
          }
        });
    }

    /* If data for a particular execution is not requested,
       load data from the most recent execution */
    loadLastPublishedAnalysis() {
      if (!this._executionId) {
        this._executionId = get(this.analyses, '[0].id', null);
        this.loadExecutionData();
      }
    }

    loadExecutedAnalyses(analysisId) {
      this._AnalyzeService.getPublishedAnalysesByAnalysisId(analysisId)
        .then(analyses => {
          this.analyses = analyses;
          this.loadLastPublishedAnalysis();
        });
    }

    fork() {
      this._AnalyzeActionsService.fork(this.analysis);
    }

    edit() {
      this._AnalyzeActionsService.edit(this.analysis);
    }

    publish() {
      this._AnalyzeActionsService.publish(this.analysis);
    }

    onSuccessfulDeletion(analysis) {
      this._$state.go('analyze.view', {id: analysis.categoryId});
    }
  }
};
