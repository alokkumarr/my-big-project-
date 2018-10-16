import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserAssignmentService } from './../userassignment.service';

const template = require('./add-attribute-dialog.component.html');
require('./add-attribute-dialog.component.scss');

@Component({
  selector: 'add-attribute-dialog',
  template
})
export class AddAttributeDialogComponent {
  public attribute = {};
  constructor(
    private _dialogRef: MatDialogRef<AddAttributeDialogComponent>,
    private _userAssignmentService: UserAssignmentService,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create'
    }
  ) {}

  create() {
    this._userAssignmentService.addAttributetoGroup(this.attribute).then(response => {
      console.log(resposne);
    });
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
