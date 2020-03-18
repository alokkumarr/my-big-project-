import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { Analysis } from '../types';
import {
  USER_ANALYSIS_CATEGORY_NAME,
  USER_ANALYSIS_SUBCATEGORY_NAME
} from '../../../../common/consts';
import {
  validateEntityName,
  entityNameErrorMessage
} from './../../../../common/validators/field-name-rule.validator';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material';
import { AnalyzeService } from '../../services/analyze.service';
import { PRIVILEGES } from '../../consts';
import * as find from 'lodash/find';

@Component({
  selector: 'designer-save',
  templateUrl: 'designer-save.component.html'
})
export class DesignerSaveComponent implements OnInit {
  @Output() public nameChange: EventEmitter<string> = new EventEmitter();
  @Output() public descriptionChange: EventEmitter<string> = new EventEmitter();
  @Output() public parentPublishChange: EventEmitter<
    number
  > = new EventEmitter();
  @Input() public analysis: Analysis;
  @Input() public designerMode: string;

  publishingToParentCategory = false;
  categories = [];

  userCategoryName = USER_ANALYSIS_CATEGORY_NAME;
  userSubCategoryName = USER_ANALYSIS_SUBCATEGORY_NAME;
  parentCategory = null;

  public saveForm: FormGroup;

  constructor(public fb: FormBuilder, private analyzeService: AnalyzeService) {}

  ngOnInit() {
    this.saveForm = this.fb.group({
      name: [
        this.analysis.name,
        [Validators.required, Validators.maxLength(30)],
        this.validatePattern
      ]
    });

    this.checkForParentAnalysis();
  }

  async checkForParentAnalysis() {
    if (!this.analysis.parentAnalysisId) {
      return;
    }

    try {
      this.categories = await this.analyzeService.getCategories(
        PRIVILEGES.PUBLISH
      );
      const parentAnalysis = await this.analyzeService.readAnalysis(
        this.analysis.parentAnalysisId,
        true
      );

      this.parentCategory = this.categories.reduce((result, cat) => {
        if (result) {
          return result;
        }

        const subCategory = find(
          cat.children,
          subCat => subCat.id.toString() === parentAnalysis.category.toString()
        );
        if (subCategory) {
          return {
            id: cat.id,
            name: cat.name,
            subCategoryId: subCategory.id,
            subCategoryName: subCategory.name
          };
        }
      }, null);
    } catch (error) {
      throw error;
    }
  }

  displayErrorMessage(state) {
    return entityNameErrorMessage(state);
  }

  validatePattern(control) {
    return new Promise((resolve, reject) => {
      if (/[`~!@#$%^&*()+={}|"':;?/>.<,*:/?[\]\\]/g.test(control.value)) {
        resolve({ nameIsInValid: true });
      } else {
        resolve(null);
      }
    });
  }

  onNameChange(description) {
    this.nameChange.emit(description);
  }

  validateNameField(name) {
    return validateEntityName(name);
  }

  validationErrorMessage(state) {
    return entityNameErrorMessage(state);
  }

  onDescriptionChange(description) {
    this.descriptionChange.emit(description);
  }

  getDestinationCategoryName() {
    return this.publishingToParentCategory && this.parentCategory
      ? this.parentCategory.name
      : this.userCategoryName;
  }

  getDestinationSubCategoryName() {
    return this.publishingToParentCategory && this.parentCategory
      ? this.parentCategory.subCategoryName
      : this.userSubCategoryName;
  }

  onPublishToggle({ checked }: MatCheckboxChange) {
    this.publishingToParentCategory = checked;

    this.parentPublishChange.emit(
      checked && this.parentCategory ? this.parentCategory.subCategoryId : null
    );
  }
}
