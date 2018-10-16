import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

const template = require('./add-security-dialog.component.html');
require('./add-security-dialog.component.scss');

@Component({
  selector: 'add-secuirty-dialog',
  template
})
export class AddSecurityDialogComponent {
  public securityGroup = {};

  constructor(
    private _dialogRef: MatDialogRef<AddSecurityDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create'
    }
  ) {}

  create() {
    console.log(this.securityGroup.name);

    console.log(this.securityGroup.desc);
    // const model = {
    //   ...this.data.model,
    //   ...formValues
    // };

    // let actionPromise;
    // switch (this.data.mode) {
    // case 'edit':
    //   actionPromise = this._userService.update(model);
    //   break;
    // case 'create':
    //   actionPromise = this._userService.save(model);
    //   break;
    // }

    // actionPromise && actionPromise.then(
    //   rows => {
    //     if (rows) {
    //       this._dialogRef.close(rows);
    //     }
    //   }
    // );
  }
}
