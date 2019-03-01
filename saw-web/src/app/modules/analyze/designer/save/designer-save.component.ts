import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { Analysis } from '../types';
import { DRAFT_CATEGORY_ID } from './../../consts';
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
  @Input() public analysis: Analysis;
  @Input() public designerMode: string;

  userCategoryName = USER_ANALYSIS_CATEGORY_NAME;
  userSubCategoryName = USER_ANALYSIS_SUBCATEGORY_NAME;

  public categories;

  constructor() {}

  ngOnInit() {
    this.analysis.categoryId =
      this.designerMode === 'new'
        ? DRAFT_CATEGORY_ID
        : this.analysis.categoryId;
  }

  onNameChange(description) {
    this.nameChange.emit(description);
  }

  onDescriptionChange(description) {
    this.descriptionChange.emit(description);
  }
}
