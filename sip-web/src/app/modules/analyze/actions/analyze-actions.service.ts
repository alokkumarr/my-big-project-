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
import { CUSTOM_HEADERS } from '../../../common/consts';
import { Store } from '@ngxs/store';

import * as clone from 'lodash/clone';
import * as omit from 'lodash/omit';

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

  publish(analysis, type) {
    return this.openPublishModal(clone(analysis), type);
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

  overwrite(
    parentAnalysis: AnalysisDSL,
    childAnalysis: AnalysisDSL
  ): AnalysisDSL {
    const preserveFields = [
      'id',
      'createdBy',
      'userFullName',
      'createdTimestamp',
      'parentAnalysisId',
      'parentCategoryId',
      'parentLastModfied'
    ];
    return { ...parentAnalysis, ...omit(childAnalysis, preserveFields) };
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

  publishAnalysis(
    analysis: AnalysisDSL,
    execute,
    type,
    lastCategoryId: number | string
  ) {
    const publish = (a = analysis) =>
      this._publishService.publishAnalysis(a, execute, type, lastCategoryId);

    const overwriteParent = (parent, child) => {
      /* Update parent analysis with child's changes, then delete child. */
      const modifiedParent = this.overwrite(parent, child);
      return publish(modifiedParent)
        .then(() => {
          return this._analyzeService.deleteAnalysis(child);
        })
        .then(() => modifiedParent);
    };

    /* This is not a fork-to-edit analysis. Publish this normally */
    if (!analysis.parentAnalysisId) {
      return publish();
    }

    return this._analyzeService
      .readAnalysis(analysis.parentAnalysisId, !!(analysis as any).sipQuery, {
        [CUSTOM_HEADERS.SKIP_TOAST]: '1'
      })
      .then(
        parentAnalysis => {
          if (!parentAnalysis) {
            return publish();
          }
          /* The destination category is different from parent analysis's category. Publish it normally */
          const parentAnalysisCategoryId = parentAnalysis.category;
          const childAnalysisCategoryId = analysis.category;
          if (
            parentAnalysisCategoryId.toString() !==
            childAnalysisCategoryId.toString()
          ) {
            return publish();
          }

          /* If the parent has been modified since fork/editing, allow user to choose whether
           they want to overwrite the parent analysis */
          if (analysis.updatedTimestamp < parentAnalysis.updatedTimestamp) {
            return this.showPublishOverwriteConfirmation()
              .afterClosed()
              .toPromise()
              .then(shouldPublish => {
                if (shouldPublish) {
                  return overwriteParent(parentAnalysis, analysis);
                } else {
                  return null;
                }
              });
          }
          return overwriteParent(parentAnalysis, analysis);
        },
        err => {
          delete analysis.parentAnalysisId;
          delete analysis.parentCategoryId;
          delete analysis.parentLastModified;

          return publish();
        }
      );
  }

  openPublishModal(analysis: AnalysisDSL, type) {
    switch (type) {
      case 'publish':
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
                const execute = true;
                this.publishAnalysis(
                  modifiedAnalysis,
                  execute,
                  type,
                  analysis.category
                ).then(
                  publishedAnalysis => {
                    if (!publishedAnalysis) {
                      return reject();
                    }
                    this._toastMessage.info(
                      execute
                        ? 'Analysis has been updated.'
                        : 'Analysis schedule changes have been updated.'
                    );
                    resolve(publishedAnalysis);
                  },
                  () => {
                    reject();
                  }
                );
              }
            });
        });
        break;

      case 'schedule':
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
            .subscribe(scheduledAnalysis => {
              if (scheduledAnalysis) {
                const execute = false;
                this._publishService
                  .publishAnalysis(scheduledAnalysis, execute, type)
                  .then(
                    updatedAnalysis => {
                      this._toastMessage.info(
                        execute
                          ? 'Analysis has been updated.'
                          : 'Analysis schedule changes have been updated.'
                      );
                      resolve(updatedAnalysis);
                    },
                    () => {
                      reject();
                    }
                  );
              }
            });
        });
        break;
    }
  }

  removeAnalysis(analysis) {
    // Delete schedule if exists
    const cronJobs = this._store.selectSnapshot(state => state.common.jobs);
    if (cronJobs && cronJobs[analysis.id]) {
      const deleteScheduleBody = {
        scheduleState: 'delete',
        jobName: analysis.id,
        groupName: analysis.customerCode,
        categoryId: analysis.categoryId
      };
      analysis.schedule = deleteScheduleBody;
      this._analyzeService.changeSchedule(analysis);
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
