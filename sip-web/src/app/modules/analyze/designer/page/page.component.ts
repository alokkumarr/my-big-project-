import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AnalyzeService,
  EXECUTION_MODES
} from '../../services/analyze.service';
import { Store } from '@ngxs/store';
import { DesignerSaveEvent, DesignerMode, isDSLAnalysis } from '../types';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../../common/types';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { ExecuteService } from '../../services/execute.service';
import * as filter from 'lodash/fp/filter';
import * as get from 'lodash/get';
import * as cloneDeep from 'lodash/cloneDeep';
import { DesignerLoadMetric } from '../actions/designer.actions';
import { DesignerService } from '../designer.service';

const CONFIRM_DIALOG_DATA: ConfirmDialogData = {
  title: 'There are unsaved changes',
  content: 'Do you want to discard unsaved changes and go back?',
  positiveActionLabel: 'Discard',
  negativeActionLabel: 'Cancel'
};

interface NewDesignerQueryParams {
  mode: DesignerMode;
  categoryId: string;
  metricName: string;
  semanticId: string;
  type: string;
  chartType?: string;
  supports: string;
}

interface ExistingDesignerQueryParams {
  mode: DesignerMode;
  analysisId: string;
  isDSLAnalysis: string;
}

type DesignerQueryParams = NewDesignerQueryParams | ExistingDesignerQueryParams;

@Component({
  selector: 'designer-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.scss']
})
export class DesignerPageComponent implements OnInit {
  analysis: any;
  analysisStarter: any;
  designerMode: string;

  constructor(
    private locationService: Location,
    private analyzeService: AnalyzeService,
    private dialogService: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
    public _executeService: ExecuteService,
    private designerService: DesignerService,
    private store: Store
  ) {}

  ngOnInit() {
    const params = this.route.snapshot.queryParams as DesignerQueryParams;
    this.convertParamsToData(params);
  }

  warnUser() {
    return this.dialogService.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data: CONFIRM_DIALOG_DATA
    } as MatDialogConfig);
  }

  onBack(isInDraftMode) {
    if (isInDraftMode) {
      this.warnUser()
        .afterClosed()
        .subscribe(shouldDiscard => {
          if (shouldDiscard) {
            this.locationService.back();
          }
        });
    } else {
      this.locationService.back();
    }
  }

  onSave({ analysis, requestExecution }: DesignerSaveEvent) {
    const navigateBackTo =
      this.designerMode === 'fork' || this.designerMode === 'new'
        ? 'home'
        : 'back';
    if (requestExecution) {
      this._executeService
        .executeAnalysis(analysis, EXECUTION_MODES.PUBLISH, navigateBackTo)
        .then(executionSuccess => {
          /* If all non-optional prompt filters aren't applied, don't let user
             exit the designer */
          if (!executionSuccess) {
            return;
          }
          const navigateToList = !filter(
            f => f.isRuntimeFilter,
            get(analysis, 'sqlBuilder.filters', [])
          ).length;
          if (navigateToList) {
            if (navigateBackTo === 'home') {
              this.router.navigate([
                'analyze',
                isDSLAnalysis(analysis)
                  ? analysis.category
                  : analysis.categoryId
              ]);
            } else {
              // For DSL analysis, if on view analysis page we have loaded a previous execution and enter edit mode and save and close,
              // we need to always navigate to the latest execution and not to the previously selected execution result.
              if (isDSLAnalysis(analysis)) {
                this.router.navigateByUrl(
                  `/analyze/analysis/${
                    analysis.id
                  }/executed?isDSL=${isDSLAnalysis(analysis)}`
                );
              } else {
                this.locationService.back();
              }
            }
          }
        });
    }
  }

  convertParamsToData(params: DesignerQueryParams) {
    this.designerMode = params.mode;

    /* If new analysis, setup a basic analysis starter */
    if (params.mode === 'new') {
      const newAnalysisParams = <NewDesignerQueryParams>params;
      const supports = newAnalysisParams.supports
        ? JSON.parse(newAnalysisParams.supports)
        : [];
      this.analysisStarter = {
        categoryId: newAnalysisParams.categoryId,
        metricName: newAnalysisParams.metricName,
        semanticId: newAnalysisParams.semanticId,
        type: newAnalysisParams.type,
        chartType: newAnalysisParams.chartType,
        name: 'Untitled Analysis',
        description: '',
        scheduled: null,
        supports
      };

      /* Else, load existing analysis */
    } else {
      const existingAnalysisParams = <ExistingDesignerQueryParams>params;
      this.analyzeService
        .readAnalysis(
          existingAnalysisParams.analysisId,
          existingAnalysisParams.isDSLAnalysis === 'true'
        )
        .then(analysis => {
          return this.analyzeService
            .getArtifactsForDataSet(analysis.semanticId)
            .toPromise()
            .then(metric => {
              this.store.dispatch(
                new DesignerLoadMetric({
                  metricName: metric.metricName,
                  artifacts: metric.artifacts
                })
              );
              const { artifacts } = this.store.selectSnapshot(
                state => state.designerState.metric
              );
              this.analysis = {
                ...analysis,
                artifacts: this.fixArtifactsForSIPQuery(
                  analysis,
                  cloneDeep(artifacts)
                ),
                name:
                  this.designerMode === 'fork'
                    ? `${analysis.name} Copy`
                    : analysis.name
              };
            });
        });
    }
  }

  fixArtifactsForSIPQuery(analysis, artifacts) {
    if (!isDSLAnalysis(analysis) || analysis.designerEdit) {
      return artifacts;
    }

    return this.designerService.addDerivedMetricsToArtifacts(
      artifacts,
      analysis.sipQuery
    );
  }
}
