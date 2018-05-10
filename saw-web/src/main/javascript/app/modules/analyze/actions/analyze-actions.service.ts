import { Inject, Injectable } from '@angular/core';
import * as clone from 'lodash/clone';
import * as deepClone from 'lodash/cloneDeep';
import * as defaultsDeep from 'lodash/defaultsDeep';
import { HeaderProgressService } from '../../../common/services/header-progress.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { AnalyseTypes } from '../consts';
import { AnalyzeDialogService } from '../services/analyze-dialog.service';
import { AnalyzeService } from '../services/analyze.service';
import { FilterService } from '../services/filter.service';


@Injectable()
export class AnalyzeActionsService {

  constructor(
    private _filterService: FilterService,
    private _analyzeService: AnalyzeService,
    private _analyzeDialogService: AnalyzeDialogService,
    private _headerProgressService: HeaderProgressService,
    private _toastMessage: ToastService,
    @Inject('$mdDialog') private _$mdDialog: any
  ) {}

  execute(analysis) {
    return this._filterService.getRuntimeFilterValues(analysis).then(model => {
      this._analyzeService.executeAnalysis(model);
      return model;
    });
  }

  fork(analysis) {
    const model = clone(analysis);
    model.name += ' Copy';
    return this.openEditModal(model, 'fork');
  }

  edit(analysis) {
    return this.openEditModal(clone(analysis), 'edit');
  }

  publish(analysis) {
    return this.openPublishModal(clone(analysis));
  }

  delete(analysis) {
    return this.openDeleteModal(analysis);
  }

  openDeleteModal(analysis) {
    return new Promise(resolve => {
      this._analyzeDialogService.openDeleteConfirmationDialog().afterClosed().subscribe({
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
    const openModal = template => this.showDialog({
      template,
      controller: scope => {
        scope.model = deepClone(analysis);
      },
      multiple: true
    });

    switch (analysis.type) {
    case AnalyseTypes.ESReport:
    case AnalyseTypes.Report:
      return openModal(`<analyze-report model="model" mode="${mode}"></analyze-report>`);
    case AnalyseTypes.Chart:
      return openModal(`<analyze-chart model="model" mode="${mode}"></analyze-chart>`);
    case AnalyseTypes.Pivot:
      return this._analyzeDialogService.openEditAnalysisDialog(analysis, mode)
        .afterClosed().first().toPromise();
    default:
    }
  }

  openPublishModal(analysis) {
    const template = '<analyze-publish-dialog model="model" on-publish="onPublish(model)"></analyze-publish-dialog>';

    return this.showDialog({
      template,
      controller: scope => {
        scope.model = analysis;
        scope.onPublish = this.doPublish;
      }
    });
  }

  removeAnalysis(analysis) {
    this._headerProgressService.show();
    return this._analyzeService.deleteAnalysis(analysis).then(() => {
      this._headerProgressService.hide();
      this._toastMessage.info('Analysis deleted.');
      return true;
    }, err => {
      this._headerProgressService.hide();
      this._toastMessage.error(err.message || 'Analysis not deleted.');
    });
  }

  doPublish(analysis) {
    const execute = false;
    this._headerProgressService.show();
    return this._analyzeService.publishAnalysis(analysis, execute).then(updatedAnalysis => {
      this._headerProgressService.hide();
      this._toastMessage.info(execute ?
        'Analysis has been updated.' :
        'Analysis schedule changes have been updated.');
      return updatedAnalysis;
    }, () => {
      this._headerProgressService.hide();
    });
  }

  showDialog(config) {
    config = defaultsDeep(config, {
      controllerAs: '$ctrl',
      multiple: false,
      autoWrap: false,
      focusOnOpen: false,
      clickOutsideToClose: true,
      fullscreen: false
    });

    return this._$mdDialog.show(config);
  }
}
