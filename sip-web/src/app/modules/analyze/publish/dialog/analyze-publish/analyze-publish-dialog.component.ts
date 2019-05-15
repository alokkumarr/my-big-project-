import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as find from 'lodash/find';
import * as first from 'lodash/first';

import { AnalyzeService } from '../../../services/analyze.service';
import { JwtService } from '../../../../../common/services';
import { BaseDialogComponent } from '../../../../../common/base-dialog/base-dialog.component';
import { Analysis } from '../../../types';
import { PRIVILEGES } from '../../../consts';
import { USER_ANALYSIS_CATEGORY_NAME } from '../../../../../common/consts';
import { isDSLAnalysis } from '../../../designer/types';

@Component({
  selector: 'analyze-publish-dialog',
  templateUrl: './analyze-publish-dialog.component.html',
  styleUrls: ['./analyze-publish-dialog.component.scss']
})
export class AnalyzePublishDialogComponent extends BaseDialogComponent
  implements OnInit {
  categories: any[] = [];
  token: any;
  hasPublishableCategories = true;

  constructor(
    public _dialogRef: MatDialogRef<AnalyzePublishDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      analysis: Analysis;
    },
    public _analyzeService: AnalyzeService,
    public _jwt: JwtService
  ) {
    super();
  }

  ngOnInit() {
    this.token = this._jwt.getTokenObj();
    this._analyzeService
      .getCategories(PRIVILEGES.PUBLISH)
      .then((response: any[]) => {
        this.categories = response.filter(
          category => category.name !== USER_ANALYSIS_CATEGORY_NAME
        );
        this.checkPublishableCategoriesPresent();
        this.setDefaultCategory();
      });
  }

  checkPublishableCategoriesPresent() {
    let publishableCategoriesCount = 0;
    this.categories.forEach(
      ({ children }) => (publishableCategoriesCount += children.length)
    );
    this.hasPublishableCategories = publishableCategoriesCount > 0;
  }

  onCategorySelected(value) {
    if (isDSLAnalysis(this.data.analysis)) {
      this.data.analysis.category = value;
    } else {
      this.data.analysis.categoryId = value;
    }
  }

  get categoryId() {
    if (isDSLAnalysis(this.data.analysis)) {
      return this.data.analysis.category;
    } else {
      return this.data.analysis.categoryId;
    }
  }

  setDefaultCategory() {
    const analysis = this.data.analysis;
    const categoryId = isDSLAnalysis(analysis)
      ? analysis.category
      : analysis.categoryId;
    if (!categoryId) {
      const defaultCategory = find(
        this.categories,
        category => category.children.length > 0
      );

      if (defaultCategory) {
        if (isDSLAnalysis(analysis)) {
          analysis.category = first(defaultCategory.children).id;
        } else {
          analysis.categoryId = first(defaultCategory.children).id;
        }
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
