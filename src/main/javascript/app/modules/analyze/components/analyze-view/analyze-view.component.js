import template from './analyze-view.component.html';
import style from './analyze-view.component.scss';

import cloneDeep from 'lodash/cloneDeep';
import remove from 'lodash/remove';
import {Subject} from 'rxjs/Subject';

import {Events, AnalyseTypes} from '../../consts';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const AnalyzeViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeViewController extends AbstractComponentController {
    constructor($injector, $compile, AnalyzeService, $state, $mdDialog, $mdToast, $rootScope) {
      'ngInject';
      super($injector);

      this._$compile = $compile;
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$mdDialog = $mdDialog;
      this._$mdToast = $mdToast;
      this._$rootScope = $rootScope;
      this._analysisCache = [];

      this.LIST_VIEW = 'list';
      this.CARD_VIEW = 'card';

      this.states = {
        reportView: 'card',
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
      remove(this.analyses, report => {
        return report.id === model.id;
      });
      this._$mdToast.show({
        template: '<md-toast><span>Analysis Deleted</span></md-toast>',
        position: 'bottom left',
        toastClass: 'toast-primary'
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
          this.removeAnalysis(payload);
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

    view(analysisId) {
      this.goToLastPublishedAnalysis(analysisId);
    }

    execute(analysis) {
      this._AnalyzeService.executeAnalysis(analysis);
      this.goToAnalysis(analysis);
    }

    openDeleteModal(analysis) {
      const confirm = this._$mdDialog.confirm()
        .title('Are you sure you want to delete this analysis?')
        .textContent('Any published analyses will also be deleted.')
        .ok('Delete')
        .cancel('Cancel');

      this._$mdDialog.show(confirm).then(() => {
        return this._AnalyzeService.deleteAnalysis(analysis.id);
      }).then(data => {
        this.onCardAction('delete', data);
      });
    }

    openEditModal(mode, model) {
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
