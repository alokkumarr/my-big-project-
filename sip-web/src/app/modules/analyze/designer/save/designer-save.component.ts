import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { Analysis } from '../types';
import {
  USER_ANALYSIS_CATEGORY_NAME,
  USER_ANALYSIS_SUBCATEGORY_NAME
} from '../../../../common/consts';
import { JwtService } from '../../../../common/services';
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
  @Output() public categoryChange: EventEmitter<number> = new EventEmitter();
  @Input() public analysis: Analysis;
  @Input() public designerMode: string;

  publishingToOtherCategory = false;
  categories = [];

  userCategoryName = USER_ANALYSIS_CATEGORY_NAME;
  userSubCategoryName = USER_ANALYSIS_SUBCATEGORY_NAME;

  public saveForm: FormGroup;

  constructor(
    private jwtService: JwtService,
    public fb: FormBuilder,
    private analyzeService: AnalyzeService
  ) {}

  ngOnInit() {
    this.saveForm = this.fb.group({
      name: [
        this.analysis.name,
        [Validators.required, Validators.maxLength(30)],
        this.validatePattern
      ]
    });

    this.analyzeService.getCategories(PRIVILEGES.PUBLISH).then(response => {
      this.categories = response;
    });
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

  onCategoryChange($event) {}

  onPublishToggle({ checked }: MatCheckboxChange) {
    this.publishingToOtherCategory = checked;

    if (!checked) {
      this.categoryChange.emit(this.jwtService.userAnalysisCategoryId);
    } else {
      const category = find(
        this.categories,
        cat => cat.children && cat.children.length > 0
      );
      if (category) {
        this.categoryChange.emit(category.children[0].id);
      } else {
        this.categoryChange.emit(this.jwtService.userAnalysisCategoryId);
      }
    }
  }
}
