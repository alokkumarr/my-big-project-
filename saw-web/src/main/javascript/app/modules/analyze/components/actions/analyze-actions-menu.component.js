import * as filter from 'lodash/filter';
import * as isString from 'lodash/isString';
import * as invoke from 'lodash/invoke';
import * as upperCase from 'lodash/upperCase';

import * as template from './analyze-actions-menu.component.html';

export const AnalyzeActionsMenuComponent = {
  template,
  bindings: {
    analysis: '<',
    exclude: '@',
    onSuccessfulDeletion: '&',
    onSuccessfulExecution: '&',
    onSuccessfulPublish: '&',
    onFrontEndExport: '&'
  },
  controller: class AnalyzeActionsMenuController {
    constructor(AnalyzeActionsService, $state, JwtService) {
      'ngInject';
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._$state = $state;
      this._JwtService = JwtService;

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
      /* gui-cleanup-2.0 */
      //   label: 'PRINT',
      //   value: 'print',
      //   fn: this.print.bind(this)
      // }, {
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
      this.actions = filter(this.ACTIONS, ({value}) => {
        const notExcluded = !actionsToExclude.includes(value);
        const privilegeName = upperCase(value === 'print' ? 'export' : value);
        const hasPriviledge = this._JwtService.hasPrivilege(privilegeName, {
          subCategoryId: this.analysis.categoryId,
          creatorId: this.analysis.userId
        });

        return notExcluded && hasPriviledge;
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
      this._AnalyzeActionsService.publish(this.analysis).then(analysis => {
        invoke(this, 'onSuccessfulPublish', {analysis});
      });
    }

    print() {
      this._AnalyzeActionsService.print();
    }

    export() {
      // this._AnalyzeActionsService.exportAnalysis();
      this.onFrontEndExport();
    }

    delete() {
      this._AnalyzeActionsService.deleteAnalysis(this.analysis).then(analysis => {
        invoke(this, 'onSuccessfulDeletion', {analysis});
      });
    }
  }
};
