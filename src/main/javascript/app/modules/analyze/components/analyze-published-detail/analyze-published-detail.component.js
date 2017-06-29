import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import get from 'lodash/get';
import clone from 'lodash/clone';

import {AnalyseTypes} from '../../consts';

import template from './analyze-published-detail.component.html';
import style from './analyze-published-detail.component.scss';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const AnalyzePublishedDetailComponent = {
  template,
  styles: [style],
  controller: class AnalyzePublishedDetailController extends AbstractComponentController {
    constructor($injector, AnalyzeService, $state, $rootScope, $mdDialog, $window, toastMessage, FilterService) {
      'ngInject';
      super($injector);

      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$rootScope = $rootScope;
      this._FilterService = FilterService;
      this._toastMessage = toastMessage;
      this._$window = $window; // used for going back from the template
      this._$mdDialog = $mdDialog;
      this._executionId = $state.params.executionId;
      this.isPublished = true;

      this.requester = new BehaviorSubject({});
    }

    $onInit() {
      const analysisId = this._$state.params.analysisId;
      const analysis = this._$state.params.analysis;
      if (analysis) {
        this.analysis = analysis;
        if (!this.analysis.schedule) {
          this.isPublished = false;
        }
        this.loadExecutionData();
        this.loadExecutedAnalyses(analysisId);
      } else {
        this.loadAnalysisById(analysisId).then(() => {
          this.loadExecutionData();
          this.loadExecutedAnalyses(analysisId);
        });
      }
    }

    showExecutingFlag() {
      return this.analysis && this._AnalyzeService.isExecuting(this.analysis.id);
    }

    executeAnalysis() {
      if (this.analysis) {
        this._FilterService.getRuntimeFilterValues(this.analysis).then(model => {
          this._AnalyzeService.executeAnalysis(model);
        });
      }
    }

    exportData() {
      this.requester.next({
        export: true
      });
    }

    loadExecutionData() {
      if (this._executionId) {
        this._AnalyzeService.getExecutionData(this.analysis.id, this._executionId).then(data => {
          this.requester.next({data});
        });
      }
    }

    loadAnalysisById(analysisId) {
      return this._AnalyzeService.readAnalysis(analysisId)
        .then(analysis => {
          this.analysis = analysis;
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

    removeAnalysis(model) {
      const category = model.categoryId;
      this._$rootScope.showProgress = true;
      this._AnalyzeService.deleteAnalysis(model).then(() => {
        this._toastMessage.info('Analysis deleted.');
        this._$state.go('analyze.view', {id: category});
      }, err => {
        this._$rootScope.showProgress = false;
        this._toastMessage.error(err.message || 'Analysis not deleted.');
      });
    }

    openDeleteModal() {
      const confirm = this._$mdDialog.confirm()
            .title('Are you sure you want to delete this analysis?')
            .textContent('Any published analyses will also be deleted.')
        .ok('Delete')
        .cancel('Cancel');

      this._$mdDialog.show(confirm).then(() => {
        this.removeAnalysis(this.analysis);
      }, err => {
        if (err) {
          this._$log.error(err);
        }
      });
    }

    openEditModal(mode) {
      const openModal = template => {
        this.showDialog({
          template,
          controller: scope => {
            const model = clone(this.analysis);
            if (mode === 'fork') {
              model.name += ' Copy';
            }
            scope.model = model;
          },
          multiple: true
        });
      };

      switch (this.analysis.type) {
        case AnalyseTypes.Report:
          openModal(`<analyze-report model="model" mode="${mode}"></analyze-report>`);
          break;
        case AnalyseTypes.Chart:
          openModal(`<analyze-chart model="model" mode="${mode}"></analyze-chart>`);
          break;
        case AnalyseTypes.Pivot:
          openModal(`<analyze-pivot model="model" mode="${mode}"></analyze-pivot>`);
          break;
        default:
      }
    }

    publish(model) {
      this._$rootScope.showProgress = true;
      this._AnalyzeService.publishAnalysis(model).then(() => {
        this._$rootScope.showProgress = false;
      }, () => {
        this._$rootScope.showProgress = false;
      });
    }

    openPublishModal() {
      const template = '<analyze-publish-dialog model="model" on-publish="onPublish(model)"></analyze-publish-dialog>';

      this.showDialog({
        template,
        controller: scope => {
          scope.model = clone(this.analysis);
          scope.onPublish = this.publish.bind(this);
        },
        multiple: true
      });
    }
  }
};
