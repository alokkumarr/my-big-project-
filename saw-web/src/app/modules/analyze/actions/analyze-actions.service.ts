import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import * as clone from 'lodash/clone';
import { ToastService } from '../../../common/services/toastMessage.service';
import { AnalyseTypes } from '../consts';
import { AnalyzeDialogService } from '../services/analyze-dialog.service';
import { AnalyzeService } from '../services/analyze.service';
import { ExecuteService } from '../services/execute.service';
import { PublishService } from '../services/publish.service';
import { Analysis } from '../types';
import { AnalyzePublishDialogComponent } from '../publish/dialog/analyze-publish';
import { AnalyzeScheduleDialogComponent } from '../publish/dialog/analyze-schedule';

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
    public dialog: MatDialog
  ) {}

  execute(analysis, mode = EXECUTION_MODES.LIVE) {
    return this._executeService.executeAnalysis(analysis, mode);
  }

  fork(analysis) {
    const model = clone(analysis);
    model.name += ' Copy';
    return this.openEditModal(model, 'fork');
  }

  edit(analysis) {
    return this.openEditModal(clone(analysis), 'edit');
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
      return this._analyzeDialogService
        .openEditAnalysisDialog(analysis, mode);
    default:
    }
  }

  openPublishModal(analysis, type) {
    switch (type) {
      case 'publish':
        return new Promise<Analysis>((resolve, reject) => {
          this.dialog
            .open(AnalyzePublishDialogComponent, {
              width: 'auto',
              height: 'auto',
              data: { analysis }
            } as MatDialogConfig)
            .afterClosed()
            .subscribe(modifiedAnalysis => {
              if (analysis) {
                const execute = true;
                this._publishService
                  .publishAnalysis(modifiedAnalysis, execute, type)
                  .then(
                    publishedAnalysis => {
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
        return new Promise<Analysis>((resolve, reject) => {
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
