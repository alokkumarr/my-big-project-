import clone from 'lodash/clone';

import template from './analyze-card.component.html';
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

    constructor($mdDialog, AnalyzeService, $log) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
      this._$log = $log;
    }

    openMenu($mdMenu, ev) {
      $mdMenu.open(ev);
    }

    showExecutingFlag() {
      return this._AnalyzeService.isExecuting(this.model.id);
    }

    remove() {
      this.onAction({
        type: 'delete',
        model: this.model
      });
    }

    publish() {
      this.onAction({
        type: 'publish',
        model: clone(this.model)
      });
    }

    execute() {
      this.onAction({
        type: 'execute',
        model: this.model
      });
    }

    fork() {
      this.onAction({
        type: 'fork',
        model: this.model
      });
    }

    edit() {
      this.onAction({
        type: 'edit',
        model: this.model
      });
    }

    print() {
      this.onAction({
        type: 'print',
        model: this.model
      });
    }

    export() {
      this.onAction({
        type: 'export',
        model: this.model
      });
    }
  }
};
