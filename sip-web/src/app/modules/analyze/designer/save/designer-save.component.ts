import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { Analysis } from '../types';
import {
  USER_ANALYSIS_CATEGORY_NAME,
  USER_ANALYSIS_SUBCATEGORY_NAME
} from '../../../../common/consts';
import { JwtService } from '../../../../common/services';
import { validateEntityName,
  entityNameErrorMessage
} from './../../../../common/validators/field-name-rule.validator';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'designer-save',
  templateUrl: 'designer-save.component.html'
})
export class DesignerSaveComponent implements OnInit {
  @Output() public nameChange: EventEmitter<string> = new EventEmitter();
  @Output() public descriptionChange: EventEmitter<string> = new EventEmitter();
  @Input() public analysis: Analysis;
  @Input() public designerMode: string;

  userCategoryName = USER_ANALYSIS_CATEGORY_NAME;
  userSubCategoryName = USER_ANALYSIS_SUBCATEGORY_NAME;

  public categories;
  public saveForm: FormGroup;

  constructor(private jwtService: JwtService,
    public fb: FormBuilder,
    ) {}

  ngOnInit() {
    this.saveForm = this.fb.group({
      name: ['', [Validators.required,
        Validators.maxLength(30)],
        this.validatePattern
      ]
    });

    this.analysis.categoryId =
      this.designerMode === 'new'
        ? this.jwtService.userAnalysisCategoryId
        : this.analysis.categoryId;
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
}
