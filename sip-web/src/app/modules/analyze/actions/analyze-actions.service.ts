import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef, MatDialogConfig } from '@angular/material';
import { ToastService } from '../../../common/services/toastMessage.service';
import { AnalyseTypes } from '../consts';
import { AnalyzeDialogService } from '../services/analyze-dialog.service';
import { AnalyzeService } from '../services/analyze.service';
import { ExecuteService } from '../services/execute.service';
import { PublishService } from '../services/publish.service';
import { AnalysisDSL } from '../types';
import { AnalyzePublishDialogComponent } from '../publish/dialog/analyze-publish';
import { AnalyzeScheduleDialogComponent } from '../publish/dialog/analyze-schedule';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog';
import { Store } from '@ngxs/store';

import * as clone from 'lodash/clone';

import {
  EXECUTION_MODES,
  EXECUTION_DATA_MODES
} from '../services/analyze.service';

@Injectable()
export class AnalyzeActionsService {
  constructor(
    public _analyzeService: AnalyzeService,
    public _executeService: ExecuteService,
    public _publishService: PublishService,
    public _analyzeDialogService: AnalyzeDialogService,
    public _toastMessage: ToastService,
    public dialog: MatDialog,
    private _store: Store
  ) {}

  execute(analysis, mode = EXECUTION_MODES.LIVE) {
    return this._executeService.executeAnalysis(analysis, mode);
  }

  fork(analysis) {
    return this.openEditModal(analysis, 'fork');
  }

  edit(analysis) {
    return this.openEditModal(analysis, 'edit');
  }

  publish(analysis) {
    return this.openPublishModal(clone(analysis));
  }

  schedule(analysis) {
    return this.openScheduleModal(clone(analysis));
  }

  delete(analysis) {
    return this.openDeleteModal(analysis);
  }

  openDeleteModal(analysis) {
    return new Promise<boolean | void>(resolve => {
      this._analyzeDialogService
        .openDeleteConfirmationDialog()
        .afterClosed()
        .subscribe({
          next: result => {
            if (result) {
              this.removeAnalysis(analysis).then(deletionSuccess => {
                resolve(deletionSuccess);
              });
            } else {
              resolve(false);
            }
          },
          error: () => {
            resolve(false);
          }
        });
    });
  }

  openEditModal(analysis, mode: 'edit' | 'fork') {
    /* prettier-ignore */
    switch (analysis.type) {
    case AnalyseTypes.Chart:
    case AnalyseTypes.ESReport:
    case AnalyseTypes.Report:
    case AnalyseTypes.Pivot:
    case AnalyseTypes.Map:
      return this._analyzeDialogService
        .openEditAnalysisDialog(analysis, mode);
    default:
    }
  }

  showPublishOverwriteConfirmation(): MatDialogRef<
    ConfirmDialogComponent,
    any
  > {
    return this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Original analysis has been modified.',
        content:
          'Do you want to overwrite original analysis with your changes?',
        negativeActionLabel: 'Cancel',
        positiveActionLabel: 'Publish'
      }
    });
  }

  publishAnalysis(analysis: AnalysisDSL, lastCategoryId: number | string) {
    return this._publishService.publishAnalysis(analysis, lastCategoryId);
  }

  openPublishModal(analysis: AnalysisDSL) {
    return new Promise<AnalysisDSL>((resolve, reject) => {
      this.dialog
        .open(AnalyzePublishDialogComponent, {
          width: 'auto',
          height: 'auto',
          data: { analysis: clone(analysis) }
        } as MatDialogConfig)
        .afterClosed()
        .subscribe(modifiedAnalysis => {
          if (modifiedAnalysis) {
            this.publishAnalysis(modifiedAnalysis, analysis.category).then(
              publishedAnalysis => {
                if (!publishedAnalysis) {
                  return reject();
                }
                this._toastMessage.info('Analysis has been updated.');
                resolve(publishedAnalysis);
              },
              () => {
                reject();
              }
            );
          }
        });
    });
  }

  openScheduleModal(analysis: AnalysisDSL) {
    return new Promise<AnalysisDSL>((resolve, reject) => {
      this.dialog
        .open(AnalyzeScheduleDialogComponent, {
          width: 'auto',
          height: 'auto',
          minWidth: '750px',
          autoFocus: false,
          data: { analysis }
        } as MatDialogConfig)
        .afterClosed()
        .subscribe(schedule => {
          if (schedule) {
            this._publishService.scheduleAnalysis(schedule).then(
              () => {
                this._toastMessage.info(
                  'Analysis schedule changes have been updated.'
                );
                resolve(analysis);
              },
              () => {
                reject();
              }
            );
          }
        });
    });
  }

  removeAnalysis(analysis) {
    // Delete schedule if exists
    const cronJobs = this._store.selectSnapshot(state => state.common.jobs);
    if (cronJobs && cronJobs[analysis.id]) {
      const deleteScheduleBody = {
        scheduleState: 'delete',
        jobName: analysis.id,
        groupName: analysis.customerCode,
        categoryId: analysis.category
      };
      this._analyzeService.changeSchedule(deleteScheduleBody);
    }
    return this._analyzeService.deleteAnalysis(analysis).then(
      () => {
        this._toastMessage.info('Analysis deleted.');
        return true;
      },
      err => {
        this._toastMessage.error(err.message || 'Analysis not deleted.');
      }
    );
  }

  exportAnalysis(
    analysisId,
    executionId,
    analysisType,
    executionType = EXECUTION_DATA_MODES.NORMAL
  ) {
    return this._analyzeService.getExportData(
      analysisId,
      executionId,
      analysisType,
      executionType
    );
  }
}
