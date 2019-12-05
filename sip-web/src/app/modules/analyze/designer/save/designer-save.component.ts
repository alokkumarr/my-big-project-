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

  constructor(private jwtService: JwtService) {}

  ngOnInit() {
    this.analysis.categoryId =
      this.designerMode === 'new'
        ? this.jwtService.userAnalysisCategoryId
        : this.analysis.categoryId;
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
