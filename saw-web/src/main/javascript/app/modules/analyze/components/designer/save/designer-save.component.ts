import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as first from 'lodash/first';
import * as find from 'lodash/find';
import {
  Analysis
} from '../types';
import {Events, PRIVILEGES} from '../../../consts';
import { DesignerService } from '../designer.service';

const template = require('./designer-save.component.html');

@Component({
  selector: 'designer-save',
  template
})
export class DesignerSaveComponent {
  @Output() public nameChange: EventEmitter<string> = new EventEmitter();
  @Output() public descriptionChange: EventEmitter<string> = new EventEmitter();
  @Input() public analysis: Analysis;

  public categories;

  constructor(
    private _designerService: DesignerService
  ) {}

  ngOnInit() {
    this._designerService.getCategories(PRIVILEGES.CREATE)
      .then(response => {
        this.categories = response;
        this.setDefaultCategory();
      });
  }

  setDefaultCategory() {
    if (!this.analysis.categoryId) {
      const defaultCategory = find(this.categories, category => category.children.length > 0);

      if (defaultCategory) {
        this.analysis.categoryId = first(defaultCategory.children).id;
      }
    }
  }

  onNameChange(description) {
    this.nameChange.emit(description);
  }

  onDescriptionChange(description) {
    this.descriptionChange.emit(description);
  }

  onCategorySelected(categoryId) {
    this.analysis.categoryId = categoryId;
  }
}
