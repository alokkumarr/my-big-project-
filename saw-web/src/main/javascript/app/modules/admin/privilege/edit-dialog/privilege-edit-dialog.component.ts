import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import * as find from 'lodash/find';
import { PrivilegeService } from '../privilege.service';
import { BaseDialogComponent } from '../../../../common/base-dialog';

const template = require('./privilege-edit-dialog.component.html');
require('./privilege-edit-dialog.component.scss');

@Component({
  selector: 'privilege-edit-dialog',
  template
})
export class PrivilegeEditDialogComponent extends BaseDialogComponent {

  formGroup: FormGroup;
  subCategoryFormGroup: FormGroup;
  formIsValid = false;
  privilegeId: number;
  products$;
  roles$;
  modules$;
  categories$;
  subCategories$;

  constructor(
    private _privilegeService: PrivilegeService,
    private _fb: FormBuilder,
    private _dialogRef: MatDialogRef<PrivilegeEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      model: any,
      formDeps: {customerId: string},
      mode: 'edit' | 'create'
    }
  ) {
    super();
    const { customerId } = this.data.formDeps;

    if (this.data.mode === 'edit') {
      this.formIsValid = true;
      const { productId, moduleId, roleId, categoryCode } = this.data.model;
      this.modules$ = this.loadModules(productId, customerId);
      this.categories$ = this.loadCategories(moduleId, customerId);
      this.subCategories$ = this.loadSubCategories(moduleId, customerId, roleId, productId, categoryCode);
    }

    this.products$ = this._privilegeService.getProducts(customerId);
    this.roles$ = this._privilegeService.getRoles(customerId);
    this.privilegeId = this.data.model.privilegeId;
    this.createForm(this.data.model);
  }

  onPrivilegeChange({index, privilege}) {
    console.log('index', index);
    console.log('privilege', privilege);
  }

  create() {
    const formValues = this.formGroup.getRawValue();

    const model = {
      ...this.data.model,
      ...formValues
    };
    this.save(model);
  }

  save(model) {
    let actionPromise;
    switch (this.data.mode) {
    case 'edit':
      actionPromise = this._privilegeService.update(model);
      break;
    case 'create':
      actionPromise = this._privilegeService.save(model);
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
    const isInCreateMode = mode === 'create';
    const {
      productId = '',
      roleId = '',
      moduleId = '',
      categoryCode = ''
    } = formModel;

    const productIdControl = this._fb.control(productId, Validators.required);
    const roleIdControl = this._fb.control(roleId, Validators.required);
    const moduleIdControl = this._fb.control({value: moduleId, disabled: isInCreateMode}, Validators.required);
    const categoryCodeControl = this._fb.control({value: categoryCode, disabled: isInCreateMode});

    this.formGroup = this._fb.group({
      productId: productIdControl,
      roleId: roleIdControl,
      moduleId: moduleIdControl,
      categoryCode: categoryCodeControl
    });

    this.formGroup.statusChanges.subscribe(change => {
      if (change === 'VALID') {
        this.formIsValid = true;
      } else {
        this.formIsValid = false;
      }
    });

    productIdControl.valueChanges.subscribe(productId => {
      const { customerId } = this.data.formDeps;
      this.modules$ = this.loadModules(productId, customerId).then(modules => {
        moduleIdControl.enable();
        return modules;
      });
    });

    roleIdControl.valueChanges.subscribe(roleId => {
      const { moduleId, customerId, productId, categoryCode } = this.formGroup.value;
      this.modules$ = this.loadSubCategories(moduleId, customerId, roleId, productId, categoryCode)
        .then(subCategories => {
          categoryCodeControl.enable();
          return subCategories;
        });
    });

    moduleIdControl.valueChanges.subscribe(moduleId => {
      const { customerId } = this.data.formDeps;
      this.categories$ = this.loadCategories(moduleId, customerId).then(categories => {
        return categories;
      });
    });

    categoryCodeControl.valueChanges.subscribe(roleId => {
      const { moduleId, customerId, productId, categoryCode } = this.formGroup.value;
      this.modules$ = this.loadSubCategories(moduleId, customerId, roleId, productId, categoryCode)
        .then(subCategories => {
          return subCategories;
        });
    });
  }

  loadModules(productId, customerId) {
    const moduleParams = {
      customerId,
      productId,
      moduleId: 0
    };
    return this._privilegeService.getModules(moduleParams);
  }

  loadCategories(moduleId, customerId) {
    const categoryParams = {
      customerId,
      productId: 0,
      moduleId
    };
    return this._privilegeService.getParentCategories(categoryParams);
  }

  loadSubCategories(moduleId, customerId, roleId, productId, categoryCode) {
    const categoryParams = {
      customerId,
      roleId,
      productId,
      moduleId,
      categoryCode
    };
    return this._privilegeService.getSubCategories(categoryParams);
  }
}
