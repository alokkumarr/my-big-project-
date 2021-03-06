import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { Store } from '@ngxs/store';
import * as findIndex from 'lodash/findIndex';
import * as reduce from 'lodash/reduce';
import * as values from 'lodash/values';
import * as isEmpty from 'lodash/isEmpty';
import { JwtService } from '../../../common/services';
import { AnalyzeService, EXECUTION_MODES } from '../services/analyze.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';
import { AnalyzeNewDialogComponent } from './new-dialog';
import { Analysis, AnalysisDSL, AnalyzeViewActionEvent } from './types';
import { ExecuteService } from '../services/execute.service';
import { isDSLAnalysis } from '../designer/types';
import {
  CommonLoadAllMetrics,
  CommonStateScheuleJobs
} from 'src/app/common/actions/common.actions';
import { first, map, tap, switchMap } from 'rxjs/operators';

const VIEW_KEY = 'analyseReportView';
const SEARCH_CONFIG = [
  { keyword: 'NAME', fieldName: 'name' },
  { keyword: 'TYPE', fieldName: 'type' },
  { keyword: 'CREATOR', fieldName: 'userFullName' },
  {
    keyword: 'CREATED',
    fieldName: 'new Date(rowData.createdTimestamp).toDateString()'
  },
  { keyword: 'METRIC', fieldName: 'metricName' }
];
@Component({
  selector: 'analyze-view-u',
  templateUrl: './analyze-view.component.html',
  styleUrls: ['./analyze-view.component.scss']
})
export class AnalyzeViewComponent implements OnInit {
  public analyses: Array<Analysis | AnalysisDSL> = [];
  public filteredAnalyses: Array<Analysis | AnalysisDSL>;
  public categoryName: Promise<string>;
  public cronJobs: any;
  public LIST_VIEW = 'list';
  public CARD_VIEW = 'card';
  public canUserCreate: boolean;
  public viewMode = this.LIST_VIEW;
  public privileges = {
    create: false
  };
  public subCategoryId: string;
  public analysisTypes = [
    ['all', 'All'],
    ['chart', 'Chart'],
    ['report', 'Report'],
    ['pivot', 'Pivot'],
    ['map', 'Map'],
    ['scheduled', 'Scheduled']
  ].map(([value, label]) => ({ value, label }));
  public filterObj = {
    analysisType: this.analysisTypes[0].value,
    searchTerm: '',
    searchTermValue: ''
  };
  constructor(
    public _analyzeService: AnalyzeService,
    public _router: Router,
    public _route: ActivatedRoute,
    public _jwt: JwtService,
    public _localSearch: LocalSearchService,
    public _toastMessage: ToastService,
    public _dialog: MatDialog,
    public _executeService: ExecuteService,
    private store: Store
  ) {
    const metrics$ = this.store
      .select(state => state.common.metrics)
      .pipe(first(metrics => values(metrics).length > 0));
    let analyses;
    this._route.data
      .pipe(
        switchMap(data => {
          analyses = data.analyses;
          return metrics$;
        }),
        tap(() => this.prepareLoadedAnalyses(analyses))
      )
      .subscribe();
  }

  ngOnInit() {
    this._route.params.subscribe(params => {
      this.onParamsChange(params);
    });
    const savedView = <string>localStorage.getItem(VIEW_KEY);
    this.viewMode = [this.LIST_VIEW, this.CARD_VIEW].includes(savedView)
      ? savedView
      : this.LIST_VIEW;
  }

  onParamsChange(params) {
    this.subCategoryId = params.id;
    const subCategoryId = this.subCategoryId;
    const privilegeName = 'CREATE';
    const hasPrivilegeForCurrentFolder = this._jwt.hasPrivilege(privilegeName, {
      subCategoryId
    });
    const hasPrivilegeForDraftsFolder = this._jwt.hasPrivilegeForDraftsFolder(
      privilegeName
    );
    this.canUserCreate =
      hasPrivilegeForCurrentFolder && hasPrivilegeForDraftsFolder;
    this.categoryName = this._analyzeService
      .getCategory(this.subCategoryId)
      .then(category => category.name);

    this.getCronJobs(this.subCategoryId);
  }

  onAction(event: AnalyzeViewActionEvent) {
    switch (event.action) {
      case 'fork': {
        const { analysis, requestExecution } = event;
        if (analysis) {
          this.loadAnalyses(this.subCategoryId).then(() => {
            if (requestExecution) {
              this._executeService.executeAnalysis(
                analysis,
                EXECUTION_MODES.PUBLISH
              );
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
          this._executeService.executeAnalysis(
            analysis,
            EXECUTION_MODES.PUBLISH
          );
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
    localStorage.setItem(VIEW_KEY, view);
  }

  onAnalysisTypeChange(type) {
    this.filterObj.analysisType = type;
  }

  goToAnalysis(analysis) {
    const isDSL = analysis.sipQuery ? true : false;
    this._router.navigate(['analyze', 'analysis', analysis.id, 'executed'], {
      queryParams: {
        isDSL,
        awaitingExecution: true
      }
    });
  }

  afterPublish(analysis) {
    const categroyID = isDSLAnalysis(analysis)
      ? analysis.category
      : analysis.categoryId;
    this.getCronJobs(categroyID).then(() => {
      // Wait till cron job and schedule is created or updated or deleted successfully.
      /* Update the new analysis in the current list */
      this._router.navigate(['analyze', categroyID]);
    });
  }

  spliceAnalyses(analysis, replace) {
    const index = findIndex(this.analyses, ({ id }) => id === analysis.id);
    const filteredIndex = findIndex(
      this.filteredAnalyses,
      ({ id }) => id === analysis.id
    );
    if (replace) {
      this.analyses.splice(index, 1, analysis);
      this.filteredAnalyses.splice(filteredIndex, 1, analysis);
    } else {
      this.analyses.splice(index, 1);
      this.filteredAnalyses.splice(filteredIndex, 1);
    }
  }

  openNewAnalysisModal() {
    const metrics = this.store.selectSnapshot(state => state.common.metrics);
    if (isEmpty(metrics)) {
      return false;
    }
    this.store.dispatch(new CommonLoadAllMetrics());
    this.store
      .select(state => state.common.metrics)
      .pipe(
        first(metrics => values(metrics).length > 0),
        map(metrics => values(metrics))
      )
      .toPromise()
      .then(metrics => {
        this._dialog
          .open(AnalyzeNewDialogComponent, {
            width: 'auto',
            height: 'auto',
            autoFocus: false,
            data: {
              metrics,
              id: this.subCategoryId
            }
          } as MatDialogConfig)
          .afterClosed()
          .subscribe(event => {
            if (!event) {
              return;
            }
            const { analysis, requestExecution } = event;
            if (analysis) {
              this.loadAnalyses(this.subCategoryId).then(() => {
                if (requestExecution) {
                  this._executeService.executeAnalysis(
                    analysis,
                    EXECUTION_MODES.PUBLISH
                  );
                }
              });
            }
          });
      });
  }

  prepareLoadedAnalyses(analyses) {
    this.analyses = this.applyMetricNames(analyses);
    this.filteredAnalyses = [...this.analyses];
  }

  loadAnalyses(subCategoryId) {
    return this.store
      .dispatch(new CommonLoadAllMetrics())
      .pipe(switchMap(() => this._analyzeService.getAnalysesFor(subCategoryId)))
      .toPromise()
      .then(this.prepareLoadedAnalyses.bind(this));
  }

  applyMetricNames(analyses: Array<Analysis | AnalysisDSL>) {
    const metrics = this.store.selectSnapshot(state => state.common.metrics);
    return analyses.map(analysis => ({
      ...analysis,
      metricName: metrics[analysis.semanticId]
        ? metrics[analysis.semanticId].metricName
        : ''
    }));
  }

  getCronJobs(analysisId) {
    const token = this._jwt.getTokenObj();
    const requestModel = {
      categoryId: analysisId,
      groupkey: token.ticket.custCode
    };
    return this._analyzeService
      .getAllCronJobs(requestModel)
      .then((response: any) => {
        if (response.statusCode === 200) {
          this.cronJobs = reduce(
            response.data,
            (accumulator, cron) => {
              const { analysisID } = cron.jobDetails;
              accumulator[analysisID] = cron;
              return accumulator;
            },
            {}
          );
          this.store.dispatch(new CommonStateScheuleJobs(this.cronJobs));
        } else {
          this.cronJobs = {};
        }
      })
      .catch(err => {
        this._toastMessage.error(err.message);
      });
  }

  applySearchFilter(value) {
    this.filterObj.searchTerm = value;
    const searchCriteria = this._localSearch.parseSearchTerm(
      this.filterObj.searchTerm
    );
    this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
    this._localSearch
      .doSearch(searchCriteria, this.analyses, SEARCH_CONFIG)
      .then(
        data => {
          this.filteredAnalyses = data;
        },
        err => {
          this._toastMessage.error(err.message);
        }
      );
  }
}
