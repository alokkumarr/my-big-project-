import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { UserService } from '../user.service';
import { ToastService } from '../../../../common/services/toastMessage.service';

const template = require('./user-edit-dialog.component.html');
require('./user-edit-dialog.component.scss');

const namePattern = /^[a-zA-Z]*$/;
const loginIdPattern = /^[A-z\d_@.#$=!%^)(\]:\*;\?\/\,}{'\|<>\[&\+-`~]*$/;
const dummyPassword = '*********';

@Component({
  selector: 'user-edit-dialog',
  template
})
export class UserEditDialogComponent {

  userGroup: FormGroup;
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
    private _userService: UserService,
    private _fb: FormBuilder,
    private _toastMessage: ToastService,
    private _dialogRef: MatDialogRef<UserEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      user: any,
      roles$: any[],
      mode: 'edit' | 'create'
    }
  ) {
    if (this.data.mode === 'edit') {
      this.formIsValid = true;
    }
    this.createForm(this.data.user);
  }

  createUser() {
    const formValues = this.userGroup.getRawValue();
    // if the password wasn't changed, set it to null
    if (this.data.mode === 'edit' && formValues.password === dummyPassword) {
      formValues.password = null;
    }
    const user = {
      ...this.data.user,
      ...formValues
    };

    let actionPromise;
    switch (this.data.mode) {
    case 'edit':
      actionPromise = this._userService.updateUser(user);
      break;
    case 'create':
      actionPromise = this._userService.saveUser(user);
      break;
    }

    actionPromise && actionPromise.then(
      users => {
        if (users) {
          this._dialogRef.close(users);
        }
      }
  );
  }

  onPasswordFocus(event) {
    if (this.data.mode === 'edit' && event.target.value === dummyPassword) {
      const password = '';
      this.userGroup.patchValue({password});
    }
  }

  onPasswordBlur(event) {
    if (this.data.mode === 'edit' && event.target.value === '') {
      const password = dummyPassword;
      this.userGroup.patchValue({password});
    }
  }

  createForm(user) {
    const mode = this.data.mode;
    if (mode === 'edit') {
      user.activeStatusInd = user.activeStatusInd === 'Active' ? 1 : 0;
    }
    const {
      roleId = '',
      activeStatusInd = 1,
      masterLoginId = '',
      firstName = '',
      lastName = '',
      middleName = '',
      email = ''
    } = user;

    const firstNameControl = this._fb.control(firstName, [
      Validators.required,
      Validators.pattern(namePattern)
    ]);
    const lastNameControl = this._fb.control(lastName, [
      Validators.required,
      Validators.pattern(namePattern)
    ]);

    const passwordValue = mode === 'edit' ?
      dummyPassword : '';
    const passwordControl = this._fb.control(
      passwordValue,
      Validators.required
    );

    this.userGroup = this._fb.group({
      roleId: [roleId, Validators.required],
      middleName: middleName,
      firstName: firstNameControl,
      lastName: lastNameControl,
      masterLoginId: [masterLoginId, [
        Validators.required,
        Validators.pattern(loginIdPattern)
      ]],
      password: passwordControl,
      email: [email, [Validators.required, Validators.email]],
      activeStatusInd: [activeStatusInd, Validators.required]
    });

    // combine firstname and lastName into masterLoginId
    Observable.combineLatest(
      firstNameControl.valueChanges,
      lastNameControl.valueChanges
    ).subscribe(([first, last]) => {
      const masterLoginId = `${first}.${last}`;
      this.userGroup.patchValue({masterLoginId});
    });

    // enable disable the create user/ save button
    this.userGroup.statusChanges.subscribe(change => {
      if (change === 'VALID') {
        this.formIsValid = true;
      } else {
        this.formIsValid = false;
      }
    });
  }
}
