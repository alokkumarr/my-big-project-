import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AnalyzeService,
  EXECUTION_MODES
} from '../../services/analyze.service';
import { JwtService } from '../../../../common/services/jwt.service';
import { DesignerSaveEvent, DesignerMode, isDSLAnalysis } from '../types';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../../common/types';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { ExecuteService } from '../../services/execute.service';
import { Analysis, AnalysisDSL } from '../types';
import * as filter from 'lodash/fp/filter';
import * as get from 'lodash/get';
import * as find from 'lodash/find';

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
    private jwtService: JwtService,
    public _executeService: ExecuteService
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
      this._executeService.executeAnalysis(
        analysis,
        EXECUTION_MODES.PUBLISH,
        navigateBackTo
      );

      const navigateToList = !filter(
        f => f.isRuntimeFilter,
        get(analysis, 'sqlBuilder.filters', [])
      ).length;
      if (navigateToList) {
        if (navigateBackTo === 'home') {
          this.router.navigate([
            'analyze',
            isDSLAnalysis(analysis) ? analysis.category : analysis.categoryId
          ]);
        } else {
          this.locationService.back();
        }
      }
    }
  }

  convertParamsToData(params: DesignerQueryParams) {
    this.designerMode = params.mode;

    /* If new analysis, setup a basic analysis starter */
    if (params.mode === 'new') {
      const newAnalysisParams = <NewDesignerQueryParams>params;
      this.analysisStarter = {
        categoryId: newAnalysisParams.categoryId,
        metricName: newAnalysisParams.metricName,
        semanticId: newAnalysisParams.semanticId,
        type: newAnalysisParams.type,
        chartType: newAnalysisParams.chartType,
        name: 'Untitled Analysis',
        description: '',
        scheduled: null
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
          this.analyzeService
            .getArtifactsForDataSet(analysis.semanticId)
            .then(artifacts => {
              this.analysis = this.forkIfNecessary({
                ...analysis,
                artifacts: this.fixArtifactsForSIPQuery(analysis, artifacts),
                name:
                  this.designerMode === 'fork'
                    ? `${analysis.name} Copy`
                    : analysis.name
              });
            });
        });
    }
  }

  fixArtifactsForSIPQuery(analysis, artifacts) {
    if (!isDSLAnalysis(analysis)) {
      return artifacts;
    }

    analysis.sipQuery.artifacts[0].fields.forEach(field => {
      const artifactColumn = find(
        artifacts[0].columns,
        col => col.columnName === field.columnName
      );

      if (!artifactColumn) {
        return;
      }

      artifactColumn.checked = true;
      artifactColumn.area = field.area;
      if (field.aggregate) {
        artifactColumn.aggregate = field.aggregate;
      }
    });

    return artifacts;
  }

  /**
   * If analysis is being edited and is from a public category,
   * fork it to user's private folder and add reference to original analysis.
   * User doesn't edit public analyses directly.
   *
   * This will be later used to overwrite the analysis in public folder
   * when publishing this new fork.
   *
   * @param {*} analysis
   * @returns
   * @memberof DesignerPageComponent
   */
  forkIfNecessary(analysis: Analysis | AnalysisDSL) {
    const userAnalysisCategoryId = this.jwtService.userAnalysisCategoryId.toString();
    const analysisCategoryId = isDSLAnalysis(analysis)
      ? analysis.category
      : analysis.categoryId;
    if (
      userAnalysisCategoryId === analysisCategoryId.toString() ||
      this.designerMode !== 'edit'
    ) {
      /* Analysis is from user's private folder or action is not edit.
         No special steps needed. for this.
      */
      return analysis;
    } else {
      this.designerMode = 'fork';
      return {
        ...analysis,
        categoryId: userAnalysisCategoryId,
        parentAnalysisId: analysis.id,
        parentCategoryId: analysisCategoryId
      };
    }
  }
}
