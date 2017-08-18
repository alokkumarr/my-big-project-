import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import get from 'lodash/get';

import {Events} from '../../consts';

import template from './analyze-executed-detail.component.html';
import style from './analyze-executed-detail.component.scss';
import AbstractComponentController from 'app/common/components/abstractComponent';

export const AnalyzeExecutedDetailComponent = {
  template,
  styles: [style],
  controller: class AnalyzeExecutedDetailController extends AbstractComponentController {
    constructor($injector, AnalyzeService, $state, $rootScope, JwtService, $mdDialog,
                $window, toastMessage, FilterService, AnalyzeActionsService, $scope, $q) {
      'ngInject';
      super($injector);

      this._AnalyzeService = AnalyzeService;
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
        this._$state.go('analyze.publishedDetail', {
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
        subCategoryId: this.analysis.categoryId
      });
    }

    exportData() {
      this.requester.next({
        export: true
      });
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
