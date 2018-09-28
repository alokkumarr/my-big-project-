import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { CategoryService } from '../category.service';
import { BaseDialogComponent } from '../../../../common/base-dialog';

const style = require('./category-delete-dialog.component.scss');

const MY_ANALYSIS_CATEGORY_ID = 3;

@Component({
  selector: 'delete-edit-dialog',
  templateUrl: './category-delete-dialog.component.html',
  styles: [style]
})
export class CategoryDeleteDialogComponent extends BaseDialogComponent {

  MY_ANALYSIS_CATEGORY_ID = MY_ANALYSIS_CATEGORY_ID;

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
    const { customerId, masterLoginId, category } = this.data;
    const params = {
      customerId,
      masterLoginId,
      categoryId: category.categoryId,
      categoryCode: category.categoryCode
    };
    this._dialogRef.close(params);
  }

  deleteSubCategory(subCategory) {
    const { customerId, masterLoginId, category } = this.data;
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
