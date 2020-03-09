import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as flatMap from 'lodash/flatMap';

import { AnalyzeService } from '../../../services/analyze.service';
import { JwtService } from '../../../../../common/services';
import { BaseDialogComponent } from '../../../../../common/base-dialog/base-dialog.component';
import { AnalysisDSL } from '../../../types';
import { PRIVILEGES } from '../../../consts';
import { USER_ANALYSIS_CATEGORY_NAME } from '../../../../../common/consts';

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
      analysis: AnalysisDSL;
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

  get publishableSubCategories(): any[] {
    if (!this.categories) {
      return [];
    }

    return flatMap(this.categories, category => category.children);
  }

  checkPublishableCategoriesPresent() {
    let publishableCategoriesCount = 0;
    this.categories.forEach(
      ({ children }) => (publishableCategoriesCount += children.length)
    );
    this.hasPublishableCategories = publishableCategoriesCount > 0;
  }

  onCategorySelected(value) {
    this.data.analysis.category = value;
  }

  get categoryId() {
    return (this.data.analysis.category || '').toString();
  }

  setDefaultCategory() {
    const analysis = this.data.analysis;
    const categoryId = analysis.category.toString();
    const publishableCategoryIds = this.publishableSubCategories.map(subcat =>
      subcat.id.toString()
    );
    if (!categoryId || !publishableCategoryIds.includes(categoryId)) {
      const defaultCategory = publishableCategoryIds[0];

      if (defaultCategory) {
        analysis.category = defaultCategory;
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
