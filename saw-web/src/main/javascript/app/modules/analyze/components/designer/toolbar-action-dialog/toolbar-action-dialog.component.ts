import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as cloneDeep from 'lodash/cloneDeep';
import { IToolbarActionData, IToolbarActionResult } from '../types';
import * as filter from 'lodash/filter';
import { DesignerService } from '../designer.service';
import { AnalysisReport } from '../types';

const template = require('./toolbar-action-dialog.component.html');
require('./toolbar-action-dialog.component.scss');

@Component({
  selector: 'toolbar-action-dialog',
  template
})
export class ToolbarActionDialogComponent {
  showProgressBar = false;
  filterValid: boolean = true;
  constructor(
    public dialogRef: MatDialogRef<ToolbarActionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IToolbarActionData,
    private _designerService: DesignerService
  ) {}

  ngOnInit() {
    /* prettier-ignore */
    switch (this.data.action) {
    case 'sort':
      this.data.sorts = cloneDeep(this.data.sorts);
      break;
    }
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
    let result: IToolbarActionResult = {};
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

  save() {
    this.showProgressBar = true;
    this._designerService
      .saveAnalysis(this.data.analysis)
      .then(response => {
        this.data.analysis.id = response.id;

        if (response.type === 'report') {
          (this.data.analysis as AnalysisReport).query = response.query;
        }
      })
      .finally(() => {
        this.showProgressBar = false;
        const result: IToolbarActionResult = {
          isSaveSuccessful: true,
          analysis: this.data.analysis
        };
        this.dialogRef.close(result);
      });
  }
}
