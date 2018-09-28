import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as find from 'lodash/find';
import * as first from 'lodash/first';

import { AnalyzeService } from '../../../services/analyze.service';
import { JwtService } from '../../../../../common/services';
import { BaseDialogComponent } from '../../../../../common/base-dialog/base-dialog.component';
import { Analysis } from '../../../types';
import {PRIVILEGES} from '../../../consts';

const style = require('./analyze-publish-dialog.component.scss');

@Component({
  selector: 'analyze-publish-dialog',
  templateUrl: './analyze-publish-dialog.component.html',
  styles: [style]
})

export class AnalyzePublishDialogComponent extends BaseDialogComponent implements OnInit {

  categories: any[] = [];
  token: any;

  constructor(
    private _dialogRef: MatDialogRef<AnalyzePublishDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      analysis: Analysis;
    },
    private _analyzeService: AnalyzeService,
    private _jwt: JwtService
  ) {
    super();
  }

  ngOnInit() {
    this.token = this._jwt.getTokenObj();
    this._analyzeService.getCategories(PRIVILEGES.PUBLISH)
      .then(response => {
        this.categories = response;
        this.setDefaultCategory();
      });
  }

  onCategorySelected(value) {
    this.data.analysis.categoryId = value;
  }

  setDefaultCategory() {
    const analysis = this.data.analysis;
    if (!analysis.categoryId) {
      const defaultCategory = find(this.categories, category => category.children.length > 0);

      if (defaultCategory) {
        analysis.categoryId = first(defaultCategory.children).id;
      }
    }
  }

  publish() {
    this._dialogRef.close(this.data.analysis);
  }

  close() {
    this._dialogRef.close();
  }
}
