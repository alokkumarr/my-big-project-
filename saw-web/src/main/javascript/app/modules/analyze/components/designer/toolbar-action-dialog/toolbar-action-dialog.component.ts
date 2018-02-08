declare const require: any;
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as cloneDeep from 'lodash/cloneDeep';
import {
  IToolbarActionData,
  IToolbarActionResult
} from '../types';
import { DesignerService } from '../designer.service';

const template = require('./toolbar-action-dialog.component.html');
require('./toolbar-action-dialog.component.scss');

@Component({
  selector: 'toolbar-action-dialog',
  template
})
export class ToolbarActionDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ToolbarActionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IToolbarActionData,
    private _designerService: DesignerService
  ) { }

  ngOnInit() {
    switch (this.data.action) {
    case 'sort':
      this.data.sorts = cloneDeep(this.data.sorts);
      break;
    case 'filter':
      this.data.filters = cloneDeep(this.data.filters);
      break;
    }
  }

  onBack() {
    this.dialogRef.close();
  }

  onSortsChange(sorts) {
    this.data.sorts = sorts;
  }

  onFiltersChange(filters) {
    this.data.filters = filters;
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
    switch (this.data.action) {
    case 'sort':
      result.sorts = this.data.sorts;
      break;
    case 'filter':
      result.filters = this.data.filters;
      result.booleanCriteria = this.data.booleanCriteria;
      break;
    case 'description':
      result.description = this.data.description;
      break;
    }
    this.dialogRef.close(result);
  }

  save() {
    this._designerService.saveAnalysis(this.data.analysis)
      .then(response => {
        this.data.analysis.id = response.id;
      }).finally(() => {
        const result: IToolbarActionResult = {
          isSaveSuccessful: true
        }
        this.dialogRef.close(result);
      });
  }
}
