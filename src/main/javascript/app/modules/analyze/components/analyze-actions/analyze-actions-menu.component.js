import filter from 'lodash/filter';
import isString from 'lodash/isString';
import invoke from 'lodash/invoke';

import template from './analyze-actions-menu.component.html';

export const AnalyzeActionsMenuComponent = {
  template,
  bindings: {
    analysis: '<',
    exclude: '@',
    onSuccessfulDeletion: '&',
    onSuccessfulExecution: '&'
  },
  controller: class AnalyzeActionsMenuController {
    constructor(AnalyzeActionsService, $state) {
      'ngInject';
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._$state = $state;

      this.ACTIONS = [{
        label: 'EXECUTE',
        value: 'execute',
        fn: this.execute.bind(this)
      }, {
        label: 'FORK_AND_EDIT',
        value: 'fork',
        fn: this.fork.bind(this)
      }, {
        label: 'EDIT',
        value: 'edit',
        fn: this.edit.bind(this)
      }, {
        label: 'PUBLISH',
        value: 'publish',
        fn: this.publish.bind(this)
      }, {
        label: 'PRINT',
        value: 'print',
        fn: this.print.bind(this)
      }, {
        label: 'EXPORT',
        value: 'export',
        fn: this.export.bind(this)
      }, {
        label: 'DELETE',
        value: 'delete',
        fn: this.delete.bind(this),
        color: 'warn'
      }];
    }

    $onInit() {

      const actionsToExclude = isString(this.exclude) ? this.exclude.split('-') : [];

      this.actions = filter(this.ACTIONS, action => {
        return !actionsToExclude.includes(action.value);
      });

    }

    execute() {
      this._AnalyzeActionsService.execute(this.analysis).then(analysis => {
        invoke(this, 'onSuccessfulExecution', {analysis});
      });
    }

    fork() {
      this._AnalyzeActionsService.fork(this.analysis);
    }

    edit() {
      this._AnalyzeActionsService.edit(this.analysis);
    }

    publish() {
      this._AnalyzeActionsService.publish(this.analysis).then(() => {
        this._$state.go('analyze.view', {id: this.analysis.categoryId});
      });
    }

    print() {
      this._AnalyzeActionsService.print();
    }

    export() {
      this._AnalyzeActionsService.exportAnalysis();
    }

    delete() {
      this._AnalyzeActionsService.deleteAnalysis(this.analysis).then(analysis => {
        invoke(this, 'onSuccessfulDeletion', {analysis});
      });
    }
  }
};
