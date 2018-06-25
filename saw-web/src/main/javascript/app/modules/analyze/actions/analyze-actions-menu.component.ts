import { Component, EventEmitter, Input, Output } from '@angular/core';
import * as filter from 'lodash/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpReduce from 'lodash/fp/reduce';
import * as isString from 'lodash/isString';
import * as upperCase from 'lodash/upperCase';
import { JwtService } from '../../../../login/services/jwt.service';
import { Analysis } from '../types';
import { AnalyzeActionsService } from './analyze-actions.service';
import { DesignerSaveEvent } from '../components/designer/types';

const template = require('./analyze-actions-menu.component.html');

@Component({
  selector: 'analyze-actions-menu-u',
  template
})

export class AnalyzeActionsMenuComponent {
  @Output() afterEdit: EventEmitter<Analysis> = new EventEmitter();
  @Output() afterExport: EventEmitter<null> = new EventEmitter();
  @Output() afterExecute: EventEmitter<Analysis> = new EventEmitter();
  @Output() afterDelete: EventEmitter<Analysis> = new EventEmitter();
  @Output() afterPublish: EventEmitter<Analysis> = new EventEmitter();
  @Input() analysis: Analysis;
  @Input() exclude: string;
  @Input('actionsToDisable') set disabledActions(actionsToDisable: string) {
    this.actionsToDisable = fpPipe(
      actionsToDisableString => isString(actionsToDisableString) ? actionsToDisableString.split('-') : [],
      fpReduce((acc, action) => {
        acc[action] = true;
        return acc;
      }, {})
    )(actionsToDisable);
  };
  public actionsToDisable = {};

  actions = [{
    label: 'Execute',
    value: 'execute',
    fn: this.execute.bind(this)
  }, {
    label: 'Fork & Edit',
    value: 'fork',
    fn: this.fork.bind(this)
  }, {
    label: 'Edit',
    value: 'edit',
    fn: this.edit.bind(this)
  }, {
    label: 'Publish',
    value: 'publish',
    fn: this.publish.bind(this)
  }, {
    label: 'Export',
    value: 'export',
    fn: this.export.bind(this)
  }, {
    label: 'Delete',
    value: 'delete',
    fn: this.delete.bind(this),
    color: 'red'
  }]

  constructor(
    private _analyzeActionsService: AnalyzeActionsService,
    private _jwt: JwtService
  ) {}

  ngOnInit() {
    const actionsToExclude = isString(this.exclude) ? this.exclude.split('-') : [];
    this.actions = filter(this.actions, ({value}) => {
      const notExcluded = !actionsToExclude.includes(value);
      const privilegeName = upperCase(value === 'print' ? 'export' : value);
      const hasPriviledge = this._jwt.hasPrivilege(privilegeName, {
        subCategoryId: this.analysis.categoryId,
        creatorId: this.analysis.userId
      });

      return notExcluded && hasPriviledge;
    });
  }

  edit() {
    this._analyzeActionsService.edit(this.analysis).then((result: DesignerSaveEvent) => {
      if (result) {
        const {isSaveSuccessful, analysis} = result;
        if (!isSaveSuccessful) {
          return isSaveSuccessful;
        }
        this.afterEdit.emit(analysis);
      }
    });
  }

  fork() {
    this._analyzeActionsService.fork(this.analysis).then(status => {
      if (!status) {
        return status;
      }
      this.afterEdit.emit();
    });
  }

  execute() {
    this._analyzeActionsService.execute(this.analysis).then(analysis => {
      if (analysis) {
        this.afterExecute.emit(analysis);
      }
    });
  }

  delete() {
    this._analyzeActionsService.delete(this.analysis).then(wasSuccessful => {
      if (wasSuccessful) {
        this.afterDelete.emit(this.analysis);
      }
    });
  }

  publish() {
    this._analyzeActionsService.publish(this.analysis).then(analysis => {
      this.afterPublish.emit(analysis);
    });
  }

  export() {
    this.afterExport.emit();
  }
}
