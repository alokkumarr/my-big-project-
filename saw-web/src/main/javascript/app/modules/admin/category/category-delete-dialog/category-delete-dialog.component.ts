import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { CategoryService } from '../category.service';
import { BaseDialogComponent } from '../../../../common/base-dialog';

const template = require('./category-delete-dialog.component.html');
require('./category-delete-dialog.component.scss');

@Component({
  selector: 'delete-edit-dialog',
  template
})
export class CategoryDeleteDialogComponent extends BaseDialogComponent {

  constructor(
    private _categoryService: CategoryService,
    private _dialogRef: MatDialogRef<CategoryDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      category: any,
      customerId: number,
      masterLoginId: string
    }
  ) {
    super();
  }

  deleteCategory() {
    const { customerId, masterLoginId, category } = this.data
    const params = {
      customerId,
      masterLoginId,
      categoryId: category.categoryId,
      categoryCode: category.categoryCode
    };
    this._dialogRef.close(params);
  }

  deleteSubCategory(subCategory) {
    const { customerId, masterLoginId, category } = this.data
    const params = {
      customerId,
      masterLoginId,
      categoryId: subCategory.subCategoryId,
      categoryCode: category.categoryCode
    };
    this._categoryService.removeSubCategory(params).then(subCategories => {
      this.data.category.subCategories = subCategories;
    });
  }
}
