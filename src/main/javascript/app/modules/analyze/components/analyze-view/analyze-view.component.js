import template from './analyze-view.component.html';
import style from './analyze-view.component.scss';

import cloneDeep from 'lodash/cloneDeep';
import remove from 'lodash/remove';
import findIndex from 'lodash/findIndex';
import {Subject} from 'rxjs/Subject';

import {Events, AnalyseTypes} from '../../consts';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const AnalyzeViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeViewController extends AbstractComponentController {
    constructor($injector, $compile, AnalyzeService, $state, $mdDialog, toastMessage, $rootScope, localStorageService, FilterService) {
      'ngInject';
      super($injector);

      this._$compile = $compile;
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$mdDialog = $mdDialog;
      this._localStorageService = localStorageService;
      this._FilterService = FilterService;
      this._toastMessage = toastMessage;
      this._$rootScope = $rootScope;
      this._analysisCache = [];

      this.LIST_VIEW = 'list';
      this.CARD_VIEW = 'card';

      const savedView = localStorageService.get('analyseReportView');

      this.states = {
        reportView: [this.LIST_VIEW, this.CARD_VIEW].indexOf(savedView) >= 0 ?
          savedView :
          this.CARD_VIEW,
        analysisType: 'all',
        searchTerm: ''
      };
      this.updater = new Subject();
    }

    $onInit() {
      this._destroyHandler = this.on(Events.AnalysesRefresh, () => {
        this.loadAnalyses();
      });

      this.loadCategory();
      this.loadAnalyses();
    }

    $onDestroy() {
      this._destroyHandler();
    }

    onReportViewChange() {
      this._localStorageService.set('analyseReportView', this.states.reportView);
    }

    onAnalysisTypeChange() {
      this.updater.next({analysisType: this.states.analysisType});
    }

    loadCategory() {
      return this._AnalyzeService.getCategory(this.$state.params.id)
        .then(category => {
          this.category = category;
        });
    }

    goToAnalysis(analysis) {
      this._$state.go('analyze.publishedDetail', {analysisId: analysis.id, analysis});
    }

    goToLastPublishedAnalysis(analysis) {
      this.goToAnalysis(analysis);
    }

    loadAnalyses() {
      this._$rootScope.showProgress = true;
      return this._AnalyzeService.getAnalysesFor(this.$state.params.id, {
        filter: this.states.searchTerm
      }).then(analyses => {
        this._analysisCache = this.analyses = analyses;
        this.updater.next({analyses});
        this._$rootScope.showProgress = false;
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }

    applySearchFilter() {
      this.analyses = this._AnalyzeService.searchAnalyses(this._analysisCache, this.states.searchTerm);
      this.updater.next({analyses: this.analyses});
    }

    openNewAnalysisModal() {
      this._$rootScope.showProgress = true;
      this._AnalyzeService.getSemanticLayerData().then(metrics => {
        this._$rootScope.showProgress = false;
        this.showDialog({
          controller: scope => {
            scope.metrics = metrics;
            scope.subCategory = this.$state.params.id;
          },
          template: '<analyze-new metrics="metrics" sub-category="{{::subCategory}}"></analyze-new>',
          fullscreen: true
        });
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }

    removeAnalysis(model) {
      this._$rootScope.showProgress = true;
      this._AnalyzeService.deleteAnalysis(model).then(() => {
        remove(this.analyses, report => {
          return report.id === model.id;
        });
        this.updater.next({analyses: this.analyses});
        this._$rootScope.showProgress = false;
        this._toastMessage.info('Analysis deleted.');
      }, err => {
        this._$rootScope.showProgress = false;
        this._toastMessage.error(err.message || 'Analysis not deleted.');
      });
    }

    openDeleteModal(model) {
      const confirm = this._$mdDialog.confirm()
            .title('Are you sure you want to delete this analysis?')
        .textContent('Any published analyses will also be deleted.')
        .ok('Delete')
        .cancel('Cancel');

      this._$mdDialog.show(confirm).then(() => {
        this.removeAnalysis(model);
      }, err => {
        if (err) {
          this._$log.error(err);
        }
      });
    }

    /* ACTIONS */

    onCardAction(actionType, payload) {
      switch (actionType) {
        case 'fork':
        case 'edit': {
          const clone = cloneDeep(payload);
          this.openEditModal(actionType, clone);
          break;
        }
        case 'delete':
          this.openDeleteModal(payload);
          break;
        case 'publish':
          this.publish(payload);
          break;
        case 'execute':
          this.execute(payload);
          break;
        case 'view':
          this.view(payload);
          break;
        case 'export':
          this.export(payload);
          break;
        case 'print':
          this.print(payload);
          break;
        default:
      }
    }

    export() {
    }

    print() {
    }

    openPublishModal(model, onPublish) {
      const tpl = '<analyze-publish-dialog model="model" on-publish="onPublish(model, execute)"></analyze-publish-dialog>';

      this._$mdDialog
        .show({
          template: tpl,
          controllerAs: '$ctrl',
          controller: scope => {
            scope.model = model;
            scope.onPublish = onPublish.bind(this);
          },
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          clickOutsideToClose: true
        });
    }

    publish(analysis) {
      this.openPublishModal(analysis, (model, execute) => {
        this._$rootScope.showProgress = true;
        this._AnalyzeService.publishAnalysis(model, execute).then(() => {
          this._$rootScope.showProgress = false;

          /* Update the new analysis in the current list */
          const id = findIndex(this.analyses, report => {
            return report.id === model.id;
          });
          this.analyses.splice(id, 1, model);
          this.updater.next({analyses: this.analyses});

          this._toastMessage.info(execute ?
                                  'Analysis has been published.' :
                                  'Analysis schedule changes have been updated.');

          this._$state.go('analyze.view', {id: model.categoryId});
        }, () => {
          this._$rootScope.showProgress = false;
        });
      });
    }

    view(analysisId) {
      this.goToLastPublishedAnalysis(analysisId);
    }

    execute(analysis) {
      this._FilterService.getRuntimeFilterValues(analysis).then(model => {
        this._AnalyzeService.executeAnalysis(model);
        this.goToAnalysis(model);
      });
    }

    openEditModal(mode, model) {
      if (mode === 'fork') {
        model.name += ' Copy';
      }
      const openModal = template => {
        this.showDialog({
          template,
          controller: scope => {
            scope.model = model;
          },
          multiple: true
        });
      };

      switch (model.type) {
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
  }
};
