import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import { Component } from '@angular/core';
import { MdDialogRef } from '@angular/material';
import { JwtService } from '../../../../../login/services/jwt.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

require('./analysis-choice.component.scss');
const template = require('./analysis-choice.component.html');

const ALLOWED_ANALYSIS_TYPES = ['chart'];

@Component({
  selector: 'analysis-choice',
  template
})
export class AnalysisChoiceComponent {
  public selection = {
    category: null,
    subCategory: null,
    analysis: null
  };

  public showProgress = false;
  public categories = [];
  public subCategories = [];
  public analyses = [];

  constructor(public dialogRef: MdDialogRef<AnalysisChoiceComponent>,
    public jwt: JwtService,
    public analyze: AnalyzeService
  ) {}

  ngOnInit() {
    this.categories = map(
      this.jwt.getCategories(),
      category => ({ name: category.prodModFeatureName, id: category.prodModFeatureID, data: category })
    );
  }

  onCategoryUpdated() {
    this.selection.subCategory = null;
    this.selection.analysis = null;
    const subCategories = map(
      get(find(this.categories, category => this.selection.category === category.id), 'data.productModuleSubFeatures'),
      subCategory => ({ id: subCategory.prodModFeatureID, name: subCategory.prodModFeatureName, data: subCategory })
    );

    /* Only allow subcategories which have access to execute operation */
    this.subCategories = filter(subCategories, subCategory =>
      this.jwt.hasPrivilege('EXECUTE', {subCategoryId: subCategory.id})
    );
  }

  onSubCategoryUpdated() {
    this.selection.analysis = null;
    this.showProgress = true;
    this.analyze.getAnalysesFor(this.selection.subCategory.toString()).then(result => {
      this.showProgress = false;
      this.analyses = filter(result, analysis => analysis && ALLOWED_ANALYSIS_TYPES.includes(analysis.type));
    }, () => {
      this.showProgress = false;
    });
  }

  closeDialog(data) {
    this.dialogRef.close(data);
  }

  save() {
    const analysis = find(this.analyses, analysis => analysis.id === this.selection.analysis);
    this.closeDialog(analysis);
  }
}
