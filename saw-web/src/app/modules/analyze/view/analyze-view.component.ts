import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {MatDialog, MatDialogConfig} from '@angular/material';
import { LocalStorageService } from 'angular-2-local-storage';
import * as isUndefined from 'lodash/isUndefined';
import * as findIndex from 'lodash/findIndex';

import { JwtService } from '../../../common/services';
import { AnalyzeService, EXECUTION_MODES } from '../services/analyze.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';
import { AnalyzeNewDialogComponent } from './new-dialog';
import { Analysis, AnalyzeViewActionEvent } from './types';
import { ExecuteService } from '../services/execute.service';

const template = require('./analyze-view.component.html');
const style = require('./analyze-view.component.scss');

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
  template,
  styles: [
    `:host {width: 100%;}`,
    style
  ]
})
export class AnalyzeViewComponent implements OnInit {

  public analyses: Analysis[] = [];
  public filteredAnalyses: Analysis[];
  public categoryName: Promise<string>;
  public cronJobs: any;
  public LIST_VIEW  = 'list';
  public CARD_VIEW  = 'card';
  public analysisId: string;
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
    private _router: Router,
    private _route: ActivatedRoute,
    private _localStorage: LocalStorageService,
    private _jwt: JwtService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService,
    public _dialog: MatDialog,
    private _executeService: ExecuteService
  ) { }

  ngOnInit() {
    this._route.params.subscribe(params => {
      this.onParamsChange(params);
    });
    const savedView = <string>this._localStorage.get(VIEW_KEY);
    this.viewMode = [this.LIST_VIEW, this.CARD_VIEW].includes(savedView) ?
    savedView : this.LIST_VIEW;
  }

  onParamsChange(params) {
    this.analysisId = params.id;

    this.canUserCreate = this._jwt.hasPrivilege('CREATE', {
      subCategoryId: this.analysisId
    });

    this.categoryName = this._analyzeService.getCategory(this.analysisId)
      .then(category => category.name);

    this.loadAnalyses(this.analysisId);
    this.getCronJobs(this.analysisId);
  }

  onAction(event: AnalyzeViewActionEvent) {
    switch (event.action) {
    case 'fork': {
      const { analysis, requestExecution } = event;
      if (analysis) {
        this.loadAnalyses(this.analysisId).then(() => {
          if (requestExecution) {
            this._executeService.executeAnalysis(analysis, EXECUTION_MODES.PUBLISH);
          }
        });
      }
      break;
    }
    case 'edit': {
      const { analysis, requestExecution } = event;
      if (analysis) {
        this.spliceAnalyses(analysis, true);
      }
      if (requestExecution) {
        this._executeService.executeAnalysis(analysis, EXECUTION_MODES.PUBLISH);
      }
      break;
    }
    case 'delete':
      this.spliceAnalyses(event.analysis, false);
      break;
    case 'execute':
      if (event.analysis) {
        this.goToAnalysis(event.analysis);
      }
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
    this._router.navigate(
      ['analyze', 'analysis', analysis.id, 'executed'], {
        queryParams: {
          executedAnalysis: null,
          awaitingExecution: true,
          loadLastExecution: false
        }
      }
    );
  }

  afterPublish(analysis) {
    this.getCronJobs(this.analysisId);
    /* Update the new analysis in the current list */
    this._router.navigate(
      ['analyze', analysis.categoryId]
    );
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
    this._analyzeService.getSemanticLayerData().then(metrics => {
      this._dialog.open(AnalyzeNewDialogComponent, {
        width: 'auto',
        height: 'auto',
        autoFocus: false,
        data: {
          metrics,
          id: this.analysisId
        }
      } as MatDialogConfig).afterClosed().subscribe(event => {
        const { analysis, requestExecution } = event;
        if (analysis) {
          this.loadAnalyses(this.analysisId).then(() => {
            if (requestExecution) {
              this._executeService.executeAnalysis(analysis, EXECUTION_MODES.PUBLISH);
            }
          });
        }
      });
    });
  }

  loadAnalyses(analysisId) {
    return this._analyzeService.getAnalysesFor(analysisId).then(analyses => {
      this.analyses = analyses;
      this.filteredAnalyses = [...analyses];
    });
  }

  getCronJobs(analysisId) {
    const token = this._jwt.getTokenObj();
    const requestModel = {
      categoryId: analysisId,
      groupkey: token.ticket.custCode
    };
    this._analyzeService.getAllCronJobs(requestModel).then((response: any) => {
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
