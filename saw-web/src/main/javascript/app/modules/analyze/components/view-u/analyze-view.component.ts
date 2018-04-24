import { Component, OnInit } from '@angular/core';
import { Transition } from '@uirouter/angular';
import { LocalStorageService } from 'angular-2-local-storage';
import * as isUndefined from 'lodash/isUndefined';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { JwtService } from '../../../../../login/services/jwt.service';
import { AnalyzeService } from '../../services/analyze.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../../common/services/local-search.service';
import { Analysis } from '../../types';

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

  public analyses: Analysis[];
  public filteredAnalyses: Analysis[];
  public categoryName: Promise<string>;
  public cronJobs: any;
  public LIST_VIEW  = 'list';
  public CARD_VIEW  = 'card';
  public analysisId : string;
  public canUserCreate: boolean;
  public states = {
    viewMode: this.CARD_VIEW,
    analysisType: 'all',
    searchTerm: '',
    searchTermValue: ''
  };
  public analysisTypes = [
    ['all', 'All'],
    ['chart', 'Chart'],
    ['report', 'Report'],
    ['pivot', 'Pivot'],
    ['scheduled', 'Scheduled']
  ].map(([value, label]) => ({value, label}));
  constructor(
    private _analyzeService: AnalyzeService,
    private _headerProgress: HeaderProgressService,
    private _transition: Transition,
    private _localStorage: LocalStorageService,
    private _jwt: JwtService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService
  ) { }

  ngOnInit() {
    this.analysisId = this._transition.params().id;
    const savedView = <string>this._localStorage.get(VIEW_KEY);
    console.log('savedView', savedView);
    this.states.viewMode = [this.LIST_VIEW, this.CARD_VIEW].includes(savedView) ?
    savedView : this.LIST_VIEW;

    this.canUserCreate = this._jwt.hasPrivilege('CREATE', {
      subCategoryId: this.analysisId
    });

    this.categoryName = this._analyzeService.getCategory(this.analysisId).then(category => category.name);
    this.loadAnalyses();
    this.getCronJobs();
  }

  onViewChange(view) {
    this.states.viewMode = view;
  }

  onAnalysisTypeChange(type) {
    this.states.analysisType = type;
  }

  loadAnalyses() {
    this._headerProgress.show();
    return this._analyzeService.getAnalysesFor(this.analysisId).then(analyses => {
      this.analyses = analyses;
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
          console.log('cronJobs', this.cronJobs);
        } else {
          this.cronJobs = '';
        }
      }
    }).catch(err => {
      this._toastMessage.error(err.message);
    });
  }

  applySearchFilter(value) {
    this.states.searchTerm = value;
    const searchCriteria = this._localSearch.parseSearchTerm(this.states.searchTerm);
    this.states.searchTermValue = searchCriteria.trimmedTerm;
    this._localSearch.doSearch(searchCriteria, this.analyses, SEARCH_CONFIG).then(data => {
      this.filteredAnalyses = data;
    }, err => {
      this._toastMessage.error(err.message);
    });
  }
}
