import * as template from './analyze-view.component.html';
import style from './analyze-view.component.scss';

import * as remove from 'lodash/remove';
import * as findIndex from 'lodash/findIndex';
import {Subject} from 'rxjs/Subject';
import * as isUndefined from 'lodash/isUndefined';

import {Events} from '../../consts';
import AbstractComponentController from '../../../../common/components/abstractComponent';

const SEARCH_CONFIG = [
  {keyword: 'NAME', fieldName: 'name'},
  {keyword: 'TYPE', fieldName: 'type'},
  {keyword: 'CREATOR', fieldName: 'userFullName'},
  {keyword: 'CREATED', fieldName: 'new Date(rowData.createdTimestamp).toDateString()'},
  {keyword: 'METRIC', fieldName: 'metricName'}
];

export const AnalyzeViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeViewController extends AbstractComponentController {
    constructor($injector, $compile, AnalyzeService, $state, $mdDialog, JwtService,
      toastMessage, $rootScope, localStorageService, FilterService, LocalSearchService) {
      'ngInject';
      super($injector);

      this._$compile = $compile;
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$mdDialog = $mdDialog;
      this._localStorageService = localStorageService;
      this._LocalSearchService = LocalSearchService;
      this._FilterService = FilterService;
      this._toastMessage = toastMessage;
      this._$rootScope = $rootScope;
      this._JwtService = JwtService;
      this._analysisCache = [];
      this.resp = this._JwtService.getTokenObj();

      this.LIST_VIEW = 'list';
      this.CARD_VIEW = 'card';

      const savedView = localStorageService.get('analyseReportView');

      this.states = {
        reportView: [this.LIST_VIEW, this.CARD_VIEW].indexOf(savedView) >= 0 ?
          savedView : this.LIST_VIEW,
        analysisType: 'all',
        searchTerm: ''
      };
      this.updater = new Subject();
      this.canUserCreate = false;
      this.loadCards = false;
    }

    $onInit() {
      this._destroyHandler = this.on(Events.AnalysesRefresh, () => {
        this.loadAnalyses();
      });

      this.loadCategory();
      this.loadAnalyses();
      this.canUserCreate = this._JwtService.hasPrivilege('CREATE', {
        subCategoryId: this.$state.params.id
      });
      this.getCronJobs();
    }

    getCronJobs() {
      this.resp = this._JwtService.getTokenObj();
      this.requestModel = {
        categoryId: this.$state.params.id,
        groupkey: this.resp.ticket.custCode
      };
      this._AnalyzeService.getAllCronJobs(this.requestModel).then(response => {
        this.loadCards = true;
        if (!isUndefined(response.data.data[0])) {
          this.cronSavedJobs = response.data.data;
        } else {
          this.cronSavedJobs = '';
        }
      });
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
      this._$state.go('analyze.executedDetail', {analysisId: analysis.id, analysis});
    }

    goToLastPublishedAnalysis(analysis) {
      this.goToAnalysis(analysis);
    }

    loadAnalyses() {
      this._$rootScope.showProgress = true;
      return this._AnalyzeService.getAnalysesFor(this.$state.params.id, {
        filter: this.states.searchTerm
      }).then(analyses => {
        this._analysisCache = analyses;
        this.analyses = analyses;
        this.updater.next({analyses});
        this._$rootScope.showProgress = false;
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }

    applySearchFilter() {
      const searchCriteria = this._LocalSearchService.parseSearchTerm(this.states.searchTerm);
      this.states.searchTermValue = searchCriteria.trimmedTerm;
      this._LocalSearchService.doSearch(searchCriteria, this._analysisCache, SEARCH_CONFIG).then(data => {
        this.analyses = data;
        this.updater.next({analyses: this.analyses});
      }, err => {
        this._toastMessage.error(err.message);
      });
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

    removeDeletedAnalysis(analysis) {
      remove(this.analyses, report => {
        return report.id === analysis.id;
      });
      this.updater.next({analyses: this.analyses});
    }

    /* ACTIONS */

    onCardAction(actionType, payload) {
      switch (actionType) {
      case 'onSuccessfulDeletion':
        this.removeDeletedAnalysis(payload);
        break;
      case 'onSuccessfulExecution':
        this.goToAnalysis(payload);
        break;
      case 'onSuccessfulPublish':
        this.onSuccessfulPublish(payload);
        break;
      case 'view':
        this.view(payload);
        break;
      default:
      }
    }

    onSuccessfulPublish(analysis) {
      this.getCronJobs();
      /* Update the new analysis in the current list */
      const analysisId = findIndex(this.analyses, ({id}) => {
        return id === analysis.id;
      });
      this.analyses.splice(analysisId, 1, analysis);
      this.updater.next({analyses: this.analyses});
      this._$state.go('analyze.view', {id: this.$state.params.id});
    }

    view(analysisId) {
      this.goToLastPublishedAnalysis(analysisId);
    }
  }
};
