import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RoleService } from '../role.service';
import { BaseDialogComponent } from '../../../../common/base-dialog';

const template = require('./role-edit-dialog.component.html');
require('./role-edit-dialog.component.scss');

const namePattern = /^[a-zA-Z]*$/;

@Component({
  selector: 'role-edit-dialog',
  template
})
export class RoleEditDialogComponent extends BaseDialogComponent {

  formGroup: FormGroup;
  formIsValid = false;
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
    private _roleService: RoleService,
    private _fb: FormBuilder,
    private _dialogRef: MatDialogRef<RoleEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      model: any,
      formDeps: {
        roleTypes$: any[]
      },
      mode: 'edit' | 'create'
    }
  ) {
    super();
    if (this.data.mode === 'edit') {
      this.formIsValid = true;
    }
    this.createForm(this.data.model);
  }

  create() {
    const formValues = this.formGroup.getRawValue();
    const model = {
      ...this.data.model,
      ...formValues
    };

    let actionPromise;
    switch (this.data.mode) {
    case 'edit':
      actionPromise = this._roleService.update(model);
      break;
    case 'create':
      actionPromise = this._roleService.save(model);
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
    if (mode === 'edit') {
      formModel.activeStatusInd = formModel.activeStatusInd === 'Active' ? 1 : 0;
    }
    const {
      roleName = '',
      roleDesc = '',
      activeStatusInd = 1,
      roleType = '',
      myAnalysis
    } = formModel;

    this.formGroup = this._fb.group({
      roleName: [roleName, [Validators.required, Validators.pattern(namePattern)]],
      roleDesc: roleDesc,
      activeStatusInd: [activeStatusInd, Validators.required],
      roleType: [roleType, Validators.required],
      myAnalysis: myAnalysis
    });

    this.formGroup.statusChanges.subscribe(change => {
      if (change === 'VALID') {
        this.formIsValid = true;
      } else {
        this.formIsValid = false;
      }
    });
  }
}
