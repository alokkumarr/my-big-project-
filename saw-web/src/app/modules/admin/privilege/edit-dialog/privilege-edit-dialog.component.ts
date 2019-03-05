import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as every from 'lodash/every';
import * as isEmpty from 'lodash/isEmpty';
import { getPrivilegeDescription } from '../privilege-code-transformer';
import { PrivilegeService } from '../privilege.service';
import { BaseDialogComponent } from '../../../../common/base-dialog';
import { first, tap, withLatestFrom, map as map$ } from 'rxjs/operators';

@Component({
  selector: 'privilege-edit-dialog',
  templateUrl: './privilege-edit-dialog.component.html',
  styleUrls: ['./privilege-edit-dialog.component.scss']
})
export class PrivilegeEditDialogComponent extends BaseDialogComponent {
  formGroup: FormGroup;
  subCategoryFormGroup: FormGroup;
  formIsValid = false;
  privilegeId: number;
  allowedPrivileges: string[];
  products$;
  roles$;
  modules$;
  categories;
  subCategories;

  constructor(
    public _privilegeService: PrivilegeService,
    public _fb: FormBuilder,
    public _dialogRef: MatDialogRef<PrivilegeEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      model: any;
      formDeps: { customerId: string; masterLoginId: string };
      mode: 'edit' | 'create';
    }
  ) {
    super();
    const { customerId } = this.data.formDeps;

    if (this.data.mode === 'edit') {
      this.formIsValid = true;
      const {
        productId,
        moduleId,
        moduleName,
        roleId,
        categoryCode
      } = this.data.model;
      this.modules$ = this.loadModules(productId);
      this.loadAllowedPrivilegesForModule(moduleName);
      this.loadCategories(moduleId);
      this.loadSubCategories(moduleId, roleId, productId, categoryCode);
    }

    this.products$ = this._privilegeService.getProducts(customerId);
    this.products$.then(() => {
      const productIdControl = this.formGroup.controls.productId;
      if (productIdControl) {
        productIdControl.enable();
      }
    });
    this.roles$ = this._privilegeService.getRoles(customerId);
    this.roles$.then(() => {
      const roleIdControl = this.formGroup.controls.roleId;
      if (roleIdControl) {
        roleIdControl.enable();
      }
    });
    this.privilegeId = this.data.model.privilegeId;
    this.createForm(this.data.model);
  }

  onPrivilegeChange({ index, privilege }) {
    const oldSubCategoryPrivilege = this.subCategories[index];
    this.subCategories.splice(index, 1, {
      ...oldSubCategoryPrivilege,
      ...privilege
    });
  }

  create() {
    const formValues = this.formGroup.getRawValue();
    const { customerId, masterLoginId } = this.data.formDeps;

    const targetCategory = find(
      this.categories,
      ({ categoryCode }) => formValues.categoryCode === categoryCode
    );
    const { categoryType, categoryId } = targetCategory;
    const model = {
      ...this.data.model,
      ...formValues,
      categoryId,
      categoryType,
      subCategoriesPrivilege: this.getSubCategoriesPrivilege(
        this.subCategories
      ),
      customerId,
      masterLoginId
    };
    this.save(model);
  }

  getSubCategoriesPrivilege(subCategories) {
    return map(
      subCategories,
      ({ privilegeCode, subCategoryId, privilegeId }) => {
        const privilegeDesc = getPrivilegeDescription(
          privilegeCode,
          this.allowedPrivileges
        );
        return {
          privilegeCode,
          privilegeDesc,
          subCategoryId,
          privilegeId
        };
      }
    );
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

    actionPromise &&
      actionPromise.then(rows => {
        if (rows) {
          this._dialogRef.close(rows);
        }
      });
  }

  createForm(formModel) {
    const {
      productId = '',
      roleId = '',
      moduleId = '',
      categoryCode = ''
    } = formModel;

    const productIdControl = this._fb.control(
      { value: productId, disabled: true },
      Validators.required
    );
    const roleIdControl = this._fb.control(
      { value: roleId, disabled: true },
      Validators.required
    );
    const moduleIdControl = this._fb.control(
      { value: moduleId, disabled: true },
      Validators.required
    );
    const categoryCodeControl = this._fb.control(
      { value: categoryCode, disabled: true },
      Validators.required
    );

    this.formGroup = this._fb.group({
      productId: productIdControl,
      roleId: roleIdControl,
      moduleId: moduleIdControl,
      categoryCode: categoryCodeControl
    });

    this.formGroup.statusChanges.subscribe(() => {
      // we have to use a manual validation because disabling a form control disables it's validation,
      // and the form can't be validated properly
      const isValid = every(this.formGroup.getRawValue(), Boolean);

      if (isValid) {
        this.formIsValid = true;
      } else {
        this.formIsValid = false;
      }
    });

    productIdControl.valueChanges.subscribe(pId => {
      this.modules$ = this.loadModules(pId);
    });

    roleIdControl.valueChanges.subscribe(rId => {
      const f = this.formGroup.value;
      this.loadSubCategories(f.moduleId, rId, f.productId, f.categoryCode);
    });

    moduleIdControl.valueChanges
      .pipe(
        withLatestFrom(this.modules$),
        map$(([mId, modules]) => {
          const module = find(modules, mod => mod.moduleId === mId) || {};
          return { modId: mId, modName: module.moduleName };
        })
      )
      .subscribe(({ modId, modName }) => {
        this.loadCategories(modId);
        this.loadAllowedPrivilegesForModule(modName);
        this.formGroup.controls.categoryCode.reset('');
        this.subCategories = [];
      });

    categoryCodeControl.valueChanges.subscribe(cCode => {
      const f = this.formGroup.value;
      this.loadSubCategories(f.moduleId, f.roleId, f.productId, cCode);
    });
  }

  loadModules(productId) {
    const { customerId } = this.data.formDeps;
    const moduleParams = {
      customerId,
      productId,
      moduleId: 0
    };
    return this._privilegeService.getModules(moduleParams).then(modules => {
      this.formGroup.controls.moduleId.enable({ emitEvent: false });
      return modules;
    });
  }

  /**
   * Loads allowed provileges for selected module
   *
   * @param {*} moduleName
   * @memberof PrivilegeEditDialogComponent
   */
  loadAllowedPrivilegesForModule(moduleName: string) {
    this._privilegeService
      .getPrivilegesForModule(moduleName)
      .pipe(
        first(),
        tap(privileges => {
          this.allowedPrivileges = privileges;
        })
      )
      .subscribe();
  }

  loadCategories(moduleId) {
    const { customerId } = this.data.formDeps;
    const categoryParams = {
      customerId,
      productId: 0,
      moduleId
    };
    return this._privilegeService
      .getParentCategories(categoryParams)
      .then(categories => {
        this.categories = categories;
        const categoryCodeControl = this.formGroup.controls.categoryCode;
        if (isEmpty(categories)) {
          categoryCodeControl.disable();
        } else {
          categoryCodeControl.enable();
        }
      });
  }

  loadSubCategories(moduleId, roleId, productId, categoryCode) {
    if (!(productId > 0 && roleId > 0 && moduleId > 0 && categoryCode !== '')) {
      return;
    }
    const { customerId } = this.data.formDeps;
    const categoryParams = {
      customerId,
      roleId,
      productId,
      moduleId,
      categoryCode
    };
    return this._privilegeService
      .getSubCategories(categoryParams)
      .then(subCategories => {
        this.subCategories = subCategories;
      });
  }
}
