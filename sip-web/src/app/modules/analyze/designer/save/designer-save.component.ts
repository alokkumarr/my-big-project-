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
  @Input() public analysis: AnalysisDSL;
  @Input() public designerMode: string;

  categories = [];

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

  onCategorySelect({ value }: MatSelectChange) {
    if (+value === this.userSubCategoryId) {
      this.parentPublishChange.emit(null);
    } else {
      this.parentPublishChange.emit(value);
    }
  }
}
