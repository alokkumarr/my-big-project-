import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import * as filter from 'lodash/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpReduce from 'lodash/fp/reduce';
import * as isString from 'lodash/isString';
import * as upperCase from 'lodash/upperCase';
import { JwtService } from '../../../common/services';
import { ToastService } from '../../../common/services/toastMessage.service';
import { Analysis } from '../types';
import { AnalysisDSL } from '../../../models';
import { AnalyzeActionsService } from './analyze-actions.service';
import { DesignerSaveEvent, isDSLAnalysis } from '../designer/types';
import * as clone from 'lodash/clone';
import * as get from 'lodash/get';
import { SYSTEM_CATEGORY_OPERATIONS } from './../../../common/consts';

@Component({
  selector: 'analyze-actions-menu-u',
  templateUrl: 'analyze-actions-menu.component.html'
})
export class AnalyzeActionsMenuComponent implements OnInit {
  @Output() afterEdit: EventEmitter<DesignerSaveEvent> = new EventEmitter();
  @Output() afterExport: EventEmitter<null> = new EventEmitter();
  @Output() afterExecute: EventEmitter<AnalysisDSL> = new EventEmitter();
  @Output() afterDelete: EventEmitter<Analysis> = new EventEmitter();
  @Output() afterPublish: EventEmitter<AnalysisDSL> = new EventEmitter();
  @Output() afterSchedule: EventEmitter<AnalysisDSL> = new EventEmitter();
  @Output() detailsRequested: EventEmitter<boolean> = new EventEmitter();
  @Input() analysis: Analysis | AnalysisDSL;
  @Input() exclude: string;
  @Input() category;
  @Input('actionsToDisable')
  set disabledActions(actionsToDisable: string) {
    this.actionsToDisable = fpPipe(
      actionsToDisableString =>
        isString(actionsToDisableString)
          ? actionsToDisableString.split('-')
          : [],
      fpReduce((acc, action) => {
        acc[action] = true;
        return acc;
      }, {})
    )(actionsToDisable);
  }
  public actionsToDisable = {};
  public categoryDetails;

  actions = [
    {
      label: 'Details',
      value: 'details',
      fn: () => {
        this.detailsRequested.emit(true);
      }
    },
    {
      label: 'Execute',
      value: 'execute',
      fn: this.execute.bind(this)
    },
    {
      label: 'Fork & Edit',
      value: 'fork',
      fn: this.fork.bind(this)
    },
    {
      label: 'Edit',
      value: 'edit',
      fn: this.edit.bind(this)
    },
    {
      label: 'Publish',
      value: 'publish',
      fn: this.publish.bind(this)
    },
    {
      label: 'Schedule',
      value: 'publish',
      fn: this.schedule.bind(this)
    },
    {
      label: 'Export',
      value: 'export',
      fn: this.export.bind(this)
    },
    {
      label: 'Delete',
      value: 'delete',
      fn: this.delete.bind(this),
      color: 'red'
    }
  ];

  constructor(
    public _analyzeActionsService: AnalyzeActionsService,
    public _jwt: JwtService,
    public _toastMessage: ToastService
  ) {}

  ngOnInit() {
    this.categoryDetails = this._jwt.fetchCategoryDetails(this.category)[0];
    const privilegeMap = { print: 'export', details: 'access' };
    const actionsToExclude = isString(this.exclude)
      ? this.exclude.split('-')
      : [];
    this.actions = filter(this.actions, ({ value }) => {
      if (get(this.categoryDetails, 'systemCategory')) {
        if (SYSTEM_CATEGORY_OPERATIONS.includes(value)) {
          return true;
        }
      }
      const notExcluded = !actionsToExclude.includes(value);
      const privilegeName = upperCase(privilegeMap[value] || value);
      const subCategoryId = isDSLAnalysis(this.analysis)
        ? this.analysis.category
        : this.analysis.categoryId;
      const hasPriviledge = this.doesUserHavePrivilege(
        privilegeName,
        subCategoryId
      );
      return notExcluded && hasPriviledge;
    });
  }

  doesUserHavePrivilege(privilegeName, subCategoryId) {
    const hasPrivilegeForCurrentFolder = this._jwt.hasPrivilege(privilegeName, {
      subCategoryId
    });
    const needsPrivilegeForDraftsFolder = ['EDIT', 'FORK', 'CREATE'].includes(
      privilegeName
    );
    const hasPrivilegeForDraftsFolder = this._jwt.hasPrivilegeForDraftsFolder(
      privilegeName
    );
    return (
      hasPrivilegeForCurrentFolder &&
      (!needsPrivilegeForDraftsFolder || hasPrivilegeForDraftsFolder)
    );
  }

  edit() {
    this._analyzeActionsService.edit(this.analysis);
  }

  fork() {
    this._analyzeActionsService.fork(this.analysis);
  }

  execute() {
    this._analyzeActionsService
      .execute(this.analysis)
      .then((analysis: AnalysisDSL) => {
        if (analysis) {
          this.afterExecute.emit(analysis);
        }
      });
  }

  delete() {
    if (get(this.categoryDetails, 'systemCategory')) {
      this._toastMessage.error('Cannot Delete From a System Category Folder');
      return false;
    }
    this._analyzeActionsService.delete(this.analysis).then(wasSuccessful => {
      if (wasSuccessful) {
        this.afterDelete.emit(<Analysis>this.analysis);
      }
    });
  }

  publish() {
    if (get(this.categoryDetails, 'systemCategory')) {
      this._toastMessage.error('Cannot Publish From a System Category Folder');
      return false;
    }
    const analysis = clone(this.analysis);
    this._analyzeActionsService.publish(analysis).then(publishedAnalysis => {
      this.analysis = publishedAnalysis;
      this.afterPublish.emit(publishedAnalysis);
    });
  }

  schedule() {
    const analysis = clone(this.analysis);
    this._analyzeActionsService.schedule(analysis).then(scheduledAnalysis => {
      this.analysis = scheduledAnalysis;
      this.afterPublish.emit(scheduledAnalysis);
    });
  }

  export() {
    this.afterExport.emit();
  }
}
