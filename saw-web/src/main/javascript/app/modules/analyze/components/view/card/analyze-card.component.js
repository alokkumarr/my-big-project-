import * as template from './analyze-card.component.html';
import style from './analyze-card.component.scss';

export const AnalyzeCardComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onAction: '&',
    highlightTerm: '<'
  },
  controller: class AnalyzeCardController {

    constructor($mdDialog, AnalyzeService, $log, AnalyzeActionsService, JwtService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._$log = $log;
      this._JwtService = JwtService;

      this.canUserFork = false;
    }

    $onInit() {
      this.canUserFork = this._JwtService.hasPrivilege('FORK', {
        subCategoryId: this.model.categoryId
      });
      this.analysisType = this.model.type;
      if (this.model.type === 'esReport') {
        this.analysisType = 'REPORT';
      }
    }

    showExecutingFlag() {
      return this._AnalyzeService.isExecuting(this.model.id);
    }

    fork() {
      this._AnalyzeActionsService.fork(this.model);
    }

    onSuccessfulDeletion(analysis) {
      this.onAction({
        type: 'onSuccessfulDeletion',
        model: analysis
      });
    }

    onSuccessfulExecution(analysis) {
      this.onAction({
        type: 'onSuccessfulExecution',
        model: analysis
      });
    }

    onSuccessfulPublish(analysis) {
      this.onAction({
        type: 'onSuccessfulPublish',
        model: analysis
      });
    }
  }
};
