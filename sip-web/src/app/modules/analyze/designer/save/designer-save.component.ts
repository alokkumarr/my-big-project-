import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import {
  validateEntityName,
  entityNameErrorMessage
} from './../../../../common/validators/field-name-rule.validator';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSelectChange } from '@angular/material';
import { AnalyzeService } from '../../services/analyze.service';
import { PRIVILEGES } from '../../consts';
import { AnalysisDSL } from '../../types';
import { JwtService } from 'src/app/common/services';
import * as get from 'lodash/get';

import {
  USER_ANALYSIS_CATEGORY_NAME,
  USER_ANALYSIS_SUBCATEGORY_NAME
} from '../../../../common/consts';

@Component({
  selector: 'designer-save',
  templateUrl: 'designer-save.component.html'
})
export class DesignerSaveComponent implements OnInit {
  @Output() public nameChange: EventEmitter<string> = new EventEmitter();
  @Output() public descriptionChange: EventEmitter<string> = new EventEmitter();
  @Output() public categoryChange: EventEmitter<number> = new EventEmitter();
  @Input() public analysis: AnalysisDSL;
  @Input() public designerMode: string;

  categories = [];

  userCategoryName = USER_ANALYSIS_CATEGORY_NAME;
  userSubCategoryName = USER_ANALYSIS_SUBCATEGORY_NAME;

  userSubCategoryId = +this.jwtService.userAnalysisCategoryId;

  public saveForm: FormGroup;

  constructor(
    public fb: FormBuilder,
    private analyzeService: AnalyzeService,
    private jwtService: JwtService
  ) {}

  ngOnInit() {
    this.saveForm = this.fb.group({
      name: [
        this.analysis.name,
        [Validators.required, Validators.maxLength(100)],
        this.validatePattern
      ],
      category: [
        (this.analysis.category || this.userSubCategoryId).toString(),
        [Validators.required]
      ]
    });

    this.onCategorySelect({
      value: this.saveForm.get('category').value
    } as any);

    this.loadAllCategories();


  }

  async loadAllCategories() {
    try {
      const categoryDetails = this.jwtService.fetchCategoryDetails(this.analysis.category)[0];
      if (get(categoryDetails, 'systemCategory')) {
        this.analysis.category = this.jwtService.userAnalysisCategoryId;
        this.categories = await this.analyzeService.getCategories(
          PRIVILEGES.PUBLISH
        );

        this.categories = this.categories.filter(
          category => category.data.prodModFeatureName.trim().toLowerCase() === 'my analysis'
        )
      } else {
        this.categories = await this.analyzeService.getCategories(
          PRIVILEGES.PUBLISH
        );
      }

    } catch (error) {
      throw error;
    }
  }

  fetchChildren(children) {
    return this.jwtService.fetchChildren(children);
  }

  displayErrorMessage(state) {
    if (state === 'nameLength') {
      return `* Name cannot be empty or exceed ${100} characters.`;
    }
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

  onCategorySelect({ value }: MatSelectChange) {
    this.categoryChange.emit(value);
  }
}
