import { Component, Inject, HostBinding } from '@angular/core'; import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import * as find from 'lodash/find';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpMap from 'lodash/fp/map';
import * as fpOmit from 'lodash/fp/omit';
import { CategoryService } from '../category.service';
import { BaseDialogComponent } from '../../../../common/base-dialog';

const style = require('./category-edit-dialog.component.scss');

const namePattern = /^[a-zA-Z\s]*$/;

@Component({
  selector: 'category-edit-dialog',
  templateUrl: './category-edit-dialog.component.html',
  styles: [
    `:host {
      max-width: 500px;
      width: 700px;
    }`,
    style
  ]
})
export class CategoryEditDialogComponent extends BaseDialogComponent {

  @HostBinding('class.wide') isInWideMode = false;
  formGroup: FormGroup;
  subCategoryFormGroup: FormGroup;
  formIsValid = false;
  subCaegoryFormIsValid = true;
  products$;
  modules$;
  categories$;
  subCategories = [];
  isSubCategoryModified = false;
  subCategoryFlag = false;
  isNewSubCategorySelecting = false;
  statuses = [{
    id: 1,
    value: 'Active',
    name: 'ACTIVE'
  }, {
    id: 0,
    value: 'Inactive',
    name: 'INACTIVE'
  }];

  constructor(
    public _categoryService: CategoryService,
    public _fb: FormBuilder,
    public _dialogRef: MatDialogRef<CategoryEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      model: any,
      formDeps: {customerId: string},
      mode: 'edit' | 'create'
    }
  ) {
    super();
    if (this.data.mode === 'edit') {
      this.formIsValid = true;
      const { productId, moduleId, subCategories } = this.data.model;
      this.modules$ = this.loadModules(productId, this.data.formDeps);
      this.categories$ = this.loadParentCategories(moduleId, this.data.formDeps);
      this.subCategories = subCategories;
      this.isInWideMode = subCategories && subCategories.length > 0;
    }
    const customerId = data.formDeps.customerId;

    this.products$ = this._categoryService.getProducts(customerId);
    this.createForm(this.data.model);
  }

  create() {
    const formValues = this.formGroup.getRawValue();

    let model;
    switch (this.data.mode) {
    case 'create':
      model = {
        ...this.data.model,
        ...formValues
      };
      if (!model.subCategoryInd) {
        model.categoryId = null;
      }
      break;
    case 'edit':
      const subCategories = fpPipe(
        fpFilter('modifiedFlag'),
        fpMap(fpOmit('modifiedFlag'))
      )(this.subCategories);
      model = {
        ...this.data.model,
        ...formValues,
        subCategories
      };
      break;
    }

    if (model.subCategoryInd) {
      this.categories$.then(categories => {
        const target = find(categories, ({categoryId}) => categoryId === model.categoryId);
        if (target) {
          model.categoryType = target.categoryType;
          model.categoryCode = target.categoryCode;
        }
        this.save(model);
      });
    } else {
      this.save(model);
    }
  }

  save(model) {
    let actionPromise;
    switch (this.data.mode) {
    case 'edit':
      actionPromise = this._categoryService.update(model);
      break;
    case 'create':
      actionPromise = this._categoryService.save(model);
      break;
    }

    actionPromise && actionPromise.then(
      rows => {
        if (rows) {
          this._dialogRef.close(rows);
        }
      }
    );
  }

  createForm(formModel) {
    const mode = this.data.mode;
    const {
      subCategoryInd = false,
      productId = '',
      moduleId = '',
      categoryId = '',
      categoryName = '',
      categoryDesc = '',
      activeStatusInd = 1
    } = formModel;

    const productIdControl = this._fb.control(productId, Validators.required);
    const subCategoryIndControl = this._fb.control(subCategoryInd);
    const moduleIdControl = this._fb.control({value: moduleId, disabled: true}, Validators.required);
    const categoryIdControl = this._fb.control({value: categoryId, disabled: true});

    this.formGroup = this._fb.group({
      subCategoryInd: subCategoryIndControl,
      productId: productIdControl,
      moduleId: moduleIdControl,
      categoryId: categoryIdControl,
      categoryName: [categoryName, [Validators.required, Validators.pattern(namePattern)]],
      categoryDesc: categoryDesc,
      activeStatusInd: [activeStatusInd, Validators.required]
    });

    this.formGroup.statusChanges.subscribe(change => {
      if (change === 'VALID') {
        this.formIsValid = true;
      } else {
        this.formIsValid = false;
      }
    });

    productIdControl.valueChanges.subscribe(productId => {
      this.modules$ = this.loadModules(productId, this.data.formDeps).then(modules => {
        moduleIdControl.enable();
        return modules;
      });
    });

    moduleIdControl.valueChanges.subscribe(moduleId => {
      this.categories$ = this.loadParentCategories(moduleId, this.data.formDeps).then(categories => {
        categoryIdControl.enable();
        return categories;
      });
    });

    if (this.data.mode === 'edit') {
      this.createSubCategoryForm(formModel);
    } else {
      subCategoryIndControl.valueChanges.subscribe(isSubCategory => {
        if (isSubCategory) {
          categoryIdControl.setValidators(Validators.required);
        } else {
          categoryIdControl.setValidators(Validators.nullValidator);
        }
      });
    }
  }

  loadModules(productId, formDeps) {
    const { customerId } = formDeps;
    const moduleParams = {
      customerId,
      productId,
      moduleId: 0
    };
    return this._categoryService.getModules(moduleParams);
  }

  loadParentCategories(moduleId, formDeps) {
    const { customerId } = formDeps;
    const categoryParams = {
      customerId,
      productId: 0,
      moduleId
    };
    return this._categoryService.getParentCategories(categoryParams);
  }

  createSubCategoryForm(model) {
    const { subCategories } = model;
    if (!(subCategories && subCategories.length > 0)) {
      return;
    }
    this.subCategoryFlag = true;
    const selected = subCategories[0];
    const selectedControl = this._fb.control(selected);
    const categoryControl = this._fb.group({
      subCategoryName: [selected.subCategoryName, [Validators.required, Validators.pattern(namePattern)]],
      subCategoryDesc: selected.subCategoryDesc,
      activestatusInd: [selected.activestatusInd, Validators.required]
    });

    this.subCategoryFormGroup = this._fb.group({
      selectedSubCategory: selectedControl,
      categoryFormGroup: categoryControl
    });

    selectedControl.valueChanges.subscribe(value => {
      const { subCategoryName, subCategoryDesc, activestatusInd } = value;
      this.isNewSubCategorySelecting = true;
      categoryControl.setValue({
        subCategoryName,
        subCategoryDesc,
        activestatusInd
      });
    });

    categoryControl.valueChanges.subscribe(values => {
      if (this.isNewSubCategorySelecting) {
        this.isNewSubCategorySelecting = false;
        return;
      }
      const target = find(subCategories, cat => cat === selectedControl.value);
      target.subCategoryName = values.subCategoryName;
      target.subCategoryDesc = values.subCategoryDesc;
      target.activestatusInd = values.activestatusInd;
      target.modifiedFlag = true;
      if (!this.isSubCategoryModified) {
        this.isSubCategoryModified = true;
        selectedControl.disable();
      }
    });

    categoryControl.statusChanges.subscribe(validity => {
      if (selectedControl.enabled) {
        if (validity === 'INVALID') {
          if (!this.isSubCategoryModified) {
            selectedControl.disable();
          }
          this.subCaegoryFormIsValid = false;
        }
      } else {
        if (validity === 'VALID') {
          if (!this.isSubCategoryModified) {
            selectedControl.enable();
          }
          this.subCaegoryFormIsValid = true;
        }
      }
    });
  }
}
