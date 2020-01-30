import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { combineLatest } from 'rxjs';
import { UserService } from '../user.service';
import { BaseDialogComponent } from '../../../../common/base-dialog';
import { validatePassword } from 'src/app/common/validators/password-policy.validator';

/* Need to match validations put in place in BE and FE.
Hence these custom regex overriding default angular validations */
const emailIdPattern = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*";
const loginIdPattern = "^[A-z\\d_@.#$=!%^)(\\]:\\*;\\?\\/\\,}{'\\|<>\\[&\\+-`~]*$";

const dummyPassword = '*********';

@Component({
  selector: 'user-edit-dialog',
  templateUrl: './user-edit-dialog.component.html',
  styleUrls: ['./user-edit-dialog.component.scss']
})
export class UserEditDialogComponent extends BaseDialogComponent {
  formGroup: FormGroup;
  formIsValid = false;
  statuses = [
    {
      id: 1,
      value: 'Active',
      name: 'ACTIVE'
    },
    {
      id: 0,
      value: 'Inactive',
      name: 'INACTIVE'
    }
  ];

  public passwordError = '';
  constructor(
    public _userService: UserService,
    public _fb: FormBuilder,
    public _dialogRef: MatDialogRef<UserEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      model: any;
      formDeps: {
        roles$: any[];
      };
      mode: 'edit' | 'create';
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
    // if the password wasn't changed, set it to null
    if (this.data.mode === 'edit' && formValues.password === dummyPassword) {
      formValues.password = null;
    } else {
      this.passwordError = validatePassword(
        formValues.password,
        formValues.masterLoginId
      );
      if (this.passwordError) {
        return;
      }
    }
    const model = {
      ...this.data.model,
      ...formValues
    };

    let actionPromise;
    switch (this.data.mode) {
      case 'edit':
        actionPromise = this._userService.update(model);
        break;
      case 'create':
        actionPromise = this._userService.save(model);
        break;
    }

    actionPromise &&
      actionPromise.then(rows => {
        if (rows) {
          this._dialogRef.close(rows);
        }
      });
  }

  createForm(model) {
    const mode = this.data.mode;
    if (mode === 'edit') {
      model.activeStatusInd = model.activeStatusInd === 'Active' ? 1 : 0;
    }
    const {
      roleId = '',
      activeStatusInd = 1,
      masterLoginId = '',
      firstName = '',
      lastName = '',
      middleName = '',
      email = ''
    } = model;

    const firstNameControl = this._fb.control(firstName, [
      Validators.required
    ]);

    const middleNameControl = this._fb.control(middleName, []);

    const lastNameControl = this._fb.control(lastName, [
      Validators.required
    ]);

    const passwordValue = mode === 'edit' ? dummyPassword : '';
    const passwordControl = this._fb.control(
      passwordValue,
      Validators.required
    );

    this.formGroup = this._fb.group({
      roleId: [roleId, Validators.required],
      middleName: middleNameControl,
      firstName: firstNameControl,
      lastName: lastNameControl,
      masterLoginId: [
        masterLoginId,
        [Validators.required, Validators.pattern(loginIdPattern)]
      ],
      password: passwordControl,
      email: [email, [Validators.required, Validators.pattern(emailIdPattern)]],
      activeStatusInd: [activeStatusInd, Validators.required]
    });

    // combine firstname and lastName into masterLoginId
    combineLatest(
      firstNameControl.valueChanges,
      lastNameControl.valueChanges
    ).subscribe(([first, last]) => {
      const masterLoginIdValue = `${first}.${last}`;
      this.formGroup.patchValue({ masterLoginId: masterLoginIdValue });
    });

    // enable disable the create user/ save button
    this.formGroup.statusChanges.subscribe(change => {
      if (change === 'VALID') {
        this.formIsValid = true;
      } else {
        this.formIsValid = false;
      }
    });
  }

  createUserPasswordChange(event) {
    const password =
      event.target.value === '' ? dummyPassword : event.target.value;
    this.formGroup.patchValue({ password });
  }
}
