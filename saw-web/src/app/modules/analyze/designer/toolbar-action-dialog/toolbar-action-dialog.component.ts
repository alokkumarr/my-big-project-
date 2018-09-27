import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as cloneDeep from 'lodash/cloneDeep';

import { IToolbarActionData, IToolbarActionResult } from '../types';
import { DesignerService } from '../designer.service';
import { AnalysisReport } from '../types';
import { HeaderProgressService } from '../../../../common/services';

const template = require('./toolbar-action-dialog.component.html');
const style = require('./toolbar-action-dialog.component.scss');

@Component({
  selector: 'toolbar-action-dialog',
  template,
  styles: [style]
})
export class ToolbarActionDialogComponent implements OnInit, OnDestroy {
  showProgress = false;
  progressSub;
  filterValid = true;
  constructor(
    public dialogRef: MatDialogRef<ToolbarActionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IToolbarActionData,
    private _designerService: DesignerService,
    private _headerProgress: HeaderProgressService
  ) {
    this.progressSub = _headerProgress.subscribe(showProgress => {
      this.showProgress = showProgress;
    });
  }

  ngOnInit() {
    /* prettier-ignore */
    switch (this.data.action) {
    case 'sort':
      this.data.sorts = cloneDeep(this.data.sorts);
      break;
    }
  }

  ngOnDestroy() {
    this.progressSub.unsubscribe();
  }

  validateSaving() {
    const validateState =
      this.data.analysis.name.replace(/\s/g, '').length === 0 ? true : false;
    return validateState;
  }

  onBack() {
    this.dialogRef.close();
  }

  onSortsChange(sorts) {
    this.data.sorts = sorts;
  }

  onBooleanCriteriaChange(booleanCriteria) {
    this.data.booleanCriteria = booleanCriteria;
  }

  onDescriptionChange(description) {
    this.data.description = description;
  }

  onSaveDescriptionChange(description) {
    this.data.analysis.description = description;
  }

  onNameChange(name) {
    this.data.analysis.name = name;
  }

  onOk() {
    const result: IToolbarActionResult = {};
    /* prettier-ignore */
    switch (this.data.action) {
    case 'sort':
      result.sorts = this.data.sorts;
      break;
    case 'description':
      result.description = this.data.description;
      break;
    }
    this.dialogRef.close(result);
  }

  save(action) {
    this._designerService
      .saveAnalysis(this.data.analysis)
      .then((response: any) => {
        this.data.analysis.id = response.id;

        if (response.type === 'report') {
          (this.data.analysis as AnalysisReport).query = response.query;
        }
        const result: IToolbarActionResult = {
          analysis: this.data.analysis,
          action
        };
        this.dialogRef.close(result);
      });
  }
}
