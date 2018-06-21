import { Component, OnInit, Inject } from '@angular/core';
import { Transition, StateService } from '@uirouter/angular';
import { LocalStorageService } from 'angular-2-local-storage';
import * as isUndefined from 'lodash/isUndefined';
import * as findIndex from 'lodash/findIndex';
import { HeaderProgressService } from '../../../common/services/header-progress.service';
import { JwtService } from '../../../../login/services/jwt.service';
import { AnalyzeService } from '../services/analyze.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';
import { Analysis, AnalyzeViewActionEvent } from './types';

const template = require('./analyze-view.component.html');
require('./analyze-view.component.scss');

const VIEW_KEY = 'analyseReportView';
const SEARCH_CONFIG = [
  {keyword: 'NAME', fieldName: 'name'},
  {keyword: 'TYPE', fieldName: 'type'},
  {keyword: 'CREATOR', fieldName: 'userFullName'},
  {keyword: 'CREATED', fieldName: 'new Date(rowData.createdTimestamp).toDateString()'},
  {keyword: 'METRIC', fieldName: 'metricName'}
];
@Component({
  selector: 'analyze-view-u',
  template
})
export class AnalyzeViewComponent implements OnInit {

  public analyses: Analysis[] = [];
  public filteredAnalyses: Analysis[];
  public categoryName: Promise<string>;
  public cronJobs: any;
  public LIST_VIEW  = 'list';
  public CARD_VIEW  = 'card';
  public analysisId : string;
  public canUserCreate: boolean;
  public viewMode = this.LIST_VIEW;
  public analysisTypes = [
    ['all', 'All'],
    ['chart', 'Chart'],
    ['report', 'Report'],
    ['pivot', 'Pivot'],
    ['scheduled', 'Scheduled']
  ].map(([value, label]) => ({value, label}));
  public filterObj = {
    analysisType: this.analysisTypes[0].value,
    searchTerm: '',
    searchTermValue: ''
  };
  constructor(
    private _analyzeService: AnalyzeService,
    private _headerProgress: HeaderProgressService,
    private _transition: Transition,
    private _state: StateService,
    private _localStorage: LocalStorageService,
    private _jwt: JwtService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService,
    @Inject('$mdDialog') private _$mdDialog: any
  ) { }

  ngOnInit() {
    this.analysisId = this._transition.params().id;
    const savedView = <string>this._localStorage.get(VIEW_KEY);
    this.viewMode = [this.LIST_VIEW, this.CARD_VIEW].includes(savedView) ?
    savedView : this.LIST_VIEW;

    this.canUserCreate = this._jwt.hasPrivilege('CREATE', {
      subCategoryId: this.analysisId
    });

    this.categoryName = this._analyzeService.getCategory(this.analysisId).then(category => category.name);
    this.loadAnalyses();
    this.getCronJobs();
  }

  onAction(event: AnalyzeViewActionEvent) {
    switch(event.action) {
    case 'fork':
      this.loadAnalyses();
      break;
    case 'edit':
      this.spliceAnalyses(event.analysis, true);
      break;
    case 'delete':
      this.spliceAnalyses(event.analysis, false);
      break;
    case 'execute':
      this.goToAnalysis(event.analysis);
      break;
    case 'publish':
      this.afterPublish(event.analysis);
      this.spliceAnalyses(event.analysis, true);
      break;
    }
  }

  onViewChange(view) {
    this.viewMode = view;
    this._localStorage.set(VIEW_KEY, view);
  }

  onAnalysisTypeChange(type) {
    this.filterObj.analysisType = type;
  }

  goToAnalysis(analysis) {
    this._state.go('analyze.executedDetail', {analysisId: analysis.id, analysis});
  }

  afterPublish(analysis) {
    this.getCronJobs();
    /* Update the new analysis in the current list */
    const index = findIndex(this.analyses, ({id}) => id === analysis.id);
    this.analyses.splice(index, 1, analysis);
    this._state.go('analyze.view', {id: analysis.categoryId});
  }

  spliceAnalyses(analysis, replace) {
    const index = findIndex(this.analyses, ({id}) => id === analysis.id);
    const filteredIndex = findIndex(this.filteredAnalyses, ({id}) => id === analysis.id);
    if (replace) {
      this.analyses.splice(index, 1, analysis);
      this.filteredAnalyses.splice(filteredIndex, 1, analysis);
    } else {
      this.analyses.splice(index, 1);
      this.filteredAnalyses.splice(filteredIndex, 1);
    }
  }

  openNewAnalysisModal() {
    this._headerProgress.show();
    this._analyzeService.getSemanticLayerData().then(metrics => {
      this._headerProgress.hide();
      this._$mdDialog.show({
        controller: scope => {
          scope.metrics = metrics;
          scope.subCategory = this.analysisId;
        },
        template: '<analyze-new metrics="metrics" sub-category="{{::subCategory}}"></analyze-new>',
        fullscreen: true,
        controllerAs: '$ctrl',
        multiple: false,
        autoWrap: false,
        focusOnOpen: false,
        clickOutsideToClose: true
      }).then(result => {
        if (result) {
          this.loadAnalyses();
        }
      });
    }).catch(() => {
      this._headerProgress.hide();
    });
  }

  loadAnalyses() {
    this._headerProgress.show();
    return this._analyzeService.getAnalysesFor(this.analysisId).then(analyses => {
      this.analyses = analyses;
      this.filteredAnalyses = [...analyses];
      this._headerProgress.hide();
    }).catch(() => {
      this._headerProgress.hide();
    });
  }

  getCronJobs() {
    const token = this._jwt.getTokenObj();
    const requestModel = {
      categoryId: this.analysisId,
      groupkey: token.ticket.custCode
    };
    this._analyzeService.getAllCronJobs(requestModel).then(response => {
      if (response.statusCode === 200) {
        if (!isUndefined(response)) {
          this.cronJobs = response.data;
        } else {
          this.cronJobs = '';
        }
      }
    }).catch(err => {
      this._toastMessage.error(err.message);
    });
  }

  applySearchFilter(value) {
    this.filterObj.searchTerm = value;
    const searchCriteria = this._localSearch.parseSearchTerm(this.filterObj.searchTerm);
    this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
    this._localSearch.doSearch(searchCriteria, this.analyses, SEARCH_CONFIG).then(data => {
      this.filteredAnalyses = data;
    }, err => {
      this._toastMessage.error(err.message);
    });
  }
}
